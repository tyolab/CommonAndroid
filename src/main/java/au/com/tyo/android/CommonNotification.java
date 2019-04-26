package au.com.tyo.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 7/10/17.
 */

public abstract class CommonNotification implements NotificationClient {

    private static final String TAG = "CommonNotification";

    private static final String CHANNEL_ID = "CommonNotification";

    protected int countNoti = 0;

    public static final int STATE_NONE = -1;
    public static final int STATE_STARTED = 0;
    public static final int STATE_UPDATE = 1;
    public static final int STATE_COMPLETED = 2;

    private int mState;
    private final Context mContext;
    private final NotificationManager mNotificationManager;
    private String mCurrentTitle;

    private Notification mNotification;
    private Notification mCurrentNotification;

    private CharSequence mLabel;
    private String mCurrentText;
    private PendingIntent mContentIntent;

    private NotificationHelpers helpers;

    private int smallIcondResourceId;

    private String channelId;

    private boolean notifictionShowing;

    private int notificationId = this.getClass().getSimpleName().hashCode();

    public CommonNotification(Context ctx, CharSequence applicationLabel) {
        mState = -1;
        mContext = ctx;
        mLabel = applicationLabel;
        mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        smallIcondResourceId = -1;
        channelId = CHANNEL_ID;

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getContext().getString(R.string.channel_name);
            String description = getContext().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setSmallIcondResourceId(int smallIcondResourceId) {
        this.smallIcondResourceId = smallIcondResourceId;
    }

    public int getNotificationId() {
        return notificationId + countNoti;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public PendingIntent getClientIntent() {
        return mContentIntent;
    }

    public void setClientIntent(PendingIntent mClientIntent) {
        this.mContentIntent = mClientIntent;
    }

    public void setNotificationHelpers(NotificationHelpers helpers) {
        this.helpers = helpers;
    }

    public abstract void onStateChanged(int newState);

    public void createNotification(boolean ongoingEvent) {
        createNotification(ongoingEvent, -1);
    }

    public void createNotification() {
        createNotification (null != helpers ? helpers.isOngoingEvent() : false);
    }

    protected Notification buildNotification(int state) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, getChannelId());

        builder.setContentTitle(mLabel.toString());
        builder.setContentText(mCurrentText);

        if (null != helpers) {
            builder.setContentInfo(helpers.getNotificationContentText());
            builder.setSmallIcon(helpers.getNotificationIconId());
            builder.setContentIntent(helpers.getContentIntent(mContext, state));
        }
        else {
            builder.setContentIntent(mContentIntent);
            builder.setSmallIcon(smallIcondResourceId > -1 ? smallIcondResourceId : R.drawable.ic_noti_backup);
        }

        // builder.setOngoing(true);
        builder.setTicker(mLabel + ": " + mCurrentText);
        builder.setVibrate(new long[] {200, 100, 300, 200, 100});
        // builder.setOnlyAlertOnce(!ongoingEvent);

        return builder.build();
    }

    /**
     * Override this method to create your own custom notification
     *
     * @param ongoingEvent
     * @param state
     * @return
     */
    public Notification updateNotification(boolean ongoingEvent, int state) {
        if (null != helpers)
            mCurrentText = helpers.getNotificationTextByState(state);
        mCurrentTitle = mLabel.toString();

        Notification noti = null;

        if (ongoingEvent) {
            // TODO put the stuff here
            if (null == mNotification) {
                mNotification = buildNotification(state);
                mCurrentNotification = mNotification;
            }

            noti = mCurrentNotification;
            noti.tickerText = mLabel + ": " + mCurrentText;

            // already set in buildNotfication
            // noti.contentIntent = mContentIntent;
        }
        else
            noti = buildNotification(state);

        return noti;
    }

    public void createNotification(boolean ongoingEvent, int state) {
//        if (null != helpers)
//            mCurrentText = helpers.getNotificationTextByState(state);
//        mCurrentTitle = mLabel.toString();
//        mCurrentNotification.tickerText = mLabel + ": " + mCurrentText;
//
//        if (null != helpers)
//            mCurrentNotification.icon = helpers.getNotificationIconId();
//            mCurrentNotification.setLatestEventInfo(mContext, mCurrentTitle, mCurrentText,
//                    mContentIntent);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            try {
                Method deprecatedMethod = mCurrentNotification.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                deprecatedMethod.invoke(mCurrentNotification, mContext,  mCurrentTitle, mCurrentText, mContentIntent);
            } catch (Exception e/*NoSuchMethodException | IllegalAccessException | IllegalArgumentException
	                    | InvocationTargetException e*/) {
                Log.w(TAG, "Method not found", e);
            }
        }

        mCurrentNotification = updateNotification(ongoingEvent, state);

        if (ongoingEvent) {
            mCurrentNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        } else {
            mCurrentNotification.flags &= ~Notification.FLAG_ONGOING_EVENT;
            mCurrentNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        }

        mNotificationManager.notify(notificationId, mCurrentNotification);
        setNotifictionShowing(true);
    }

    public static boolean isNotificationVisible(Context context, Class cls, int notificationId) {
        Intent notificationIntent = new Intent(context, cls);
        PendingIntent test = PendingIntent.getActivity(context, notificationId, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }

    public static void cancel(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    public void cancel(int notificationId) {
        cancel(mContext, notificationId);
    }

    public void cancel() {
        cancel(notificationId);
    }

    public Context getContext() {
        return mContext;
    }

    public boolean isNotifictionShowing() {
        return notifictionShowing;
    }

    public void setNotifictionShowing(boolean notifictionShowing) {
        this.notifictionShowing = notifictionShowing;
    }
}
