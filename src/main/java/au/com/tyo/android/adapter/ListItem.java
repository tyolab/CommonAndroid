package au.com.tyo.android.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 3/8/17.
 */

public interface ListItem extends ListItemViewType {

    /**
     *
     * Column 1
     *
     */
    CharSequence getAltText();
    Drawable getImageViewDrawable();

    /**
     *
     * Column 2 - Title
     *
     */
    CharSequence getText1();


    /**
     *
     * Column 3
     *
     */
    View.OnClickListener getImageButtonOnClickListener();
    Drawable getImageButtonDrawable();
    boolean shouldShowImageButton();

    /**
     *
     * Row 2 / Column 3
     */
    CharSequence getText2();
}
