package au.com.tyo.android;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;
import au.com.tyo.io.IO;

public class ResourceManagerBase {
	public static final String LOG_TAG = "ResourceManagerBase";	
	
	protected Context context;
	
	public ResourceManagerBase(Context context) {
		this.context = context;
	}
	
	public String assestToString(String name) {
		String tmp = "";
        InputStream is;
        try {
			is = context.getAssets().open(name);
	        tmp = new String(IO.readFileIntoBytes(is));
		} catch (IOException e) {
			Log.e(LOG_TAG, "loading asset (" + name + ") error.");
		}

        return tmp;
	}
	
	public String getString(int resourceId) {
		return context.getResources().getString(resourceId);
	}
}
