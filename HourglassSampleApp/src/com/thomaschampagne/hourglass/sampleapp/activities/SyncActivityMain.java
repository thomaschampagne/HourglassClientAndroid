package com.thomaschampagne.hourglass.sampleapp.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.thomaschampagne.hourglass.library.Synchronization;
import com.thomaschampagne.hourglass.library.callback.ISynchronizationListener;
import com.thomaschampagne.hourglass.library.enums.SyncStorageMode;
import com.thomaschampagne.hourglass.library.exceptions.SyncException;
import com.thomaschampagne.hourglass.library.exceptions.SyncFileException;
import com.thomaschampagne.hourglass.library.filemanagement.FileCommons;
import com.thomaschampagne.hourglass.library.model.SyncConfig;
import com.thomaschampagne.hourglass.sampleapp.R;
import com.thomaschampagne.hourglass.sampleapp.constants.K;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Thomas Champagne
 *
 */
public class SyncActivityMain extends ListActivity implements ISynchronizationListener {

	
	
	/**
	 *	These Statics Properties will be dropped into a SyncConfig object (class member). 
	 *	You can change this for testing purpose... of course
	 *	Theses properties are not in ANDROID Resources STRING for fast understanding...
	 */
	public static final String SERVER_ENDPOINT_URL = "http://10.10.10.10/hourglass/endpoint/call.php";
	public static final Boolean SYNC_ON_TEST_RESOURCES = false;
	public static final SyncStorageMode DEFAULT_STORAGE_MODE = SyncStorageMode.INTERNAL;
//	public static final String DEFAULT_STORAGE_SUBFOLDER = "MySubDir";
	public static final String MY_CANCEL_MESSAGE = "Your Cancel Message";
	public static final String MY_DOWNLOADING_MESSAGE = "Your downloading message...";
	public static final String MY_PUBLISHING_MESSAGE = "Your publishing message...";
	
	/**
	 *	Activity demo member : 
	 *	Local persisted client revision. Can be null or 0 before first checkout
	 */
	public Integer mCurrentClientRevision = null;
	public Long mCurrentClientRevisionTimestamp = null;
	private SyncConfig mSyncConfig;
	private List<File> mFilesOnStorage;
	private Boolean mToggleSyncButtonActive = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_activity_main);
		doSyncStuffDemo(); // Prepare demo
	}

	
	private void doSyncStuffDemo() {
		initSyncConfiguration(); // Prepare synchronization configuration
		setupActivityUserInterface(); // Setup user interface
		cleanClientSide(); // clean client status on first launch
	}

	private void setupActivityUserInterface() {
		setupActionBar();
		setupFilesListView();
		invalidateOptionsMenu(); // refresh menus, we want to refresh action bar especially by this
	}

	private void setupActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.title_synchronization_demo);
		actionBar.setSubtitle("Local Revison <" + ((mCurrentClientRevision != null) ? mCurrentClientRevision :  "NONE") + "> Storage <" + mSyncConfig.getSyncStorageMode() + ">");
	}
	
	private void setupFilesListView() {
		// Set list view
		mFilesOnStorage = FileCommons.ListDir(mSyncConfig.getStoragePath(this));

		ArrayList<String> fileList = new ArrayList<String>();

		if (mFilesOnStorage != null) {
			for (File f : mFilesOnStorage) {
				fileList.add(f.getName());
			}
		}
		
		
		if (fileList.size() == 0) { // No files on storage
			
			getListView().setVisibility(View.GONE); // Hide the list view.
			
		} else { // Files on storage. update list 
			
			ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, fileList);
			setListAdapter(directoryList);
			
			getListView().setVisibility(View.VISIBLE);
		}
	
	}

	private void initSyncConfiguration() { // Creating a sync configuration
		mSyncConfig = new SyncConfig(SERVER_ENDPOINT_URL, DEFAULT_STORAGE_MODE,
				FileCommons.md5(this.getPackageName()));
		mSyncConfig.setSyncOnTestResources(SYNC_ON_TEST_RESOURCES);
		mSyncConfig.setDownloadingMessage(MY_DOWNLOADING_MESSAGE);
		mSyncConfig.setCancelMessage(MY_CANCEL_MESSAGE);
		mSyncConfig.setPublishingUpdateMessage(MY_PUBLISHING_MESSAGE);
		Log.d("SyncConfig", mSyncConfig.toString());
	}

	private void synchronize() {
		if(mToggleSyncButtonActive) {
			try {
				// Let's synchronize
				Synchronization.synchronize(
						this,
						this, 
						mCurrentClientRevision,
						mSyncConfig); 
				
				// We currently disable sync button from user. wait for cancel or failed or finish to active it again 
				mToggleSyncButtonActive = false;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, "Sync processing...", Toast.LENGTH_SHORT).show();			
		}
	}
	
	/**
	 * Clean client local info and both sdcard/internal storage
	 */
	private void cleanClientSide()  {
		
		try {
			
			// Remove local persisted client revision 
			mCurrentClientRevision = null;
			mCurrentClientRevisionTimestamp = null;
			
			// Delete content synced before into current storage mode location
			FileCommons.eraseFolder(mSyncConfig.getInternalStoragePath(this));
			FileCommons.eraseFolder(mSyncConfig.getSdCardStoragePath());
			
		} catch (SyncFileException e) {
			e.printStackTrace();
		}
		
		// Refresh UI
		setupActivityUserInterface();
	}

	private void forceClientCheckout() {
		
		// Remove local persisted client revision 
		mCurrentClientRevision = null;
		mCurrentClientRevisionTimestamp = null;
		
		// Force checkout
		try {
			Synchronization.checkout(this, this, mSyncConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void switchStorageMode() {
		mSyncConfig.setSyncStorageMode((mSyncConfig.getSyncStorageMode() == DEFAULT_STORAGE_MODE) ? 
				SyncStorageMode.SDCARD : DEFAULT_STORAGE_MODE);
		
		setupActivityUserInterface();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.sync_activity_main, menu);

	    // Update sync icon
	    int syncIconToDisplay = (mCurrentClientRevision != null) ? R.drawable.ic_action_logo_sync2arrows : R.drawable.ic_action_logo_checkout;
	    menu.findItem(R.id.action_sync).setIcon(syncIconToDisplay);
	   
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()){
			
		case R.id.action_sync:
			synchronize();
			break;
		
		case R.id.action_switchStorage:
			confirmSwitchStorageMode();
			break;
			
		case R.id.action_clean:
			askForCleanClientSide();
			break;
			
		case R.id.action_info:
			Intent i = new Intent(this, SyncInfoActivity.class);
			i.putExtra(K.INTENT_INFO_KEY, mSyncConfig);
			startActivity(i);
			break;
			
		case R.id.action_cfg_inclfilter:
			configureFilterRegexInclude();
			break;
			
		case R.id.action_cfg_exclfilter:
			configureFilterRegexExclude();
			break;
			
		case R.id.action_cfg_endpoint:
			configureWebserviceUrlChanger();
			break;
			
		case R.id.action_cfg_subfolder:
			configureSubFolder();
			break;
			
		case R.id.action_resetcfg:
			confirmResetConfiguration();
			break;
			
		default:
			break;
		}
		
		return true;
	}
	
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File clickedFile = mFilesOnStorage.get(position);
		String popupMessage = clickedFile.getAbsolutePath() +"\r\n\r\n(Storage " + mSyncConfig.getSyncStorageMode() + ")";
		createPopup("Absolute path", popupMessage);
	}

	@Override
	public void onSynchronizationFailed(SyncException e) {
		Log.d("CALLBACK", "onSynchronizationFailed");
		createPopup(getString(R.string.title_sync_failed_callback), getString(R.string.message_synchronization_failed_cause)+ e.getMessage());
		mToggleSyncButtonActive = true;
	}

	@Override
	public void onSynchronizationCancel() {
		Log.d("CALLBACK", "onSynchronizationCancel");
		createPopup(getString(R.string.title_sync_canceled_callback), getString(R.string.message_synchronization_canceled));
		mToggleSyncButtonActive = true;
	}

	@Override
	public void onSynchronizationFinished(int serverLatestRevision, long serverLatestRevisionTimestamp) {

		Log.d("CALLBACK", "onPublishFinished, serverSideRevision is : "
				+ serverLatestRevision);

		// Client up to date
		if (mCurrentClientRevision != null
				&& mCurrentClientRevision.equals(serverLatestRevision)) {
			
			createPopup(getString(R.string.title_already_synced_callback), getString(R.string.message_you_are_already_up_to_date_local_revision)+ mCurrentClientRevision);

		} else {
			
			// File sync done, client wasn't up to date.
			// Update the client revision with server revision value
			mCurrentClientRevision = serverLatestRevision;
			mCurrentClientRevisionTimestamp = serverLatestRevisionTimestamp;
			
			createPopup(getString(R.string.title_sync_finished_callback),
					getString(R.string.message_local_revision_is_now) + mCurrentClientRevision 
					+ "\r\n\r\n" + 	
					getString(R.string.message_timestamp_is)	+ mCurrentClientRevisionTimestamp);
		}

		mToggleSyncButtonActive = true;
		
		// Refresh UI
		setupActivityUserInterface();
	}

	private void configureWebserviceUrlChanger() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.configure_endpoint);

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText(mSyncConfig.getServerUrl());
		alert.setView(input);

		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mSyncConfig.setServerUrl(input.getText().toString());
			}
		});

		alert.setNegativeButton(R.string.cancel,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});
		alert.show();
	}

	private void configureFilterRegexInclude() {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.enter_regular_expression);

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText(mSyncConfig.getFilterRegexInclude());
		alert.setView(input);
		alert.setMessage(R.string.warning_filter_regex_include_change);

		alert.setPositiveButton(R.string.apply_and_checkout, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mSyncConfig.setFilterRegexInclude(input.getText().toString());
				forceClientCheckout();
			}
		});

		alert.setNegativeButton(R.string.cancel,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				}
		});
		alert.show();
	}

	private void configureFilterRegexExclude() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.enter_regular_expression);

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText(mSyncConfig.getFilterRegexExclude());
		alert.setView(input);
		alert.setMessage(R.string.warning_filter_regex_exclude_change);

		alert.setPositiveButton(R.string.apply_and_checkout, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
				mSyncConfig.setFilterRegexExclude(input.getText().toString());
				forceClientCheckout();
			}
		});

		alert.setNegativeButton(R.string.cancel,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
				}
			});
		alert.show();
	}

	private void configureSubFolder() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.enter_new_sub_folder_name);
		alert.setMessage(R.string.warning_changing_sub_folder_name);
		
		final EditText input = new EditText(this);
		input.setText(mSyncConfig.getStorageSubfolder());
		alert.setView(input);

		alert.setPositiveButton(R.string.apply_and_clean, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				cleanClientSide();
				mSyncConfig.setStorageSubfolder(input.getText().toString());
			}
		});

		alert.setNegativeButton(R.string.cancel,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
				}
			});
		alert.show();
	}
	
	private void confirmSwitchStorageMode() {
		
		// If local client revision exist and files on current storage too
		if(mCurrentClientRevision != null && mCurrentClientRevision > 0 && mFilesOnStorage != null && mFilesOnStorage.size() > 0) {

			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(R.string.switch_storage_internal_sdcard);
			alert.setMessage(R.string.warning_storage_mode_switching);
			alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
					// Clean current folder before switch
					try {
						FileCommons.eraseFolder(mSyncConfig.getStoragePath(SyncActivityMain.this));
					} catch (SyncFileException e) {
						e.printStackTrace();
					}
					
					switchStorageMode();
					
					forceClientCheckout();
				}

			});
			
			alert.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
				}
			});
			alert.show();
			
		} else {
			switchStorageMode();
			Toast.makeText(this, getString(R.string.toast_storage_mode_changed_pre) + mSyncConfig.getSyncStorageMode(), Toast.LENGTH_SHORT).show();
		}
	}

	
	private void askForCleanClientSide() {

		// If local client revision exist OR files on current storage too
		if((mCurrentClientRevision != null && mCurrentClientRevision > 0) || (mFilesOnStorage != null && mFilesOnStorage.size() > 0)) {
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(R.string.clean_synchronization_status);
			alert.setMessage(R.string.prevent_cleaning);
			
			alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					cleanClientSide();	// clean
				}
			});
			
			alert.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
				}
			});
			alert.show();
			
		} else {
			Toast.makeText(this, R.string.sync_status_already_cleaned , Toast.LENGTH_LONG).show();
		}
	}
	
	private void confirmResetConfiguration() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.reset_configuration);
		alert.setMessage(R.string.warning_reset_implies);
		
		alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				initSyncConfiguration();
				forceClientCheckout();
			}
		});
		
		alert.setNegativeButton(R.string.no,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled. 
			}
		});
		alert.show();
	}
	
	private void createPopup(String title, String message) {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(title);
		alert.setMessage(message);
		
		alert.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		
		alert.show();
	}
	
}