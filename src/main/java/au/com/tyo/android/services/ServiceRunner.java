package au.com.tyo.android.services;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import au.com.tyo.android.Constants;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 12/2/18.
 */

public class ServiceRunner {

    private static final String TAG = "ServiceRunner";

    private Messenger serviceMessenger = null;

    private boolean isRunning = false;

    private Class serviceClass;

    private IBinder service;

    private boolean requiresMessenger;

    public ServiceRunner(Class serviceClass) {
        this.serviceClass = serviceClass;

        setRequireMessager(true);
    }

    public interface ServiceListener {
        void onConnected();
    }

    private ServiceListener serviceListener;

    public ServiceListener getServiceListener() {
        return serviceListener;
    }

    public void setServiceListener(ServiceListener serviceListener) {
        this.serviceListener = serviceListener;
    }

    public boolean doesRequireMessager() {
        return requiresMessenger;
    }

    public void setRequireMessager(boolean requiresMessenger) {
        this.requiresMessenger = requiresMessenger;
    }

    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            ServiceRunner.this.service = service;

            if (doesRequireMessager()) {
                serviceMessenger = new Messenger(service);
                sendMessage(Constants.MESSAGE_SERVICE_REGISTER_CLIENT);
            }

            if (null != serviceListener)
                serviceListener.onConnected();
        }

        public void onServiceDisconnected(ComponentName className) {
            serviceMessenger = null;
        }
    };

    public boolean sendMessage(int message) {
        return sendMessage(message, null);
    }

    public boolean sendMessage(int message, Object obj) {
        Message msg = Message.obtain(null, message);
        msg.replyTo = serviceMessenger;
        msg.obj = obj;
        return sendMessage(msg);
    }

    public boolean sendMessage(Message msg) {
        if (null != serviceMessenger)
            try {
                serviceMessenger.send(msg);
                return true;
            }
            catch (RemoteException e) {
                Log.e(TAG, "Error in sending message", e);
            }
        return false;
    }

    public void sendCommand(Context context, String command) {
        handlerService(context, serviceClass, command, null, true);
    }

    public void startService(Context context) {
        handlerService(context, true);
    }

    public void stopService(Context context) {
        handlerService(context, false);
    }

    public void stopService(Context context, String command) {
        handlerService(context, serviceClass, command, null, false);
    }

    public void handlerService(Context context, boolean toStart) {
        handlerService(context, serviceClass, toStart);
    }

    public void handlerService(Context context, Class cls, boolean toStart) {
        handlerService(context, cls, null, null, toStart);
    }

    public void handlerService(Context context, Class cls, String command, PendingIntent pendingIntent, boolean toStart) {
        Intent locationIntent = new Intent(context, cls);
        if (!TextUtils.isEmpty(command))
            locationIntent.setAction(command);

        // Build PendingIntent used to open this activity from
        // Notification

        if (toStart) {
            if (null != pendingIntent)
                locationIntent.putExtra(CommonIntentService.EXTRA_PENDING_INTENT, pendingIntent);

            if (!isRunning || !TextUtils.isEmpty(command)) {
                context.startService(locationIntent);
                isRunning = true;
            }

            if (null == serviceMessenger)
                context.bindService(locationIntent, connection, Context.BIND_AUTO_CREATE);
        }
        else {
            if (null != serviceMessenger) {
                sendMessage(Constants.MESSAGE_SERVICE_UNREGISTER_CLIENT);
                context.unbindService(connection);
            }

            if (isRunning) {
                context.stopService(locationIntent);
                isRunning = false;
            }
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isServiceRunning(Context context) {
        return isServiceRunning(context, serviceClass);
    }

    public static boolean isServiceRunning(Context context, Class cls) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
