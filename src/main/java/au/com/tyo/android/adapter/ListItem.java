package au.com.tyo.android.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 3/8/17.
 */

public interface ListItem {

    Drawable getImageViewDrawable();
    CharSequence getText1();
    CharSequence getImageButtonDrawable();
    View.OnClickListener getImageButtonOnClickListener();
    CharSequence getText2();

}
