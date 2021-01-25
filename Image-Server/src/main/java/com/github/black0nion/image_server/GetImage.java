package com.github.black0nion.image_server;

import static spark.Spark.get;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;

public class GetImage {
	
	/**
	 * Handles all <b>get a picture</b> requests
	 */
	public static void init() {
		get("/image/:key", (request, response) -> {
			try {
	            File file = new File("images/" + request.params("key"));
	            if (!file.exists()) {
	            	response.status(404);
	            	return "404 NOT FOUND";
	            }
	            OutputStream outputStream = response.raw().getOutputStream();
	            outputStream.write(Files.readAllBytes(file.toPath()));
	            outputStream.flush();
	            return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});
	}
}
