package com.github.black0nion.image_server;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import spark.Spark;

public class ImageServer {
	
	/**
	 * The Port the server should listen to
	 */
	public static final int PORT = 1011;
	
	/**
	 * The folder the images should be saved in
	 */
	public static final String IMAGES_FOLDER = "images";
	/**
	 * The length of the random file name (the more pictures you want to save, the longer)
	 */
	public static final int FILE_NAME_LENGTH = 20;
	
	/**
	 * The Key the image should be saved as a file in the body (content-type: <b>multipart/form-data</b>)
	 */
	public static final String KEY = "image";
	
	/**
	 * Used only for returning the link, set it to your public IP / your domain
	 * @see UploadImage
	 */
	public static final String PROTOCOL = "http";
	public static final String LOCAL_IP = "localhost";
	
	/**
	 * The maximum size allowed for uploaded files
	 */
	public static final long MAX_FILE_SIZE = 100000000;
	
	/**
	 * The maximum size allowed for multipart/form-data requests
	 */
	public static final long MAX_REQUEST_SIZE = 100000000;
	
	/**
	 * The size threshold after which files will be written to disk
	 */
	public static final int FILE_SIZE_THRESHOLD = 1024;
	
	/**
	 * Used to store all names which are already in use
	 */
	public static final ArrayList<String> usedNames = new ArrayList<>();

	/**
	 * The File the Credentials of the Users are configured in
	 */
	public static final File CREDENTIALS_FILE = new File("credentials.json");
	
	/**
	 * Stores whether the users have to authenticate with credentials or not
	 */
	public static final boolean AUTHENTICATION_ENABLED = true;
	
	public static void main(String[] args) {
		usedNames.addAll(Arrays.asList(new File(IMAGES_FOLDER).list()));
		Credentials.refresh();
		Spark.port(PORT);
		UploadImage.init();
		GetImage.init();
	}
}
