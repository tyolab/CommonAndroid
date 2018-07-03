package au.com.tyo.android.utils;

import android.content.Context;
import android.content.res.Resources;

public class ResourceUtils {

    public static String getStringByIdName(Context context, String name) {
        Resources res = context.getResources();
        String value = null;
        try {
            value = res.getString(res.getIdentifier(name, "string", context.getPackageName()));
        }
        catch (Exception ex) {
            // ResourceNotFound
        }
        return value;
    }

}
