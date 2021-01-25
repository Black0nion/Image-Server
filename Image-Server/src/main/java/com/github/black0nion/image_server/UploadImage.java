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
	
	/**
	 * Handles all <b>upload</b> requests
	 */
	public static void init() {
		post("upload", "multipart/form-data", (request, response) -> {
			try {
				MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
					     ImageServer.IMAGES_FOLDER, ImageServer.MAX_FILE_SIZE, ImageServer.MAX_REQUEST_SIZE, ImageServer.FILE_SIZE_THRESHOLD);
					 request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
					     multipartConfigElement);

				Part uploadedFile = request.raw().getPart(ImageServer.KEY);
				Path out = Paths.get(ImageServer.IMAGES_FOLDER + "/" + getRandomFileName(ImageServer.FILE_NAME_LENGTH) + "." + uploadedFile.getContentType().split("/")[1]);
				
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
				return ImageServer.PROTOCOL  + "://" + ImageServer.LOCAL_IP + ":" + ImageServer.PORT + "/" + ImageServer.IMAGES_FOLDER + "/" + out.toFile().getName();
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.status(400);
			return "";
		});
	}
	
	public static String getRandomFileName(final int length) {
		int leftLimit1 = 65; // letter 'A'
		int rightLimit1 = 90; // letter 'B'
		int leftLimit2 = 97; // letter 'a'
	    int rightLimit2 = 122; // letter 'z'
	    
	    int targetStringLength = length;
	    if (random == null)
	    	random = new Random();

	    String generatedString = random.ints(leftLimit1, rightLimit2 + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .filter(i -> (i <= rightLimit1 || i >= leftLimit2))
	      .limit(targetStringLength + 2)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();

	    return generatedString;
	}
}
