package au.com.tyo.android;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;

import java.util.Observer;

public interface CommonController extends Observer {
	
//	void initializeUserInterface(CommonUI ui);

	boolean onKeyDown(int keyCode, KeyEvent event);

	Handler getHandler();

	boolean onKeyUp(int keyCode, KeyEvent event);
	
	Context getContext();
	
	void setContext(Context context);
	
	void quitOrRestart(boolean restart);

	void restart();
	
	void sendMessage(int msgId);
	
	void sendMessage(int msgId, Object content);

	NotificationManager getNotificationManager();

	Class getSplashScreenClass();

	void setMainActivityClass(Class cls);

	Class getPreferenceActivityClass();

	Class getMainActivityClass();

	boolean hasAd();

	void startSplashScreenActivity(Context context);

	void startMainActivity();
	
	void setCurrentActivity(Activity activity);
	
	void onSaveInstanceState(Bundle savedInstanceState);

	void onRestoreInstanceState(Bundle savedInstanceState);

//	void onScaleChanged(float oldScale, float newScale);

	Activity getCurrentActivity();

	Context getApplicationContext();

	void initializeInMainThread(Context context);

	void initializeInBackgroundThread(Context context);

	void setAdStatus(Context context);

	boolean onOptionsItemSelected(Activity activity, MenuItem item);

	String getVersion();

	void onActivityResult(int requestCode, int resultCode, Intent data);

	void onPostCreate(Bundle savedInstanceState);

	void showInfo();
	
	String getAppName();
	
	void showAlertDialog(String string, String message,
			OnClickListener okListener, OnClickListener cancelListener);

	void showAlertDialog(String title, String message,
			OnClickListener okListener, OnClickListener cancelListner,
			boolean cancelable);

	void onCreate();

    void onDeleteFromList(int listId, Object data);

	void onListItemClick(int listId, Object obj);
}
