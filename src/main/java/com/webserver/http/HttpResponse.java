package com.webserver.http;

import com.webserver.exceptions.HttpExecutionException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * RFC-7230
 *   	       HTTP-message   = HTTP-version SP status-code SP reason-phrase CRLF
 *   	                        *( header-field CRLF )
 *   	                        CRLF
 *   	                        [ message-body ]
 */
public class HttpResponse {
	private String httpVersion = "HTTP/1.1";
	private HttpStatusCode httpStatusCode;

	private boolean keepAlive;
	private final Map<String, String> headerFields = new HashMap();
	private InputStream body;

	public HttpResponse() {}

	public HttpResponse(HttpStatusCode statusCode) { this.httpStatusCode = statusCode; }

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	public void setStatusCode(HttpStatusCode statusCode) {
		this.httpStatusCode = statusCode;
	}

	public void setContentType(String contentType) {
		headerFields.put("Content-Type", contentType);
	}

	public void setContentLength(int contentLength) {
		headerFields.put("Content-Length", String.valueOf(contentLength));
	}


	public void setBody(InputStream inputStream) {
		this.body = inputStream;
	}

	public void setHeader(String key, String value) {
		headerFields.put(key, value);
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	/**
	 * Write response to the socket.
	 * @param outputStream
	 * @throws HttpExecutionException
	 */
	public void respond(OutputStream outputStream) throws HttpExecutionException {
		PrintStream printStream = new PrintStream(outputStream);

		// Status line
		printStream.println(httpVersion +  " " + httpStatusCode.code + " " + httpStatusCode.message);

		// Headers
		for (Map.Entry<String, String> entry: headerFields.entrySet()) {
			printStream.println(entry.getKey() + ":" + entry.getValue());
		}
		printStream.println();

		// Body
		if (body != null) {
			try {
				BufferedInputStream bis = new BufferedInputStream(body);
				byte[] bs = new byte[Integer.parseInt(headerFields.get("Content-Length"))];
				bis.read(bs);
				printStream.write(bs);
				body.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new HttpExecutionException(e);
			}
		}

		printStream.flush();
	}
}
