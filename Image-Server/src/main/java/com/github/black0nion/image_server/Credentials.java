package com.github.black0nion.image_server;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.google.common.io.Files;

public class Credentials {
	public static HashMap<String, String> keys = new HashMap<>();
	
	public static void refresh() {
		if (!ImageServer.AUTHENTICATION_ENABLED)
			return;
		try {
			ImageServer.CREDENTIALS_FILE.createNewFile();
			JSONObject credentials = new JSONObject(String.join(" ", Files.readLines(ImageServer.CREDENTIALS_FILE, StandardCharsets.UTF_8)));
			
			credentials.keySet().stream().filter(key -> credentials.has(key)).forEach(key -> keys.put(key, credentials.getString(key)));
		} catch (Exception e) {
			try {
				Files.asCharSink(ImageServer.CREDENTIALS_FILE, StandardCharsets.UTF_8).write(new JSONObject().toString());
			} catch (Exception ex) { ex.printStackTrace(); }
			e.printStackTrace();
			System.err.println("Couldn't parse credentials!");
			System.exit(0);
		}
	}
	
	public static boolean allowed(String key) {
		return keys.containsValue(key);
	}
	
	public static String getUserName(String key) {
		if (!allowed(key))
			return "isn't a user!";
		for (Map.Entry<String, String> entry : keys.entrySet()) {
			if (entry.getValue().equals(key))
				return entry.getKey();
		}
		return "NONE";
	}
}
