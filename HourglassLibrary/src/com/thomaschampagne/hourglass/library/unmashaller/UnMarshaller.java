package com.thomaschampagne.hourglass.library.unmashaller;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.thomaschampagne.hourglass.library.exceptions.SyncException;
import com.thomaschampagne.hourglass.library.exceptions.SyncWSException;
import com.thomaschampagne.hourglass.library.model.Archive;
import com.thomaschampagne.hourglass.library.model.SyncResponse;

public class UnMarshaller {

	public static SyncResponse versionnedFilesJsonResponseToSyncResponse(
			Context context, String jsonResponse) throws SyncException,
			JSONException {

		SyncResponse syncResponse = null;

		Log.d("Hourglass WebService Response", jsonResponse);
		
		// Retrieve response and error JSONObjects
		JSONObject jsonObj = new JSONObject(jsonResponse);
		JSONArray names = new JSONArray();
		names.put(UnMarshallerConstants.Response);
		names.put(UnMarshallerConstants.Error);
		JSONArray globalResponse = jsonObj.toJSONArray(names);

		// GlobalResponse now contains response + error
		// Checking for errors
		if (!globalResponse.getString(1).equals("null")) {
			JSONObject errorJsonObj = globalResponse.getJSONObject(1);
			Integer errorCode = (Integer) errorJsonObj
					.get(UnMarshallerConstants.ErrorCode);
			String errorMessage = (String) errorJsonObj
					.get(UnMarshallerConstants.ErrorMessage);
			throw new SyncWSException(errorCode + " : " + errorMessage, null);
		}

		// No web service error from here.
		// Now getting objects inside response zone.
		JSONObject responseJsonObj = globalResponse.getJSONObject(0);
		Integer latestServerRevision = (Integer) responseJsonObj
				.get(UnMarshallerConstants.LatestRevision);
		Integer latestRevisionDate = (Integer) responseJsonObj
				.get(UnMarshallerConstants.LatestRevisionDate);

		// Files to delete. Let's compute a filesToDelete java List
		JSONArray filesToDeleteJsonArray = responseJsonObj
				.getJSONArray(UnMarshallerConstants.FilesToDelete);
		ArrayList<String> filesToDeleteList = new ArrayList<String>();
		for (int i = 0; i < filesToDeleteJsonArray.length(); i++) {
			String fileToDeletePath = (String) filesToDeleteJsonArray.get(i);
			filesToDeleteList.add(fileToDeletePath);
		}

		// Handle archive
		Archive archive = null;
		Object archiveProbablyNull = responseJsonObj
				.get(UnMarshallerConstants.Archive);

		if (!archiveProbablyNull.equals(null)) {

			JSONObject archiveJsonObj = responseJsonObj
					.getJSONObject(UnMarshallerConstants.Archive);

			Integer archiveFileCount = (Integer) archiveJsonObj
					.get(UnMarshallerConstants.ArchiveFilesCount);

			Integer archiveFileSizeBytes = (Integer) archiveJsonObj
					.get(UnMarshallerConstants.ArchiveFileSizeBytes);

			String archiveMd5FingerPrint = (String) archiveJsonObj
					.get(UnMarshallerConstants.ArchiveMd5FingerPrint);

			Object archiveBinaryLinkAsObject = archiveJsonObj
					.get(UnMarshallerConstants.ArchiveBinaryLink);

			Boolean archiveFromCache = (Boolean) archiveJsonObj
					.get(UnMarshallerConstants.ArchiveFromCache);

			String archiveBinaryLinkAsString = "";

			if (!archiveBinaryLinkAsObject.equals(null)) {
				archiveBinaryLinkAsString = (String) archiveBinaryLinkAsObject;
			}

			archive = new Archive();
			archive.setArchiveFilesCount(archiveFileCount);
			archive.setArchiveFileSizeBytes(archiveFileSizeBytes);
			archive.setArchiveMd5FingerPrint(archiveMd5FingerPrint);
			archive.setArchiveBinaryLink(archiveBinaryLinkAsString);
			archive.setArchiveFromCache(archiveFromCache);

		}

		syncResponse = new SyncResponse(latestServerRevision,
				latestRevisionDate, filesToDeleteList, archive);

		return syncResponse;

	}

	public static Integer serverSimpleJsonResponseToInteger(Context context,
			String jsonResponse) throws Exception {

		// Retrieve response and error JSONObjects
		JSONObject jsonObj = new JSONObject(jsonResponse);
		if (!jsonObj.getString(UnMarshallerConstants.Error).equals("null")) {
			JSONObject errorJsonObj = jsonObj
					.getJSONObject(UnMarshallerConstants.Error);
			Integer errorCode = (Integer) errorJsonObj
					.get(UnMarshallerConstants.ErrorCode);
			String errorMessage = (String) errorJsonObj
					.get(UnMarshallerConstants.ErrorMessage);
			throw new SyncWSException(errorCode + " : " + errorMessage, null);
		}

		return jsonObj.getInt(UnMarshallerConstants.Response);

	}
}
