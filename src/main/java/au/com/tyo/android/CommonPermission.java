package au.com.tyo.android;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 17/6/17.
 */

public class CommonPermission {

    public static final int LOCATION_PERMISSON_RC = 19001;

    public static final int STORAGE_PERMISSON_RC = 19002;

    public interface PermissionCheckListener {
        void onPermissionRequestReturned(int rc);
    }

    protected static boolean checkPermissions(Activity context, String[] requiredPermissions, int rc) {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean hasIt = true;
            for (String permission : requiredPermissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)!= PackageManager.PERMISSION_GRANTED) {
                    hasIt = false;
                    break;
                }
            }

            if (!hasIt) {
                ActivityCompat.requestPermissions(context, requiredPermissions, rc);
                return false;
            }
        }
        return true;
    }

    public static boolean checkLocationPermissions(Activity context) {
       return checkPermissions(context, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSON_RC);
    }
}
