package au.com.tyo.android.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import au.com.tyo.android.R;

public class ListViewItemAdapter<ItemType> extends BaseAdapter {

	protected List<ItemType> items;
	
	private int resId;
	
	public ListViewItemAdapter(){
		this(R.layout.simple_dropdown_item_1line, null);
	}
	
	public ListViewItemAdapter(int resId){
		this(resId, null);
	}
	
	public ListViewItemAdapter(List<ItemType> items) {
		this(R.layout.simple_dropdown_item_1line, items);
	}
	
	public ListViewItemAdapter(int resId, List<ItemType> items) {
		super();
		this.resId = resId;
		
		this.items = new ArrayList<ItemType>();
		
		if (items != null) 
			this.items.addAll(items);
	}
	
	public List<ItemType> getItems() {
		return items;
	}

	@Override
	public int getCount() {
		return items == null ? 0 : items.size();
	}

	@Override
	public ItemType getItem(int position) {
		return this.items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setItems(List<ItemType> obj) {
		this.items = obj;
	}
	
	public void pushItem(ItemType item) {
		items.add(0, item);
	}
	
	public void removeItem(ItemType item) {
		items.remove(item);
	}
	
	protected View inflate(ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(resId, null);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemType item = items.get(position);
		
        if (convertView == null) 
        	convertView = inflate(parent);
        	
        TextView tvTitle = (TextView) convertView.findViewById(android.R.id.text1);
        if (null != tvTitle)
        	tvTitle.setText(item.toString());
        
        return convertView;
	}
	
	public void clear() {
		items.clear();
		this.notifyDataSetChanged();
	}
}
