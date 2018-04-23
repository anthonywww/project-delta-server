package com.github.anthonywww.projectdeltaserver;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class Configuration {
	
	private final String comments;
	private final Properties properties;
	private final File configFile;
	private final HashMap<String, String> defaults;
	
	public Configuration(String filePath, HashMap<String, String> defaults, String comments) {
		this.configFile = new File(filePath);
		this.properties = new Properties();
		this.defaults = defaults;
		this.comments = comments;
		
		// Load configuration file
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
				properties.clear();
				properties.putAll(defaults);
				properties.store(new FileWriter(configFile), this.comments);
			} catch (IOException e) {
				handleException(e);
			}
		} else {
			try {
				properties.load(new FileReader(configFile));
				
				// Configuration file is missing some defaults, so add those
				if (!properties.keySet().containsAll(defaults.keySet())) {
					properties.putAll(defaults);
					save();
				}
			} catch (IOException e) {
				handleException(e);
			}
		}
	}
	
	/**
	 * Store a key-value pair in memory
	 * @param key
	 * @param value
	 */
	public synchronized void set(String key, String value) {
		if (value == null) {
			properties.remove(key);
			return;
		}
		
		properties.setProperty(key, value);
	}
	
	/**
	 * Store the current property values in memory to the storage medium (as a file)
	 */
	public synchronized void save() {
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
				properties.store(new FileWriter(configFile), comments);
			} catch (IOException e) {
				handleException(e);
			}
		} else {
			try {
				properties.load(new FileReader(configFile));
			} catch (IOException e) {
				handleException(e);
			}
		}
	}
	
	/**
	 * Get the the key-value as integer
	 * @param key
	 * @return
	 */
	public int getAsInt(String key) {
		int value = 0;
		try {
			if (properties.containsKey(key)) {
				value = Integer.parseInt(properties.getProperty(key));
			} else {
				value = Integer.parseInt(defaults.get(key));
			}
		} catch (NumberFormatException e) {
			handleException(e);
		}
		return value;
	}
	
	/**
	 * Get the key-value as a float
	 * @param key
	 * @return
	 */
	public float getAsFloat(String key) {
		float value = 0;
		try {
			if (properties.containsKey(key)) {
				value = Float.parseFloat(properties.getProperty(key));
			} else {
				value = Float.parseFloat(defaults.get(key));
			}
		} catch (NumberFormatException e) {
			handleException(e);
		}
		return value;
	}
	
	/**
	 * Get the key-value as a boolean
	 * @param key
	 * @return
	 */
	public boolean getAsBoolean(String key) {
		if (properties.containsKey(key)) {
			return Boolean.parseBoolean(properties.getProperty(key));
		}
		return Boolean.parseBoolean(defaults.get(key));
	}
	
	/**
	 * Get the key-value as a string
	 * @param key
	 * @return
	 */
	public String getAsString(String key) {
		if (properties.containsKey(key)) {
			return properties.getProperty(key);
		}
		return defaults.get(key);
	}
	
	private void handleException(Throwable t) {
		t.printStackTrace();
	}
	
}
