package com.thomaschampagne.hourglass.library.filemanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.thomaschampagne.hourglass.library.exceptions.SyncFileException;

public class FileCommons {

	public static List<String> unzip(Context context, String outFolderLocation,
			File outputFile) throws Exception {

		try {
			// Let's unzip the file
			List<String> list = Zip.unzip(context, outputFile.getPath(),
					outFolderLocation);

			for (String unzippedFilePath : list) {

				File file = new File(unzippedFilePath);

				if (!file.exists()) {
					
					String message = "File " + file.getPath() + " do not exist";
					Log.e("UnZip", message);
					
					throw new SyncFileException(message, null);
				} 
			}
			return list;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static List<String> eraseFiles(Context context,
			List<String> filePathsToDelete, String pathDirectoryLocation)
			throws SyncFileException {

		List<String> deletedFilesList = new ArrayList<String>();

		if (filePathsToDelete.size() > 0) {

			for (String filepath : filePathsToDelete) {
				String fullpath = pathDirectoryLocation + filepath;
				File file = new File(fullpath);
				if (file.exists()) {
					if (file.delete()) {
						deletedFilesList.add(filepath);
					} else {
						throw new SyncFileException("Unable to delete file at "	+ fullpath, null);
					}
				}

			}
		}
		return deletedFilesList;
	}

	public static Boolean eraseFolder(String folderPath)
			throws SyncFileException {
		File folder = new File(folderPath);

		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					eraseFolder(f.getPath());
				} else {
					f.delete();
				}
			}
		}
		return folder.delete();
	}

	public static List<File> ListDir(String path) {
		Filewalker fileWalker = new Filewalker();
		fileWalker.walk(path);
		return fileWalker.getFileList();
	}

	public static Boolean eraseFile(Context mContext, File file) throws SyncFileException {
		if (file.exists()) {
			if (!file.delete()) {
				throw new SyncFileException("Unable to delete file at "	+ file.toString(), null);
			}
		}
		return true;
	}

	public static String generateBufferedHash(File file) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		InputStream is = new FileInputStream(file);
		
		byte[] buffer = new byte[8192];
		int read = 0;
		
		while ((read = is.read(buffer)) > 0)
			md.update(buffer, 0, read);
		
		byte[] md5 = md.digest();
		BigInteger bi = new BigInteger(1, md5);
		
		is.close();
		
		String result = bi.toString(16);
		
		if(result.length() == 32) {
			return result;
		} else {
			int nbOfZerosToAdd = 32 - result.length();
			for(int i=0; i< nbOfZerosToAdd; i++) {
				result = "0".concat(result);
			}
			return result;
		}
		
		//return String.format("%0"+(32-bi.toString(16).length())+"d%s", 0, bi.toString(16));
		//return bi.toString(16);
	}
	
	public static String md5(String s) 
	{
	    MessageDigest digest;
	    try 
	    {
	        digest = MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes(),0,s.length());
	        String hash = new BigInteger(1, digest.digest()).toString(16);
	        return hash;
	    } 
	    catch (NoSuchAlgorithmException e) 
	    {
	        e.printStackTrace();
	    }
	    return null;
	}

}
