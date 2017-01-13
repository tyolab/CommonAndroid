package au.com.tyo.android.utils;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import au.com.tyo.android.AndroidUtils;
import au.com.tyo.io.Cache;

public abstract class CacheManager<FileType> extends Cache<FileType> {
	
	private static final String LOG_TAG = "CacheManager";
	
	protected Map<String, SoftReference<FileType>> fileCache;
	
	protected Context context;
	
	protected String subDirStr;
	
	protected File cacheDir;
	
	protected boolean cacheEnabled;
	
	public CacheManager() {
		this(null, "data");
	}
	
	public CacheManager(Context context) {
		this(context, "data");
	}
	
	public CacheManager(Context context, String subdir) {
		this.context = context;
		this.subDirStr = subdir;
		
		cacheDir = this.getCacheDirectory();
		if (cacheDir != null && !cacheDir.exists())
			cacheDir.mkdirs();
		if (cacheDir.exists())
			cacheEnabled = true;
		else
			cacheEnabled = false;
	}
	
	public File getCacheDirectory() {
		if (context != null)
			return getCacheDirectory(context);
		return null;
	}
	
	public File getCacheDirectory(Context refContext) {
		return getCacheDirectory(refContext, subDirStr);
	}
	
	//our caching functions
	// Find the dir to save cached images
	public static File getCacheDirectory(Context refContext, String subDirStr){
		String sdState = android.os.Environment.getExternalStorageState();
		File cacheDir = null;
    
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();  
			
//			try {
				cacheDir = new File(sdDir, "Android" + File.separator + "data" + File.separator + AndroidUtils.getPackageName(refContext) + File.separator+ subDirStr);
//			} catch (NameNotFoundException e) {
				if (!cacheDir.exists())
					Log.e(LOG_TAG, "cannot access external sd card to create a package data directory");
//			}
		}
		else
			cacheDir = new File(refContext.getCacheDir(),  subDirStr);

		if(cacheDir != null && !cacheDir.exists())
			cacheDir.mkdirs();
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
	public File urlToFile(String url, boolean readingOrWriting) {
	   	 String filename = urlHashCodeToString(url);
	   	 File f = new File(getCacheDirectory(), filename);
	   	 return f;
	}
	
	public FileType fileCheck(String url) throws Exception {
		return loadCache(url);
	}
	
	public FileType loadCache(String url) throws Exception {
	   	 File f = urlToFile(url, true);
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
}
