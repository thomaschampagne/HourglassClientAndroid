package com.thomaschampagne.hourglass.library.asynctasks;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.thomaschampagne.hourglass.library.callback.ISynchronizationListener;
import com.thomaschampagne.hourglass.library.filemanagement.FileCommons;
import com.thomaschampagne.hourglass.library.model.SyncConfig;
import com.thomaschampagne.hourglass.library.model.SyncResponse;

/**
 * 
 * @author Thomas Champagne 
 * Async task for UnZip files and update versionning
 * 
 */
public class PublishSyncAsyncTask extends AsyncTask<File, Integer, String> {

	Context mContext;
	ProgressDialog mProgressDialog;
	SyncConfig mSyncConfig;
	SyncResponse mSyncResponse;
	ISynchronizationListener mSyncHandler;

	public PublishSyncAsyncTask(Context pContext, ISynchronizationListener syncHandler, SyncConfig syncConfig) {
		mContext = pContext;
		mSyncResponse = null;
		mSyncHandler = syncHandler;
		mSyncConfig = syncConfig;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mSyncConfig.getPublishingUpdateMessage());
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();
	}

	protected String doInBackground(File... outputFile) {

		try {
			Thread.sleep(1000); // Wait 1 second to see message in case of small file to publish :)
			
			if (mSyncResponse.getArchive() != null) {
				FileCommons.unzip(mContext, mSyncConfig.getStoragePath(mContext), outputFile[0]);
				FileCommons.eraseFile(mContext, outputFile[0]); // delete tmp zip
			}
			
		} catch (Exception e) {
			this.cancel(true);
			Log.d("SyncError", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		mProgressDialog.setProgress(values[0]);
	}

	@Override
	protected void onPostExecute(String jsonOutString) {
		mProgressDialog.dismiss();
		mSyncHandler.onSynchronizationFinished(mSyncResponse.getLatestRevision(), mSyncResponse.getLatestRevisionDate());
	}

	public SyncResponse getSyncResponse() {
		return mSyncResponse;
	}

	public void setSyncResponse(SyncResponse mSyncResponse) {
		this.mSyncResponse = mSyncResponse;
	}
}