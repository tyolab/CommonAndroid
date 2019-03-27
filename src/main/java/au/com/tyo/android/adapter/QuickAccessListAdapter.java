package au.com.tyo.android.adapter;

import android.content.Context;
import androidx.annotation.NonNull;

import java.util.List;

public class QuickAccessListAdapter extends ListWithHeadersAdapter {

    public QuickAccessListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public QuickAccessListAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public QuickAccessListAdapter(@NonNull Context context, int resource, @NonNull ListItemViewType[] objects) {
        super(context, resource, objects);
    }

    public QuickAccessListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull ListItemViewType[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public QuickAccessListAdapter(@NonNull Context context, int resource, @NonNull List<ListItemViewType> objects) {
        super(context, resource, objects);
    }

    public QuickAccessListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<ListItemViewType> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public void initialize(String fullListTitle, List fullList, String quickAccessTitle, List quickAccess, boolean addSeparator, int[] selected) {
        clear();
        setSelected(selected);

        if (null != quickAccess) {
            if (null != quickAccessTitle)
                add(new SectionTitleItem(quickAccessTitle));

            addAll(quickAccess);
        }

        if (addSeparator && getCount() > 0)
            add(new SeparatorItem());

        if (null != fullListTitle)
            add(new SectionTitleItem(fullListTitle));

        if (null != fullList)
            addAll((fullList));
    }
}
