package au.com.tyo.android.utils;

import android.content.Intent;

import au.com.tyo.android.Constants;

public class ActivityUtils {

    public static Object getActivityResult(Intent data) {
        Object result = null;
        if (data.hasExtra(Constants.RESULT)) {
            try {
                result = data.getParcelableExtra(Constants.RESULT);
            }
            catch (Exception ex) {}
            if (result == null)
                try {
                    result = data.getStringExtra(Constants.RESULT);
                }
                catch (Exception ex) {}
            if (result == null)
                try {
                    result = data.getStringArrayExtra(Constants.RESULT);
                }
                catch (Exception ex) {}
            if (result == null)
                try {
                    result = data.getStringArrayListExtra(Constants.RESULT);
                }
                catch (Exception ex) {}
            if (result == null)
                try {
                    result = data.getParcelableArrayExtra(Constants.RESULT);
                }
                catch (Exception ex) {}
            if (result == null)
                try {
                    result = data.getParcelableArrayListExtra(Constants.RESULT);
                }
                catch (Exception ex) {}
        }

        return result;
    }

}
