package au.com.tyo.android.adapter;

import android.content.Context;

import au.com.tyo.android.R;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 28/8/17.
 */

public class ListSeparatorFactory extends InflaterFactory {

    public ListSeparatorFactory(Context context) {
        super(context, R.layout.list_separator);
    }

    @Override
    public void bindData(ViewHolder holder, Object obj) {

    }

    public static class Header implements ListItemViewType {
        @Override
        public int getViewType() {
            return ListWithHeadersAdapter.ItemType.SEPARATOR.ordinal();
        }
    }
}
