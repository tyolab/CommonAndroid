package au.com.tyo.android;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CommonUIBase implements CommonUI {

	private static final String LOG_TAG = "CommonUIBase";

	protected int width;
	
	protected int height;
	
    protected int orientation;
	
	public void UIBase() {
		width = 0;
		height = 0;
		orientation = -1;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public void setScreenSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	@Override
	public void startActivity(Context context, Class cls, boolean mainActivity) {
		if (null != context)
			startActivity(context, cls);
		else
			Log.e(LOG_TAG, "trying to start a new activity without context");
	}

	public static void startActivity(Context context, Class cls) {
		Intent i = new Intent(context, cls);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(i);
	}
}
