/*
Yuml.me class diagram :

[SyncException]-^[Exception]
[SyncFileException]-^[SyncException]
[SyncParsingException]-^[SyncException]
[SyncWSException]-^[SyncException],
[Synchronization|s+ Synchronize(SyncConfig syncConfig;Integer clientRevision)]
[Synchronization]++->[SyncServices|s+ syncAllCall();s+ syncFromRevisionCall();]
[Synchronization]++->[SyncConfig|-serverUrl : string;-filterRegexInclude : string;-filterRegexExclude : string;-syncOnTestResources : boolean;syncStorageMode : SyncStorageMode]
[SyncStorageMode|INTERNAL;SDCARD]
[SyncConfig]++->[SyncStorageMode]
 */

package com.thomaschampagne.hourglass.library;

import android.app.Activity;

import com.thomaschampagne.hourglass.library.asynctasks.BeginSynchronizationASyncTask;
import com.thomaschampagne.hourglass.library.callback.ISynchronizationListener;
import com.thomaschampagne.hourglass.library.model.SyncConfig;
/**
 * 
 * @author Thomas Champagne
 * 
 */
public class Synchronization {

	/**
	 * Entry point method for a checkout (FYI, it use synchronize method with currentClientVersion equals to 'null')
	 * @param activity Android activity from which you want to synchronize
	 * @param syncHandler SynchronizationListener Object from which you want to receive synchronization callback
	 * @param syncConfig Your synchronization configuration instance. Where you define server url, regex sync filters, Storage MODE
	 * @throws Exception
	 */
	public static void checkout(
			Activity activity,
			ISynchronizationListener syncHandler,
			SyncConfig syncConfig) throws Exception {
		synchronize(activity, syncHandler, null, syncConfig);
	}
	
	/**
	 * Entry point method for synchronize
	 * @param activity  Android activity from which you want to synchronize
	 * @param syncHandler SynchronizationListener Object from which you want to receive synchronization callback
	 * @param currentClientVersion Your current client revision Integer can be null or <= 0.  If set to null or 0 this is understood as a checkout.
	 * @param syncConfig Your synchronization configuration instance. Where you define server url, regex sync filters, Storage MODE
	 * @throws Exception 
	 */
	public static void synchronize(Activity activity,
			ISynchronizationListener syncHandler, Integer currentClientVersion,
			SyncConfig syncConfig) throws Exception {
			BeginSynchronizationASyncTask beginSynchronizationASyncTask = new BeginSynchronizationASyncTask(
					activity, currentClientVersion, syncConfig, syncHandler);
			beginSynchronizationASyncTask.execute();
			

	}
}
