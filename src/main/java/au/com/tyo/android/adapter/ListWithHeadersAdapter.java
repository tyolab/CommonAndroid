package au.com.tyo.android.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 3/8/17.
 *
 * Credits: https://stackoverflow.com/questions/13590627/android-listview-headers
 */

public class ListWithHeadersAdapter extends ArrayAdapter<ListItemViewType> {

    protected ListHeaderFactory headerFactory;

    protected ListItemFactory itemFactory;

    public enum ItemType {
        ITEM, HEADER
    }

    public ListWithHeadersAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        onCreate(context, resource);
    }

    public ListWithHeadersAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId) {
        super(context, resource, textViewResourceId);
        onCreate(context, resource);
    }

    public ListWithHeadersAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ListItemViewType[] objects) {
        super(context, resource, objects);
        onCreate(context, resource);
    }

    public ListWithHeadersAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull ListItemViewType[] objects) {
        super(context, resource, textViewResourceId, objects);
        onCreate(context, resource);
    }

    public ListWithHeadersAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ListItemViewType> objects) {
        super(context, resource, objects);
        onCreate(context, resource);
    }

    public ListWithHeadersAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<ListItemViewType> objects) {
        super(context, resource, textViewResourceId, objects);
        onCreate(context, resource);
    }

    protected void onCreate(Context context, int resource) {
        headerFactory = new ListHeaderFactory(context);
        itemFactory = new ListItemFactory(context, resource);
    }

    @Override
    public int getViewTypeCount() {
        return ItemType.values().length;

    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ItemType itemType = ItemType.values()[getItemViewType(position)];
        Object obj = getItem(position);

        InflaterFactory.ViewHolder holder = null;
        switch (itemType) {
            case ITEM:
                holder = itemFactory.getViewHolder(convertView, parent, obj);
                break;
            case HEADER:
                holder = headerFactory.getViewHolder(convertView, parent, obj);
                break;
        }
        return holder.view;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

}