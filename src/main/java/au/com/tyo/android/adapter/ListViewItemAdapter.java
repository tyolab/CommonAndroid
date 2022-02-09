package au.com.tyo.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import au.com.tyo.android.R;

public class ListViewItemAdapter extends BaseAdapter {

    /**
     * for identification of a list when you have lots of them
     * particularly when you let controller to handle the on click listener
     */
    private int listId;

	protected List items;
	
	private int resId;

	private CommonItemFactory itemFactory;
	
	public ListViewItemAdapter(){
		this(null);
	}
	
	public ListViewItemAdapter(int resId){
		this(resId, null);
	}
	
	public ListViewItemAdapter(List items) {
		this(R.layout.image_text_list_cell, items);
	}
	
	public ListViewItemAdapter(int resId, List items) {
		super();
		this.resId = resId;
        this.listId = -1;
		setItems(items);

		init();
	}

	private void init() {

	}

	public CommonItemFactory getItemFactory() {
		return itemFactory;
	}

	public void setItemFactory(CommonItemFactory itemFactory) {
		this.itemFactory = itemFactory;
	}

	public List getItems() {
		return items;
	}

	@Override
	public int getCount() {
		return getItems() == null ? 0 : getItems().size();
	}

	public int size() { return getCount(); }

	@Override
	public Object getItem(int position) {
		if (position < 0 || position >= getItems().size())
			return null;
		return getItems().get(position);
	}

	public Object get(int position) { return getItem(position); }

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setItems(List obj) {
        if (null == obj)
            return;

        if (null == getItems())
			this.items = new ArrayList();

        this.items.addAll(obj);
	}
	
	public void add(Object item) {
		if (getItems() == null)
			items = new ArrayList();

		getItems().add(0, item);
	}

	public void pushItem(Object item) { add(item); }
	
	public void removeItem(Object item) {
		getItems().remove(item);
	}

	public void remove(Object item) {
		removeItem(item);
	}
	
	protected View inflate(ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(resId, null);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object obj = get(position);

		if (null == itemFactory)
			itemFactory = new CommonItemFactory(parent.getContext(), resId);

        return itemFactory.getViewHolder(convertView, parent, obj).view;
	}
	
	public void clear() {
        if (null != items) {
            getItems().clear();
            this.notifyDataSetChanged();
        }
	}

    public void replaceWith(int i, Object obj) {
		getItems().set(i, obj);
    }
}
