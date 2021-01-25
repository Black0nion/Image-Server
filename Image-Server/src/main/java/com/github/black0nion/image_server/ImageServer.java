package com.github.black0nion.image_server;

import spark.Spark;

public class ImageServer {
	
	private static final int PORT = 1011;
	
	public static void main(String[] args) {
		Spark.port(PORT);
		UploadImage.init();
		GetImage.init();
	}
}
