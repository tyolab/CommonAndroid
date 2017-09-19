package au.com.tyo.android.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 11/8/17.
 */

public class ThreeColumnsItem implements DisablableListItem {

    private Object id;

    private Drawable leftMostDrawable;

    private Drawable rightMostDrawable;

    private String middleText;

    private String rightMostText;

    private String altText = null;

    private boolean disabled = false;

    public ThreeColumnsItem(Drawable leftMostDrawable, Drawable rightMostDrawable, String middleText, Object id) {
        this.leftMostDrawable = leftMostDrawable;
        this.rightMostDrawable = rightMostDrawable;
        this.middleText = middleText;
        this.id = id;
    }

    public ThreeColumnsItem(Drawable leftMostDrawable, Drawable rightMostDrawable, String middleText) {
        this.leftMostDrawable = leftMostDrawable;
        this.rightMostDrawable = rightMostDrawable;
        this.middleText = middleText;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
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

    public void update() {
        // no ops
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
