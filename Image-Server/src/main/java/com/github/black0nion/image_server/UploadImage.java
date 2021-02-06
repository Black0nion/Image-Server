package com.github.black0nion.image_server;

import static spark.Spark.post;
import static spark.Spark.get;

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
		get("upload", (request, response) -> {
			response.type("text/html;charset=utf-8");
			return "<html><head><title>405 Method not allowed</title></head><body><h2>405 Method not allowed</h2></body></html>";
		});
		
		post("upload", "multipart/form-data", (request, response) -> {
			if (ImageServer.AUTHENTICATION_ENABLED) {
				if (!Credentials.allowed(request.headers("token"))) {
					response.status(401);
					response.type("text/html;charset=utf-8");
					return "<html><head><title>401 Unauthorized</title></head><body><h2>400 Unauthorized</h2></body></html>";
				}
			}
			try {
				MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
					     ImageServer.IMAGES_FOLDER, ImageServer.MAX_FILE_SIZE, ImageServer.MAX_REQUEST_SIZE, ImageServer.FILE_SIZE_THRESHOLD);
					 request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
					     multipartConfigElement);

				Part uploadedFile = request.raw().getPart(ImageServer.KEY);
				String[] fileNameSplit = uploadedFile.getContentType().split("/");
				final String ending = fileNameSplit[fileNameSplit.length - 1];
				Path out = Paths.get(ImageServer.IMAGES_FOLDER + "/" + getRandomFileName(ImageServer.FILE_NAME_LENGTH, ending) + "." + ending);
				
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
			response.status(500);
			response.type("text/html;charset=utf-8");
			return "<html><head><title>500 Internal Server Error</title></head><body><h2>500 Internal Server Error</h2><p>Please report this to the admin of this site!</p></body></html>";
		});
		
		post("upload", (request, response) -> {
			response.status(400);
			response.type("text/html;charset=utf-8");
			return "<html><head><title>400 Bad Request</title></head><body><h2>400 Bad Request</h2></body></html>";
		});
	}
	
	public static String getRandomFileName(final int length, final String ending) {
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
	    
	    if (ImageServer.usedNames.contains(generatedString + ending))
	    	return getRandomFileName(targetStringLength, ending);
	    
	    return generatedString;
	}
}
