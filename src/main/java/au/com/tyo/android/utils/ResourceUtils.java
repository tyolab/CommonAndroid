package au.com.tyo.android.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.TypedValue;

public class ResourceUtils {

    /**
     *
     * @param context
     * @param name
     * @return
     */
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

    /**
     *
     * @param context
     * @param name
     * @return
     */
    public static String[] getStringArrayByIdName(Context context, String name) {
        Resources res = context.getResources();
        String value[] = null;
        try {
            value = res.getStringArray(res.getIdentifier(name, "array", context.getPackageName()));
        }
        catch (Exception ex) {
            // ResourceNotFound
        }
        return value;
    }

    /**
     *
     * @param context
     * @param resId
     * @return
     */
    public static Drawable getVectorDrawable(Context context, int resId) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT  >= 21)
            drawable = VectorDrawableCompat.create(context.getResources(), resId, context.getTheme());
        else
            drawable = AppCompatResources.getDrawable(context, resId);
        return drawable;
    }

    public static int getAttributeColor(Context context, int resId) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(resId, typedValue, true);
        return typedValue.data;
    }
}
