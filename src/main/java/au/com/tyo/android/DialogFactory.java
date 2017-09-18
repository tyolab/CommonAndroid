package au.com.tyo.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

public class DialogFactory {

	/**
	 *
	 */
	public static DialogInterface.OnClickListener dismissMeListener = new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int which) {
        	dialog.dismiss();
	    }
	};

	/**
	 *
	 * @param context
	 * @param style
	 * @return
	 */
	public static AlertDialog.Builder getBuilder(Context context, int style) {
		return getBuilder(context, style, android.R.attr.alertDialogIcon);
	}

	/**
	 *
	 * @param context
	 * @param style
	 * @param iconResId
	 * @return
	 */
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

	/**
	 *
	 * @param activity
	 * @return
	 */
	public static DialogInterface.OnClickListener createDismissListener(final Activity activity) {
		return createDismissListener(activity, false);
	}

	/**
	 *
	 * @param activity
	 * @param exitApp
	 * @return
	 */
	public static DialogInterface.OnClickListener createDismissListener(final Activity activity, final boolean exitApp) {
		return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
        		dialog.dismiss();
        		if (exitApp)
        			AndroidUtils.exit(activity);
            }
        };
	}

	/**
	 *
	 * @param context
	 * @param themeResId
	 * @param title
	 * @param message
	 * @return
	 */
	public static AlertDialog.Builder createDialogBuilder(Context context, int themeResId, String title,
                                                          String message) {
		return createDialogBuilder(context, themeResId, title, message, null, null);
	}

	/**
	 *
	 * @param context
	 * @param themeResId
	 * @param dialogArrayResId
	 * @param okListener
	 * @param cancelListener
	 * @return
	 */
	public static Dialog createDialog(Context context, int themeResId, int dialogArrayResId,
                                      DialogInterface.OnClickListener okListener,
                                      DialogInterface.OnClickListener cancelListener) {
		String [] strings = context.getResources().getStringArray(dialogArrayResId);
		return createDialog(context, themeResId, strings[0], strings[1], okListener, cancelListener);
	}

	public static Dialog createDialog(Context context, int themeResId, String title,
                                      String message,
                                      DialogInterface.OnClickListener okListener,
                                      DialogInterface.OnClickListener cancelListener) {
		Dialog dialog;
		AlertDialog.Builder builder = createDialogBuilder(context, themeResId, title, message, okListener, cancelListener);
		dialog = builder.create();
		return dialog;
	}
	
	public static AlertDialog.Builder createDialogBuilder(Context context, int themeResId, String title, 
			String message, 
			DialogInterface.OnClickListener okListener, 
			DialogInterface.OnClickListener cancelListener) {
		
		AlertDialog.Builder builder = getBuilder(context, themeResId);
		
		builder
        .setTitle(title)
        .setMessage(message);
		
		if (okListener != null)
			builder.setPositiveButton(R.string.alert_dialog_ok, okListener);
		
		if (cancelListener != null)
			builder.setNegativeButton(R.string.alert_dialog_cancel,  cancelListener);
		
       return builder;
	}
	
	public static Dialog createHoloLightDialog(Context context, String title, 
			String message, 
			DialogInterface.OnClickListener okListener, 
			DialogInterface.OnClickListener cancleListener) {
		
		return createDialogBuilder(context, R.style.Theme_AppCompat_Light_Dialog, title, message, okListener, cancleListener).create();
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
	
	public static Dialog createExitPromptDialog(Context context, String what, DialogInterface.OnClickListener listener) {
		return createExitPromptDialog(context, what, listener, null);
	}
	
	public static Dialog createExitPromptDialog(final Context context, String what, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener cancelListener) {
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

	public static void setDialogAttributes(final AlertDialog dialog, final int button1Color, final int button2Color) {
		setDialogAttributes(dialog, true, button1Color, button2Color);
	}

	/**
	 *
	 * @param dialog
	 * @param button1Color
	 * @param button2Color
	 */
    public static void setDialogAttributes(final AlertDialog dialog, final boolean verticalButtons, final int button1Color, final int button2Color) {

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface di) {
                AlertDialog dia = (AlertDialog) di;
                final Button button1 = dia.getButton(DialogInterface.BUTTON_POSITIVE);
                final Button button2 = dia.getButton(DialogInterface.BUTTON_NEGATIVE);

                if (button1Color != -1  && null != button1)
                    button1.setTextColor(button1Color);

                if (button2Color != -1 && null != button2)
                    button2.setTextColor(button2Color);

				if (verticalButtons)
					try {
						LinearLayout linearLayout = (LinearLayout) button1.getParent();
						if (linearLayout != null) {
							linearLayout.setOrientation(LinearLayout.VERTICAL);
							linearLayout.setGravity(Gravity.RIGHT);
						}
					} catch (Exception ignored) {

					}
            }
        });
    }

    /**
     * This method won't work, as the buttons are not created when the dialog is
     *
     * @param dialog
     * @param whatButton
     * @param color
     */
    public static void setDialogButtonColor(AlertDialog dialog, int whatButton, int color) {
        Button button = dialog.getButton(whatButton);
        if (null != button)
            button.setTextColor(color);
    }
}
