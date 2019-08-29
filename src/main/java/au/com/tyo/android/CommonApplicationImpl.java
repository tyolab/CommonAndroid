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
	protected String acknowledgementTitle = null;
	protected String acknowledgementInfo = null;

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
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode) {
        case KeyEvent.KEYCODE_BACK:
            if (event.isTracking() && !event.isCanceled()) {
                onBackKeyPressed();
                return true;
            }
            break;
        }
		return false;
	}
	
	@SuppressLint("NewApi")
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean hasNoModifiers = true;
		
        if (AndroidUtils.getAndroidVersion() > android.os.Build.VERSION_CODES.HONEYCOMB /*>=11*/) {
	        hasNoModifiers = event.hasNoModifiers();
//	        ctrl = event.hasModifiers(KeyEvent.META_CTRL_ON);
//	        shift = event.hasModifiers(KeyEvent.META_SHIFT_ON);
        }
        
        switch(keyCode) {
        case KeyEvent.KEYCODE_VOLUME_UP:
        case KeyEvent.KEYCODE_VOLUME_DOWN:

        	break;
			
        case KeyEvent.KEYCODE_BACK:
//			if (getCurrentActivity().getApplicationInfo().targetSdkVersion
//					>= Build.VERSION_CODES.ECLAIR) {
//				event.startTracking();
//			} else {
				onBackKeyPressed();
//			}
        	return true;

        case  KeyEvent.KEYCODE_F12:
        case  KeyEvent.KEYCODE_I:
            showInfo();
            break;
		}
		return false;
	}

	protected void onBackKeyPressed() {
		showConfirmQuitDialog();
	}
	
	protected void showConfirmQuitDialog() {
		Dialog dialog = DialogFactory.createExitPromptDialog(context, this.getAppName(),
				new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						backKeyCount = 0;
						quitOrRestart(false);
					}
					
				}, new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						backKeyCount = 0;
					}
					
				});
		showDialog(dialog);
	}
	
//	public void initializeUserInterface(CommonUI ui) {
//		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
//		int width = display.getWidth();
//		int height = display.getHeight();
//		
//   		int rotation = display.getOrientation();
//   		if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)
//   			ui.setOrientation(Configuration.ORIENTATION_LANDSCAPE);
//   		else
//   			ui.setOrientation(Configuration.ORIENTATION_PORTRAIT);
//		ui.setScreenSize(width, height);
//		
//	}

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
	public boolean onOptionsItemSelected(Activity activity, android.view.MenuItem item) {
	    int itemId = item.getItemId();
	    
	    if (itemId == R.id.menuItemAbout) {
			showInfo(context.getResources().getBoolean(R.bool.showAcknowledgement)
					|| context.getResources().getString(R.string.app_acknowledgement).length() > 0);
			return true;
	    }
	    
		return false;
	}
	
	@Override
	public void showInfo() {
		showInfo(false);
	}
	
	protected abstract void showInfo(boolean showAcknowledgement);
	
	protected void showDialog(Dialog dialog) {
		if(dialog != null && !((Activity) context).isFinishing())
			dialog.show();
	}
	
	@Override
	public void showAlertDialog(String title, String message,
			OnClickListener okListener, OnClickListener cancelListener) {
		showAlertDialog(title, message, okListener, cancelListener, true);
	}
	
	@Override
	public void showAlertDialog(String title, String message, DialogInterface.OnClickListener okListener, 
			DialogInterface.OnClickListener cancelListner, boolean cancelable) {
		Dialog dialog = DialogFactory.createDialogBuilder(context, -1, title, message, okListener, cancelListner).create();
		dialog.setCancelable(cancelable);
		showDialog(dialog);
	}

	@Override
	public String getVersion() {
		return version;
	}

    public void setAcknowledgementTitle(String acknowledgementTitle) {
        this.acknowledgementTitle = acknowledgementTitle;
    }

    public void setAcknowledgementInfo(String acknowledgementInfo) {
        this.acknowledgementInfo = acknowledgementInfo;
    }
}
