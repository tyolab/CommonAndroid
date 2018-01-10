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

    public static final int PERMISSON_RC = 19000;

    public static final int PERMISSON_RC_LOCATION = 19001;

    public static final int PERMISSON_RC_STORAGE = 19002;

    public static final String[] PERMISSIONS_LOCATION = new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final String[] PERMISSIONS_CAMERA = new String[] {Manifest.permission.CAMERA};

    public interface PermissionCheckListener {
        void onPermissionRequestReturned(int rc);
    }

    public static void requestPermissions(Activity context, String[] requiredPermissions) {
        ActivityCompat.requestPermissions(context, requiredPermissions, PERMISSON_RC);
    }

    public static boolean checkPermission(Activity context, String permission) {
        if (Build.VERSION.SDK_INT >= 23)
            return (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
        return true;
    }

    public static boolean checkAndRequestPermissions(Activity context, String[] requiredPermissions) {
        boolean hasIt = true;
        for (String permission : requiredPermissions) {
            if (!checkPermission(context, permission)) {
                hasIt = false;
                break;
            }
        }

        if (!hasIt) {
            requestPermissions(context, requiredPermissions);
        }
        return hasIt;
    }

    public static boolean checkLocationPermissions(Activity context) {
       return checkAndRequestPermissions(context, PERMISSIONS_LOCATION);
    }
}
