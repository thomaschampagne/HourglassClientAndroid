package com.thomaschampagne.hourglass.library.filemanagement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Filewalker {

	List<File> fileList;

    public Filewalker() {
    	fileList = new ArrayList<File>();
	}

	public void walk( String path ) {
    	
        File root = new File( path );
        File[] list = root.listFiles();

        if (list != null) {
	        for ( File f : list ) {
	            if ( f.isDirectory() ) {
	                walk( f.getAbsolutePath() );
	            } else {
	            	fileList.add(f);
	            }
	        }
        }
    }

	public List<File> getFileList() {
		return fileList;
	}

}