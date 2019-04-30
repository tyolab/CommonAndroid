package au.com.tyo.android;

import android.app.PendingIntent;
import android.content.Context;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 7/10/17.
 */

public interface NotificationHelpers {

    int getTextResourcedIdFromState(int state);

    int getNotificationIconId();

    String getNotificationTextByState(int state);

    CharSequence getNotificationContentText(int state);

    PendingIntent getContentIntent(Context context, int state);

    boolean isOngoingEvent(int state);

    CharSequence getNotificationTitle(int state);
}
