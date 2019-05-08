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

import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;

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

    private boolean notificationShowing;

    private NotificationCompat.Builder builder;

    private int notificationId = this.getClass().getSimpleName().hashCode() + Integer.MAX_VALUE;

    private int importance;

    public CommonNotification(Context ctx, CharSequence applicationLabel) {
        this(ctx, applicationLabel, AndroidUtils.getAndroidVersion() > 25 ? NotificationManager.IMPORTANCE_DEFAULT : PRIORITY_DEFAULT);
    }

    public CommonNotification(Context ctx, CharSequence applicationLabel, int importance) {
        mState = -1;
        mContext = ctx;
        mLabel = applicationLabel;
        mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        smallIcondResourceId = -1;
        channelId = CHANNEL_ID;

        this.importance = importance;

        createNotificationChannel();
    }

    protected void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getContext().getString(R.string.channel_name);
            String description = getContext().getString(R.string.channel_description);
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

    public void createNotification(int state) {
        createNotification (null != helpers ? helpers.isOngoingEvent(state) : false, state);
    }

    protected NotificationCompat.Builder createNotificationBuilder(int state) {
        if (null == builder) {
            mCurrentTitle = mLabel.toString();

            builder = new NotificationCompat.Builder(mContext, getChannelId());

            builder.setContentText(mCurrentText);

            builder.setTicker(mLabel + ": " + mCurrentText);
            builder.setVibrate(new long[]{200, 100, 300, 200, 100});

            if (null != helpers) {
                builder.setSmallIcon(helpers.getNotificationIconId());
            }
        }

        if (null != helpers) {
            builder.setContentInfo(helpers.getNotificationContentText(state));
            builder.setContentIntent(helpers.getContentIntent(mContext, state));
            builder.setContentTitle(helpers.getNotificationTitle(state));
            builder.setContentText(helpers.getNotificationTextByState(state));
        }
        else {
            builder.setContentTitle(mLabel.toString());
            builder.setContentIntent(mContentIntent);
            builder.setSmallIcon(smallIcondResourceId > -1 ? smallIcondResourceId : R.drawable.ic_noti_backup);
        }

        return builder;
    }

    protected Notification buildNotification(boolean ongoingEvent, int state) {
        NotificationCompat.Builder builder = createNotificationBuilder(state);

        if (ongoingEvent) {
            builder.setOngoing(true);
        } else {
            builder.setOngoing(false);
            builder.setAutoCancel(true);
        }

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
        Notification noti = buildNotification(ongoingEvent, state);

        if (ongoingEvent) {
            // TODO put the stuff here
            if (null == mNotification) {
                mNotification = noti;
            }

            // already set in buildNotification
            // noti.tickerText = mLabel + ": " + mCurrentText;
            // noti.contentIntent = mContentIntent;
        }

        return noti;
    }

    public void createNotification(boolean ongoingEvent, int state) {
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

        // if (ongoingEvent) {
        //     mCurrentNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        // } else {
        //     mCurrentNotification.flags &= ~Notification.FLAG_ONGOING_EVENT;
        //     mCurrentNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        // }

        mNotificationManager.notify(notificationId, mCurrentNotification);
        setNotificationShowing(true);
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

    public boolean isNotificationShowing() {
        return notificationShowing;
    }

    public void setNotificationShowing(boolean showing) {
        this.notificationShowing = showing;
    }

    public NotificationCompat.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(NotificationCompat.Builder builder) {
        this.builder = builder;
    }
}
