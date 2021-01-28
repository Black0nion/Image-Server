package com.github.black0nion.image_server;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import com.google.common.io.Files;

public class Config {
	
	private static JSONObject config;
	private static File configFile = new File("config.json");
	
	public static void reload() {
		try {
			configFile.createNewFile();
			config = new JSONObject(String.join("\n", Files.readLines(new File("config.json"), StandardCharsets.UTF_8)));
		} catch (Exception e) {
			try {
				Files.asCharSink(configFile, StandardCharsets.UTF_8).write(new JSONObject().toString());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			config = new JSONObject();
		}
	}
	
	public static String getOrDefault(String key, String defaultValue) {
		try {
			if (config.has(key))
				return config.getString(key);
		} catch (Exception ignored) {}
		save(key, defaultValue);
		return defaultValue;
	}
	
	public static int getOrDefault(String key, int defaultValue) {
		try {
			if (config.has(key))
				return config.getInt(key);
		} catch (Exception ignored) {}
		save(key, defaultValue);
		return defaultValue;
	}
	
	public static boolean getOrDefault(String key, boolean defaultValue) {
		try {
			if (config.has(key))
				return config.getBoolean(key);
		} catch (Exception ignored) {}
		save(key, defaultValue);
		return defaultValue;
	}
	
	public static void save(String key, Object value) {
		config.put(key, value);
		save();
	}
	
	public static void save() {
		try {
			Files.asCharSink(configFile, StandardCharsets.UTF_8).write(config.toString(2));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
