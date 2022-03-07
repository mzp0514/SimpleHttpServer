package com.webserver.http;

/**
 * RFC-2616
 * The methods GET and HEAD MUST be supported by all general-purpose servers.
 * All other methods are OPTIONAL.
 *
 * Currently support GET and HEAD
 * TODO: OPTIONS, POST, PUT, DELETE, TRACE, CONNECT
 */
public enum HttpMethod {
	GET, HEAD
}
