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

import au.com.tyo.android.services.CommonIntentService;
import au.com.tyo.utils.LocationUtils;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 24/9/17.
 */

public class CommonLocation extends CommonIntentService {

    private static LocationManager locationManager;

    private List<LocationUtils.LocationPoint> locations;

//    private Context context;

    /**
     *
     */
    private Location startLocation;
    private Location lastKnownLocation;

    private LocationListener androidLocationListener;
    private boolean toStop;

    public CommonLocation() {
        super("CommonLocationService");

        locations = new ArrayList();
    }

    public static LocationManager getLocationManager(Context context) {
        if (null == locationManager)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager;
    }

    public boolean isToStop() {
        return toStop;
    }

    public void setToStop(boolean toStop) {
        this.toStop = toStop;
    }

    public LocationListener getAndroidLocationListener() {
        return androidLocationListener;
    }

    public void setAndroidLocationListener(LocationListener androidLocationListener) {
        this.androidLocationListener = androidLocationListener;
    }

    public static boolean checkLocationSetting(Context context) {
        if(!isLocationEnabled(context))
            showLocationSettingOffAlert(context);
        return isLocationEnabled(context);
    }

    public static void showLocationSettingOffAlert(final Context context) {
        final AlertDialog.Builder builder = DialogFactory.createDialogBuilder(
                context,
                R.style.Theme_AppCompat_Light_Dialog_Alert,
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

    private static boolean isLocationEnabled(Context context) {
        return getLocationManager(context).isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                getLocationManager(context).isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static void requestGPSLocationUpdates(Context context, LocationListener locationListener) {
        requestLocationUpdates(context, LocationManager.GPS_PROVIDER, locationListener);
    }

    public static void requestLocationUpdates(Context context, String provider, LocationListener locationListener) {
        getLocationManager(context).requestLocationUpdates(
                provider, BuildConfig.DEBUG ? 1000 : 5 * 60 * 1000 /* request update for every 5 minutes */,BuildConfig.DEBUG ? 0 : 5, locationListener);
    }

    public void removeAndroidLocationUpdates(Context context) {
        if (null != androidLocationListener)
            getLocationManager(context).removeUpdates(androidLocationListener);
    }

    public void startTracking(Context context, String provider, LocationListener locationListener) {
        LocationManager locationManager = getLocationManager(context);
        if (null == lastKnownLocation)
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (null == lastKnownLocation)
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (null != locationListener)
            requestLocationUpdates(context, provider, locationListener);
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
//        if (startLocation == null)
//            startLocation = lastKnownLocation;

        this.lastKnownLocation = lastKnownLocation;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public void addLocationPoint(LocationUtils.LocationPoint point) {
        if (locations == null)
            locations = new ArrayList<>();

        if (locations.size() == 0) {
            locations.add(point);
            return;
        }
        if (locations.get(locations.size() - 1) == point)
            return;

        locations.add(point);
    }

    @Override
    protected void onHandleIntent(Intent paramIntent) {
        super.onHandleIntent(paramIntent);
    }

    @Override
    protected boolean shouldStop() {
        return toStop;
    }
}
