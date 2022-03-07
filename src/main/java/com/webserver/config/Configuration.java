package com.webserver.config;

/**
 * This class store the configuration of the webserver.
 */
public class Configuration {

	/**
	 * The port of the server.
	 */
	private int port;

	/**
	 * The maximum thread number of the thread pool to handle the clients.
	 */
	private int maxThreadNum;

	/**
	 * Directory where the files that clients can request are stored.
	 */
	private String webroot;

	/**
	 * The time in seconds that the host will allow an idle connection to remain open before it is closed.
	 * 	A connection is idle if no data is sent or received by a host.
	 */
	private int timeout;

	public Configuration(int port, int maxThreadNum, String webroot, int timeout) {
		this.port = port;
		this.maxThreadNum = maxThreadNum;
		this.webroot = webroot;
		this.timeout = timeout;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getWebroot() {
		return webroot;
	}

	public void setWebroot(String webroot) {
		this.webroot = webroot;
	}

	public int getMaxThreadNum() {
		return maxThreadNum;
	}

	public void setMaxThreadNum(int maxThreadNum) {
		this.maxThreadNum = maxThreadNum;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
