package au.com.tyo.android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 24/9/17.
 */

public class CommonLocation {

    private LocationManager locationManager;

    private List<Location> locations;

    private Context context;

    /**
     *
     */
    private Location lastKnownLocation;

    public CommonLocation(Context context) {
        this.context = context;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locations = new ArrayList<>();
    }

    public Context getContext() {
        return context;
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showLocationSettingOffAlert();
        return isLocationEnabled();
    }

    private void showLocationSettingOffAlert() {
        showLocationSettingOffAlert(context);
    }

    public static void showLocationSettingOffAlert(final Context context) {
        final AlertDialog.Builder builder = DialogFactory.createDialogBuilder(
                context,
                R.style.CommonAlertDialog_Light,
                context.getResources().getString(R.string.enable_location_title),
                context.getResources().getString(R.string.enable_location_message),
                null,
                DialogFactory.dismissMeListener);

        builder.setPositiveButton(context.getResources().getString(R.string.location_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int paramInt) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void requestGPSLocationUpdates(LocationListener locationListener) {
        requestLocationUpdates(LocationManager.GPS_PROVIDER, locationListener);
    }

    public void requestLocationUpdates(String provider, LocationListener locationListener) {
        locationManager.requestLocationUpdates(
                provider, 5 * 60 * 1000 /* request update for every 5 minutes */, 10, locationListener);
    }

    public void startTracking(String provider, LocationListener locationListener) {
        if (null == lastKnownLocation)
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (null == lastKnownLocation)
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (null != locationListener)
            requestLocationUpdates(provider, locationListener);
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }
}
