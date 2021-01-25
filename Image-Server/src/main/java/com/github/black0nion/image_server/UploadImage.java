package com.github.black0nion.image_server;

import static spark.Spark.post;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

public class UploadImage {
	
	private static Random random;
	
	public static void init() {
		post("upload", "multipart/form-data", (request, response) -> {
			String location = "images";          // the directory location where files will be stored
			long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
			long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
			int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk

			try {
				MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
					     location, maxFileSize, maxRequestSize, fileSizeThreshold);
					 request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
					     multipartConfigElement);

				Part uploadedFile = request.raw().getPart("image");
				Path out = Paths.get("images/" + getRandomFileName() + "." + uploadedFile.getContentType().split("/")[1]);
				
				// buffer
				byte[] buffer = new byte[uploadedFile.getInputStream().available()];
				// read file

				uploadedFile.getInputStream().read(buffer);
				// make a file for the target
			    File targetFile = out.toFile();
			    targetFile.getParentFile().mkdirs();
			    // create the out stream
			    OutputStream outStream = new FileOutputStream(targetFile);
			    // write the buffer
			    outStream.write(buffer);
			    
				// cleanup
				multipartConfigElement = null;
				uploadedFile = null;
				outStream.close();
				return "http://localhost:1011/image/" + out.toFile().getName();
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.status(400);
			return "";
		});
	}
	
	public static String getRandomFileName() {
		int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    
	    int targetStringLength = 20;
	    if (random == null)
	    	random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(targetStringLength + 2)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();

	    return generatedString;
	}
}
