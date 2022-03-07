package com.webserver.exceptions;

import com.webserver.http.HttpStatusCode;

public class HttpParsingException extends RuntimeException {
	private HttpStatusCode code;

	public HttpParsingException(HttpStatusCode code) {
		super(code.message);
		this.code = code;
	}

	public HttpStatusCode getCode() {
		return code;
	}
}
