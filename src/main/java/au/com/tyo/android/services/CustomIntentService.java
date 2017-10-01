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
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

/**
 * This service differs from IntentService in a few minor ways/ It will not
 * auto-stop itself after the intent is handled unless the target returns "true"
 * in should stop. Since the goal of this service is to handle a single kind of
 * intent, it does not queue up batches of intents of the same type.
 */
public abstract class CustomIntentService extends Service {

    public static final String EXTRA_PACKAGE_NAME = "EPN";
    public static final String EXTRA_PENDING_INTENT = "EPI";
    public static final String EXTRA_MESSAGE_HANDLER = "EMH";

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

    public CustomIntentService(String paramString) {
        this.mName = paramString;
    }

    @Override
    public IBinder onBind(Intent paramIntent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread localHandlerThread = new HandlerThread("IntentService["
                + this.mName + "]");
        localHandlerThread.start();
        this.mServiceLooper = localHandlerThread.getLooper();
        this.mServiceHandler = new ServiceHandler(this.mServiceLooper);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onDestroy() {
        Thread localThread = this.mServiceLooper.getThread();
        if ((localThread != null) && (localThread.isAlive())) {
            localThread.interrupt();
        }
        this.mServiceLooper.quit();
        Log.d(LOG_TAG, "onDestroy");
    }

    @SuppressLint("LongLogTag")
    protected void onHandleIntent(Intent intent) {
        final PendingIntent pendingIntent = (PendingIntent) intent
                .getParcelableExtra(EXTRA_PENDING_INTENT);

        if (null != pendingIntent)
        {
            mPendingIntent = pendingIntent;
        } else if (null != mPendingIntent) {

        } else {
            Log.e(LOG_TAG, "Downloader started in bad state without notification intent.");
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
            CustomIntentService.this
                    .onHandleIntent((Intent) paramMessage.obj);
            if (shouldStop()) {
                Log.d(LOG_TAG, "stopSelf");
                CustomIntentService.this.stopSelf(paramMessage.arg1);
                Log.d(LOG_TAG, "afterStopSelf");
            }
        }
    }
}