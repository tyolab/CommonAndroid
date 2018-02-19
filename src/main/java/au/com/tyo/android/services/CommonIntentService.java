/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.com.tyo.android.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import au.com.tyo.android.CommonNotification;
import au.com.tyo.android.Constants;

/**
 * This service differs from IntentService in a few minor ways/ It will not
 * auto-stop itself after the intent is handled unless the target returns "true"
 * in should stop. Since the goal of this service is to handle a single kind of
 * intent, it does not queue up batches of intents of the same type.
 */
public abstract class CommonIntentService extends Service {

    public static final String EXTRA_PACKAGE_NAME = "EPN";
    public static final String EXTRA_PENDING_INTENT = "EPI";
    public static final String EXTRA_MESSAGE_HANDLER = "EMH";
    private static final String TAG = "CommonIntentService";

    private static boolean sIsRunning;

    private String mName;
    private boolean mRedelivery;
    private volatile ServiceHandler mServiceHandler;
    private volatile Looper mServiceLooper;
    private static final String LOG_TAG = "CancellableIntentService";
    private static final int WHAT_MESSAGE = -10;

    private BroadcastReceiver mConnReceiver;
    private Messenger mClientMessenger;
    private PendingIntent mPendingIntent;
    private PendingIntent mAlarmIntent;
    private CommonNotification notificationFactory;

    private IBinder mBinder;

    private PackageInfo mPackageInfo;
    private CharSequence applicationLabel;

    public CommonIntentService() {
        this(TAG);
    }

    public CommonIntentService(String paramString) {
        this.mName = paramString;

        init();
    }

    protected boolean handleServiceMessage(Message m) {
        return false;
    }

    private void init() {
        mClientMessenger = new Messenger(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "received message from client");
                if (!handleServiceMessage(msg))
                    super.handleMessage(msg);
            }
        });
    }

    @Override
    public IBinder onBind(Intent paramIntent) {
        return mBinder = mClientMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mPackageInfo = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            ApplicationInfo ai = getApplicationInfo();
            applicationLabel = getPackageManager().getApplicationLabel(ai);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        HandlerThread localHandlerThread = new HandlerThread("IntentService["
                + this.mName + "]");
        localHandlerThread.start();
        this.mServiceLooper = localHandlerThread.getLooper();
        this.mServiceHandler = new ServiceHandler(this.mServiceLooper);
    }

    public CharSequence getApplicationLabel() {
        return applicationLabel;
    }

    public void setNotificatonFactory(CommonNotification notificationFactory) {}

    public void setNotificationFactory(CommonNotification con) {
        notificationFactory = con;
    }

    public CommonNotification getNotificationFactory() {
        return notificationFactory;
    }

    protected void onClientStateChanged(int newState) {
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onDestroy() {
        setServiceRunning(false);

        Thread localThread = this.mServiceLooper.getThread();
        if ((localThread != null) && (localThread.isAlive())) {
            localThread.interrupt();
        }
        this.mServiceLooper.quit();
        Log.d(LOG_TAG, "onDestroy");
    }

    @SuppressLint("LongLogTag")
    protected void onHandleIntent(Intent intent) {
        setServiceRunning(true);
        final PendingIntent pendingIntent = (PendingIntent) intent
                .getParcelableExtra(EXTRA_PENDING_INTENT);

        if (null != pendingIntent)
        {
            notificationFactory.setClientIntent(pendingIntent);
            mPendingIntent = pendingIntent;
        } else if (null != mPendingIntent) {
            notificationFactory.setClientIntent(mPendingIntent);
        } else {
            Log.e(LOG_TAG, "Service started in bad state without notification intent.");
            return;
        }
    }

    protected abstract boolean shouldStop();

    @Override
    public void onStart(Intent paramIntent, int startId) {
        if (!this.mServiceHandler.hasMessages(WHAT_MESSAGE)) {
            Message localMessage = this.mServiceHandler.obtainMessage();
            localMessage.arg1 = startId;
            localMessage.obj = paramIntent;
            localMessage.what = WHAT_MESSAGE;
            this.mServiceHandler.sendMessage(localMessage);
        }
    }

    @Override
    public int onStartCommand(Intent paramIntent, int flags, int startId) {
        onStart(paramIntent, startId);
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    public void setIntentRedelivery(boolean enabled) {
        this.mRedelivery = enabled;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @SuppressLint("LongLogTag")
        @Override
        public void handleMessage(Message paramMessage) {
            CommonIntentService.this
                    .onHandleIntent((Intent) paramMessage.obj);
            if (shouldStop()) {
                Log.d(LOG_TAG, "stopSelf");
                CommonIntentService.this.stopSelf(paramMessage.arg1);
                Log.d(LOG_TAG, "afterStopSelf");
            }
        }
    }

    /**
     * Careful! Only use this internally.
     *
     * @return whether we think the service is running
     */
    public static synchronized boolean isServiceRunning() {
        return sIsRunning;
    }

    public static synchronized void setServiceRunning(boolean isRunning) {
        sIsRunning = isRunning;
    }

    /**
     *
     * @param messenger
     * @param what
     */
    public static void sendMessage(Messenger messenger, int what) {
        sendMessage(messenger, what, null);
    }

    /**
     *
     * @param messenger
     * @param what
     * @param replyTo
     */
    public static void sendMessage(Messenger messenger, int what, Messenger replyTo) {
        try {
            Message msg = Message.obtain(null, what);
            msg.replyTo = replyTo;
            messenger.send(msg);
        }
        catch (RemoteException e) {
        }
    }

}
