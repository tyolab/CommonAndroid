/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.android.utils;

public class ColorUtils {
	
	public static String toHexString(int color) {
		return String.format("#%06X", (0xFFFFFF & color));
	}
}
