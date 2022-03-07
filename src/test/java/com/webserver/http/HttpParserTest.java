package com.webserver.http;

import com.webserver.exceptions.HttpParsingException;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

class HttpParserTest {

	private static String[] testStrs = {
			"GET /index.html HTTP/1.1\n" +
					"Host: localhost:8080\n" +
					"Connection: keep-alive\n" +
					"Cache-Control: max-age=0\n" +
					"sec-ch-ua: \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"98\", \"Google Chrome\";v=\"98\"\n" +
					"sec-ch-ua-mobile: ?0\n" +
					"sec-ch-ua-platform: \"Windows\"\n" +
					"Upgrade-Insecure-Requests: 1\n" +
					"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36\n" +
					"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
					"cp-extension-installed: Yes\n" +
					"Sec-Fetch-Site: cross-site\n" +
					"Sec-Fetch-Mode: navigate\n" +
					"Sec-Fetch-User: ?1\n" +
					"Sec-Fetch-Dest: document\n" +
					"Accept-Encoding: gzip, deflate, br\n" +
					"Accept-Language: en-US,en;q=0.9\n\n",
			"GET /index.html?k1=v1 HTTP/1.1\n" +
					"Host: localhost:8080\n" +
					"Connection: keep-alive\n" +
					"Cache-Control: max-age=0\n" +
					"sec-ch-ua: \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"98\", \"Google Chrome\";v=\"98\"\n" +
					"sec-ch-ua-mobile: ?0\n" +
					"sec-ch-ua-platform: \"Windows\"\n" +
					"Upgrade-Insecure-Requests: 1\n" +
					"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36\n" +
					"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
					"cp-extension-installed: Yes\n" +
					"Sec-Fetch-Site: cross-site\n" +
					"Sec-Fetch-Mode: navigate\n" +
					"Sec-Fetch-User: ?1\n" +
					"Sec-Fetch-Dest: document\n" +
					"Accept-Encoding: gzip, deflate, br\n" +
					"Accept-Language: en-US,en;q=0.9\n\n",
			"HEAD /index.html HTTP/1.1\n" +
					"Host: localhost:8080\n" +
					"Connection: keep-alive\n" +
					"Cache-Control: max-age=0\n" +
					"sec-ch-ua: \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"98\", \"Google Chrome\";v=\"98\"\n" +
					"sec-ch-ua-mobile: ?0\n" +
					"sec-ch-ua-platform: \"Windows\"\n" +
					"Upgrade-Insecure-Requests: 1\n" +
					"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36\n" +
					"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
					"cp-extension-installed: Yes\n" +
					"Sec-Fetch-Site: cross-site\n" +
					"Sec-Fetch-Mode: navigate\n" +
					"Sec-Fetch-User: ?1\n" +
					"Sec-Fetch-Dest: document\n" +
					"Accept-Encoding: gzip, deflate, br\n" +
					"Accept-Language: en-US,en;q=0.9\n\n",
			"POST /index.html HTTP/1.1\n" +
					"Host: localhost:8080\n" +
					"Connection: keep-alive\n" +
					"Cache-Control: max-age=0\n" +
					"sec-ch-ua: \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"98\", \"Google Chrome\";v=\"98\"\n" +
					"sec-ch-ua-mobile: ?0\n" +
					"sec-ch-ua-platform: \"Windows\"\n" +
					"Upgrade-Insecure-Requests: 1\n" +
					"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36\n" +
					"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
					"cp-extension-installed: Yes\n" +
					"Sec-Fetch-Site: cross-site\n" +
					"Sec-Fetch-Mode: navigate\n" +
					"Sec-Fetch-User: ?1\n" +
					"Sec-Fetch-Dest: document\n" +
					"Accept-Encoding: gzip, deflate, br\n" +
					"Accept-Language: en-US,en;q=0.9\n\n",
			"GET /index.html HTTP/1.1\n" +
					"Host: localhost:8080\n" +
					"Connection: keep-alive\n" +
					"Cache-Control: max-age=0\n" +
					"sec-ch-ua: \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"98\", \"Google Chrome\";v=\"98\"\n" +
					"sec-ch-ua-mobile: ?0\n" +
					"sec-ch-ua-platform: \"Windows\"\n" +
					"Upgrade-Insecure-Requests: 1\n" +
					"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36\n" +
					"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
					"cp-extension-installed: Yes\n" +
					"Sec-Fetch-Site: cross-site\n" +
					"Sec-Fetch-Mode: navigate\n" +
					"Sec-Fetch-User: ?1\n" +
					"Sec-Fetch-Dest: document\n" +
					"Accept-Encoding: gzip, deflate, br\n" +
					"Accept-Language: en-US,en;q=0.9\n" +
					"Content-Length: 2\n" +
					"\n" +
					"aa"
	};

	@org.junit.jupiter.api.Test
	void parseHttpRequest0() {
		String testString = testStrs[0];
		InputStream inputStream = new ByteArrayInputStream(testString.getBytes());
		try {
			HttpRequest request = HttpParser.parseHttpRequest(inputStream);
			assertEquals(request.getMethod().name(), "GET");
			assertEquals(request.getRequestTarget(), "/index.html");
			assertEquals(request.getHttpVersion(), "HTTP/1.1");
			assertTrue(request.getKeepAlive());
		} catch (HttpParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@org.junit.jupiter.api.Test
	void parseHttpRequest1() {
		String testString = testStrs[1];
		InputStream inputStream = new ByteArrayInputStream(testString.getBytes());
		try {
			HttpRequest request = HttpParser.parseHttpRequest(inputStream);
			assertEquals(request.getMethod().name(), "GET");
			assertEquals(request.getRequestTarget(), "/index.html");
			assertEquals(request.getHttpVersion(), "HTTP/1.1");
			assertTrue(request.getKeepAlive());
			assertEquals(request.getQueries().get("k1"), "v1");
		} catch (HttpParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@org.junit.jupiter.api.Test
	void parseHttpRequest2() {
		String testString = testStrs[2];
		InputStream inputStream = new ByteArrayInputStream(testString.getBytes());
		try {
			HttpRequest request = HttpParser.parseHttpRequest(inputStream);
			assertEquals(request.getMethod().name(), "HEAD");
			assertEquals(request.getRequestTarget(), "/index.html");
			assertEquals(request.getHttpVersion(), "HTTP/1.1");
			assertTrue(request.getKeepAlive());
		} catch (HttpParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@org.junit.jupiter.api.Test
	void parseHttpRequest3() {
		String testString = testStrs[3];
		InputStream inputStream = new ByteArrayInputStream(testString.getBytes());
		try {
			HttpParser.parseHttpRequest(inputStream);
		} catch (HttpParsingException e) {
			assertEquals(e.getCode().code, 501);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@org.junit.jupiter.api.Test
	void parseHttpRequest4() {
		String testString = testStrs[4];
		InputStream inputStream = new ByteArrayInputStream(testString.getBytes());
		try {
			HttpRequest request = HttpParser.parseHttpRequest(inputStream);
			assertEquals(request.getMethod().name(), "GET");
			assertEquals(request.getRequestTarget(), "/index.html");
			assertEquals(request.getHttpVersion(), "HTTP/1.1");
			assertEquals(request.getHeaderFields().get("Content-Length"), "2");
			assertEquals(String.valueOf(request.getBody()), "aa");
			assertTrue(request.getKeepAlive());
		} catch (HttpParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}