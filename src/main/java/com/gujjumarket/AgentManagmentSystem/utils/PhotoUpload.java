package com.gujjumarket.AgentManagmentSystem.utils;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public class PhotoUpload {
	public static String saveFile(String uploadDir, String fileName, MultipartFile multipartFile) {
		try {
			// Build the full path to save the file
			String filePath = uploadDir + "/" + fileName;

			// Save the file to the specified path
			multipartFile.transferTo(new File(filePath));

			// Return the file name for storage in the database
			return fileName;
		} catch (IOException e) {
			// Handle the exception (e.g., log it, throw a custom exception)
			throw new RuntimeException("Failed to save file: " + fileName, e);
		}
	}
}
