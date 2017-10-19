package au.com.tyo.android.adapter;

import android.content.Context;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 28/8/17.
 */

public class ListHeaderFactory extends InflaterFactory {

    public ListHeaderFactory(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void bindData(ViewHolder holder, Object obj) {

    }

    public static class Header implements ListItemViewType {
        @Override
        public int getViewType() {
            return ListWithHeadersAdapter.ItemType.HEADER.ordinal();
        }
    }
}
