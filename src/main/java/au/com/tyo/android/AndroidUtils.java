package au.com.tyo.android;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;

public class AndroidUtils {
	
	public static final String LOG_TAG = "AndroidUtils";
	
	public static boolean doesPackageExist(String targetPackage, Context context){
		List<ApplicationInfo> packages;
		PackageManager pm;
		pm = context.getPackageManager();  
		packages = pm.getInstalledApplications(0);
		for (ApplicationInfo packageInfo : packages) 
			  if(packageInfo.packageName.equals(targetPackage)) 
			  	return true;
		
		return false;
	}

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
	
	public static int getAndroidVersion() {
		return android.os.Build.VERSION.SDK_INT; 
	}
	
	public static void exit(Activity activity) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		activity.startActivity(intent); 
		activity.finish();   
	}
	
	public static int getPackageVersionCode(Context context) {
		PackageInfo pInfo = null;
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG, "couldn't get package version");
		}
		return pInfo == null ? 1 : pInfo.versionCode;
	}
	
	public static String getPackageVersionName(Context context) {
		PackageInfo pInfo = null;
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG, "couldn't get package version");
		}
		return pInfo == null ? "" : pInfo.versionName;
	}	

	public static String getPackageName(Context context) {
		PackageInfo pInfo = null;
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG, "couldn't get package name");
		}
		return pInfo == null ? "" : pInfo.packageName;
	}
	
	private static final Pattern DIR_SEPORATOR = Pattern.compile("/");

	/**
	 * Raturns all available SD-Cards in the system (include emulated)
	 *
	 * Warning: Hack! Based on Android source code of version 4.3 (API 18)
	 * Because there is no standart way to get it.
	 * TODO: Test on future Android versions 4.4+
	 *
	 * @return paths to all available SD-Cards in the system (include emulated)
	 */
	public static String[] getStorageDirectories()
	{
	    // Final set of paths
	    final Set<String> rv = new HashSet<String>();
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
	            final String[] folders = DIR_SEPORATOR.split(path);
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
	    return rv.toArray(new String[rv.size()]);
	}
	
	public static double getStorageSizeInGigabytes(String path) {
		StatFs stat = new StatFs(path);
		double sdAvailSize =Double.valueOf(stat.getBlockCount())
		               * Double.valueOf(stat.getBlockSize());
		//One binary gigabyte equals 1,073,741,824 bytes.
		double gigaAvailable = sdAvailSize / 1073741824;
		return gigaAvailable;
	}
	
	public static double getStorageAvailableSizeInGigabytes(String path) {
		StatFs stat = new StatFs(path);
		double sdAvailSize =Double.valueOf(stat.getAvailableBlocks())
		               * Double.valueOf(stat.getBlockSize());
		//One binary gigabyte equals 1,073,741,824 bytes.
		double gigaAvailable = sdAvailSize / 1073741824;
		return gigaAvailable;
	}
	
	public static void hideAppIcon(Activity context) {
		PackageManager p = context.getPackageManager();
		p.setComponentEnabledSetting(context.getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	}
	
	public static void startApp(Context activity, String packageName) throws NameNotFoundException {
		startApp(activity, packageName, 0, null);
	}
	
	public static void startApp(Context activity, String packageName, int flags, Bundle extras) throws NameNotFoundException {
		Intent i;
		PackageManager manager = activity.getPackageManager();
	    i = manager.getLaunchIntentForPackage(packageName);
	    if (i == null)
	        throw new PackageManager.NameNotFoundException();
	    
//	    if (action != null) {
//	    	i.setAction(action);
//	    }
//	    else {
		    i.setAction(Intent.ACTION_MAIN);
//	    }
	    
	    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | flags);
	    i.addCategory(Intent.CATEGORY_LAUNCHER);
	    
//	    if (flags > 0)
//	    	i.setFlags(flags);
	    
	    if (extras != null)
	    	i.putExtras(extras);
	    
	    activity.startActivity(i);
	}
	
	public static void gotoMarket(Context context, String url) {
		openLinkWithDefaultAction(context, url);
	}
	
	public static void openLinkWithDefaultAction(Context context, String url) {
		Uri goUri = Uri.parse(url);

		Intent goIntent = new Intent(Intent.ACTION_VIEW, goUri);
		goIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(goIntent);
	}
	
	public static void openSystemFileBrowser(Context context) {
	     Intent intent = new Intent();
	     intent.setAction(Intent.ACTION_GET_CONTENT);
	     intent.setType("file/*");
	     context.startActivity(intent);	
	}
	
	public static int getActionBarHeight(Context context) {
		TypedValue tv = new TypedValue();
		int actionBarHeight = 0;
		if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
		    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}
	
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
	 * getting the account email addresses associated with the device 
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	public static List<String> getDeviceAccounts(Context context) {
		ArrayList list = new ArrayList<String>();
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(context).getAccounts();
		
		for (Account account : accounts) 
		    if (emailPattern.matcher(account.name).matches()) 
		        list.add(account.name);

		return list;
	}
	
	public static Uri getRawResourceUri(Context context, int resId) {
		return Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);
	}
	
	public static Uri getRawResourceUri(Context context, String resName) {
		return Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + resName);
	}
}
