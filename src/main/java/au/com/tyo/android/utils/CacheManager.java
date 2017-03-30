package au.com.tyo.android.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Map;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.io.Cache;
import au.com.tyo.io.FileUtils;

public abstract class CacheManager<FileType> extends Cache<FileType> {

	/**
	 * External storage can't be granteed
	 */
	public enum CacheLocation {
		SYSTEM_CACHE, SYSTEM_DATA, EXTERNAL_STORAGE
	}
	
	private static final String LOG_TAG = "CacheManager";
	
	protected Map<String, SoftReference<FileType>> fileCache;
	
	protected Context context;
	
	protected String subDirStr;
	
	protected File cacheDir;
	
	protected boolean cacheEnabled;

	CacheLocation location;
	
	public CacheManager() {
		this(null, "data");
	}
	
	public CacheManager(Context context) {
		this(context, "data");
	}

	public CacheManager(Context context, CacheLocation location) {
		this(context, "data", location);
	}

	public CacheManager(Context context, String subdir) {
		this(context, subdir, CacheLocation.SYSTEM_CACHE);
	}
	
	public CacheManager(Context context, String subdir, CacheLocation location) {
		this.context = context;
		this.subDirStr = subdir;
		this.location = location;
		
		cacheDir = this.getCacheDirectory();

		if (cacheDir != null && !cacheDir.exists())
			cacheDir.mkdirs();
		if (cacheDir.exists())
			cacheEnabled = true;
		else
			cacheEnabled = false;
	}
	
	public File getCacheDirectory() {
		if (context != null) {
			switch (location) {
				case SYSTEM_CACHE:
					default:
					return getCacheDirectory(context);
				case SYSTEM_DATA:
					return getDataDirectory(context, subDirStr);
				case EXTERNAL_STORAGE:
					return getCacheDirectoryFromExternalStorage(context, subDirStr);
			}
		}

		return null;
	}
	
	public File getCacheDirectory(Context refContext) {
		return getCacheDirectory(refContext, subDirStr);
	}

	/**
	 *
	 * @param refContext
	 * @param subDirStr
	 * @return
	 */
	public static File getCacheDirectoryFromExternalStorage(Context refContext, String subDirStr) {
		String sdState = "";
		try {
			android.os.Environment.getExternalStorageState();
		}
		catch (Exception ex) {
			Log.e(LOG_TAG, "Unable to get external storage state");
		}
		File cacheDir = null;
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();

			cacheDir = new File(sdDir, "Android" + File.separator + "data" + File.separator + AndroidUtils.getPackageName(refContext) + File.separator+ subDirStr);
			if (!cacheDir.exists())
				Log.e(LOG_TAG, "cannot access external sd card to create a package data directory");
		}
		return cacheDir;
	}

	/**
	 * Caching directory
	 * Find the dir to save cached images
	 * @param refContext
	 * @param subDirStr
	 * @return
	 */

	public static File getCacheDirectory(Context refContext, String subDirStr){
		File cacheDir = null;

		cacheDir = new File(refContext.getCacheDir(),  subDirStr);

		return cacheDir;
	}

	public static File getDataDirectory(Context refContext, String subDirStr){
		File cacheDir = null;

		cacheDir = new File(refContext.getFilesDir(),  subDirStr);

		return cacheDir;
	}
	
//	public File createFile(String filename) {
//		return new File(getCacheDirectory(), filename);
//	}
	
	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	public void setCacheEnabled(boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}

	public static String urlHashCodeToString(String url) {
		if (url != null && url.length() > 0)
			return String.valueOf(url.hashCode());
		return "";
	}
	
	/**
	 * 
	 * @param url
	 * @param readingOrWriting - true for reading, false for writing
	 * @return
	 */
	public File locationToFile(String url, boolean readingOrWriting) {
	   	 String filename = urlHashCodeToString(url);
	   	 File f = new File(getCacheDirectory(), filename);
	   	 return f;
	}
	
	public FileType fileCheck(String url) throws Exception {
		return loadCache(url);
	}
	
	public FileType loadCache(String url) throws Exception {
	   	 File f = locationToFile(url, true);
		  // Is the file in our memory cache?
		 FileType file = null;
		 
		 SoftReference<FileType> fileRef = (SoftReference<FileType>) fileCache.get(f.getPath());
		 
	   	  if(fileRef == null){
	   		  
	   		  file = read(f);
			  fileRef = new SoftReference<FileType>(file);
			  
			  if(file != null){
				  fileCache.put(f.getPath(), fileRef);
			  }
		  
		  }
	   	  
	   	  file = fileRef.get();

		 return file;
	}

	/**
	 * Clean up cache
	 */
	public void cleanup() {
		try {
			FileUtils.delete(getCacheDirectory());
		}
		catch (Exception ex) {
			Log.e(LOG_TAG, "failed to clean up cache with error " + null != ex.getMessage() ? ex.getMessage() : "unknown");
		}
	}
}
