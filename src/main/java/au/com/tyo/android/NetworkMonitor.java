/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */
package au.com.tyo.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import au.com.tyo.services.Internet;
import au.com.tyo.utils.StringUtils;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */


public class NetworkMonitor implements Runnable {
	
	public static final String LOG_TAG = "NetworkMonitor";
	
	public static final int JOB_NONE = -1;
	public static final int JOB_CHECK_INTERNET = 0;
	public static final int JOB_CHECK_NETWORK = 1;

	public static final int NETWORK_TYPE_NONE = 0;

	public static final int NETWORK_TYPE_WIFI = 1;

	public static final int NETWORK_TYPE_MOBILE = 2;

	public static final int NETWORK_TYPE_OTHERS = 4;
	
	private int job;
	
	private boolean hasInternet;
	private boolean networkConnected;
	
	private static NetworkMonitor monitor;
	private static Thread thread;
	
	private CommonController controller;

	public synchronized boolean hasInternet() {
		return hasInternet;
	}
	
	public NetworkMonitor(CommonController controller) {
		this();
		this.controller = controller;
	}
	
	public NetworkMonitor() {
		job = JOB_CHECK_NETWORK; // the first thing when the job starts to initialise
		this.hasInternet = false;
	}

	public static int checkNetworkState(Context context) {
		ConnectivityManager conMgr =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		int result = NETWORK_TYPE_NONE;
		@SuppressLint("MissingPermission") NetworkInfo wifiNetwork = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && (wifiNetwork.isConnected() || wifiNetwork.isConnectedOrConnecting())) {
			result = NETWORK_TYPE_WIFI;
		}
		else {
			@SuppressLint("MissingPermission") NetworkInfo mobileNetwork = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mobileNetwork != null && (mobileNetwork.isConnected() || mobileNetwork.isConnectedOrConnecting())) {
				result = NETWORK_TYPE_MOBILE;
			}
			else {
				@SuppressLint("MissingPermission") NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
				if (activeNetwork != null && (activeNetwork.isConnected() || activeNetwork.isConnectedOrConnecting())) {
					result = NETWORK_TYPE_OTHERS;
				}
			}
		}
		return result;
	}
	
	public synchronized void setHasInternet(boolean haveIt) {
		this.hasInternet = haveIt;
	}

	public boolean isNetworkConnected() {
		return networkConnected;
	}

	public void setNetworkConnected(boolean networkConnected) {
		this.networkConnected = networkConnected;
	}
	
	public static NetworkMonitor getInstance(){
		return getInstance(null);
	}
	
	public static NetworkMonitor getInstance(CommonController controller) {
		if (monitor == null) {
			monitor = new NetworkMonitor(controller);
		}
		return monitor;
	}

	public void setController(CommonController controller) {
		this.controller = controller;
	}

	public void start() {
		if (thread == null)
			thread = new Thread(getInstance());
		if (!thread.isAlive()) {
			thread.setDaemon(true);
			thread.start();
		}
	}

	public synchronized int getJob() {
		return job;
	}

	public synchronized void setJob(int job) {
		this.job = job;
	}
	
	@Override
	public void run() {
		while (true)
		{
    		try
    		{
        		switch (monitor.getJob()) {
        		case JOB_CHECK_NETWORK:
        			if (controller != null && controller.getContext() != null) {
        				boolean connected = hasInternet(controller.getContext());
        				monitor.setHasInternet(connected);  // assuming has network has internet
        				monitor.setNetworkConnected(connected);
        			}
        			job = JOB_CHECK_INTERNET;
        			break;
        		case  JOB_CHECK_INTERNET:
        			checkInternetAvailability(monitor.hasInternet() );
        			if (monitor.hasInternet() || !isNetworkConnected()) {
        				monitor.setJob(JOB_NONE );
//        				thread = null;
//        				return;
        			}
        			break;
        		case JOB_NONE:
        		default:
        			break;
        		}
        		
        		Thread.sleep(2000);  // pause 2 second
    		} catch (InterruptedException e) {
    			Log.e(LOG_TAG, "something tried to stop me");
			}
    		catch(Exception e)
    		{   
    			Log.e(LOG_TAG, e.getMessage());
    		}
    		finally {
    			
    		}
		}
	}
	
	public void checkInternetAvailability(boolean previousState) {
		
		monitor.setHasInternet(checkInternet());
		
		if (monitor.hasInternet()/* && monitor.hasInternet() != previousState*/ && controller != null) {
            controller.sendMessage(Constants.MESSAGE_NETWORK_READY, null);
		}
	}

	public CommonController getController() {
		return controller;
	}
	
	public static boolean hasInternet(Context context) {
		return AndroidSettings.hasInternet(context);
	}
	
	public static boolean checkInternet() {
		try {
			return Internet.checkAvailability();
		} catch (Exception e) {
			Log.e(LOG_TAG, "oops, something wrong when connecting to the Internet: " + StringUtils.exceptionStackTraceToString(e));
		}
		return false;
	}

	public static boolean isConnectedToWiFi(Context context) {
		return checkNetworkState(context) == NETWORK_TYPE_WIFI;
	}
}
