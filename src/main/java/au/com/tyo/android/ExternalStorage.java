package au.com.tyo.android;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

public class ExternalStorage {
	
	BroadcastReceiver mExternalStorageReceiver;
	
	boolean mExternalStorageAvailable = false;
	
	boolean mExternalStorageWriteable = false;
	
	ExternalStorageListener listener = null;
	
	private Context context;
	
	public ExternalStorage(Context context) {
		this.context = context;
	}
	
	public static interface ExternalStorageListener {
		void handleExternalStorageState(boolean state1, boolean state2);
	}

	public void setListener(ExternalStorageListener listener) {
		this.listener = listener;
	}

	public void updateExternalStorageState() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        mExternalStorageAvailable = mExternalStorageWriteable = true;
	    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        mExternalStorageAvailable = true;
	        mExternalStorageWriteable = false;
	    } else {
	        mExternalStorageAvailable = mExternalStorageWriteable = false;
	    }
	    
	    if (listener != null)
	    	listener.handleExternalStorageState(mExternalStorageAvailable,
	            mExternalStorageWriteable);
	}

	public void startWatchingExternalStorage() {
	    mExternalStorageReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            Log.i("test", "Storage: " + intent.getData());
	            updateExternalStorageState();
	        }
	    };
	    IntentFilter filter = new IntentFilter();
	    filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
	    filter.addAction(Intent.ACTION_MEDIA_REMOVED);
	    context.registerReceiver(mExternalStorageReceiver, filter);
	    updateExternalStorageState();
	}

	public void stopWatchingExternalStorage() {
		context.unregisterReceiver(mExternalStorageReceiver);
	}
	
	public static File getDir() {
		return Environment.getExternalStorageDirectory();
	}
	
	public boolean isWriteable() {
		return this.mExternalStorageWriteable;
	}
	
	public boolean isAvailable() {
		return this.mExternalStorageAvailable;
	}
}
