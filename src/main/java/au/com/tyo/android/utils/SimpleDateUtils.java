package au.com.tyo.android.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 17/8/17.
 */

public class SimpleDateUtils {

    public static String toMonthYear(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
        return dateFormat.format(date);
    }

    public static String toDayMonthYear(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("DD MMMM yyyy");
        return dateFormat.format(date);
    }

}
