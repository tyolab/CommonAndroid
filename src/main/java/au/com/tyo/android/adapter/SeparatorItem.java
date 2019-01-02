package au.com.tyo.android.adapter;

public class SeparatorItem implements ListItemViewType {
    @Override
    public int getViewType() {
        return ListWithHeadersAdapter.ITEM_TYPE_SEPARATOR;
    }
}
