package au.com.tyo.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.tyo.android.R;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 3/8/17.
 */

public class ListItemFactory extends InflaterFactory {

    private View.OnClickListener listener;

    public ListItemFactory(Context context) {
        this(context, R.layout.image_text_list_cell);
    }

    public ListItemFactory(Context context, int resId) {
        super(context, resId);
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    /**

     define this in the listitem implementation

        @Override
        public int getViewType() {
            return ListWithHeadersAdatper.ItemType.ITEM.ordinal();
        }

     */

    @Override
    public void bindData(ViewHolder holder, Object obj) {
        View view = holder.view;
        TextView tvTitle = (TextView) view.findViewById(android.R.id.text1);

        if (obj instanceof ListItem){
            ListItem item = (ListItem) obj;

            if (null != tvTitle) {
                tvTitle.setText(item.getText1());
            }

            // containers
            View containerCol1 = view.findViewById(R.id.itl_image_view_container);

            if (null != containerCol1) {
                ImageView imgView = (ImageView) view.findViewById(R.id.itl_image_view);
                if (null != imgView && null != item.getImageViewDrawable())
                    imgView.setImageDrawable(item.getImageViewDrawable());

                CharSequence altText = item.getAltText();
                TextView tvAlt = (TextView) view.findViewById(R.id.itl_image_alt);
                if (null != tvAlt && null != altText) {
                    tvAlt.setText(altText);
                    tvAlt.setVisibility(View.VISIBLE);

                    if (null != imgView)
                        imgView.setVisibility(View.GONE);
                }
                else {
                    imgView.setVisibility(View.VISIBLE);

                    if (tvAlt != null)
                        tvAlt.setVisibility(View.GONE);
                }

                if (null != tvAlt || null != imgView)
                    containerCol1.setVisibility(View.VISIBLE);
            }


            View containerCol3 = view.findViewById(R.id.itl_image_button_container);

            if (null != containerCol3) {
                ImageView imgButton = (ImageView) view.findViewById(R.id.itl_image_button);
                //
                if (imgButton != null) {
                    if (null != item.getImageButtonOnClickListener())
                        imgButton.setOnClickListener(item.getImageButtonOnClickListener());
                    else if (null != listener)
                        imgButton.setOnClickListener(listener);

                    if (null != item.getImageButtonDrawable())
                        imgButton.setImageDrawable(item.getImageButtonDrawable());
                }

                if (null != imgButton)
                    containerCol3.setVisibility(View.VISIBLE);
            }

            CharSequence text2 = item.getText2();

            if (text2 != null) {
                TextView tv2 = (TextView) view.findViewById(android.R.id.text2);
                tv2.setText(text2);
            }
        }
        else {
            tvTitle.setText(obj.toString());
        }
    }
}
