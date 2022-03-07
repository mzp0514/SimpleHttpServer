package com.webserver.core;

import com.webserver.config.ConfigurationManager;
import com.webserver.exceptions.HttpExecutionException;
import com.webserver.exceptions.HttpParsingException;
import com.webserver.http.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.net.Socket;

/**
 * This class handles the requests from one socket.
 */
public class ClientHandler implements Runnable {

	private final static Logger LOGGER = LogManager.getLogger(ClientHandler.class);

	/**
	 * The socket between the client and the server.
	 */
	private final Socket socket;

	/**
	 * The last request timestamp of the client. To determine if the client expired and should be cleaned up.
	 * Unit: millisecond
	 */
	private long lastRequestTimestamp;

	public ClientHandler(Socket socket) {
		this.socket = socket;
		lastRequestTimestamp = System.currentTimeMillis();
	}

	public long getLastRequestTimestamp() {
		return lastRequestTimestamp;
	}

	public void closeSocket() {
		if (socket != null) {
			try {
				if (!socket.isInputShutdown()) {
					socket.shutdownInput();
				}
				if (!socket.isOutputShutdown()) {
					socket.shutdownOutput();
				}
				if (!socket.isClosed()) {
					socket.close();
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}


	@Override
	public void run() {
		// Obtain the input and output stream of the socket
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			return;
		}

		// To support keep-alive, process the requests from the client in a loop,
		// until the socket is closed due to timeout
		while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
			HttpResponse response = null;
			try {

				LOGGER.debug("Before handle request");
				HttpRequest request = HttpParser.parseHttpRequest(inputStream);
				LOGGER.debug("After handle request");
				lastRequestTimestamp = System.currentTimeMillis();
				response = handleRequest(request);
			} catch (FileNotFoundException e) {

				LOGGER.error(e.getMessage());
				response = new HttpResponse(HttpStatusCode.SC_NOT_FOUND);
			} catch (HttpParsingException e) {

				LOGGER.error(e.getMessage());
				response = new HttpResponse(e.getCode());
			} catch (IOException e) { // thrown by readLine() due to the socket closed by the cleaner

				LOGGER.error("Thread interrupted: " + e.getMessage());
				break;
			}

			try {
				response.respond(outputStream);
			} catch (HttpExecutionException e) {

				HttpResponse httpResponse = new HttpResponse(HttpStatusCode.SC_INTERNAL_SERVER_ERROE);
				httpResponse.respond(outputStream);
				LOGGER.error(e.getMessage());
			}

			// If not keep alive, break and close the socket
			if (!response.isKeepAlive()) {
				break;
			}
		}

		closeSocket();
	}

	/**
	 * @param request
	 * @return response
	 * @throws FileNotFoundException
	 */
	private HttpResponse handleRequest(HttpRequest request) throws FileNotFoundException {
		HttpResponse response = new HttpResponse();

		if (request.getKeepAlive()) {
			response.setKeepAlive(true);
			response.setHeader("Connection", "keep-alive");
			response.setHeader("Keep-Alive", "timeout=5");

		}

		// Generate response according to the request method
		// Currently support GET and HEAD
		switch (request.getMethod()) {
			case GET:
				handleGET(request, response);
				break;
			case HEAD:
				handleHEAD(request, response);
				break;
		}

		return response;
	}

	/**
	 * RFC-2616
	 * The GET method means retrieve whatever information (in the form of an
	 *    entity) is identified by the Request-URI. If the Request-URI refers
	 *    to a data-producing process, it is the produced data which shall be
	 *    returned as the entity in the response and not the source text of the
	 *    process, unless that text happens to be the output of the process.
	 * @param request
	 * @param response
	 * @throws FileNotFoundException
	 */
	private void handleGET(HttpRequest request, HttpResponse response) throws FileNotFoundException {
		LOGGER.info("enter handle get");
		File file = new File(ConfigurationManager.getInstance().getConfiguration().getWebroot()
				+ request.getRequestTarget());
		FileInputStream fileInputStream = new FileInputStream(file);

		response.setBody(fileInputStream);
		response.setContentLength((int) file.length());
		response.setStatusCode(HttpStatusCode.SC_OK);
		response.setContentType("text/html");
	}

	/**
	 * RFC-2616
	 * The HEAD method is identical to GET except that the server MUST NOT
	 *    return a message-body in the response. The metainformation contained
	 *    in the HTTP headers in response to a HEAD request SHOULD be identical
	 *    to the information sent in response to a GET request.
	 * @param request
	 * @param response
	 */
	private void handleHEAD(HttpRequest request, HttpResponse response) {
		LOGGER.info("enter handle head");
		File file = new File(ConfigurationManager.getInstance().getConfiguration().getWebroot()
				+ request.getRequestTarget());
		response.setContentLength((int) file.length());
		response.setStatusCode(HttpStatusCode.SC_OK);
		response.setContentType("text/html");
	}
}
