/*
 * Copyright (c) 2019. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.android.adapter;

import android.content.Context;

public class DisablableItemFactory extends ListItemFactory<DisablableListItem> {

    public DisablableItemFactory(Context context) {
        super(context);
    }

    public DisablableItemFactory(Context context, int resId) {
        super(context, resId);
    }

    protected boolean isItemDisabled(DisablableListItem listItem) {
        return listItem.isDisabled();
    }
}
