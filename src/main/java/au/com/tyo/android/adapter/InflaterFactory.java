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

    public InflaterFactory(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public abstract View getView(View convertView, ViewGroup parent, Object obj);
}
