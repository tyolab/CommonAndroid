package au.com.tyo.android;

import android.content.res.Configuration;

public class CommonUIBase implements CommonUI {

	protected int width;
	
	protected int height;
	
    protected int orientation;
	
	public void UIBase() {
		width = 0;
		height = 0;
		orientation = Configuration.ORIENTATION_PORTRAIT;
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
}
