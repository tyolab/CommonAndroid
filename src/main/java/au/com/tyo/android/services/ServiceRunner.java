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

import java.io.Serializable;

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
    private boolean alive;

    public ServiceRunner(Class serviceClass) {
        this.serviceClass = serviceClass;
        this.alive = false;

        setRequireMessager(true);
    }

    public boolean isAlive() {
        return alive;
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
            alive = true;

            if (doesRequireMessager()) {
                serviceMessenger = new Messenger(service);
                sendMessage(Constants.MESSAGE_SERVICE_REGISTER_CLIENT);
            }

            if (null != serviceListener)
                serviceListener.onConnected();
        }

        public void onServiceDisconnected(ComponentName className) {
            serviceMessenger = null;
            alive = false;
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

    public void sendCommand(Context context, String command, Serializable data) {
        handlerService(context, serviceClass, command, null, data, true);
    }

    public void sendCommand(Context context, String command) {
        sendCommand(context, command, null);
    }

    public void startService(Context context) {
        handlerService(context, serviceClass, null,null, true);
    }

    public void stopService(Context context) {
        handlerService(context, serviceClass, null, null, false);
    }

    public void stopService(Context context, String command) {
        handlerService(context, serviceClass, command, null, false);
    }

    public void handlerService(Context context, Class serviceClass, String command, Serializable data, boolean toStart) {
        handlerService(context, serviceClass, command, null, data, toStart);
    }

    public void handlerService(Context context, Class cls, Serializable data, boolean toStart) {
        handlerService(context, cls, null, null, data, toStart);
    }

    public void handlerService(Context context, Class cls, String command, PendingIntent pendingIntent, Serializable data, boolean toStart) {
        Intent serviceIntent = new Intent(context, cls);
        if (!TextUtils.isEmpty(command))
            serviceIntent.setAction(command);
        if (null != data)
            serviceIntent.putExtra(Constants.DATA, data);

        // Build PendingIntent used to open this activity from
        // Notification

        if (toStart) {
            if (null != pendingIntent)
                serviceIntent.putExtra(CommonIntentService.EXTRA_PENDING_INTENT, pendingIntent);

            if (!isRunning || !TextUtils.isEmpty(command)) {
                context.startService(serviceIntent);
                isRunning = true;
            }

            if (null == serviceMessenger)
                context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        }
        else {
            if (null != serviceMessenger) {
                sendMessage(Constants.MESSAGE_SERVICE_UNREGISTER_CLIENT);
                context.unbindService(connection);
            }

            if (isRunning) {
                context.stopService(serviceIntent);
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
