package au.com.tyo.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 3/8/17.
 */

public abstract class InflaterFactory {

    protected LayoutInflater inflater;

    private int resId;

    private Context context;

    public InflaterFactory(Context context, int resource) {
        this.context = context;

        inflater = LayoutInflater.from(context);
        resId = resource;
    }

    public Context getContext() {
        return context;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public View getView(View convertView, ViewGroup parent) {

        View view;
        if (convertView == null) {
            view = inflater.inflate(getResId(), parent, false);
        } else {
            view = convertView;
        }

        return view;
    }

    protected ViewHolder newViewHolderInstance(){
        return new ViewHolder();
    }

    protected ViewHolder createViewHolder(View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        holder = newViewHolderInstance();
        convertView = getView(convertView, parent);
        holder.view = convertView;
        convertView.setTag(holder);
        return holder;
    }

    public ViewHolder getViewHolder(View convertView, ViewGroup parent, Object obj) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = createViewHolder(convertView, parent);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        bindData(holder, obj);

        return holder;
    }

    public abstract void bindData(ViewHolder holder, Object obj);

    public static class ViewHolder {
        public  View view;
    }
}
