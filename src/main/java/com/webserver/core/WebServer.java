package com.webserver.core;

import com.webserver.config.Configuration;
import com.webserver.config.ConfigurationManager;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * This class is a web server
 */
public class WebServer {
	private final static Logger LOGGER = LogManager.getLogger(WebServer.class);
	/**
	 * The socket waits for clients to connect.
	 */
	private ServerSocket serverSocket;

	/**
	 * The thread pool is for threads handling client requests.
	 */
	private final ExecutorService pool;

	/**
	 * The thread pool is to execute cleaner thread repetitively.
	 */
	private final ScheduledExecutorService cleanerPool;

	/**
	 * The list is to store all existing client handlers.
	 */
	private final LinkedList<ClientHandler> handlers = new LinkedList<>();

	public WebServer() {
		Configuration configuration = ConfigurationManager.getInstance().getConfiguration();
		try {
			serverSocket = new ServerSocket(configuration.getPort());
		} catch (IOException e) {
			LOGGER.info("Error binding port ", e);
		}
		pool = Executors.newFixedThreadPool(configuration.getMaxThreadNum());
		cleanerPool = Executors.newScheduledThreadPool(1);
		cleanerPool.scheduleAtFixedRate(new SocketCleaner(), 5, 5, TimeUnit.SECONDS);
	}

	public void start() {
		try {
			while (serverSocket.isBound() && !serverSocket.isClosed()) {
				Socket socket = serverSocket.accept();
				LOGGER.info("Connected with " + socket.getInetAddress());
				ClientHandler handler = new ClientHandler(socket);
				handlers.add(handler);
				pool.execute(handler);
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} finally {
			stop();
		}
	}

	public void stop() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		ArgumentParser parser = ArgumentParsers.newArgumentParser("A simple webserver", true);
		parser.addArgument("--conf")
				.setDefault("webserver.properties")
				.help("Specify the configuration file.");

		Namespace ns = null;
		try {
			ns = parser.parseArgs(args);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}

		ConfigurationManager.getInstance().loadConfigFile(ns.getString("conf"));
		WebServer server = new WebServer();
		server.start();
	}

	/**
	 * This thread class clean up expired socket.
	 */
	public class SocketCleaner implements Runnable {

		@Override
		public void run() {
			long currentTimestamp = System.currentTimeMillis();
			LOGGER.debug("check: " + currentTimestamp);
			Iterator<ClientHandler> iterator = handlers.iterator();
			while (iterator.hasNext()) {
				ClientHandler handler = iterator.next();
				// If the client expires, close and remove it
				if (currentTimestamp - handler.getLastRequestTimestamp() >
						1000L * ConfigurationManager.getInstance().getConfiguration().getTimeout()) {
					handler.closeSocket();
					iterator.remove();
				}
			}
		}
	}
}
