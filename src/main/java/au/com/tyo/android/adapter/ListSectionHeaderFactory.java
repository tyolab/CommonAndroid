package au.com.tyo.android.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import au.com.tyo.android.R;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 3/8/17.
 */

public class ListSectionHeaderFactory extends InflaterFactory {

    private int backgroundColor = -1;

    public static class SectionHeader implements ListItem {

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

    @Override
    public View getView(View convertView, ViewGroup parent) {
        View view = super.getView(convertView, parent);
        view.setEnabled(false);
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
        if (backgroundColor > -1) {
            holder.view.setBackgroundColor(backgroundColor);
        }
        TextView text = (TextView) holder.view.findViewById(android.R.id.text1);
        text.setText(obj.toString());
    }
}
