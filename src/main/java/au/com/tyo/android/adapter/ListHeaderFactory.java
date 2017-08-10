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

public class ListHeaderFactory extends InflaterFactory {

    public static class SectionHeader implements ListItemViewType {

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
        public CharSequence getImageButtonDrawable() {
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
            return ListWithHeadersAdatper.ItemType.HEADER.ordinal();
        }
    }

    public ListHeaderFactory(Context context) {
        super(context);
    }

    /**

     define this in the listitem implementation
        @Override
        public int getViewType() {
            return ListWithHeadersAdatper.ItemType.HEADER.ordinal();
        }

     */

    @Override
    public View getView(View convertView, ViewGroup parent) {

        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.list_header, null);
        } else {
            view = convertView;
        }

        return view;
    }

    @Override
    public void bindData(View view, Object obj) {
        TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setText(obj.toString());
    }
}
