/*
 * Copyright (c) 2019. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.android.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

public class DrawableUtils {

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

    public static Drawable createTintedVectorDrawable(Context context, int resId, int tint) {
        Drawable drawable =  getVectorDrawable(context, resId);
        return createTintedDrawable(drawable, tint);
    }

    public static Drawable createTintedDrawable(Drawable drawable, int tint) {
        DrawableCompat.setTint(drawable.mutate(), tint);
        return drawable;
    }
}
