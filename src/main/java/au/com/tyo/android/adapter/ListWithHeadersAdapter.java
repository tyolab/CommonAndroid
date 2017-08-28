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

    protected ListSectionHeaderFactory sectionHeaderFactory;

    protected ListItemFactory itemFactory;

    protected ListFooterFactory footerFactory;

    public enum ItemType {
        ITEM, SECTION_HEADER, FOOTER, HEADER /* better not to implement header here */
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

    public ListFooterFactory getFooterFactory() {
        return footerFactory;
    }

    public void setFooterFactory(ListFooterFactory footerFactory) {
        this.footerFactory = footerFactory;
    }

    protected void onCreate(Context context, int resource) {
        sectionHeaderFactory = new ListSectionHeaderFactory(context);
        itemFactory = new ListItemFactory(context, resource);
    }

    @Override
    public int getCount() {
        return super.getCount() + (null != footerFactory ? 1 : 0);
    }

    @Override
    public int getViewTypeCount() {
        return ItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    public void addFooter(int resource) {
        addFooter(resource, new ListFooterFactory.Footer());
    }

    public void addFooter(int resource, ListFooterFactory.Footer footer) {
        footerFactory = new ListFooterFactory(getContext(), resource);
        add(footer);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        InflaterFactory.ViewHolder holder = null;
        InflaterFactory factory = null;
        Object obj = null;
        ItemType itemType = ItemType.values()[getItemViewType(position)];
        obj = getItem(position);

        switch (itemType) {
            case ITEM:
                factory = itemFactory;
                break;
            case SECTION_HEADER:
                factory = sectionHeaderFactory;
                break;
            case FOOTER:
                if (null == footerFactory)
                    throw new IllegalArgumentException("A footer factory should be implemented");
                factory = footerFactory;
            default:
                throw new IllegalArgumentException("Unknown item type.");
        }

        holder = factory.getViewHolder(convertView, parent, obj);
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