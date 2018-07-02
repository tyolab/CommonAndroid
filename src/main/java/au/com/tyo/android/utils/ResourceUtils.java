package au.com.tyo.android.utils;

import android.content.Context;
import android.content.res.Resources;

public class ResourceUtils {

    public static String getStringByIdName(Context context, String name) {
        Resources res = context.getResources();
        return res.getString(res.getIdentifier(name, "string", context.getPackageName()));
    }

}
