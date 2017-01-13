package au.com.tyo.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogFactory {
	
	public static DialogInterface.OnClickListener dismissMeListener = new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int which) {
        	dialog.dismiss();
	    }
	};
	
	public static AlertDialog.Builder getBuilder(Context context, int style) {
		return getBuilder(context, style, android.R.attr.alertDialogIcon);
	}
	
	public static AlertDialog.Builder getBuilder(Context context, int style, int iconResId) {
		AlertDialog.Builder builder;
		if (AndroidUtils.getAndroidVersion() > 10 && style > 0) {
			builder = new AlertDialog.Builder(context, style)
	        .setIconAttribute(iconResId);
		}
		else
			builder = new AlertDialog.Builder(context);
		return builder;
	}
	
	public static DialogInterface.OnClickListener createDissmissListener(final Activity activity) {
		return createDissmissListener(activity, false);
	}
	
	public static DialogInterface.OnClickListener createDissmissListener(final Activity activity, final boolean exitApp) {
		return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
        		dialog.dismiss();
        		if (exitApp)
        			AndroidUtils.exit(activity);
            }
        };
	}
	
	public static Builder createDialogBuilder(Context context, int themeResId, String title, 
			String message) {
		return createDialogBuilder(context, themeResId, title, message, null, null);
	}
	
	public static AlertDialog.Builder createDialogBuilder(Context context, int themeResId, String title, 
			String message, 
			DialogInterface.OnClickListener okListener, 
			DialogInterface.OnClickListener cancleListener) {
		
		AlertDialog.Builder builder = getBuilder(context, themeResId);
		
		builder
        .setTitle(title)
        .setMessage(message);
		
		if (okListener != null)
			builder.setPositiveButton(R.string.alert_dialog_ok, okListener);
		
		if (cancleListener != null)
			builder.setNegativeButton(R.string.alert_dialog_cancel,  cancleListener);
		
       return builder;
	}
	
	public static Dialog createHoloLightDialog(Context context, String title, 
			String message, 
			DialogInterface.OnClickListener okListener, 
			DialogInterface.OnClickListener cancleListener) {
		
		return createDialogBuilder(context, AlertDialog.THEME_HOLO_LIGHT, title, message, okListener, cancleListener).create();
	}
	
	public static Dialog createClearCacheDialog(Context context, DialogInterface.OnClickListener listener) {
		Dialog dialog = null;
		dialog = new AlertDialog.Builder(context)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle(R.string.clear_cache)
		.setMessage(R.string.clear_cache_prompt)
		.setPositiveButton(R.string.alert_dialog_ok, listener)
		.setNegativeButton(R.string.alert_dialog_cancel, null)
		.create();
		return dialog;
	}
	
	public static Dialog createExitPromtDialog(Context context, String what, DialogInterface.OnClickListener listener) {
		return createExitPromtDialog(context, what, listener, null);
	}
	
	public static Dialog createExitPromtDialog(final Context context, String what, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener cancelListener) {
		Dialog dialog = null;
		dialog = new AlertDialog.Builder(context)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle(String.format("Closing %s", what))
		.setMessage(R.string.exit_app_prompt)
		.setPositiveButton(R.string.alert_dialog_ok, listener == null ? new DialogInterface.OnClickListener()
		{
		    public void onClick(DialogInterface dialog, int which) {
		        /*
		         * From the Browser code
		         * ==============================================================
		         * Instead of finishing the activity, simply push this to the back
		         * of the stack and let ActivityManager to choose the foreground
		         * activity. As BrowserActivity is singleTask, it will be always the
		         * root of the task. So we can use either true or false for
		         * moveTaskToBack().
		         */
		    	CommonApplicationImpl.quitOrRestart(context, false);
		    }
		
		} : listener)
		.setNegativeButton(R.string.alert_dialog_cancel, cancelListener)
//		.setNegativeButton(R.string.alert_dialog_cancel, null)
		.create();
		return dialog;
	}
	
	public static void createExternalDirectoryChooser(Context context, String[] storages,
			AndroidSettings settings) {
		createExternalDirectoryChooser(context, storages, settings, null);
	}
	
	@SuppressLint("NewApi")
	public static void createExternalDirectoryChooser(Context context, 
														final String[] storages,
														final AndroidSettings settings, 
														DialogInterface.OnClickListener listener) {
		
		DialogInterface.OnClickListener newListener =  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	int index = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
            	
            	settings.setDataStoragePath(storages[index]);
            	
            	dialog.dismiss();
            }
        };
        
        final String[] items = new String[storages.length];
        int count = 0;
        for (String str : storages) 
        	items[count++] = String.format("%s (%.2fG)", str, AndroidUtils.getStorageSizeInGigabytes(str));
		
		AlertDialog.Builder builder = getBuilder(context, -1, 
				settings.isLightThemeUsed() ? R.drawable.ic_action_device_access_sd_storage_light : R.drawable.ic_action_device_access_sd_storage_dark)
            .setTitle(R.string.please_choose_an_external_storage_for_data)
            .setSingleChoiceItems(items, 0, listener == null ? newListener : listener)
            .setPositiveButton(R.string.alert_dialog_ok, listener == null ? newListener : listener);
	
		if (AndroidUtils.getAndroidVersion() >= 11)
			builder.setIconAttribute(android.R.drawable.ic_menu_manage);
		
		Dialog dialog = builder.create();
		
		showDialog((Activity) context, dialog);
	}
	
	public static void showDialog(Activity activity, Dialog dialog) {
		if(dialog != null && !activity.isFinishing())
			dialog.show();
	}
}
