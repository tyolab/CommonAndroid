package au.com.tyo.android.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 11/8/17.
 */

public class ThreeColumnsItem implements ListItem {

    private Drawable leftMostDrawable;

    private Drawable rightMostDrawable;

    private String middleText;

    private String rightMostText;

    private String altText;

    public ThreeColumnsItem(Drawable leftMostDrawable, Drawable rightMostDrawable, String middleText) {
        this.leftMostDrawable = leftMostDrawable;
        this.rightMostDrawable = rightMostDrawable;
        this.middleText = middleText;
    }

    public void setLeftMostDrawable(Drawable leftMostDrawable) {
        this.leftMostDrawable = leftMostDrawable;
    }

    public void setRightMostDrawable(Drawable rightMostDrawable) {
        this.rightMostDrawable = rightMostDrawable;
    }

    public void setRightMostText(String rightMostText) {
        this.rightMostText = rightMostText;
    }

    public void setMiddleText(String middleText) {
        this.middleText = middleText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    @Override
    public Drawable getImageViewDrawable() {
        return leftMostDrawable;
    }

    @Override
    public CharSequence getText1() {
        return middleText;
    }

    @Override
    public Drawable getImageButtonDrawable() {
        return rightMostDrawable;
    }

    @Override
    public View.OnClickListener getImageButtonOnClickListener() {
        return null;
    }

    @Override
    public CharSequence getText2() {
        return rightMostText;
    }

    @Override
    public int getViewType() {
        return ListWithHeadersAdapter.ItemType.ITEM.ordinal();
    }

    @Override
    public CharSequence getAltText() {
        return altText;
    }
}
