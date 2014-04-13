package com.thomaschampagne.hourglass.library.asynctasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.thomaschampagne.hourglass.library.SyncServices;
import com.thomaschampagne.hourglass.library.callback.ISynchronizationListener;
import com.thomaschampagne.hourglass.library.exceptions.SyncException;
import com.thomaschampagne.hourglass.library.filemanagement.FileCommons;
import com.thomaschampagne.hourglass.library.model.SyncConfig;

public class BeginSynchronizationASyncTask extends AsyncTask<Void, Integer, Integer[]> {

	Activity mActivity;
	Integer mCurrentClientVersion;
	SyncConfig mSyncConfig;
	ISynchronizationListener mSyncHandler;
	SyncException mUpdateFromRevisionException;

	public BeginSynchronizationASyncTask(Activity activity,
			Integer currentClientVersion, SyncConfig syncConfig,
			ISynchronizationListener syncHandler) {
		mUpdateFromRevisionException = null;
		this.mActivity = activity;
		this.mCurrentClientVersion = currentClientVersion;
		this.mSyncConfig = syncConfig;
		this.mSyncHandler = syncHandler;
	}

	protected void onProgressUpdate(Integer... progress) {
		//... do nothing at this time :)
	}

	protected void onPostExecute(Integer[] serverLastRevisionArrayResponse) {

		Integer serverLastRevisionNumber = serverLastRevisionArrayResponse[0];
		Integer serverLastRevisionDate = serverLastRevisionArrayResponse[1];
		
		Log.d("Sync", "ServerSide revision number is: " + serverLastRevisionNumber);

		try {

			// Client revision is away or set to ZERO.
			// Then a server checkout is requested
			if (mCurrentClientVersion == null || mCurrentClientVersion <= 0) {
				
				cleanAndCheckout(); 

			} 
			
			// Client revision number exist and not null/zero
			else { 
				
				updateFromRevision(serverLastRevisionNumber, serverLastRevisionDate);
			}
			

		} catch (SyncException e) {
			
			mSyncHandler.onSynchronizationFailed(e);
			e.printStackTrace();
			
		} catch (Exception e) {
			mSyncHandler.onSynchronizationFailed(new SyncException(e.getMessage(), e.getCause()));			
			e.printStackTrace();
		}
	}

	private void cleanAndCheckout() throws Exception {
		
		// Clean configured storage
		FileCommons.eraseFolder(mSyncConfig.getStoragePath(mActivity));
		
		// Force direct Checkout
		SyncServices.checkout(mActivity, mSyncHandler, mSyncConfig);
	}
	
	private void updateFromRevision(Integer serverLastRevisionNumber, Integer serverLastRevisionDate)
			throws Exception {
		
		/**
		 * Comparing server revision with client
		 */
		if (mCurrentClientVersion < serverLastRevisionNumber) {

			// Client is not up to date.
			// Update from currentClientVersion
			SyncServices.updateFromRevision(mActivity, mSyncHandler, mCurrentClientVersion, mSyncConfig);

		} else if (mCurrentClientVersion.equals(serverLastRevisionNumber)) {
			
			// Notify user sync is finished
			mSyncHandler.onSynchronizationFinished(serverLastRevisionNumber,  serverLastRevisionDate);
			
		} else { 
			
			// client > server revision.
			// It's a problem.. make things clean from now...
			cleanAndCheckout();
		}
	}

	@Override
	protected void onCancelled() {
		
		// Canceled by errors?, checking that
		if(mUpdateFromRevisionException != null) {
			mSyncHandler.onSynchronizationFailed(mUpdateFromRevisionException);
		}
		mSyncHandler.onSynchronizationCancel();
	}

	@Override
	protected Integer[] doInBackground(Void... params) {

		try {
			// First getting the server side revision
			// This is pretty simple and fast web call
			// This let's us know fast if remote service is up
			Integer serverSideLatestRevision = SyncServices.getServerRevNumberCall(mActivity, mSyncConfig);
			Integer serverSideLatestRevisionDate = SyncServices.getServerRevNumberDateCall(mActivity, mSyncConfig);
			
			if(serverSideLatestRevision == null || serverSideLatestRevisionDate == null) {
				throw new SyncException("Server latest revision or lastest revision date cannot be null", null);
			}
			
			return new Integer[] {serverSideLatestRevision, serverSideLatestRevisionDate};
			
		} catch (Exception e) {
			mUpdateFromRevisionException = new SyncException(e.getMessage(), e.getCause());
			e.printStackTrace();
			cancel(true);
		}

		return null;
	}
}