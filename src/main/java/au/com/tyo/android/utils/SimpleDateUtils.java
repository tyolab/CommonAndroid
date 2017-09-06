package au.com.tyo.android.utils;

import android.annotation.SuppressLint;
import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;

import au.com.tyo.android.Android;
import au.com.tyo.android.AndroidUtils;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 17/8/17.
 */

public class SimpleDateUtils extends au.com.tyo.utils.SimpleDateUtils {

    private static final String DATE_PATTERN_SIMPLE = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DATE_PATTERN_ZONED = DATE_PATTERN_SIMPLE + ".SSSZZZZZ";
    private static final String DATE_PATTERN_SSS = DATE_PATTERN_SIMPLE + ".SSS";

    private static final SimpleDateFormat JSON_SIMPLE_DATE_FORMAT_ZONED = new SimpleDateFormat(DATE_PATTERN_ZONED);
    private static final SimpleDateFormat JSON_SIMPLE_DATE_FORMAT_SSS = new SimpleDateFormat(DATE_PATTERN_SSS);
    private static final SimpleDateFormat JSON_SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN_SIMPLE);
    /**
     *
     *  can be formatted into something like "2013-03-11T01:38:18.309Z" / "2013-03-11T01:38:18.309+0000"
     *
     * @param date
     * @return
     */
    public static String toJSONDateString(Date date) {
        return JSON_SIMPLE_DATE_FORMAT.format(date);
    }

    /**
     *  can parse both "2013-03-11T01:38:18.309Z" and "2013-03-11T01:38:18.309+0000"
     *
     * @param jsonDate
     * @return
     * @throws ParseException
     */
    @SuppressWarnings("Since15")
    @SuppressLint("NewApi")
    public static Date fromJSONDate(String jsonDate) throws ParseException {
        if (AndroidUtils.getAndroidVersion() >= 26) {
             OffsetDateTime odt = OffsetDateTime.parse(jsonDate);
            return new Date(odt.toInstant().toEpochMilli());
        }
        SimpleDateFormat format;
        format = JSON_SIMPLE_DATE_FORMAT;
        int pos = jsonDate.length() - 5;
        String dateString;
        if (jsonDate.charAt(pos) == '+') {
            dateString = jsonDate.substring(0, pos);
        }
        else {
            dateString = jsonDate;
        }

        if (dateString.length() == DATE_PATTERN_SSS.length())
            format = JSON_SIMPLE_DATE_FORMAT_SSS;
        else if (dateString.length() == DATE_PATTERN_ZONED.length())
            format = JSON_SIMPLE_DATE_FORMAT_ZONED;

        return format.parse(dateString);
    }

}
