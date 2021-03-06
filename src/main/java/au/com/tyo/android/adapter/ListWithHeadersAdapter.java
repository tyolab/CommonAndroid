package au.com.tyo.android.adapter;

import android.content.Context;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 3/8/17.
 *
 * Credits: https://stackoverflow.com/questions/13590627/android-listview-headers
 */

public class ListWithHeadersAdapter extends ArrayAdapter {

    public static final int ITEM_TYPE_ITEM = 0;

    public static final int ITEM_TYPE_SECTION_HEADER = 1;

    public static final int ITEM_TYPE_HEADER = 2;

    public static final int ITEM_TYPE_FOOTER = 4;

    public static final int ITEM_TYPE_SEPARATOR = 8;

    public static final int ITEM_TYPE_CUSTOM1 = 100;

    public static final int ITEM_TYPE_CUSTOM6 = 600;

    public static final int ITEM_TYPE_CUSTOM2 = 200;

    public static final int ITEM_TYPE_CUSTOM3 = 300;

    public static final int ITEM_TYPE_CUSTOM4 = 400;

    public static final int ITEM_TYPE_CUSTOM5 = 500;

    protected ListSectionHeaderFactory sectionHeaderFactory;

    protected InflaterFactory itemFactory;

    protected ListSeparatorFactory separatorFactory;

    protected ListFooterFactory footerFactory;

    protected ListHeaderFactory headerFactory;

    protected Map<ItemType, InflaterFactory> factoryMap;

    protected int[] selected;

    /**
     * The real list for the actual items (== ITEM.ordial())
     */
    // protected List items;

    public ListWithHeadersAdapter(@NonNull Context context, int resource) {
        super(context, resource);

        init();
    }

    public ListWithHeadersAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);

        init();
    }

    public ListWithHeadersAdapter(@NonNull Context context, int resource, @NonNull Object[] objects) {
        super(context, resource, objects);

        init();
    }

    public ListWithHeadersAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull Object[] objects) {
        super(context, resource, textViewResourceId, objects);

        init();
    }

    public ListWithHeadersAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);

        init();
    }

    public ListWithHeadersAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List objects) {
        super(context, resource, textViewResourceId, objects);

        init();
    }

    private void init() {

    }

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
        }, SEPARATOR {
            @Override
            public Integer valueOf() {
                return ITEM_TYPE_SEPARATOR;
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

    public void setSelected(int[] selected) {
        this.selected = selected;
    }

    public ListSectionHeaderFactory getSectionHeaderFactory() {
        if (null == sectionHeaderFactory)
            setSectionHeaderFactory(new ListSectionHeaderFactory(getContext()));
        return sectionHeaderFactory;
    }

    public void setSectionHeaderFactory(ListSectionHeaderFactory sectionHeaderFactory) {
        this.sectionHeaderFactory = sectionHeaderFactory;
    }

    public InflaterFactory getItemFactory() {
        return itemFactory;
    }

    public void setItemFactory(InflaterFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    public ListFooterFactory getFooterFactory() {
        return footerFactory;
    }

    public ListSeparatorFactory getSeparatorFactory() {
        if (null == separatorFactory)
            separatorFactory = new ListSeparatorFactory(getContext());
        return separatorFactory;
    }

    public void setFooterFactory(ListFooterFactory footerFactory) {
        this.footerFactory = footerFactory;

        if (getCount() > 0) {
            Object obj = getItem(getCount() - 1);
            if (!(obj instanceof ListFooterFactory.Footer)) {
                add(new ListFooterFactory.Footer());
            }
        }
    }

    public void setHeaderFactory(ListHeaderFactory factory) {
        this.headerFactory = factory;

        if (getCount() > 0) {
            Object obj = getItem(0);
            if (!(obj instanceof ListHeaderFactory.Header)) {
                insert(new ListHeaderFactory.Header(), 0);
            }
        }
        else
            add(new ListHeaderFactory.Header());
    }

    /**
     *
     * @param context
     * @param resource
     */
    protected void onCreate(Context context, int resource) {
        sectionHeaderFactory = new ListSectionHeaderFactory(context);
        // itemFactory = new CommonItemFactory(context, resource);
    }

    @Override
    public int getViewTypeCount() {
        return ItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = getItem(position);
        return getItemViewType(obj);
    }

    protected int getItemViewType(Object obj) {
        if (obj instanceof ListItemViewType)
            return ((ListItemViewType) obj).getViewType();
        return ItemType.ITEM.ordinal();
    }

    public void addFooter(int resource) {
        setFooterFactory(new ListFooterFactory(getContext(), resource));
    }

    public void addHeader(int resource) {
        setHeaderFactory(new ListHeaderFactory(getContext(), resource));
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
                factory = getItemFactory();
                break;
            case SECTION_HEADER:
                factory = getSectionHeaderFactory();
                break;
            case SEPARATOR:
                factory = getSeparatorFactory();
                break;
            case FOOTER:
                if (null == footerFactory)
                    throw new IllegalArgumentException("A footer factory should be implemented");
                factory = footerFactory;
                break;
            case HEADER:
                if (null == headerFactory)
                    throw new IllegalArgumentException("A header factory should be implemented");
                factory = headerFactory;
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
                if (null == factory)
                    throw new IllegalArgumentException("Unknown item type.");
        }

        if (null != factory) {
            holder = factory.getViewHolder(convertView, parent, obj);
            return holder.view;
        }
        // throw new IllegalStateException("Cannot return a null view from the list adapter");
        return super.getView(position, convertView, parent);
    }

    protected InflaterFactory getCustomFactory(ItemType itemType) {
        // do nothing
        return factoryMap != null ? factoryMap.get(itemType) : null;
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

    public void replaceWith(int i, Object obj) {
        Object oldObj = getItem(i);
        this.insert((ListItemViewType) obj, i);
        remove((ListItemViewType) oldObj);
    }

    // public List getItems() {
    //     return items;
    // }
    //
    // public int getItemCount() { return null != items ? items.size() : 0; }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // public void setItems(List obj) {
    //     if (null == obj)
    //         return;
    //
    //     if (null == getItems())
    //         this.items = new ArrayList();
    //
    //     this.items.addAll(obj);
    // }

    // @Override
    // public void add(Object item) {
    //     // if (item instanceof ListItemViewType && ((ListItemViewType) item).getViewType() == ItemType.ITEM.ordinal()) {
    //     //     push(item);
    //     //     return;
    //     // }
    //
    //     super.insert(item, 0);
    // }

    // public void push(Object item) {
    //     setItem(item, 0);
    // }

    // public void setItem(Object item, int index) {
    //     if (((ListItemViewType) item).getViewType() != ItemType.ITEM.ordinal())
    //         throw new IllegalArgumentException("The object being inserted is not a valid item type");
    //
    //     if (getItems() == null)
    //         items = new ArrayList();
    //
    //     getItems().add(index, item);
    //     super.insert(item, index);
    // }

    // public void pushItem(Object item) { push(item); }
    //
    // public void removeItem(Object item) {
    //     getItems().remove(item);
    //     remove(item);
    // }
    //
    // public void clear() {
    //     if (null != items) {
    //         items.clear();
    //     }
    //     super.clear();
    // }

}