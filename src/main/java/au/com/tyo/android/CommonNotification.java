package au.com.tyo.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 7/10/17.
 */

public abstract class CommonNotification implements NotificationClient {

    private static final String TAG = "CommonNotification";

    protected static int countNoti = 0;

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

    private int notificationId = this.getClass().getSimpleName().hashCode();

    public CommonNotification(Context ctx, CharSequence applicationLabel) {
        mState = -1;
        mContext = ctx;
        mLabel = applicationLabel;
        mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        smallIcondResourceId = -1;
    }

    public void setSmallIcondResourceId(int smallIcondResourceId) {
        this.smallIcondResourceId = smallIcondResourceId;
    }

    public int getNotificationId() {
        return notificationId;
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

    protected Notification buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

        builder.setContentTitle(mLabel.toString());
        builder.setContentText(mCurrentText);

        if (null != helpers) {
            builder.setContentInfo(helpers.getNotificationContentText());
            builder.setSmallIcon(helpers.getNotificationIconId());
            builder.setContentIntent(helpers.getContentIntent(mContext));
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
                mNotification = buildNotification();
                mCurrentNotification = mNotification;
            }

            noti = mCurrentNotification;
            noti.tickerText = mLabel + ": " + mCurrentText;
            noti.contentIntent = mContentIntent;
        }
        else
            noti = buildNotification();

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
    }

    public static boolean isNotificationVisible(Context context, Class cls) {
        Intent notificationIntent = new Intent(context, cls);
        PendingIntent test = PendingIntent.getActivity(context, countNoti - 1, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }

    public static void cancel(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

}
