/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

import au.com.tyo.android.R;

public class CommonListView extends RelativeLayout implements OnScrollListener {

    private int listViewResId;
	
	protected ListView list;
	
	private int lastVisible;
	
	private onLastItemVisibleListener lastItemVisibleListener;

	private boolean disallowParentTouch;
	
	public CommonListView(Context context) {
		super(context);
		init(context);
	}

	public CommonListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@SuppressLint("NewApi")
	public CommonListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
//		createListView();
		lastItemVisibleListener = null;
        listViewResId = R.layout.list_view;
		disallowParentTouch = false;
	}

	public boolean isDisallowParentTouch() {
		return disallowParentTouch;
	}

	public void setDisallowParentTouch(boolean disallowParentTouch) {
		this.disallowParentTouch = disallowParentTouch;
	}

	public void setListViewResId(int listViewResId) {
        this.listViewResId = listViewResId;
    }

    public static interface onLastItemVisibleListener {
		
		public void onLastItemVisible();
		
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

        findListView(R.id.list_view);
		// setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
	}
	
	public void setOnLastItemVisibleListener(onLastItemVisibleListener listener) {
		this.lastItemVisibleListener = listener;
	}

	public ListAdapter getAdapter() {
		return list.getAdapter();
	}
	
	public void setAdapter(ListAdapter adapter) {
		list.invalidateViews();
		list.setAdapter(adapter);
	}

	public void createListView() {
		createListView(listViewResId);
	}
	
	public void createListView(int listViewResId) {
		list = createListView(this.getContext(), this, listViewResId);
		list.requestFocus();
		list.setOnScrollListener(this);
	}

    public void findListView() {
        findListView(R.id.list_view);
    }
	
	public void findListView(int resId) {
		list = (ListView) findViewById(resId);
		if (null != list)
			list.setOnScrollListener(this);
	}
	
	public static ListView createListView(Context context, ViewGroup parent, int resId) {
        LayoutInflater factory = LayoutInflater.from(context);
        ListView list = (ListView) factory.inflate(resId, null);
        if (null != parent)
        	parent.addView(list);
        return list;
	}

	public static boolean doesItemsFitInScreen(ListView list) {
		if (list.getChildCount() == 0)
			return true;
		int last = list.getLastVisiblePosition();
		return isLastItemVisible(list.getCount(), last) && isLastItemVisible(list);
	}
	
	public static boolean isLastItemVisible(ListView list) {
		return list.getChildAt(list.getLastVisiblePosition()).getBottom() <= list.getHeight();
	}
	
	public static boolean isLastItemVisible(int numberOfItems, int lastVisible) {
		return (lastVisible == numberOfItems - 1);
	}
	
	public boolean doesItemsFitInScreen() {
		lastVisible = list.getLastVisiblePosition();
		return isLastItemVisible(list.getCount(), lastVisible) && isLastItemVisible(list);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView lv, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		switch(lv.getId()) {
        	case android.R.id.list:
        		lastVisible = list.getLastVisiblePosition();
        		
        		final int lastItem = firstVisibleItem + visibleItemCount;
        		if (lastItem == totalItemCount) {
        			if (null != lastItemVisibleListener)
        				lastItemVisibleListener.onLastItemVisible();
        		}
        		break;
		}
	}

	/**
	 * Remove list divider
	 *
	 */
	public void removeDivider() {
		list.setDivider(null);
		list.setDividerHeight(0);
	}

	public void refresh() {
		list.invalidateViews();
		list.refreshDrawableState();
	}
	
	public ListView getListView() {
        if (null == list) {
            createListView();
            // findListView();
        }
		return list;
	}
	
	public void setSingleChoiceModeOn() {
		if (null != list)
			list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return disallowParentTouch;
	}

	/**
	 * It is not a perfect solution particularly when list has different items
     *
     * assuming the item with same class name has the same height
	 *
	 * @param listView
	 */
	public static void setHeightAsChildren(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        int count = adapter.getCount();
        Map<String, View> viewMap = new HashMap<>();
        View contertView = null;
        for (int i = 0; i < count; i++) {
            Object item = adapter.getItem(i);

            String clsName = item.getClass().getName();

            contertView = viewMap.get(clsName);
            if (contertView == null) {
                view = adapter.getView(i, null, listView);

                /*
                if (i == 0)
                    view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

                 */

                view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
                viewMap.put(clsName, view);
            }
            else
                view = contertView;
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
