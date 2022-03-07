package com.webserver.http;

import com.webserver.exceptions.HttpParsingException;

import java.util.HashMap;
import java.util.Map;

/**
 * RFC-7230
 * 	       HTTP-message   = method SP request-target SP HTTP-version CRLF
 * 	                        *( header-field CRLF )
 * 	                        CRLF
 * 	                        [ message-body ]
 */
public class HttpRequest {
	private HttpMethod method;
	private String requestTarget;
	private String httpVersion;
	private char[] body;

	private boolean keepAlive;

	private final Map<String, String> headerFields = new HashMap();
	private final Map<String, String> queries = new HashMap();


	public HttpMethod getMethod() {
		return method;
	}

	/**
	 * If the method is not implemented, throw NOT IMPLEMENTED error.
	 * @param method
	 * @throws HttpParsingException
	 */
	public void setMethod(String method) throws HttpParsingException {
		for (HttpMethod m: HttpMethod.values()) {
			if (m.name().equals(method)) {
				this.method = m;
				return;
			}
		}
		throw new HttpParsingException(HttpStatusCode.SC_NOT_IMPLEMENTED);
	}

	public String getRequestTarget() {
		return requestTarget;
	}

	public void setRequestTarget(String requestTarget) {
		this.requestTarget = requestTarget;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	public void setQuery(String key, String value) {
		queries.put(key, value);
	}

	public void setHeader(String key, String value) {
		headerFields.put(key, value);
	}

	public Map<String, String> getHeaderFields() {
		return headerFields;
	}
	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public boolean getKeepAlive() {
		return keepAlive;
	}

	public char[] getBody() {
		return body;
	}

	public void setBody(char[] body) {
		this.body = body;
	}

	public String toString() {
		StringBuilder info = new StringBuilder();
		info.append(method.name()).append(" ").append(requestTarget).append(" ").append(httpVersion).append("\r\n");
		for (Map.Entry<String, String> entry: queries.entrySet()) {
			info.append(entry.toString()).append("\r\n");
		}
		for (Map.Entry<String, String> entry: headerFields.entrySet()) {
			info.append(entry.toString()).append("\r\n");
		}
		return info.toString();
	}
}
