/*
 * Copyright (C) 2017 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 */

package au.com.tyo.android;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import au.com.tyo.android.utils.CacheManager;
import au.com.tyo.io.IO;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 10/7/17.
 */

public class CommonCache extends CacheManager<File> {

    public static final String LOG_TAG = "CommonCache";

    public CommonCache(Context context, String subdir) {
        super(context, subdir);
    }

    public File outputStreamToFile(ByteArrayOutputStream stream) {
        return outputStreamToFile(stream, createTimestampFileName());
    }

    public File outputStreamToFile(ByteArrayOutputStream stream, String filename) {
        File file = new File(getCacheDir().getAbsolutePath() + File.separator + filename);
        try {
            IO.writeFile(file, stream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Unable to create cache file " + filename);
        }
        return file;
    }


}
