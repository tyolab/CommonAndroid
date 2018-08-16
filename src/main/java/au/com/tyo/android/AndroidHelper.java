    package au.com.tyo.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.com.tyo.data.ContentTypes;

    /**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 6/8/18.
 */
public class AndroidHelper {

    /**
     *
     * @param type
     * @return
     */
    public static Intent createOpenDocumentIntent(String type) {
        Intent intent = null;
        if (AndroidUtils.getAndroidVersion() >= 19)
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        else
            intent = new Intent(Intent.ACTION_GET_CONTENT);
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
        openDocumentManager(context, requestCode, null);
    }

    public static void openDocumentManager(Activity context, int requestCode, String type) {
        Intent intent = createOpenDocumentIntent(type);

        if (requestCode > -1)
            context.startActivityForResult(intent, requestCode);
        else
            context.startActivity(intent);
    }

    public static void openImageManager(Activity context) {
        openDocumentManager(context, Constants.REQUEST_CODE_PICK_IMAGE, "image/*");
    }

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
        Intent intent = createOpenDocumentIntent(type);

        if (AndroidUtils.getAndroidVersion() < 24)
            intent.setData(Uri.fromFile(new File(path)));
        else {
            intent.setData(FileProvider.getUriForFile(context, authority, new File(path)));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        context.startActivity(intent);
    }

    public static Intent shareToOtherApps(String title, Context ctx, String imagePath) {
        return shareExclude(title, ctx, AndroidUtils.getPackageName(ctx), imagePath);
    }

    /**
     *
     * @param title
     * @param ctx
     * @param packageNameToExclude
     * @param imagePath
     * @return
     */
    public static Intent shareExclude(String title, Context ctx, String packageNameToExclude, String imagePath) {
        return shareExclude(title, ctx, packageNameToExclude, imagePath, null);
    }

    /**
     *
     * https://stackoverflow.com/questions/4064848/how-to-exclude-your-own-app-from-the-share-menu
     *
     * @param ctx
     * @param packageNameToExclude
     * @param imagePath
     * @param text
     * @return
     */
    public static Intent shareExclude(String title, Context ctx, String packageNameToExclude, String imagePath, String text) {
        List<Intent> targetedShareIntents = new ArrayList<>();
        Intent share = new Intent(android.content.Intent.ACTION_SEND);

        if (ContentTypes.isImage(imagePath))
            share.setType("image/*");
        else
            share.setType("*/*");

        List<ResolveInfo> resInfo = ctx.getPackageManager().queryIntentActivities(createShareIntent(text,new File(imagePath)), 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                Intent targetedShare = createShareIntent(text, new File(imagePath));

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

    private static Intent createShareIntent(String text, File file) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("image/*");
        if (text != null) {
            intent.putExtra(Intent.EXTRA_TEXT, text);
        }
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        return intent;
    }
}
