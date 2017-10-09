package au.com.tyo.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 7/10/17.
 */

public abstract class CommonNotification implements NotificationClient {

    private static final String TAG = "CommonNotification";

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

    private int notificationId = this.getClass().getSimpleName().hashCode();

    public CommonNotification(Context ctx, CharSequence applicationLabel) {
        mState = -1;
        mContext = ctx;
        mLabel = applicationLabel;
        mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification = new Notification();
        mCurrentNotification = mNotification;

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

    public void createNotification(boolean ongoingEvent, int state) {
        if (null != helpers)
        mCurrentText = mContext.getString(helpers.getTextResourcedIdFromState(state));
        mCurrentTitle = mLabel.toString();
        mCurrentNotification.tickerText = mLabel + ": " + mCurrentText;

        if (null != helpers)
        mCurrentNotification.icon = helpers.getNotificationIconId();
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

        if (ongoingEvent) {
            mCurrentNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        } else {
            mCurrentNotification.flags &= ~Notification.FLAG_ONGOING_EVENT;
            mCurrentNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        }

        mNotificationManager.notify(notificationId, mCurrentNotification);
    }
}
