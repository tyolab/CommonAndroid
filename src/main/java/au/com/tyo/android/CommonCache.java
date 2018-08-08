/*
 * Copyright (C) 2017 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 */

package au.com.tyo.android;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import au.com.tyo.android.utils.CacheManager;
import au.com.tyo.io.IO;
import au.com.tyo.io.WildcardFileStack;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 10/7/17.
 */

public class CommonCache extends CacheManager<File> {

    public static final String LOG_TAG = "CommonCache";

    // already under a cache directory with the name of "cache"
    public CommonCache(Context context) {
        this(context, "");
    }

    public CommonCache(Context context, String subdir) {
        super(context, subdir);
    }

    public CommonCache(Context context, String[] subdirs) {
        this(context);

        for (String subdir : subdirs)
            makeDirectory(subdir);
    }

    public CommonCache(Context context, String subdir, CacheLocation location) {
        super(context, subdir, location);
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

    public String getCacheFilePathName(String name) {
       return getCacheDir() + File.separator + name;
    }

    public void makeDirectory(String dir) {
        new File(getCacheDir().getAbsolutePath() + File.separator + dir).mkdir();
    }

    public File createFile(String dir, String fileName) {
        String fullName = null;
        if (fileName.startsWith(File.separator))
            fullName = fileName;
        else {
            if (!android.text.TextUtils.isEmpty(dir)) {
                String dirName = getCacheFilePathName(dir);
                fullName = dirName + File.separator + fileName;
            }
            else
                fullName = getCacheFilePathName(fileName);
        }
        File file = new File(fullName);
        return file;
    }

    public File createFile(String fileName) {
        return createFile(null, fileName);
    }

    public Object read(String fileName) throws Exception {
        File file = createFile(fileName);
        if (file.exists())
            return IO.readObject(file);
        return null;
    }

    public String readText(File file) {
        if (file.exists())
            return new String(IO.readFileIntoBytes(file));
        return null;
    }

    public void write(String fileName, Object object) throws Exception {
        File file = createFile(fileName);
        IO.writeObject(object, file);
    }

    public void writeText(String fileName, String text) {
        File file = createFile(fileName);
        IO.writeFile(file, text);
    }

    public boolean exists(String fileName) {
        File file = createFile(fileName);
        return file.exists() && file.length() > 0;
    }

    public void delete(String fileName) {
        File file = createFile(fileName);
        file.delete();
    }

    public void touch(String fileName) {
        File file = createFile(fileName);
        file.setLastModified(Calendar.getInstance().getTimeInMillis());
    }

    public void save(String name, byte[] data) {
        File file = createFile(name);
        IO.writeFile(file.getAbsolutePath(), data);
    }

    public OutputStream openOutputStream(String name) throws FileNotFoundException {
        return new FileOutputStream(createFile(name));
    }

    public OutputStream openOutputStream(String dir, String name) throws FileNotFoundException {
        return new FileOutputStream(createFile(dir, name));
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
