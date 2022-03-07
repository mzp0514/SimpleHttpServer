package com.webserver.http;

public enum HttpStatusCode {
	SC_OK(200, "OK"),
	SC_BAD_REQUEST(400, "Bad Request"),
	SC_METHOD_NOT_ALLOWED(401, "Method Not Allowed"),
	SC_NOT_FOUND(404, "Not Found"),

	SC_INTERNAL_SERVER_ERROE(500, "Internal Server Error"),
	SC_NOT_IMPLEMENTED(501, "Not Implemented");

	public final int code;
	public final String message;

	HttpStatusCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
