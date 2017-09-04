package au.com.tyo.android.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import au.com.tyo.utils.StringUtils;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 4/9/17.
 */

public class AssetsUtils {

    private static final String TAG = "AssetsUtils";

    public static Drawable getDrawable(Context context, String path, String name) {
        return getDrawable(context, path + File.separator + name);
    }

    public static Drawable getDrawable(Context context, String filename) {
        try {
            Drawable drawable = Drawable.createFromStream(context.getAssets().open(filename), null);
            return drawable;
        } catch (IOException e) {
            Log.e(TAG, StringUtils.exceptionStackTraceToString(e));
        }
        return null;
    }
}
