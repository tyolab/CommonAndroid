package au.com.tyo.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import au.com.tyo.utils.StringUtils;

import static android.content.Context.AUDIO_SERVICE;

public class AndroidUtils {
	
	public static final String LOG_TAG = "AndroidUtils";

	/**
	 *
	 * @param targetPackage
	 * @param context
	 * @return
	 */
	public static boolean packageExists(String targetPackage, Context context){
		List<ApplicationInfo> packages;
		PackageManager pm;
		pm = context.getPackageManager();  
		packages = pm.getInstalledApplications(0);
		for (ApplicationInfo packageInfo : packages) 
			  if(packageInfo.packageName.equals(targetPackage)) 
			  	return true;
		
		return false;
	}

	/**
	 *
	 * @param targetPackage
	 * @param context
	 * @return
	 */
	public static boolean doesPackageExist2(String targetPackage, Context context){
		PackageManager pm = context. getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
		 } 
		catch (NameNotFoundException e) {
			return false;
		 }  
		 return true;
	}

	/**
	 *
	 * @return
	 */
	public static int getAndroidVersion() {
		return android.os.Build.VERSION.SDK_INT; 
	}

	/**
	 *
	 * @param activity
	 */
	public static void exit(Activity activity) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		activity.startActivity(intent); 
		activity.finish();   
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static int getPackageVersionCode(Context context) {
		PackageInfo pInfo = null;
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG, "couldn't get package version");
		}
		return pInfo == null ? 1 : pInfo.versionCode;
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getPackageVersionName(Context context) {
		PackageInfo pInfo = null;
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG, "couldn't get package version");
		}
		return pInfo == null ? "" : pInfo.versionName;
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getPackageName(Context context) {
		PackageInfo pInfo = null;
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG, "couldn't get package name");
		}
		return pInfo == null ? "" : pInfo.packageName;
	}
	
	private static final Pattern DIR_SEPARATOR = Pattern.compile(File.separator);

	public static  String[] getStorageDirectories(Context context) {
		// Final set of paths
		final Set<String> rv = new HashSet<String>();

		getStorageDirectories(rv);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			File[] files = context.getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS);
			/*
			 * This will return the download folder for the package
			 * which will be:
			 * /storage/emulated/0
			 * /storage/[USB-ID]/
			 */
			for (File file : files) {
				String fileStr = file.getAbsolutePath();
				String[] tokens = fileStr.split("/");
				String storagePath = null;
				if (tokens.length >= 4) {
					if (tokens[3].equals("Android")) {
						storagePath = StringUtils.join(tokens, File.separator, 0, 3);
					}
					else if (tokens.length > 4 && tokens[4].equals("Android")) {
						storagePath = StringUtils.join(tokens, File.separator, 0, 4);
					}
				}
				if (null != storagePath)
					rv.add(storagePath);
			}
		}

		return rv.toArray(new String[rv.size()]);
	}

	public static String[] getStorageDirectories() {
		final Set<String> rv = new HashSet<String>();
		getStorageDirectories(rv);
		return rv.toArray(new String[rv.size()]);
	}

	/**
	 * Returns all available SD-Cards in the system (include emulated)
	 *
	 * Warning: Hack! Based on Android source code of version 4.3 (API 18)
	 * Because there is no standard way to get it.
	 * TODO: Test on future Android versions 4.4+
	 *
	 * @return paths to all available SD-Cards in the system (include emulated)
	 */
	public static void getStorageDirectories(Set<String> rv) {
	    // Primary physical SD-CARD (not emulated)
	    final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
	    // All Secondary SD-CARDs (all exclude primary) separated by ":"
	    final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
	    // Primary emulated SD-CARD
	    final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
	    if(TextUtils.isEmpty(rawEmulatedStorageTarget))
	    {
	        // Device has physical external storage; use plain paths.
	        if(TextUtils.isEmpty(rawExternalStorage))
	        {
	            // EXTERNAL_STORAGE undefined; falling back to default.
	            rv.add("/storage/sdcard0");
	        }
	        else
	        {
	            rv.add(rawExternalStorage);
	        }
	    }
	    else
	    {
	        // Device has emulated storage; external storage paths should have
	        // userId burned into them.
	        final String rawUserId;
	        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
	        {
	            rawUserId = "";
	        }
	        else
	        {
	            final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
	            final String[] folders = DIR_SEPARATOR.split(path);
	            final String lastFolder = folders[folders.length - 1];
	            boolean isDigit = false;
	            try
	            {
	                Integer.valueOf(lastFolder);
	                isDigit = true;
	            }
	            catch(NumberFormatException ignored)
	            {
	            }
	            rawUserId = isDigit ? lastFolder : "";
	        }
	        // /storage/emulated/0[1,2,...]
	        if(TextUtils.isEmpty(rawUserId))
	        {
	            rv.add(rawEmulatedStorageTarget);
	        }
	        else
	        {
	            rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
	        }
	    }
	    // Add all secondary storages
	    if(!TextUtils.isEmpty(rawSecondaryStoragesStr))
	    {
	        // All Secondary SD-CARDs splited into array
	        final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
	        Collections.addAll(rv, rawSecondaryStorages);
	    }
	}

	/**
	 *
	 * @param path
	 * @return
	 */
	public static double getStorageSizeInGigabytes(String path) {
		StatFs stat = new StatFs(path);
		double sdAvailSize =Double.valueOf(stat.getBlockCount())
		               * Double.valueOf(stat.getBlockSize());
		//One binary gigabyte equals 1,073,741,824 bytes.
		double gigaAvailable = sdAvailSize / 1073741824;
		return gigaAvailable;
	}

	/**
	 *
	 * @param path
	 * @return
	 */
	public static double getStorageAvailableSizeInGigabytes(String path) {
		StatFs stat = new StatFs(path);
		double sdAvailSize =Double.valueOf(stat.getAvailableBlocks())
		               * Double.valueOf(stat.getBlockSize());
		//One binary gigabyte equals 1,073,741,824 bytes.
		double gigaAvailable = sdAvailSize / 1073741824;
		return gigaAvailable;
	}

	/**
	 *
	 * @param context
	 */
	public static void hideAppIcon(Activity context) {
		PackageManager p = context.getPackageManager();
		p.setComponentEnabledSetting(context.getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	}

	/**
	 *
	 * @param activity
	 * @param packageName
	 * @throws NameNotFoundException
	 */
	public static void startApp(Context activity, String packageName) throws NameNotFoundException {
		startApp(activity, packageName, 0, null);
	}

	/**
	 *
	 * @param activity
	 * @param packageName
	 * @param flags
	 * @param extras
	 * @throws NameNotFoundException
	 */
	public static void startApp(Context activity, String packageName, int flags, Bundle extras) throws NameNotFoundException {
		Intent i;
		PackageManager manager = activity.getPackageManager();
	    i = manager.getLaunchIntentForPackage(packageName);
	    if (i == null)
	        throw new PackageManager.NameNotFoundException();
	    
		i.setAction(Intent.ACTION_MAIN);
	    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | flags);
	    i.addCategory(Intent.CATEGORY_LAUNCHER);
	    
	    if (extras != null)
	    	i.putExtras(extras);
	    
	    activity.startActivity(i);
	}

	/**
	 *
	 * @param cls
	 * @param context
	 */
	public static void startActivity (Class cls, Activity context) {
		startActivity(cls, context, null, null, -1);
	}

	/**
	 *
	 * @param cls
	 * @param context
	 * @param bundle
	 * @param extra
	 * @param flags
	 */
	public static void startActivity (Class cls, Activity context, Bundle bundle, Serializable extra, int flags) {
		Intent intent = new Intent(context, cls);

		if (flags > -1) {
			intent.addFlags(flags);
		}
		else {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		}

		if (null != extra)
			intent.putExtra(Constants.ACTIVITY_EXTRA, extra);

		if (null != bundle && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
				context.startActivity(intent, bundle);
		else
			context.startActivity(intent);
	}

	/**
	 *
	 * @param context
	 * @param url
	 */
	public static void gotoMarket(Context context, String url) {
		openLinkWithDefaultAction(context, url);
	}

	/**
	 *
	 * @param context
	 * @param url
	 */
	public static void openLinkWithDefaultAction(Context context, String url) {
		Uri goUri = Uri.parse(url);

		Intent goIntent = new Intent(Intent.ACTION_VIEW, goUri);
		goIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(goIntent);
	}

	/**
	 *
	 * @param context
	 */
	public static void openSystemFileBrowser(Context context) {
	     Intent intent = new Intent();
	     intent.setAction(Intent.ACTION_GET_CONTENT);
	     intent.setType("file/*");
	     context.startActivity(intent);	
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static int getActionBarHeight(Context context) {
		TypedValue tv = new TypedValue();
		int actionBarHeight = 0;
		if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
		    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static boolean isAppDebuggable(Context context) {
		return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
	}
	
	/**
	 * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
	 * @param context Context reference to get the TelephonyManager instance from
	 * @return country code or null
	 */
	public static String getUserCountry(Context context) {
	    try {
	        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	        final String simCountry = tm.getSimCountryIso();
	        if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
	            return simCountry.toLowerCase(Locale.US);
	        }
	        else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
	            String networkCountry = tm.getNetworkCountryIso();
	            if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
	                return networkCountry.toLowerCase(Locale.US);
	            }
	        }
	    }
	    catch (Exception e) { }
	    return null;
	}

    /**
     * Get user's primary account on device
     *
     * @param context
     * @return
     */
	public static String getUserPrimaryAccount(Context context) {
        List list = getDeviceAccounts(context);

        return null != list && list.size() > 0 ? (String) list.get(0) : null;
    }
	
	/**
	 * getting the account email addresses associated with the device
	 *
	 * need permission:
	 *
	 * <uses-permission android:name="android.permission.GET_ACCOUNTS" />
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	public static List<String> getDeviceAccounts(Context context) {
		ArrayList list = new ArrayList<String>();
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		@SuppressLint("MissingPermission") Account[] accounts = AccountManager.get(context).getAccounts();
		
		for (Account account : accounts) 
		    if (emailPattern.matcher(account.name).matches()) 
		        list.add(account.name);

		return list;
	}

	/**
	 *
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Uri getRawResourceUri(Context context, int resId) {
		return Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);
	}

	/**
	 *
	 * @param context
	 * @param resName
	 * @return
	 */
	public static Uri getRawResourceUri(Context context, String resName) {
		return Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + resName);
	}

	/**
	 *
	 * @param context
	 * @param px
	 * @return
	 */
	public static float pxToDp(Context context, float px) {
		float density = context.getResources().getDisplayMetrics().density;
		return px / density;
	}

	/**
	 *
	 * @param context
	 * @param dp
	 * @return
	 */
	public static float dpToPx(Context context, float dp) {
		float density = context.getResources().getDisplayMetrics().density;
		return dp * density;
	}

	/**
	 *
	 * @param activity
	 */
	public static void finishActivity(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			activity.finishAndRemoveTask();
		}
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			activity.finishAffinity();
		}
		else {
			activity.finish();
		}

		// not to do that
		// System.exit(0);
	}

	/**
	 *
	 * @param context
	 * @param lat
	 * @param lng
	 * @return
	 * @throws IOException
	 */
	public static List<Address> getUserAddresses(Context context, int lat, int lng) throws IOException {
		Geocoder gcd = new Geocoder(context, Locale.getDefault());
		List<Address> addresses = gcd.getFromLocation(lat, lng, 5);
		return addresses;
	}

	/**
	 *
	 * @param context
	 * @param lat
	 * @param lng
	 * @return
	 * @throws IOException
	 */
	public static Address getUserFirstAddress(Context context, int lat, int lng) throws IOException {
		return getUserAddresses(context, lat, lng).get(0);
	}

	/**
	 *
	 * @param activity
	 */
	public static void hideSoftKeyboard(Activity activity) {
        hideSoftKeyboard(activity.getCurrentFocus());
}

    /**
     *
     * @param view
     */
    public static void hideSoftKeyboard(View view) {
		if (null == view)
			return;

        InputMethodManager inputMethodManager =
                (InputMethodManager) view.getContext().getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }

	/**
	 *
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */
    public static int getPredefinedApplicationThemeId(Context context) throws NameNotFoundException {
		PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		int themeResId = packageInfo.applicationInfo.theme;
		return themeResId;
    }

	/**
	 *
	 * @param context
	 * @return
	 */
	public static int getActivityThemeId(Activity context) {
		int themeResId = -1;
		try {
			themeResId = context.getPackageManager().getActivityInfo(context.getComponentName(), 0).getThemeResource();
		} catch (NameNotFoundException e) {

		}
		return themeResId;
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static int getApplicationThemeId(Context context) {
		int themeId = -1;
		Resources.Theme theme = context.getTheme();
		try {
			Method method = theme.getClass().getMethod("getThemeResId");
			method.setAccessible(true);
			themeId = (Integer) method.invoke(context);
		}
		catch (Exception ex) {
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					Field fThemeImpl = theme.getClass().getDeclaredField("mThemeImpl");
					if (!fThemeImpl.isAccessible()) fThemeImpl.setAccessible(true);
					Object mThemeImpl = fThemeImpl.get(theme);
					Field fThemeResId = mThemeImpl.getClass().getDeclaredField("mThemeResId");
					if (!fThemeResId.isAccessible()) fThemeResId.setAccessible(true);
					themeId = fThemeResId.getInt(mThemeImpl);
				} else {
					Field fThemeResId = theme.getClass().getDeclaredField("mThemeResId");
					if (!fThemeResId.isAccessible()) fThemeResId.setAccessible(true);
					themeId = fThemeResId.getInt(theme);
				}
			} catch (Exception ex1) {
				Log.e(LOG_TAG, "Getting application theme id error.", ex);
			}
		}
		return themeId;
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getApplicationThemeName(Context context) {
		Resources.Theme theme = context.getTheme();
		int themeId = getApplicationThemeId(context);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			return theme.getResources().getResourceEntryName(themeId);
		}

		if (themeId > -1)
			return context.getResources().getResourceEntryName(themeId);
		return null;
	}

	/**
	 *
	 * @param activity
	 * @param colorResId
	 */
	public static void setStatusBarColorWithResourceId(Activity activity, int colorResId) {
		setStatusBarColorWithResourceId(activity, colorResId, 0);
	}

	/**
	 *
	 * @param activity
	 * @param colorResId
	 * @param actionBarHeight
	 */
	public static void setStatusBarColorWithResourceId(Activity activity, int colorResId, int actionBarHeight) {
		setStatusBarColor(activity, ContextCompat.getColor(activity, colorResId), actionBarHeight);
	}

	/**
	 *
	 * @param activity
	 * @param color
	 */
	public static void setStatusBarColor(Activity activity, int color) {
		setStatusBarColor(activity, color, 0);
	}

	/**
	 *
	 * @param activity
	 * @param color
	 * @param actionBarHeight
	 */
	public static void setStatusBarColor(Activity activity, int color, int actionBarHeight) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = activity.getWindow();

			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

			window.setStatusBarColor(color);
		}
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window w = activity.getWindow();
			w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			//
			//status bar height
			int statusBarHeight = getStatusBarHeight(activity);

			View view = new View(activity);
			view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			view.getLayoutParams().height = statusBarHeight + actionBarHeight;

			FrameLayout frameLayout = (FrameLayout) w.getDecorView().findViewById(android.R.id.content);
			ViewGroup parent = ((ViewGroup) w.getDecorView());
			parent.addView(view);
			view.setBackgroundColor(color);
		}
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Activity context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	private static String getSystemProperty(String name) {
		try {
			Class systemPropertyClass = Class.forName("android.os.SystemProperties");
			return (String) systemPropertyClass.getMethod("get", new Class[]{String.class}).invoke(systemPropertyClass, new Object[]{name});
		}
		catch (Exception ex) {
		}
		return "";
	}

	/*
	// Build.FINGERPRINT.startsWith("generic")
	//            || Build.FINGERPRINT.startsWith("unknown")
	//					|| Build.MODEL.contains("google_sdk")
	//					|| Build.MODEL.contains("Emulator")
	//					|| Build.MODEL.contains("Android SDK built for x86")
	//					|| Build.MANUFACTURER.contains("Genymotion")
	//					|| (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
	//					|| "google_sdk".equals(Build.PRODUCT);
	*/
	/**
	 *
	 *
	 * @return
	 */
	public static boolean isEmulator() {
//		boolean goldfish = getSystemProperty("ro.hardware").contains("goldfish");
//		boolean emu = getSystemProperty("ro.kernel.qemu").length() > 0;
//		boolean sdk = getSystemProperty("ro.product.model").equals("sdk");
//		return goldfish || emu || sdk;
		return Build.FINGERPRINT.startsWith("generic")
		            || Build.FINGERPRINT.startsWith("unknown")
							|| Build.MODEL.contains("google_sdk")
							|| Build.MODEL.contains("Emulator")
							|| Build.MODEL.contains("Android SDK built for x86")
							|| Build.MANUFACTURER.contains("Genymotion")
							|| (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
							|| "google_sdk".equals(Build.PRODUCT);
	}

	public static void fixCocurrentTimeoutException()
            throws ClassNotFoundException,
            NoSuchMethodException,
            NoSuchFieldException,
            IllegalAccessException,
            InvocationTargetException {
        Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");

        Method method = clazz.getSuperclass().getDeclaredMethod("stop");
        method.setAccessible(true);

        Field field = clazz.getDeclaredField("INSTANCE");
        field.setAccessible(true);

        method.invoke(field.get(null));
	}

	/**
	 * https://stackoverflow.com/questions/6243452/how-to-know-if-the-phone-is-charging
	 *
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public static boolean isDeviceCharging(Context context) {
        boolean charging = false;

        /**
         * The following condition can't be met if the phone is charging via USB
         */
//        if (getAndroidVersion() >= 23) {
//            BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
//            charging = batteryManager.isCharging();
//        }
//        else
        {
            final Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean batteryCharge = status == BatteryManager.BATTERY_STATUS_CHARGING;

            int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

            if (batteryCharge) charging = true;
            if (usbCharge) charging = true;
            if (acCharge) charging = true;
        }

        return charging;
	}

	/**
	 *
	 * @param context
	 * @param what
	 * @return
	 */
	public static int getVolumeLevel(Context context, int what) {
		AudioManager am = (AudioManager) context.getSystemService(AUDIO_SERVICE);
		return am.getStreamVolume(what);
	}

	/**
	 * Get Volume level for media / music
	 *
	 * @param context
	 * @return
	 */
	public static int getMediaVolumeLevel(Context context) {
		return getVolumeLevel(context, AudioManager.STREAM_MUSIC);
	}

    /**
     *
     * @param context
     * @return
     */
	public static int getScreenBrightness(Context context)  {
        try {
            return Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {

        }
        return -1;
    }

	/**
	 *
	 * @param context
	 * @param bitmap
	 * @throws IOException
	 */
	@SuppressLint("ResourceType")
	public static void setSystemWallpaperWithImage(Context context, Bitmap bitmap) throws IOException {
		WallpaperManager wallpaperManager
				= WallpaperManager.getInstance(context);
		wallpaperManager.setBitmap(bitmap);
	}

	/**
	 *
	 * @param context
	 * @param resourceId
	 * @throws IOException
	 */
	@SuppressLint("MissingPermission")
	public static void setSystemWallpaperWithResource(Context context, int resourceId) throws IOException {
		WallpaperManager wallpaperManager
				= WallpaperManager.getInstance(context);
		wallpaperManager.setResource(resourceId);
	}

	/**
	 *
	 * @return
	 */
	public static int getNumberOfCpuCores() {
		if(Build.VERSION.SDK_INT >= 17) {
			return Runtime.getRuntime().availableProcessors();
		}
		else {
			// Use saurabh64's answer
			return getNumCoresOldPhones();
		}
	}

	/**
	 * <a href="https://stackoverflow.com/questions/30119604/how-to-get-the-number-of-cores-of-an-android-device">Reference</a>
	 *
	 * Gets the number of cores available in this device, across all processors.
	 * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
	 * @return The number of cores, or 1 if failed to get result
	 */
	private static int getNumCoresOldPhones() {
		//Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				//Check if filename is "cpu", followed by a single digit number
				if(Pattern.matches("cpu[0-9]+", pathname.getName())) {
					return true;
				}
				return false;
			}
		}

		try {
			//Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			//Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			//Return the number of cores (virtual CPU devices)
			return files.length;
		} catch(Exception e) {
			//Default to return 1 core
			return 1;
		}
	}

	/**
	 * Return system ABI
	 *
	 * @return
	 */
	public static String getAbi() {
		return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ?
				Build.SUPPORTED_ABIS[0]
				:
				Build.CPU_ABI;
	}

	/**
	 *
	 * @return
	 */
	public static int generateViewId() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? View.generateViewId() : (int) System.currentTimeMillis();
	}

    /**
     *
     * @param context
     * @param packageName
     * @return
     */
	public static boolean isAppRunning(Context context, String packageName) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		/**
		 * getRunningAppProcesses may not necessary return all the Apps due to Google's some concerns
		 */
		List<ActivityManager.RunningAppProcessInfo> processInfoList = activityManager.getRunningAppProcesses();
		if (processInfoList != null) {
			for (final ActivityManager.RunningAppProcessInfo processInfo : processInfoList)
				if (processInfo.processName.equals(packageName))
					return true;
		}
		return false;
	}

	/**
	 * Not all pages have toolbar
	 *
	 * @param context
	 * @param toolbar
	 * @param fontPath
	 */
	public static void changeToolbarTitleFont(Context context, Toolbar toolbar, String fontPath) {
		if (null != toolbar)
			for (int i = 0; i < toolbar.getChildCount(); i++) {
				View view = toolbar.getChildAt(i);
				if (view instanceof TextView) {
					TextView tv = (TextView) view;
					if (tv.getText().equals(toolbar.getTitle())) {
						setFontTo(context, tv, fontPath);
						break;
					}
				}
			}
	}

	/**
	 *
	 * @param context
	 * @param tv
	 * @param fontPath
	 */
	public static void setFontTo(Context context, TextView tv, String fontPath) {
		tv.setTypeface(Typeface.createFromAsset(context.getAssets(), fontPath));
	}
}
