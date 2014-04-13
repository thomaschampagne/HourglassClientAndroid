package com.thomaschampagne.hourglass.sampleapp.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.thomaschampagne.hourglass.library.model.SyncConfig;
import com.thomaschampagne.hourglass.sampleapp.R;
import com.thomaschampagne.hourglass.sampleapp.constants.K;

public class SyncInfoActivity extends Activity {
	
	SyncConfig mSyncConfig;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		prepareActivity();
		
		catchSyncConfig(); 

		setupInfo();
		
	}

	private void prepareActivity() {
		setContentView(R.layout.sync_activity_info);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(R.string.infos_title);
	}

	private void catchSyncConfig() {
		Intent i = getIntent();
		mSyncConfig = (SyncConfig) i.getSerializableExtra(K.INTENT_INFO_KEY);
	}

	private void setupInfo() {
		
		// Annoying task... Doing this on the fly ... ;)
		TextView inclFilterTv = (TextView) findViewById(R.id.info_inclFilter);
		TextView exclFilterTv = (TextView) findViewById(R.id.info_exclFilter);
		TextView endpointTv = (TextView) findViewById(R.id.info_endpoint);
		TextView timeoutRevCheckTv = (TextView) findViewById(R.id.info_timeoutRevCheck);
		TextView timeoutUpdateTv = (TextView) findViewById(R.id.info_timeoutUpdate);
		TextView storageModeTv = (TextView) findViewById(R.id.info_storageMode);
		TextView subfolderTv = (TextView) findViewById(R.id.info_subfolder);
		
		inclFilterTv.setText((mSyncConfig.getFilterRegexInclude().equals("") ? getString(R.string.info_regex_empty) : mSyncConfig.getFilterRegexInclude()));
		exclFilterTv.setText((mSyncConfig.getFilterRegexExclude().equals("") ? getString(R.string.info_regex_empty) : mSyncConfig.getFilterRegexExclude()));
		endpointTv.setText(mSyncConfig.getServerUrl());
		timeoutRevCheckTv.setText(mSyncConfig.getConnectionTimeoutMillisOnRemoteRevisionCheck() + " milliseconds");
		timeoutUpdateTv.setText(mSyncConfig.getConnectionTimeoutMillisOnUpdateFiles() + " milliseconds");
		storageModeTv.setText(mSyncConfig.getSyncStorageMode().toString());
		subfolderTv.setText(mSyncConfig.getStorageSubfolder());
	}

	@Override
	public void onBackPressed() {
		finish();
	}
	
}
