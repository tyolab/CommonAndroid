package au.com.tyo.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Use pattern for the class names:
 *
 *
 *
 */
public abstract class CommonApplicationImpl<T extends CommonController> implements CommonController {
	
	private static final String LOG_TAG = "CommonApplicationImpl";
	
	/* Meta Information */
	protected static Object instance;
	
	/* General Stuff */
	
	protected Context context;

	protected Application application;
	
	protected Handler msgHandler;

	protected NotificationManager notificationManager;
	
	protected boolean hasAd;
	
	protected Activity currentActivity;
	
	protected int logoResId;
	
	protected int backKeyCount;
	
	/* App Information */
	protected String appName = "";

	protected String version = "0.9.9"; // that is the number to show something wrong

    private CommonApplicationImpl() {
		notificationManager = null;
	}
	
	public CommonApplicationImpl(Context context) {
		this.context = context;

		if (context instanceof Application)
		    setApplication((Application) context);

		// if (CommonInitializer.clsUi == null)
		// detect the default classes
		CommonInitializer.detectDefaultClasses(context);

		notificationManager = (NotificationManager) context.getSystemService(Application.NOTIFICATION_SERVICE);

		setInstance(this);
	}

	public static <T> T getInstance() {
		return (T) instance;
	}
	
	public static void setInstance(Object obj) {
		instance = obj;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@Override
	public Context getApplicationContext() {
        if (null == application) {
            if (null != getCurrentActivity())
                return getCurrentActivity().getApplicationContext();
            return null;
        }
    	return application;
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	public void initializeInMainThread(Context context) {
    	Log.i(LOG_TAG, "App initializing in main thread");

		if (null == this.context)
			this.context = context;

		if (notificationManager == null)
			notificationManager = (NotificationManager) context.getSystemService(Application.NOTIFICATION_SERVICE);
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	public void initializeInBackgroundThread(Context context) {
		Log.i(LOG_TAG, "App initializing in background thread");
	}

	@Override
	public Handler getHandler() {
    	return msgHandler;
	}

	@Override
	public void onCreate() {
    	if (logoResId <= 0)
			logoResId = R.drawable.ic_logo;
	}

	public Handler getMessageHandler() {
		return msgHandler;
	}

	public void setMessageHandler(Handler msgHandler) {
		this.msgHandler = msgHandler;
	}

	@Override
	public void sendMessage(int msgId) {
		sendMessage(msgId, null);
	}
	
	@Override
	public void sendMessage(int msgId, Object content) {
		if (msgHandler == null)
			return;
		
        Message msg = Message.obtain();
        msg.what = msgId;
        msg.obj = content;
        msgHandler.sendMessage(msg);
    }

	public Context getContext() {
		return context;
	}

	@Override
	public void setContext(Context context) {
		this.context = context;
	}
	
	@Override
	public NotificationManager getNotificationManager() {
		return notificationManager;
	}
	
	@Override
	public void quitOrRestart(boolean restart) {
		quitOrRestart(context, restart);
	}
	
	public static void quitOrRestart(Context context, boolean restart) {
    	Activity activity = ((Activity) context);
    	quitOrRestart(activity, restart);
	}
	
	@SuppressLint("NewApi")
	public static void quitOrRestart(Activity activity, boolean restart) {		
    	if (restart) {
    		if (AndroidUtils.getAndroidVersion() >= 11)
    			activity.recreate();
    		else {
				Intent intent = activity.getIntent();
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				activity.startActivity(intent);
    			AndroidUtils.finishActivity(activity);
    		}
    	}
    	else {
			AndroidUtils.finishActivity(activity);
    	}
	}
	
	@Override
	public Class getMainActivityClass() {
		return CommonInitializer.classMainActivity;
	}

	@Override
	public void setMainActivityClass(Class cls) {
		CommonInitializer.classMainActivity = cls;
	}
	
	@Override
	public  Class getPreferenceActivityClass() {
		return CommonInitializer.preferenceActivityClass;
	}

	public void setPreferenceActivityClass(Class cls) {
		CommonInitializer.preferenceActivityClass = cls;
	}

	@Override
	public Class getSplashScreenClass() {
		return CommonInitializer.splashScreenClass;
	}

	public void setSplashScreenClass(Class splashScreenClass) {
		CommonInitializer.splashScreenClass = splashScreenClass;
	}	

	@Override
	public boolean hasAd() {
		return hasAd;
	}

	public void setHasAd(boolean hasAd) {
		this.hasAd = hasAd;
	}
	
	@Override
	public void setCurrentActivity(Activity activity) {
		this.currentActivity = activity;
        this.context = activity;
	}
	
	@Override
	public Activity getCurrentActivity() {
		return this.currentActivity;
	}
	
	@Override
	public void setAdStatus(Context context) {
		hasAd = context.getResources().getBoolean(R.bool.showAd);

		/**
		 * should solely controlled by the bools.xml

		if (hasAd && !Debug.debugging) 
			hasAd = false;

		 */
	}

	public String getAppName() {
	    if ((appName == null || appName.length() == 0)) {
	    	Context appContext = context;
	    	if (appContext == null)
	    		appContext = getContext();
	    	
	    	if (null != appContext)
	    		appName = context.getResources().getString(R.string.app_name);
	    }
	    	
		return appName;
	}
	
	public String getAppNameWithVersion() {
    	Context appContext = context;
    	if (appContext == null)
    		appContext = getContext();
    	
    	if (null != appContext)
    		return String.format(getAppName() + " (%s)"
					/*context.getResources().getString(R.string.app_desc), getVersion()*/,
					AndroidUtils.getPackageVersionName(appContext));
    	
    	return "";
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}	
	
	@Override
	public String getVersion() {
		return version;
	}

}
