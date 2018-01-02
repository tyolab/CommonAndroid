package au.com.tyo.android;

import android.content.Context;

public interface CommonUI {

	void setScreenSize(int width, int height);

	void setOrientation(int orientation);
	
	int getWidth();
	
	int getHeight();

	void startActivity(Context context, Class cls, boolean mainActivity);
}
