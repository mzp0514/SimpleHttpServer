package com.webserver.http;

import com.webserver.exceptions.HttpParsingException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class parses the request message from the client.
 */
public class HttpParser {
	private final static Logger LOGGER = LogManager.getLogger(HttpParser.class);
	private final static String SP = " ";
	private final static String CRLF = "\r\n";
	private final static String EQ = "=";
	private final static String AND = "&";
	private final static String COLON = ":";

	/**
	 *  RFC-7230
	 *      HTTP-message   = request-line
	 *                       *( header-field CRLF )
	 *                       CRLF
	 *                       [ message-body ]
	 * @param inputStream
	 * @return
	 * @throws HttpParsingException
	 * @throws IOException
	 */
	public static HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		HttpRequest request = new HttpRequest();
		parseRequestLine(reader, request);
		parseHeaders(reader, request);
	    parseBody(reader, request);
		return request;
	}

	/**
	 * RFC-7230
	 *          request-line   = method SP request-target SP HTTP-version CRLF
	 * @param reader
	 * @param request
	 * @throws HttpParsingException
	 * @throws IOException
	 */
	private static void parseRequestLine(BufferedReader reader, HttpRequest request) throws HttpParsingException, IOException {
		String requestLine = reader.readLine();
		LOGGER.info(requestLine);

		if (requestLine == null || requestLine.length() == 0) {
			throw new HttpParsingException(HttpStatusCode.SC_BAD_REQUEST);
		}

		String[] strings = requestLine.split(SP);
		if (strings.length != 3) {
			throw new HttpParsingException(HttpStatusCode.SC_BAD_REQUEST);
		}

		// Method
		request.setMethod(strings[0]);

		// Request target
		URI uri = null;
		try {
			uri = new URI(strings[1]);
		} catch (URISyntaxException e) {
			throw new HttpParsingException(HttpStatusCode.SC_BAD_REQUEST);
		}
		// Default redirection file: index.html
		request.setRequestTarget(uri.getPath().equals("/")? "/index.html": uri.getPath());

		// Query
		if (uri.getQuery() != null && uri.getQuery().length() > 0) {
			String[] queries = uri.getQuery().split(AND);
			for (String query : queries) {
				String[] kv = query.split(EQ);
				request.setQuery(kv[0], kv[1]);
			}
		}

		// Http version
		request.setHttpVersion(strings[2]);
	}

	/**
	 * RFC-7230
	 *          header-field   = field-name ":" OWS field-value OWS
	 * @param reader
	 * @param request
	 * @throws HttpParsingException
	 * @throws IOException
	 */
	private static void parseHeaders(BufferedReader reader, HttpRequest request) throws HttpParsingException, IOException {
		String headerLine = reader.readLine();
		LOGGER.info(headerLine);
		while (headerLine.length() != 0) {
			String[] kv = headerLine.split(COLON);
			request.setHeader(kv[0].trim(), kv[1].trim());
			headerLine = reader.readLine();
		}
		request.setKeepAlive(
				request.getHeaderFields().getOrDefault("Connection", "keep-alive")
						.equalsIgnoreCase("keep-alive")
		);
	}

	/**
	 * RFC-7230
	 *              message-body = *OCTET
	 * @param reader
	 * @param request
	 * @throws HttpParsingException
	 */
	private static void parseBody(BufferedReader reader, HttpRequest request) throws HttpParsingException {
		int contentLength = Integer.parseInt(
				request.getHeaderFields().getOrDefault("Content-Length", "0")
		);
		if (contentLength == 0) return;

		char[] buf = new char[contentLength];
		try {
			reader.read(buf);
			request.setBody(buf);
		} catch (IOException e) {
			throw new HttpParsingException(HttpStatusCode.SC_BAD_REQUEST);
		}
	}

}
