package com.thomaschampagne.hourglass.library.model;

import java.io.Serializable;
import java.util.Date;

import android.content.Context;
import android.os.Environment;

import com.thomaschampagne.hourglass.library.enums.SyncStorageMode;
import com.thomaschampagne.hourglass.library.filemanagement.FileCommons;

public class SyncConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String serverUrl;
	private String filterRegexInclude = "";
	private String filterRegexExclude = "";
	private SyncStorageMode syncStorageMode;
	private String storageSubfolder;
	private Boolean syncOnTestResources = false;
	private String downloadingMessage= "Downloading...";
	private String cancelMessage = "Cancel";
	private Boolean isCancelable = true;
	private String publishingUpdateMessage = "Publishing...";
	
	private int connectionTimeoutMillisOnRemoteRevisionCheck = 1500; // default 1.5 seconds
	private int connectionTimeoutMillisOnUpdateFiles = 45000;  // default 45 seconds
	
	public SyncConfig(String serverUrl, SyncStorageMode syncStorageMode,
			String storageSubfolder) {
		this.serverUrl = serverUrl;
		this.syncStorageMode = syncStorageMode;
		setStorageSubfolder(storageSubfolder);
	}

	public String getStoragePath(Context context) {

		String path = null;
		String realStorageSubfolder = null;
		
		if (storageSubfolder != null && !storageSubfolder.equals("")) {
			realStorageSubfolder = storageSubfolder + "/";
		}

		if (syncStorageMode == SyncStorageMode.INTERNAL) {
			path = context.getFilesDir().getAbsolutePath() + "/" + realStorageSubfolder;
		} else if (syncStorageMode == SyncStorageMode.SDCARD) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + realStorageSubfolder;
		}

		return path;
	}

	
	public String getInternalStoragePath(Context context) {

		String realStorageSubfolder = null;
		
		if (storageSubfolder != null && !storageSubfolder.equals("")) {
			realStorageSubfolder = storageSubfolder + "/";
		}
		return context.getFilesDir().getAbsolutePath() + "/" + realStorageSubfolder;
	}
	
	public String getSdCardStoragePath() {

		String realStorageSubfolder = null;
		
		if (storageSubfolder != null && !storageSubfolder.equals("")) {
			realStorageSubfolder = storageSubfolder + "/";
		}
		
		return Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + realStorageSubfolder;
	}
	
	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getFilterRegexInclude() {
		return filterRegexInclude;
	}

	public void setFilterRegexInclude(String filterRegexInclude) {
		this.filterRegexInclude = filterRegexInclude;
	}

	public String getFilterRegexExclude() {
		return filterRegexExclude;
	}

	public void setFilterRegexExclude(String filterRegexExclude) {
		this.filterRegexExclude = filterRegexExclude;
	}

	public SyncStorageMode getSyncStorageMode() {
		return syncStorageMode;
	}

	public void setSyncStorageMode(SyncStorageMode syncStorageMode) {
		this.syncStorageMode = syncStorageMode;
	}

	public Boolean getSyncOnTestResources() {
		return syncOnTestResources;
	}

	public void setSyncOnTestResources(Boolean syncOnTestResources) {
		this.syncOnTestResources = syncOnTestResources;
	}

	public String getStorageSubfolder() {
		return storageSubfolder;
	}

	public void setStorageSubfolder(String storageSubfolder) {
		
		if(storageSubfolder == null || storageSubfolder.equals("")) {
			Date currentDate = new Date(System.currentTimeMillis());
			storageSubfolder =  "syncmod_" + FileCommons.md5(currentDate.toString());
		}
		
		this.storageSubfolder = storageSubfolder;
	}

	public String getDownloadingMessage() {
		return downloadingMessage;
	}

	public void setDownloadingMessage(String downloadingMessage) {
		this.downloadingMessage = downloadingMessage;
	}

	public String getCancelMessage() {
		return cancelMessage;
	}

	public void setCancelMessage(String cancelMessage) {
		this.cancelMessage = cancelMessage;
	}

	public String getPublishingUpdateMessage() {
		return publishingUpdateMessage;
	}

	public void setPublishingUpdateMessage(String publishingUpdateMessage) {
		this.publishingUpdateMessage = publishingUpdateMessage;
	}

	public int getConnectionTimeoutMillisOnRemoteRevisionCheck() {
		return connectionTimeoutMillisOnRemoteRevisionCheck;
	}

	public void setConnectionTimeoutMillisOnRemoteRevisionCheck(
			int connectionTimeoutMillisOnRemoteRevisionCheck) {
		this.connectionTimeoutMillisOnRemoteRevisionCheck = connectionTimeoutMillisOnRemoteRevisionCheck;
	}

	public int getConnectionTimeoutMillisOnUpdateFiles() {
		return connectionTimeoutMillisOnUpdateFiles;
	}

	public void setConnectionTimeoutMillisOnUpdateFiles(
			int connectionTimeoutMillisOnUpdateFiles) {
		this.connectionTimeoutMillisOnUpdateFiles = connectionTimeoutMillisOnUpdateFiles;
	}

	public Boolean getIsCancelable() {
		return isCancelable;
	}

	public void setIsCancelable(Boolean isCancelable) {
		this.isCancelable = isCancelable;
	}

	@Override
	public String toString() {
		return "SyncConfig [serverUrl=" + serverUrl + ", filterRegexInclude="
				+ filterRegexInclude + ", filterRegexExclude="
				+ filterRegexExclude + ", syncStorageMode=" + syncStorageMode
				+ ", storageSubfolder=" + storageSubfolder
				+ ", syncOnTestResources=" + syncOnTestResources
				+ ", downloadingMessage=" + downloadingMessage
				+ ", cancelMessage=" + cancelMessage
				+ ", publishingUpdateMessage=" + publishingUpdateMessage
				+ ", connectionTimeoutMillisOnRemoteRevisionCheck="
				+ connectionTimeoutMillisOnRemoteRevisionCheck
				+ ", connectionTimeoutMillisOnUpdateFiles="
				+ connectionTimeoutMillisOnUpdateFiles + "]";
	}

}
