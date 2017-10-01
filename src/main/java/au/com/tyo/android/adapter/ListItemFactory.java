package au.com.tyo.android.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.tyo.android.R;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 3/8/17.
 */

public class ListItemFactory extends InflaterFactory {

    private int titleResId = android.R.id.text1;

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

    public int getTitleResId() {
        return titleResId;
    }

    public void setTitleResource(int titleResId) {
        this.titleResId = titleResId;
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
        TextView tvTitle = (TextView) view.findViewById(getTitleResId());

        if (obj instanceof ListItem){
            ListItem item = (ListItem) obj;

            if (null != tvTitle) {
                tvTitle.setText(item.getText1());

                if (obj instanceof DisablableListItem)
                    tvTitle.setEnabled(!((DisablableListItem) item).isDisabled());
            }

            // containers
            View containerCol1 = view.findViewById(R.id.itl_image_view_container);

            if (null != containerCol1) {
                ImageView imgView = null;

                if (null != item.getImageViewDrawable()) {
                    imgView = (ImageView) view.findViewById(R.id.itl_image_view);
                    if (null != imgView && null != item.getImageViewDrawable())
                        imgView.setImageDrawable(item.getImageViewDrawable());
                }

                CharSequence altText = item.getAltText();
                TextView tvAlt = (TextView) view.findViewById(R.id.itl_image_alt);

                if (null != tvAlt && null != altText) {
                    tvAlt.setText(altText);

                    tvAlt.setVisibility(View.VISIBLE);
                    if (null != imgView)
                        imgView.setVisibility(View.GONE);
                }
                else  {
                    if (null != imgView)
                        imgView.setVisibility(View.VISIBLE);

                    if (tvAlt != null)
                        tvAlt.setVisibility(View.GONE);
                }

                if ((null != tvAlt && tvAlt.getVisibility() == View.VISIBLE) || (null != imgView && imgView.getVisibility() == View.VISIBLE))
                    containerCol1.setVisibility(View.VISIBLE);
                else
                    containerCol1.setVisibility(View.GONE);
            }


            View containerCol3 = view.findViewById(R.id.itl_image_button_container);

            if (null != containerCol3) {
                ImageView imgButton = (ImageView) view.findViewById(R.id.itl_image_button);
                Drawable imgButtonDrawable = item.getImageButtonDrawable();
                //
                if (imgButton != null) {
                    imgButton.setImageDrawable(imgButtonDrawable);
                }

                if (null != imgButton || null != imgButtonDrawable) {
                    containerCol3.setVisibility(View.VISIBLE);

                    View.OnClickListener aListener = item.getImageButtonOnClickListener();
                    if (null == aListener)
                        aListener = listener;

                    if (null != aListener) {
                        containerCol3.setClickable(true);
                        containerCol3.setOnClickListener(aListener);
                    }
                }
                else
                    containerCol3.setVisibility(View.GONE);
            }

            TextView tv2 = (TextView) view.findViewById(android.R.id.text2);
            if (null != tv2) {
                CharSequence text2 = item.getText2();
                if (text2 != null) {
                    tv2.setText(text2);
                    tv2.setVisibility(View.VISIBLE);
                }
                else
                    tv2.setVisibility(View.GONE);
            }
        }
        else {
            tvTitle.setText(obj.toString());
        }
    }
}
