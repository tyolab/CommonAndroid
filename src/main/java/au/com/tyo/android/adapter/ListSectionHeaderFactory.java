package au.com.tyo.android.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import au.com.tyo.android.R;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 3/8/17.
 */

public class ListSectionHeaderFactory extends ListItemFactory {

    private Integer backgroundColor = null;

    private boolean enabled = false;

    public static class SectionHeader implements DisablableListItem {

        private String title;

        public SectionHeader(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public Drawable getImageViewDrawable() {
            return null;
        }

        @Override
        public CharSequence getText1() {
            return title;
        }

        @Override
        public Drawable getImageButtonDrawable() {
            return null;
        }

        @Override
        public View.OnClickListener getImageButtonOnClickListener() {
            return null;
        }

        @Override
        public CharSequence getText2() {
            return null;
        }

        @Override
        public int getViewType() {
            return ListWithHeadersAdapter.ItemType.SECTION_HEADER.ordinal();
        }

        @Override
        public String toString() {
            return getTitle();
        }

        @Override
        public CharSequence getAltText() {
            return null;
        }

        @Override
        public boolean isDisabled() {
            return true;
        }
    }
    public ListSectionHeaderFactory(Context context, int resId, int backgroundColor) {
        super(context, resId);
        this.backgroundColor = backgroundColor;
    }

    public ListSectionHeaderFactory(Context context) {
        super(context, R.layout.list_item_header);
    }

    public ListSectionHeaderFactory(Context context, int resource) {
        super(context, resource);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        View view = super.getView(convertView, parent);
        view.setOnClickListener(null);
        return view;
    }

    /**

     define this in the listitem implementation
        @Override
        public int getViewType() {
            return ListWithHeadersAdatper.ItemType.SECTION_HEADER.ordinal();
        }

     */


    @Override
    public void bindData(ViewHolder holder, Object obj) {
        super.bindData(holder, obj);

        if (backgroundColor != null) {
            holder.view.setBackgroundColor(backgroundColor);
        }

        if (obj instanceof DisablableListItem)
            holder.view.setEnabled(!((DisablableListItem) obj).isDisabled());
    }
}
