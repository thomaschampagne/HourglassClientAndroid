package com.thomaschampagne.hourglass.library.filemanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.util.Log;

public class Zip {
	/**
	 * Unzip a given file in the application internal storage
	 * 
	 * @param context
	 *            The context
	 * @param filePath
	 *            The zip file to unzip
	 * @return The file path list of the files which have been unziped
	 * @throws IOException
	 */
	public static List<String> unzip(Context context, String filePath, String pathDirectoryLocation)
			throws IOException {

		// build the file path and get the streams
		Log.d("UNZIP", "Now unzipping the file " + filePath);

		String path = filePath;
		InputStream fin = new FileInputStream(new File(path));
		ZipInputStream zin = new ZipInputStream(fin);
		ZipEntry ze = null;


		List<String> filePathList = new ArrayList<String>();

		while ((ze = zin.getNextEntry()) != null) {
			if (ze.isDirectory()) {
				// check if the directory exists, if it doesn't, creates it
				dirChecker(pathDirectoryLocation + ze.getName());
			} else {
				// First creating folders of files if do not exist.
				String storageFilePathName = pathDirectoryLocation + ze.getName();
				File ouputFolder = (new File(storageFilePathName))
						.getParentFile();
				ouputFolder.mkdirs();

				FileOutputStream fout = new FileOutputStream(
						storageFilePathName);

				int sizeToRead = 1024;
				byte b[] = new byte[sizeToRead];
				int n;
				
				while ((n = zin.read(b, 0, sizeToRead)) != -1) {
					fout.write(b, 0, n);
				}
				
				fout.close();
				fout.flush();
				
				zin.closeEntry();

				// add the unziped file path to the list
				filePathList.add(pathDirectoryLocation + ze.getName());
			}

		}
		zin.close();
		
		return filePathList;
	}

	/**
	 * Check if a directory exists for a given filepath, if it doesn't exists it
	 * is created
	 * 
	 * @param filePath
	 *            The file path to check
	 */
	private static void dirChecker(String filePath) {
		File f = new File(filePath);

		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}
}
