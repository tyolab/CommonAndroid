package au.com.tyo.android;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Window;
import android.annotation.SuppressLint;
import android.app.Activity;
import au.com.tyo.CommonSettings;
import au.com.tyo.Debug;
import au.com.tyo.io.IO;

public class AndroidSettings extends CommonSettings implements Android {

	public static final int API_VERSION = android.os.Build.VERSION.SDK_INT; 
	
	public static final String PREF_FIRST_TIME_RUN = "pref_first_time_run";

	public static final String PREF_THEME_ID = "pref_theme_id";
	
	public static final String PREF_CACHE_STATUS = "pref_cache_enabled";
	
	public static final String PREF_DATA_STORAGE_PATH = "pref_data_storage_path";
	
	public static final String PREF_SEARCH_HISTORY = "pref_search_history";
	
	public static final int NETWORK_TYPE_NONE = 0;
	
	public static final int NETWORK_TYPE_WIFI = 1;
	
	public static final int NETWORK_TYPE_MOBILE = 2;
	
	public static final int NETWORK_TYPE_OTHERS = 4;
	
	private static AndroidSettings instance;
	
	protected Context context;
	
	protected boolean hasVoiceRecognitionService = false;
	
	protected boolean hasVoiceRecoginitionActivity = true;
	
	protected boolean customTitleSupported = false;
	
	protected SharedPreferences prefs;
	
	protected int themeId;
	
	private boolean hasAd;
	
	private boolean showDonation;
	
	private boolean cacheEnabled;
	
	private AndroidMarket market;
	
	private int networkStatus;
	
	private String dataStoragePath;
	
	protected boolean lightThemeUsed;
	
	protected ExternalStorage externalStorage;
	
//	private HashMap<String, Long> searchHistoryArray;
	
//	protected int notificationBarHeight;

	public boolean hasVoiceRecognitionService() {
		return hasVoiceRecognitionService;
	}

	@SuppressLint("NewApi")
	public AndroidSettings(Context context) {
		super();
		this.context = context;
		
		externalStorage = new ExternalStorage(context);
		externalStorage.updateExternalStorageState();
		
		checkIfDebuggable();
		
		setIsTablet(isTablet(context));
		
		setNetworkStatus(NETWORK_TYPE_NONE);
		
		market = new AndroidMarket(context);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infoList = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (infoList.size() == 0) {
        	hasVoiceRecoginitionActivity = false;
        	/** Show some feedback to user if there is the activity. Something like "Your device is not able to run this feature..."*/
        }
	
		if (AndroidUtils.getAndroidVersion() > 7) {
			try {
				hasVoiceRecognitionService = SpeechRecognizer.isRecognitionAvailable(context);
			}
			catch (Exception ex) {
				hasVoiceRecognitionService = false;
			}
		}
		else
			hasVoiceRecognitionService = false;
		
		setHasAd(false);
		if (context.getResources().getBoolean(R.bool.showAd) && !Debug.debugging) {
			if (market.isFromGooglePlayStore()) {
				if (context.getResources().getBoolean(R.bool.showAd4GooglePlayStoreApp))
					setHasAd(true);
			}
			else
				setHasAd(true);
		}
		
		this.setShowDonation(context.getResources().getBoolean(R.bool.showDonation));

//		searchHistoryArray = null;
//		loadPreferences();
//		notificationBarHeight = calculateNotificationBarHeight(context);
		this.dataStoragePath = externalStorage.isAvailable() ? externalStorage.getDir().getAbsolutePath() : context.getCacheDir().getAbsolutePath(); /// + "/Android/data/" + AndroidUtils.getPackageName(context) + "/";
		
		Debug.build = AndroidUtils.isAppDebuggable(context);
	}
	
	public Context getContext() {
		return context;
	}
	
	private void checkIfDebuggable() {
//		if (!AndroidUtils.isAppDebuggable(context)) {
			String[] storages = AndroidUtils.getStorageDirectories();
			String keyStr = context.getResources().getString(R.string.debug_key);
			if (keyStr != null) {
				byte[] debugKey = keyStr.trim().getBytes();
				if (debugKey.length > 0)
					for (String str : storages) {
						File file = new File(str + File.separator + "debug.txt");
						if (file.exists()) {
							debugKey = IO.readFileIntoBytes(file);
							if (debugKey == null)
								continue;
							
							byte[] key = new String().trim().getBytes();
							if (Arrays.equals(key, debugKey)) {
								Debug.debugging = true;
								break;
							}
						}
					}
			}
//		}
//		else
//			DEBUG = true;
	}

	/**
	 * it is not loaded by default
	 */
	public void loadPreferences() {
		this.cacheEnabled = prefs.getBoolean(PREF_CACHE_STATUS, true);
		
		this.firstTimeRun = prefs.getBoolean(PREF_FIRST_TIME_RUN, true);
		
		this.themeId = prefs.getInt(PREF_THEME_ID, -1);
		
		if (firstTimeRun)
			onFirstTimeRun();
		
		
		String theDefaultExternalFilePath = this.dataStoragePath; 
//		if (AndroidUtils.getAndroidVersion() > 7) {
//			theDefaultExternalFilePath = context.getExternalFilesDir(null) != null ? context.getExternalFilesDir(null).getAbsolutePath() : context.getCacheDir().getAbsolutePath();
//		}
//		else {
//		}
		
		this.dataStoragePath = prefs.getString(PREF_DATA_STORAGE_PATH, theDefaultExternalFilePath);
		if (!new File(dataStoragePath).exists()) 
			this.dataStoragePath = theDefaultExternalFilePath;
		
		if (!new File(dataStoragePath).exists()) 
			this.dataStoragePath = null; //theDefaultExternalFilePath;
		
		
//		String historyStr = prefs.getString(PREF_SEARCH_HISTORY, "");
//		String[] tokens = historyStr.split(",");
//		if (tokens != null)
//			for (String str : tokens)
//				searchHistoryArray.add(str);
	}

	protected void onFirstTimeRun() {
		
	}

	/**
	 * only works when run in a main GUI activity thread
	 * 
	 * @param context
	 * @return
	 */
	public int calculateNotificationBarHeight(Context context) {
		Rect rectgle= new Rect();
		Window window= ((Activity) context).getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		int StatusBarHeight= rectgle.top;
//		int contentViewTop= 
//		    window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
//		int TitleBarHeight= contentViewTop - StatusBarHeight;
		return StatusBarHeight;
	}
	
	/**
	 * the default notification bar height is 38 in hdpi, 25 in mdpi and 19 in ldpi
	 * 
	 * @return
	 */
	public int getStatusBarHeight() {
	      int result = 0;
	      int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
	      if (resourceId > 0) {
	          result = context.getResources().getDimensionPixelSize(resourceId);
	      }
	      return result;
	}

	public static void initialize(Context context, String appName, String version, String androidVersion) {
		if (instance == null) {
			instance = new AndroidSettings(context);
		
//			Application.getInstance().getResourceManager().setCssFile("<link rel=\"stylesheet\" href=\"file:///android_asset/wikipedia/night.css\" type=\"text/css\" media=\"all\" /> ");
//			WikiApi.getInstance().setUserAgent(appName + "/" + version + " Mozilla/5.0 (Linux; U; Android" + androidVersion/*android.os.Build.VERSION.RELEASE*/ + " ; en-us; sdk Build/MR1) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
		}
	}

	public static AndroidSettings getInstance() {

		return instance;
	}
	
	public File getCacheDir() {
		return context.getCacheDir();
	}
	
	public void setCheckBoxSummary(Preference preference, boolean value) {
		if (value)
			preference.setSummary(R.string.enabled);
		else
			preference.setSummary(R.string.disabled);
	}
	
	public boolean checkNetworkState() {
		this.networkStatus = checkNetworkState(context); 
		return networkStatus > NETWORK_TYPE_NONE;
	}

	public SharedPreferences getPreferences() {
		return prefs;
	}
	
	/**
	 * @return the customTitleSupported
	 */
	public boolean isCustomTitleSupported() {
		return customTitleSupported;
	}

	/**
	 * @param customTitleSupported the customTitleSupported to set
	 */
	public void setCustomTitleSupported(boolean customTitleSupported) {
		this.customTitleSupported = customTitleSupported;
	}
	
    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }
    
	@SuppressLint("NewApi")
	public void updatePreference(String key, Object value) {
		SharedPreferences.Editor editor = prefs.edit();
    	
		if (value instanceof String)
			editor.putString(key, (String) value);
		else if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);			
		}
		else if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);			
		}
		else if (value instanceof Float) {
			editor.putFloat(key, (Float) value);			
		}
		else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		}
		else if (value instanceof Set<?>) {
			editor.putStringSet(key, (Set<String>) value);
		}
    	editor.commit();		
	}
	
	public void hasRunFirstTime() {
		this.setFirstTimeRun(false);
		this.updatePreference(PREF_FIRST_TIME_RUN, (Boolean) false);
	}
	
	public int getThemeId() {
		return themeId;
	}
	
	public void setThemeId(int themeId) {
		this.themeId = themeId;
	}

	public boolean hasVoiceRecoginitionActivity() {
		return hasVoiceRecoginitionActivity;
	}

	public boolean hasAd() {
		return hasAd;
	}

	public boolean toShowDonation() {
		return showDonation;
	}

	public void setShowDonation(boolean showDonation) {
		this.showDonation = showDonation;
	}

	public void setHasAd(boolean hasAd) {
		this.hasAd = hasAd;
	}

	public AndroidMarket getMarket() {
		return market;
	}

	public void setMarket(AndroidMarket market) {
		this.market = market;
	}

	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	public void setCacheEnabled(boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}
	
	public int getNetworkStatus() {
		return networkStatus;
	}

	public void setNetworkStatus(int networkStatus) {
		this.networkStatus = networkStatus;
	}

	public static int checkNetworkState(Context context) {
		ConnectivityManager conMgr =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		int result = NETWORK_TYPE_NONE;
		NetworkInfo wifiNetwork = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    if (wifiNetwork != null && (wifiNetwork.isConnected() || wifiNetwork.isConnectedOrConnecting())) {
	      result = NETWORK_TYPE_WIFI;
	    }
	    else {
		    NetworkInfo mobileNetwork = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		    if (mobileNetwork != null && (mobileNetwork.isConnected() || mobileNetwork.isConnectedOrConnecting())) {
		      result = NETWORK_TYPE_MOBILE;
		    }
		    else {
			    NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
			    if (activeNetwork != null && (activeNetwork.isConnected() || activeNetwork.isConnectedOrConnecting())) {
			      result = NETWORK_TYPE_OTHERS;
			    }
		    }
	    }
//	    conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()
//		Application.getInstance().setNetworkConnected(result);
//		if ( conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED 
//		    ||  conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING  ) {
//		    //notify user you are online
//			Application.getInstance().setNetworkConnected(true);
//		}
//		else if ( conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED 
//		    ||  conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
//		    //notify user you are not online
//			Application.getInstance().setNetworkConnected(false);
//		}
		return result;
	}
	
	public static boolean hasInternet(Context context) {
		int networkStatus = AndroidSettings.checkNetworkState(context); 
		return networkStatus > NETWORK_TYPE_NONE;
	}
	
	
	public void updateThemePreference(int themeId) {
		if (themeId != getThemeId()) {
			setThemeId(themeId);
			updatePreference(AndroidSettings.PREF_THEME_ID, themeId);
		}
	}

	public String getDataStoragePath() {
		return dataStoragePath;
	}

	public void setDataStoragePath(String dataStoragePath) {
		this.dataStoragePath = dataStoragePath;
	}
	
	public void updateDataStoragePath(String dataStoragePath) {
		this.setDataStoragePath(dataStoragePath);
		this.updatePreference(PREF_DATA_STORAGE_PATH, dataStoragePath);
	}
	
    public boolean isLightThemeUsed() {
		return lightThemeUsed;
	}

	public void setLightThemeUsed(boolean usesLightTheme) {
		this.lightThemeUsed = usesLightTheme;
	}
	
	public String getAppPath(String what) {
		String path = null;
		path = /*Environment.getExternalStorageDirectory().getAbsolutePath()*/ dataStoragePath
				+ File.separator + "Android" + File.separator + what + File.separator
				+ File.separator + AndroidUtils.getPackageName(context);
		return path;
	}
	
	@Override
	public String getAppDataDataPath() {
		return getAppDataSubPath("data");
	}
	
	@Override
	public String getAppDataSubPath(String subPath) {
		String path = getAppDataPath() + File.separator +subPath;
		File file = new File(path);
		if (!file.exists())
			file.mkdirs();
		return path;
	}
	
	@Override
	public String getAppDataPath() {
		return getAppPath("data");
	}
	
	@Override
	public String getAppObbPath() {
		return getAppPath("obb");
	}
	
	@Override
	public String getPatchObbName(int expansionVersion) {
		return getObbName("patch", expansionVersion);
	}
	
	@Override
	public String getMainObbName(int expansionVersion) {
		return getObbName("main", expansionVersion);
	}
	
	@Override
	public String getObbName(String mainOrPatch, int expansionVersion) {
		String obbName = null;
		obbName = String.format(OBB_NAME_TEMPLATE, mainOrPatch, expansionVersion, AndroidUtils.getPackageName(context));
		return obbName;
	}

	public ExternalStorage getExternalStorage() {
		return externalStorage;
	}
	
}
