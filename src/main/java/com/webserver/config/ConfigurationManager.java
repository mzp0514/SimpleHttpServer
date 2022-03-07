package com.webserver.config;

import com.webserver.exceptions.HttpConfigurationException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * This class is a singleton class to manage the server configuration
 */
public class ConfigurationManager {

	private static ConfigurationManager configurationManager;
	private static Configuration configuration;

	private ConfigurationManager() {

	}

	public static ConfigurationManager getInstance() {
		if (configurationManager == null) {
			configurationManager = new ConfigurationManager();
		}
		return configurationManager;
	}

	public void loadConfigFile(String configPath) throws HttpConfigurationException {
		FileReader reader = null;
		try {
			reader = new FileReader(configPath);
		} catch (FileNotFoundException e) {
			throw new HttpConfigurationException("Configuration file not found", e);
		}
		Properties properties = new Properties();
		try {
			properties.load(reader);
		} catch (IOException e) {
			throw new HttpConfigurationException("Error parsing configuration file", e);
		}
		int port = Integer.parseInt(properties.getProperty("port", "8080"));
		int maxThreadNum = Integer.parseInt(properties.getProperty("max_thread_num", "100"));
		String webroot = properties.getProperty("webroot", "WebContent");
		int timeout = Integer.parseInt(properties.getProperty("timeout", "5"));
		configuration = new Configuration(port, maxThreadNum, webroot, timeout);
	}

	public Configuration getConfiguration() throws HttpConfigurationException {
		if (configuration == null) {
			throw new HttpConfigurationException("Configuration not available");
		}
		return configuration;
	}

}
