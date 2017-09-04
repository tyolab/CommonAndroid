package au.com.tyo.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 28/8/17.
 */

public class ListFooterFactory extends InflaterFactory {

    public ListFooterFactory(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void bindData(ViewHolder holder, Object obj) {

    }

    public static class Footer implements ListItemViewType {
        @Override
        public int getViewType() {
            return ListWithHeadersAdapter.ItemType.FOOTER.ordinal();
        }
    }
}
