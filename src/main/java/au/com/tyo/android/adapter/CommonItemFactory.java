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

public class CommonItemFactory<ItemType> extends InflaterFactory {

    private int titleResId = android.R.id.text1;

    private View.OnClickListener listener;

    public CommonItemFactory(Context context) {
        this(context, R.layout.image_text_list_cell);
    }

    public CommonItemFactory(Context context, int resId) {
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

    public CharSequence getText1(ItemType obj) {
        return obj.toString();
    }

    protected boolean isItemDisabled(ItemType obj) {
        return false;
    }

    protected CharSequence getAltText(ItemType obj) {
        return null;
    }

    public Drawable getImageViewDrawable(ItemType obj) {
        return null;
    }

    protected boolean shouldShowImageButton(ItemType obj) {
        return false;
    }

    public CharSequence getText2(ItemType obj) {
        return null;
    }

    protected View.OnClickListener getImageButtonOnClickListener(ItemType obj) {
        return null;
    }

    protected Drawable getImageButtonDrawable(ItemType obj) {
        return null;
    }

    protected boolean isSelectable(ItemType obj) {
        return false;
    }

    protected boolean isSelected(ItemType obj) {
        return false;
    }

    @Override
    public void bindData(ViewHolder holder, Object obj) {
        View view = holder.view;
        TextView tvTitle = (TextView) view.findViewById(getTitleResId());

        ItemType item = (ItemType) obj;

        String title = getText1(item).toString();

        if (null != tvTitle) {
            tvTitle.setText(title);

            // if (obj instanceof DisablableListItem) {
            //     boolean disabled = ((DisablableListItem) item).isDisabled();
                tvTitle.setEnabled(!isItemDisabled(item));
            // }
        }

        // containers
        View containerCol1 = view.findViewById(R.id.itl_image_view_container);

        if (null != containerCol1) {
            ImageView imgView = (ImageView) view.findViewById(R.id.itl_image_view);

            if (null != getImageViewDrawable(item) && null != imgView) {
                imgView.setVisibility(View.VISIBLE);
                imgView.setImageDrawable(getImageViewDrawable(item));
            }
            else {
                if (null != imgView)
                    imgView.setVisibility(View.GONE);
                imgView = null;
            }

            CharSequence altText = getAltText(item);
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
                tvAlt = null;
            }

            if ((null != tvAlt && tvAlt.getVisibility() == View.VISIBLE) || (null != imgView && imgView.getVisibility() == View.VISIBLE))
                containerCol1.setVisibility(View.VISIBLE);
            else
                containerCol1.setVisibility(View.GONE);
        }


        View containerCol3 = view.findViewById(R.id.itl_image_button_container);

        if (null != containerCol3) {
            ImageView imgButton = (ImageView) view.findViewById(R.id.itl_image_button);
            Drawable imgButtonDrawable = getImageButtonDrawable(item);
            //
            if (imgButton != null && imgButtonDrawable != null) {
                imgButton.setImageDrawable(imgButtonDrawable);
            }

            if (shouldShowImageButton(item)) {
                containerCol3.setVisibility(View.VISIBLE);

                View.OnClickListener aListener = getImageButtonOnClickListener(item);
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
            CharSequence text2 = getText2(item);
            if (text2 != null) {
                tv2.setText(text2);
                tv2.setVisibility(View.VISIBLE);
            }
            else
                tv2.setVisibility(View.GONE);
        }
    }

}
