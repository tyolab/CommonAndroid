package au.com.tyo.android;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.com.tyo.data.ContentTypes;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 6/8/18.
 */
public class AndroidHelper {

    /**
     * Pick folder
     *
     * @param context
     * @param title
     * @param requestCode
     */
    public static void pickFolder(Activity context, String title, int requestCode) {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        context.startActivityForResult(Intent.createChooser(i, title), requestCode);
    }

    /**
     *
     * @param type
     * @return
     */
    public static Intent createSelectDocumentIntent(String type) {
        return createSelectDocumentIntent(type, false, false);
    }

    public static Intent createSelectDocumentIntent(String type, boolean allowMultipleSelection, boolean persistentAccess) {
        Intent intent = null;
        if (persistentAccess && AndroidUtils.getAndroidVersion() >= 19)
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        else
            intent = new Intent(Intent.ACTION_GET_CONTENT);

        if (allowMultipleSelection) {
            if (AndroidUtils.getAndroidVersion() >= 18)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            else
                Log.w("AndroidHelper", "EXTRA_ALLOW_MULTIPLE option is only available in Android API 18 and higher");
        }

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (null != type)
            intent.setType(type);
        else
            intent.setType("*/*");

        return intent;
    }

    /**
     *
     * @param context
     */
    public static void openDocumentManager(Activity context) {
        openDocumentManager(context, Constants.REQUEST_CODE_PICK_DOCUMENT);
    }

    /**
     *
      * @param context
     * @param requestCode
     */
    public static void openDocumentManager(Activity context, int requestCode) {
        openDocumentManager(context, requestCode, null, false, false);
    }

        /**
         *
         * @param context
         * @param requestCode
         * @param allowMultipleSelection
         * @param persistentAccess
         */
    public static void openDocumentManager(Activity context, int requestCode, boolean allowMultipleSelection, boolean persistentAccess) {
        openDocumentManager(context, requestCode, null, allowMultipleSelection, persistentAccess);
    }

    /**
     *  @param context
     * @param requestCode
     * @param type
     * @param allowMultipleSelection
     * @param persistentAccess
     */
    public static void openDocumentManager(Activity context, int requestCode, String type, boolean allowMultipleSelection, boolean persistentAccess) {
        Intent intent = createSelectDocumentIntent(type, allowMultipleSelection, persistentAccess);

        if (requestCode > -1)
            context.startActivityForResult(intent, requestCode);
        else
            context.startActivity(intent);
    }

    /**
     *
     * @param context
     */
    public static void openImageManager(Activity context) {
        openImageManager(context, false);
    }

    /**
     *
     * @param context
     * @param allowMultipleSelection
     */
    public static void openImageManager(Activity context, boolean allowMultipleSelection) {
        openDocumentManager(context, allowMultipleSelection ? Constants.REQUEST_CODE_PICK_IMAGES : Constants.REQUEST_CODE_PICK_IMAGE, "image/*", allowMultipleSelection, false);
    }

    /**
     *
     * @param context
     */
    public static void openGalleryApp(Activity context) {
        Intent galleryIntent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        context.startActivity(galleryIntent);
    }

    public static void openSamsungMyFiles(Activity context) {
        Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        intent.putExtra("CONTENT_TYPE", "*/*");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
    }

    public static boolean isSamsungDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("samsung");
    }

    /**
     *
     * @param context
     * @param path
     * @param type
     */
    public static void openFolder(Activity context, String path, String type, String authority) {
        Intent intent = createSelectDocumentIntent(type);

        if (AndroidUtils.getAndroidVersion() < 24)
            intent.setData(Uri.fromFile(new File(path)));
        else {
            intent.setData(FileProvider.getUriForFile(context, authority, new File(path)));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        context.startActivity(intent);
    }

    public static Intent shareToOtherApps(String title, Context ctx, Uri imagePath, String type) {
        return shareExclude(title, ctx, AndroidUtils.getPackageName(ctx), imagePath, type);
    }

    /**
     *
     * @param title
     * @param ctx
     * @param packageNameToExclude
     * @param imagePath
     * @return
     */
    public static Intent shareExclude(String title, Context ctx, String packageNameToExclude, Uri imagePath, String type) {
        return shareExclude(title, ctx, packageNameToExclude, imagePath, null, type);
    }

    /**
     *
     * https://stackoverflow.com/questions/4064848/how-to-exclude-your-own-app-from-the-share-menu
     *
     * @param context
     * @param packageNameToExclude
     * @param uri
     * @param text
     * @return
     */
    public static Intent shareExclude(String title, Context context, String packageNameToExclude, Uri uri, String text, String type) {
        List<Intent> targetedShareIntents = new ArrayList<>();
        Intent share = new Intent(android.content.Intent.ACTION_SEND);

        if (null != type) {
            try {
                ContentResolver contentResolver = context.getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                type = mime.getExtensionFromMimeType(contentResolver.getType(uri));
            }
            catch (Exception ex) {
                type = "*/*";
            }
        }

        share.setType(type);

        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(createShareIntent(text, (uri), type), 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                Intent targetedShare = createShareIntent(text, (uri), type);

                if (!info.activityInfo.packageName.equalsIgnoreCase(packageNameToExclude)) {
                    targetedShare.setPackage(info.activityInfo.packageName);
                    targetedShareIntents.add(targetedShare);
                }
            }

            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0),
                    title);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    targetedShareIntents.toArray(new Parcelable[] {}));
            return chooserIntent;
        }
        return null;
    }

    /**
     *
     * @param text
     * @param file
     * @return
     */
    public static Intent createShareIntent(String text, File file, String type) {
        return createShareIntent(text, Uri.fromFile(file), type);
    }

    /**
     *
     * @param text
     * @param uri
     * @param type
     * @return
     */
    public static Intent createShareIntent(String text, Uri uri, String type) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType(type);
        if (text != null) {
            intent.putExtra(Intent.EXTRA_TEXT, text);
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        return intent;
    }

    /**
     *
      * @param activity
     */
    public static void setFullScreenMode(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        int oldOptions = decorView.getSystemUiVisibility();
        decorView.setSystemUiVisibility(uiOptions | oldOptions);
    }

    /**
     *
     * @param activity
     */
    public static void hideHardwareButtons(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        int oldOptions = decorView.getSystemUiVisibility();
        decorView.setSystemUiVisibility(uiOptions | oldOptions);
    }
}
