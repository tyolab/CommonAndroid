/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.android.data;

import android.database.Cursor;

public class Table {
	
	public static boolean cursorHasRecord(Cursor cursor) {
		boolean ret = false;
		if (cursor != null && cursor.getCount() > 0) {
			ret = true;
			cursor.close();
		}
		return ret;
	}
}
