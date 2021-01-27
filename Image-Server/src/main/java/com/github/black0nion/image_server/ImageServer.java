package com.github.black0nion.image_server;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import spark.Filter;
import spark.Request;
import spark.Response;
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
	
	private static final File IMAGES_FOLDER_FILE = new File(IMAGES_FOLDER);
	
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
	
	/**
	 * The default size of the sizetop command
	 */
	private static final int DEFAULT_SIZE = 10;
	
	public static void main(String[] ignoredArgs) {
		IMAGES_FOLDER_FILE.mkdirs();
		usedNames.addAll(Arrays.asList(IMAGES_FOLDER_FILE.list()));
		Credentials.refresh();
		Spark.port(PORT);
		UploadImage.init();
		GetImage.init();
		
		Spark.before(new Filter() {
			@Override
			public void handle(Request request, Response response) throws Exception {
				System.out.println("New Request from IP " + (request.headers("X-Real-IP") != null ? request.headers("X-Real-IP") : request.ip()) + " to URL " + request.pathInfo() + (AUTHENTICATION_ENABLED ? (request.headers("token") != null ? " with token " + request.headers("token") + " (User " + Credentials.getUserName(request.headers("token")) + ")" : " with no token!") : ""));
			}
		});
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		
		while (true) {
			sc.hasNext();
			String line = sc.nextLine();
			String[] args = line.split(" ");
			if (args[0].equalsIgnoreCase("size")) {
				System.out.println(getSize(false));
			} else if (args[0].equalsIgnoreCase("list")) {
				System.out.println(IMAGES_FOLDER_FILE.list().length != 0 ? String.join("\n", IMAGES_FOLDER_FILE.list()) : "No pictures.");
			} else if (args[0].equalsIgnoreCase("stats")) {
				System.out.println("Files uploaded: " + IMAGES_FOLDER_FILE.list().length);
				System.out.println(getSize(false));
				HashMap<String, Integer> stats = new HashMap<>();
				for (File f : IMAGES_FOLDER_FILE.listFiles()) {
					String ending = f.getName().split("\\.")[1];
					stats.put(ending, stats.containsKey(ending) ? stats.get(ending) + 1 : 1);
				}
				stats.forEach((key, value) -> {
					System.out.println(key + ": " + value);
				});
			} else if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete")) {
				if (args.length < 2)
					System.out.println("No argument given!");
				else {
					File fileToDelete = new File(IMAGES_FOLDER + "/" + args[1]);
					if (fileToDelete.exists()) {
						System.out.println("File " + fileToDelete.getName() + " deleted.");
						fileToDelete.delete();
					} else {
						System.out.println("File " + fileToDelete.getName() + " doesn't exist!");
					}
				}
			} else if (args[0].equalsIgnoreCase("sizetop")) {
				try {
					System.out.println(getSize(false));
					List<Path> paths = Files.walk(Paths.get(IMAGES_FOLDER_FILE.toURI()))
							  .filter(Files::isRegularFile)
							  .sorted((Path a, Path b) -> a.toFile().length() < b.toFile().length() ? 1 : -1)
							  .collect(Collectors.toList());
					int length = DEFAULT_SIZE;
					try { length = Integer.parseInt(args[1]); } catch (Exception ignored) {}
					
					for (int i = 1; i <= (length > paths.size() ? paths.size() : length); i++) {
						System.out.println(paths.get(i - 1).getFileName() + ": " + getSizeString(paths.get(i - 1).toFile().length(), false));
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Something went wrong!");
				}
			}
		}
	}
	
	private static String getSize(boolean nextLine) {
		long bytes = FileUtils.sizeOfDirectory(IMAGES_FOLDER_FILE);
		String output = "";
		output += "Total size of directory " + IMAGES_FOLDER_FILE.getAbsolutePath() + ":" + (nextLine ? "\n" : " ");
		output += getSizeString(bytes, nextLine);
		return output;
	}
	
	public static String getSizeString(long bytes, boolean lineBreak) {
		return (bytes / 1000000 >= 1000 ? bytes / 1000000000 + " GB" + (lineBreak ? "\n" : " = ") : "") + (bytes / 1000 >= 1000 ? bytes / 1000000 + " MB" + (lineBreak ? "\n" : " = ") : "") + (bytes / 1000 > 0 ? bytes / 1000 + " KB" : bytes + " B");
	}
}
