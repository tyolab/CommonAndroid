package au.com.tyo.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 3/8/17.
 */

public class InflaterFactory {

    protected LayoutInflater inflater;

    public InflaterFactory(Context context) {
        inflater = LayoutInflater.from(context);
    }
}
