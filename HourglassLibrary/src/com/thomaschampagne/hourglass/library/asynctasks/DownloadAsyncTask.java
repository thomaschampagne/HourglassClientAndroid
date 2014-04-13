package com.thomaschampagne.hourglass.library.asynctasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.thomaschampagne.hourglass.library.SyncServices;
import com.thomaschampagne.hourglass.library.callback.ISynchronizationListener;
import com.thomaschampagne.hourglass.library.exceptions.SyncException;
import com.thomaschampagne.hourglass.library.exceptions.SyncFileException;
import com.thomaschampagne.hourglass.library.filemanagement.FileCommons;
import com.thomaschampagne.hourglass.library.model.SyncConfig;
import com.thomaschampagne.hourglass.library.model.SyncResponse;
import com.thomaschampagne.hourglass.library.unmashaller.UnMarshaller;


public class DownloadAsyncTask extends AsyncTask<String, Integer, File> implements OnClickListener {
	
	Activity mActivity;
	ProgressDialog mProgressDialog;
	SyncConfig mSyncConfig;
	SyncResponse mSyncResponse;
	ISynchronizationListener mSyncHandler;
	SyncException mDownloadException;

	public DownloadAsyncTask(Activity activity, ISynchronizationListener syncHandler, SyncConfig syncConfig) {
		
		mActivity = activity;
		mSyncHandler = syncHandler;
		mSyncConfig = syncConfig;
		mDownloadException = null;
		mSyncResponse = null;
		mProgressDialog = new ProgressDialog(mActivity);
		mProgressDialog.setMessage(mSyncConfig.getDownloadingMessage());
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(100);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(mSyncConfig.getIsCancelable());
		if(mSyncConfig.getIsCancelable())
			mProgressDialog.setButton(AlertDialog.BUTTON_NEGATIVE, mSyncConfig.getCancelMessage(), this);
		mProgressDialog.show();
		
		if(Build.VERSION.SDK_INT>10)
			mProgressDialog.setProgressNumberFormat(null);
	}
	
	/**
	 * 
	 * Retrieve json output through streaming: get responseSize on the fly and read response through streaming
	 * This allow to avoid 2 queries with simulateArchive true and false.
	 * @return File the zip archive
	 */
    protected File doInBackground(String... url) {
    	
    	try {
    		
			// Getting zip link from webservice function of client/server revision number.
    		String jsonResponse = SyncServices.webServiceCall(url[0], mSyncConfig.getConnectionTimeoutMillisOnUpdateFiles()); 
  
			mSyncResponse = UnMarshaller.versionnedFilesJsonResponseToSyncResponse(mActivity, jsonResponse);
			
			// First deleting files
			List<String> filesToDel = mSyncResponse.getFilesToDelete();
			String storagePath = mSyncConfig.getStoragePath(mActivity); 
			
			if(filesToDel != null && filesToDel.size() > 0) {
				FileCommons.eraseFiles(mActivity, filesToDel, storagePath);
			}	
			
			// Now downloading the zip file
			if(mSyncResponse.getArchive() != null) {
			
				URL aUrl = new URL(mSyncResponse.getArchive().getArchiveBinaryLink());
				
				Log.d("Sync Download", "Downloading archive from " + aUrl.toString());
				
	            URLConnection connexion = aUrl.openConnection();
	            connexion.connect();
	            int lenghtOfFile = connexion.getContentLength();
	            
	            // Update progress dialog
	            String dialogSize = ((mSyncResponse.getArchive().getArchiveFileSizeBytes() / 1024)) + " KB";
	            
				if(Build.VERSION.SDK_INT>10)
					mProgressDialog.setProgressNumberFormat(dialogSize);
	            
	            InputStream input = new BufferedInputStream(aUrl.openStream());
	
	            // Getting path of file
	    		String unZipFilePathDirectory = mSyncConfig.getStoragePath(mActivity);
	    		String filename = "tmp.zip";
	    		
				// create a File object for the parent directory
				File localSyncDirectory = new File(unZipFilePathDirectory);
				
				// have the object build the directory structure, if needed.
				localSyncDirectory.mkdirs();
	            
				// create a File object for the output file
				File outputFile = new File(unZipFilePathDirectory, filename);
	            OutputStream output = new FileOutputStream(outputFile);
	           
	            byte data[] = new byte[1024];	
	            long total = 0;
	            int count;
	            int progress = 0;
	            
	            while (((count = input.read(data)) != -1) && !isCancelled()) {
	                total += count;
	                progress = (int)(total * 100 / lenghtOfFile);
	                output.write(data, 0, count);
	                publishProgress(progress);
	            }
	            
	            output.close();
	            output.flush();
	            
	            // Check for integrity
	            String remoteMd5FingerPrint = mSyncResponse.getArchive().getArchiveMd5FingerPrint();
	            String localMd5FingerPrint = FileCommons.generateBufferedHash(outputFile);
	            
	            Log.d("Sync", "Local file <"+ outputFile.getPath() + "> fingerprint is <" + localMd5FingerPrint + ">");
	            
	            if(!localMd5FingerPrint.equals(remoteMd5FingerPrint)) {
	            	Log.e("Sync", "Local md5 <"+ localMd5FingerPrint + ">, remote : <" + remoteMd5FingerPrint + ">");
	            	throw new SyncFileException("local file < " + outputFile.getPath() + "> MD5 fingerprint does not match with server MD5 ref <" + remoteMd5FingerPrint + ">", null);
	            }
	  
	            return outputFile;
	            
			} else { // No archive
				Log.d("Sync", "Archive null into web service response");
			}
          
		} catch (Exception e) {
			
			Log.e("SyncError", e.getMessage());
			mDownloadException = new SyncException(e.getMessage(), e.getCause());
			cancel(true);
		}
        return null;
    }
    
	@Override
	protected void onProgressUpdate(Integer... values) {
		mProgressDialog.setProgress(values[0]);
	}

	@Override
	protected void onPostExecute(File outFile) {
		
		mProgressDialog.dismiss();
		
		if(outFile != null) { // Download of an update file success. Publishing...
			PublishSyncAsyncTask publishSyncTaskAsync = new PublishSyncAsyncTask(mActivity, mSyncHandler, mSyncConfig);
			publishSyncTaskAsync.setSyncResponse(mSyncResponse);
			publishSyncTaskAsync.execute(outFile);
			
		} else { // Nothing to download, notify that sync is finished 
			mSyncHandler.onSynchronizationFinished(mSyncResponse.getLatestRevision(), mSyncResponse.getLatestRevisionDate());
		}
	}

    @Override
    protected void onCancelled() {

        // Notify cancel through sync handler
        mSyncHandler.onSynchronizationCancel();

        try {
            // Hide progress dialog
            mProgressDialog.dismiss();
        } catch (IllegalArgumentException e) {
        }

        // Does canceled by errors... checking that...
        if (mDownloadException != null) {

            // Yes ! notify Synchronization failure through sync handler
            mSyncHandler.onSynchronizationFailed(mDownloadException);
        }

    }

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		cancel(true);
	}

}