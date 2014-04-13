package com.thomaschampagne.hourglass.library.callback;

import com.thomaschampagne.hourglass.library.exceptions.SyncException;

public interface ISynchronizationListener 
{
	/**
	 * Called when the synchronization has failed.
	 * @param SyncException exception returned on callback on a sync failure
	 */
	void onSynchronizationFailed(SyncException e);
	

	/**
	 * Called when the synchronization has been finished.
	 * @param int serverLatestRevision
	 * @param long serverLatestRevisionTimestamp
	 */
	void onSynchronizationFinished(int serverLatestRevision, long serverLatestRevisionTimestamp);
	
	/**
	 * Called when the synchronization has been cancelled.
	 * Can be triggered by user cancellation or technical failure
	 */
	void onSynchronizationCancel();
}