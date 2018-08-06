package au.com.tyo.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 6/8/18.
 */
public class AndroidHelper {

    public static void openDocumentManager(Activity context) {
        openDocumentManager(context, Constants.REQUEST_CODE_PICK_DOCUMENT);
    }

    public static void openDocumentManager(Activity context, int requestCode) {
        openDocumentManager(context, requestCode, "*/*");
    }

    public static void openDocumentManager(Activity context, int requestCode, String type) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(type);
        context.startActivityForResult(intent, requestCode);
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
}
