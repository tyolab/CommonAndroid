/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.android.utils;

import java.util.concurrent.TimeUnit;
import android.annotation.SuppressLint;
import au.com.tyo.android.AndroidUtils;

public class TimeUtils {
	
	public static final long MILLISECOND_A_SECOND = 1000;
	
	public static final long MINUTES_A_HOUR = 60;
	
	public static final long MILLISECOND_A_MINUTE = 60 * MILLISECOND_A_SECOND;
	
	public static final long MILLISECOND_A_HOUR = 60 * MILLISECOND_A_MINUTE;
	
	@SuppressLint("NewApi")
	public static long getTimeMinutes (long millis) {
		if (AndroidUtils.getAndroidVersion() > 8)
			return TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
		long hours = getTimeHours(millis);
		long minutes = millis / MILLISECOND_A_MINUTE;
		return minutes - hours * MINUTES_A_HOUR;
	}
	
	@SuppressLint("NewApi")
	public static long getTimeHours(long millis) {
		if (AndroidUtils.getAndroidVersion() > 8)
			return TimeUnit.MILLISECONDS.toHours(millis);
		return millis / MILLISECOND_A_HOUR;
	}
	
	@SuppressLint("NewApi")
	public static long getTimeSeconds(long millis) {
		if (AndroidUtils.getAndroidVersion() > 8)
			return TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
		return (millis / 1000) % 60;
	}
}
