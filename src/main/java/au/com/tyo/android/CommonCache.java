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
import au.com.tyo.io.WildcardFileStack;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 10/7/17.
 */

public class CommonCache extends CacheManager<File> {

    public static final String LOG_TAG = "CommonCache";

    public CommonCache(Context context) {
        super(context, "cache");
    }

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

    private File createFile(String fileName) {
        String fullName = getCacheDir() + File.separator + fileName;
        File file = new File(fullName);
        return file;
    }

    public Object read(String fileName) throws Exception {
        File file = createFile(fileName);
        if (file.exists())
            return IO.readObject(file);
        return null;
    }

    public void write(String fileName, Object object) throws Exception {
        File file = createFile(fileName);
        IO.writeObject(object, file);
    }

    public boolean exists(String fileName) {
        File file = createFile(fileName);
        return file.exists() && file.length() > 0;
    }

    public void delete(String fileName) {
        File file = createFile(fileName);
        file.delete();
    }

    public void save(String name, byte[] data) {
        File file = createFile(name);
        IO.writeFile(file.getAbsolutePath(), data);
    }

    public void clear() {
        try {
            WildcardFileStack fileStack = new WildcardFileStack(getCacheDir());
            fileStack.setToListAllFiles(true);
            fileStack.listFiles();

            File file;
            while ((file = fileStack.next()) != null) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
