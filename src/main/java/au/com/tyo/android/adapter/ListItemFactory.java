/*
 * Copyright (c) 2019. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.android.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

public class ListItemFactory<ItemType extends ListItem> extends CommonItemFactory<ItemType> {

    public ListItemFactory(Context context) {
        super(context);
    }

    public ListItemFactory(Context context, int resId) {
        super(context, resId);
    }

    protected CharSequence getText1(ListItem listItem) {
        return listItem.getText1();
    }

    @Override
    protected CharSequence getAltText(ListItem obj) {
        return obj.getAltText();
    }

    @Override
    protected Drawable getImageViewDrawable(ListItem obj) {
        return obj.getImageViewDrawable();
    }

    @Override
    protected boolean shouldShowImageButton(ListItem obj) {
        return obj.shouldShowImageButton();
    }

    @Override
    protected CharSequence getText2(ListItem obj) {
        return obj.getText2();
    }

    @Override
    protected View.OnClickListener getImageButtonOnClickListener(ListItem obj) {
        return obj.getImageButtonOnClickListener();
    }

    @Override
    protected Drawable getImageButtonDrawable(ListItem obj) {
        return obj.getImageButtonDrawable();
    }
}
