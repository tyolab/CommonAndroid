package au.com.tyo.android.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.io.Cache;
import au.com.tyo.io.FileUtils;

/**
 *
 * Cache content from the cloud
 *
 * @param <FileType>
 */

public abstract class CacheManager<FileType> extends Cache<FileType> {

	public static final String DEFAULT_SUBDIR_NAME = "app";

    public static final long CACHE_LIFE_SPAN_DEFAULT = TimeUnit.DAYS.toMillis(28);

    public static final long CACHE_LIFE_SPAN_INFINITE = -1;

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

	protected CacheLocation location;

	private boolean usePackageNameAsRootFolder;

	/**
	 * The lifespan of cache
	 */
	private long cacheSpan = -1; // infinite
	
	public CacheManager() {
		this(null, DEFAULT_SUBDIR_NAME);
	}
	
	public CacheManager(Context context) {
		this(context, DEFAULT_SUBDIR_NAME);
	}

	public CacheManager(Context context, CacheLocation location) {
		this(context, DEFAULT_SUBDIR_NAME, location);
	}

	public CacheManager(Context context, String subdir) {
		this(context, subdir, CacheLocation.SYSTEM_CACHE);
	}
	
	public CacheManager(Context context, String subdir, CacheLocation location) {
		this.context = context;
		this.subDirStr = subdir;
		this.location = location;

		usePackageNameAsRootFolder = false;

        setupCacheDir();
        // cacheSpan = DEFAULT_CACHE_LIFE_SPAN;
	}

    protected void setupCacheDir() {
        cacheDir = this.getCacheDirectoryFromLocation();

        if (cacheDir != null) {
            if (!cacheDir.exists())
                cacheDir.mkdirs();

            if (cacheDir.exists())
                cacheEnabled = true;
            else
                cacheEnabled = false;
        }
        else
            cacheEnabled = false;
    }

    public long getCacheSpan() {
		return cacheSpan;
	}

	public void setCacheSpan(long cacheSpan) {
		this.cacheSpan = cacheSpan;
	}

	public Context getContext() {
		return context;
	}

    public File getCacheDir() {
        return cacheDir;
    }

    public File getCacheDirectoryFromLocation() {
	    return getCacheDirectoryFromLocation(subDirStr);
    }

    public File getCacheDirectoryFromLocation(String subDir) {
		if (context != null) {
			switch (location) {
				case SYSTEM_CACHE:
				default:
					return getCacheDirectoryFromLocation(context);
				case SYSTEM_DATA:
					return getDataDirectory(context, subDir);
				case EXTERNAL_STORAGE:
                    /**
                     * OK, not all the phone has external storage
                     */
                    File externalFileDir = getCacheDirectoryFromExternalStorage(context, subDir, usePackageNameAsRootFolder);
                    if (null == externalFileDir) {
                        externalFileDir = getDataDirectory(context, subDir);
                        location = CacheLocation.SYSTEM_DATA;
                    }
					return externalFileDir;
			}
		}

		return null;
	}
	
	public File getCacheDirectoryFromLocation(Context refContext) {
		return getCacheDirectoryFromLocation(refContext, subDirStr);
	}

	public boolean isUsePackageNameAsRootFolder() {
		return usePackageNameAsRootFolder;
	}

	public void setUsePackageNameAsRootFolder(boolean usePackageNameAsRootFolder) {
		this.usePackageNameAsRootFolder = usePackageNameAsRootFolder;
	}

	/**
	 *
	 * @param refContext
	 * @param subDirStr
	 * @return
	 */
	public static File getCacheDirectoryFromExternalStorage(Context refContext, String subDirStr, boolean usePackageNameAsRootFolder) {
		String sdState = "";
		try {
			sdState = android.os.Environment.getExternalStorageState();
		}
		catch (Exception ex) {
			Log.e(LOG_TAG, "Unable to get external storage state");
			return null;
		}
		File cacheDir = null;
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();

			cacheDir = new File(sdDir,
					(usePackageNameAsRootFolder ? ("Android" + File.separator + DEFAULT_SUBDIR_NAME + File.separator + AndroidUtils.getPackageName(refContext)) : "") +
					File.separator+ subDirStr);
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

	public static File getCacheDirectoryFromLocation(Context refContext, String subDirStr){
		File cacheDir = null;

		cacheDir = new File(refContext.getCacheDir(),  subDirStr);

		return cacheDir;
	}

    /**
     * 
     * @param refContext
     * @param subDirStr
     * @return
     */
	public static File getDataDirectory(Context refContext, String subDirStr){
		File cacheDir = null;

		cacheDir = new File(refContext.getFilesDir(),  subDirStr);

		return cacheDir;
	}

	/**
	 *
	 * @return
	 */
	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	/**
	 *
	 * @param cacheEnabled
	 */
	public void setCacheEnabled(boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}

	/**
	 *
	 * @param url
	 * @return
	 */
	public static String urlHashCodeToString(String url) {
		if (url != null && url.length() > 0)
			return String.valueOf(url.hashCode());
		return "";
	}
	
	/**
	 * 
	 * @param location
	 * @param readingOrWriting - true for reading, false for writing
	 * @return
	 */
	public File locationToFile(String location, boolean readingOrWriting) {
	   	 String filename = urlHashCodeToString(location);
	   	 File f = new File(getCacheDirectoryFromLocation(), filename);
	   	 return f;
	}
	
	public FileType fileCheck(String location) throws Exception {
		return loadCache(location);
	}

	/**
	 *
	 * @param location
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public FileType loadCache(String location) throws Exception {
		return load(location);
	}

	/**
	 *
	 * @param location
	 * @return
	 * @throws Exception
	 */
	public FileType load(String location) throws Exception {
        File f = locationToFile(location, true);

        if (f.exists()) {
            long lastModified = f.lastModified();
            long now = new Date().getTime();
			long gap = (now - lastModified);
            if (cacheSpan > -1 && gap > cacheSpan) {
                f.delete();
                return null;
            }
        }

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
			FileUtils.delete(getCacheDirectoryFromLocation());
		}
		catch (Exception ex) {
			Log.e(LOG_TAG, "failed to clean up cache with error " + null != ex.getMessage() ? ex.getMessage() : "unknown");
		}
	}

	public static String createTimestampFileName(String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		return df.format(new Date());
	}

	public static String createTimestampFileName() {
		return createTimestampFileName("yyyyMMddHHmmss");
	}

	public boolean renameTo(String newName) {
		File oldDir = getCacheDir();
		if (null != oldDir) {
		    File newSub = getCacheDirectoryFromLocation(newName);

		    cacheDir = newSub;
		    if (!newSub.exists())
                return oldDir.renameTo(newSub);
		    return true;
        }
        else {
            subDirStr = newName;
            setupCacheDir();
            return true;
        }
	}
}
