package au.com.tyo.android.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 3/8/17.
 *
 * Credits: https://stackoverflow.com/questions/13590627/android-listview-headers
 */

public class ListWithHeadersAdapter extends ArrayAdapter<ListItemViewType> {

    public static final int ITEM_TYPE_ITEM = 0;

    public static final int ITEM_TYPE_SECTION_HEADER = 1;

    public static final int ITEM_TYPE_HEADER = 2;

    public static final int ITEM_TYPE_FOOTER = 4;

    public static final int ITEM_TYPE_CUSTOM1 = 100;

    public static final int ITEM_TYPE_CUSTOM6 = 600;

    public static final int ITEM_TYPE_CUSTOM2 = 200;

    public static final int ITEM_TYPE_CUSTOM3 = 300;

    public static final int ITEM_TYPE_CUSTOM4 = 400;

    public static final int ITEM_TYPE_CUSTOM5 = 500;

    protected ListSectionHeaderFactory sectionHeaderFactory;

    protected ListItemFactory itemFactory;

    protected ListFooterFactory footerFactory;

    protected Map<ItemType, InflaterFactory> factoryMap;

    public interface ItemValue<T> {
        T valueOf();
    }

    public enum ItemType implements ItemValue<Integer> {
        ITEM {
            @Override
            public Integer valueOf() {
                return ITEM_TYPE_ITEM;
            }
        }, SECTION_HEADER {
            @Override
            public Integer valueOf() {
                return ITEM_TYPE_SECTION_HEADER;
            }
        }, FOOTER {
            @Override
            public Integer valueOf() {
                return ITEM_TYPE_FOOTER;
            }
        }, HEADER {
            @Override
            public Integer valueOf() {
                return ITEM_TYPE_HEADER;
            }
        } /* better not to implement header here */
        ,
        CUSTOM1 {
            @Override
            public Integer valueOf() {
                return ITEM_TYPE_CUSTOM1;
            }
        },
        CUSTOM2 {
            @Override
            public Integer valueOf() {
                return ITEM_TYPE_CUSTOM2;
            }
        },
        CUSTOM3 {
            @Override
            public Integer valueOf() {
                return ITEM_TYPE_CUSTOM3;
            }
        },
        CUSTOM4 {
            @Override
            public Integer valueOf() {
                return ITEM_TYPE_CUSTOM4;
            }
        },
        CUSTOM5 {
            @Override
            public Integer valueOf() {
                return ITEM_TYPE_CUSTOM5;
            }
        },
        CUSTOM6 {
            @Override
            public Integer valueOf() {
                return ITEM_TYPE_CUSTOM6;
            }
        }

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

    public ListSectionHeaderFactory getSectionHeaderFactory() {
        return sectionHeaderFactory;
    }

    public void setSectionHeaderFactory(ListSectionHeaderFactory sectionHeaderFactory) {
        this.sectionHeaderFactory = sectionHeaderFactory;
    }

    public ListItemFactory getItemFactory() {
        return itemFactory;
    }

    public void setItemFactory(ListItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    public ListFooterFactory getFooterFactory() {
        return footerFactory;
    }

    public void setFooterFactory(ListFooterFactory footerFactory) {
        this.footerFactory = footerFactory;

        Object obj = getItem(getCount() - 1);
        if (!(obj instanceof ListFooterFactory.Footer)) {
            add(new ListFooterFactory.Footer());
        }
    }

    /**
     *
     * @param context
     * @param resource
     */
    protected void onCreate(Context context, int resource) {
        sectionHeaderFactory = new ListSectionHeaderFactory(context);
        itemFactory = new ListItemFactory(context, resource);
    }

    @Override
    public int getViewTypeCount() {
        return ItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = getItem(position);
        if (obj instanceof ListItemViewType)
            return ((ListItemViewType) obj).getViewType();
        return ItemType.ITEM.ordinal();
    }

    public void addFooter(int resource) {
        setFooterFactory(new ListFooterFactory(getContext(), resource));
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
                break;
            case CUSTOM1:
            case CUSTOM2:
            case CUSTOM3:
            case CUSTOM4:
            case CUSTOM5:
            case CUSTOM6:
                factory = getCustomFactory(itemType);
                break;
            default:
                factory = getCustomFactory(itemType);
                if (null == factory)
                    throw new IllegalArgumentException("Unknown item type.");
        }

        if (null != factory) {
            holder = factory.getViewHolder(convertView, parent, obj);
            return holder.view;
        }
        return null;
    }

    protected InflaterFactory getCustomFactory(ItemType itemType) {
        // do nothing
        return factoryMap.get(itemType);
    }

    public void addCustomFactory(ItemType itemType, InflaterFactory factory) {
        if (null == factoryMap)
            factoryMap = new HashMap<>();
        factoryMap.put(itemType, factory);
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