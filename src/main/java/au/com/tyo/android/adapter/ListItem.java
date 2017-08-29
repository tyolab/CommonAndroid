package au.com.tyo.android.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 3/8/17.
 */

public interface ListItem extends ListItemViewType {

    Drawable getImageViewDrawable();
    CharSequence getText1();
    CharSequence getAltText();
    Drawable getImageButtonDrawable();
    View.OnClickListener getImageButtonOnClickListener();
    CharSequence getText2();

}
