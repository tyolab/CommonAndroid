/*
 * Copyright (C) 2017 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 */

package au.com.tyo.android.images.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapUtils {
	
	private static final String LOG_TAG = "BitmapUtils";

	public static byte[] bitmapToBytesInPNG(Bitmap bitmap) {
		return bitmapToBytes(bitmap, Bitmap.CompressFormat.PNG);
	}

	/**
	 *
	 * @param bitmap
	 * @param format
	 * @return
	 */
	public static byte[] bitmapToBytes(Bitmap bitmap, Bitmap.CompressFormat format) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(format, 100, stream);
		byte[] bytes = stream.toByteArray();
		return bytes;
	}

	/**
	 *
	 * @param bitmap
	 * @param photo
	 * @throws IOException
	 */
	public static void toJPG(Bitmap bitmap, File photo) throws IOException {
		saveTo(bitmap, photo, Bitmap.CompressFormat.JPEG);
	}

	/**
	 *
	 * @param bitmap
	 * @param photo
	 * @throws IOException
	 */
	public static void toPNG(Bitmap bitmap, File photo) throws IOException {
		saveTo(bitmap, photo, Bitmap.CompressFormat.PNG);
	}

	/**
	 *
	 * @param bitmap
	 * @param format
	 * @return
	 * @throws IOException
	 */
	public static String toBase64EncodedString(Bitmap bitmap, Bitmap.CompressFormat format) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		saveTo(bitmap, stream, format);
		String encodedString = android.util.Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
		stream.close();
		return encodedString;
	}

	/**
	 *
	 * @param bitmap
	 * @param photo
	 * @param format
	 * @throws IOException
	 */
	public static void saveTo(Bitmap bitmap, File photo, Bitmap.CompressFormat format) throws IOException {
		OutputStream stream = new FileOutputStream(photo);
		saveTo(bitmap, stream, format);
		stream.close();
	}

	/**
	 *
	 * @param bitmap
	 * @param stream
	 * @param format
	 * @throws IOException
	 */
	public static void saveTo(Bitmap bitmap, OutputStream stream, Bitmap.CompressFormat format) throws IOException {
		Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(bitmap, 0, 0, null);
		newBitmap.compress(format, 80, stream);
	}

    /**
     *
     * @param drawable
     * @return
     */
	public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof PictureDrawable) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawPicture(((PictureDrawable) drawable).getPicture());
        }
        else if (drawable instanceof BitmapDrawable && (bitmap = ((BitmapDrawable) drawable).getBitmap()) != null) {
            //
        }

        if (bitmap == null) {
            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return bitmap;
	}

    /**
     *
     * @param urlStr
     * @return
     */
	public static Bitmap getBitmapFromURL(String urlStr) {
		Bitmap bitmap = null;
	    try {
	        URL url = new URL(urlStr);
	        
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        
	        InputStream input = connection.getInputStream();
	        bitmap = inputStreamToBitmap(input);
	    } catch (IOException e) {
	    	Log.e(LOG_TAG, "cannot get the image from link :" + urlStr);
	    }
        return bitmap;
	}

	public static Bitmap getBitmapFromAssets(Context context, String path) throws IOException {
		InputStream inputStream = context.getAssets().open(path);
		return inputStreamToBitmap(inputStream);
	}

    public static Bitmap getBitmapFromFile(String path) throws IOException {
        return getBitmapFromFile(new File(path));
    }

    public static Bitmap getBitmapFromFile(File path) throws IOException {
        InputStream inputStream = new FileInputStream(((path)));
        return inputStreamToBitmap(inputStream);
    }

    /**
     *
     * @param bitmap
     * @return
     */
	public static byte[] bitmapToBytes(Bitmap bitmap) {
		ByteArrayOutputStream stream = bitmapToByteArrayOutputStream(bitmap);
		byte[] bytes = stream.toByteArray();
        try {
            stream.close();
        }
        catch (Exception ex) {

        }
		return bytes;
	}

    /**
     *
     * @param bytes
     * @return
     */
	public static Bitmap bytesToBitmap(byte[] bytes) {
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

    /**
     *
     * @param stream
     * @return
     */
	public static Bitmap inputStreamToBitmap(InputStream stream) {
		return BitmapFactory.decodeStream(stream);
	}

	/**
	 * http://stackoverflow.com/questions/8381514/android-converting-color-image-to-grayscale
	 *
	 * @param original
	 * @return
	 */
	public static Bitmap toGrayscale(Bitmap original) {
        /*
        similarly
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = src.getPixel(x, y);
                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int)(0.299 * R + 0.587 * G + 0.114 * B);
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
         */
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		return colorFilterIt(original, cm);
	}

	public static Bitmap colorFilterIt(Bitmap original, ColorMatrix cm) {
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		return colorFilterIt(original, f);
	}

    public static Bitmap colorFilterIt(Bitmap original, ColorFilter colorFilter) {
        int width, height;
        height = original.getHeight();
        width = original.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, original.getConfig());
        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();

        paint.setColorFilter(colorFilter);
        c.drawBitmap(original, 0, 0, paint);

        return bitmap;
    }

	public static Bitmap toSepia(Bitmap original) {
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);

		ColorMatrix colorScale = new ColorMatrix();
		colorScale.setScale(1, 1, 0.8f, 1);

		cm.postConcat(colorScale);

		return colorFilterIt(original, cm);
	}

	public static Bitmap toBinary(Bitmap original) {
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);

		float m = 255f;
		float t = -255*128f;
		ColorMatrix threshold = new ColorMatrix(new float[] {
				m, 0, 0, 1, t,
				0, m, 0, 1, t,
				0, 0, m, 1, t,
				0, 0, 0, 1, 0
		});

		cm.postConcat(threshold);

		return colorFilterIt(original, cm);
	}

    public static Bitmap toInvert(Bitmap original) {
        ColorMatrix cm = new ColorMatrix(new float[] {
                -1,  0,  0,  0, 255,
                0, -1,  0,  0, 255,
                0,  0, -1,  0, 255,
                0,  0,  0,  1,   0
        });

        return colorFilterIt(original, cm);
    }

    public static Bitmap toAlphaBlue(Bitmap original) {
        ColorMatrix cm = new ColorMatrix(new float[] {
                0, 0, 0, 0, 0,
                0.3f, 0, 0, 0, 50,
                0, 0, 0, 0, 255,
                0.2f, 0.4f, 0.4f, 0, -30
        });

        return colorFilterIt(original, cm);
    }

    public static Bitmap toAlphaPink(Bitmap original) {
        ColorMatrix cm = new ColorMatrix(new float[] {
                0, 0, 0, 0, 255,
                0, 0, 0, 0, 0,
                0.2f, 0, 0, 0, 50,
                0.2f, 0.2f, 0.2f, 0, -20
        });

        return colorFilterIt(original, cm);
    }

    public static Bitmap redIt(Bitmap original) {
        return colorFilterIt(original, new LightingColorFilter(Color.RED, 0));
    }

    public static Bitmap blueIt(Bitmap original) {
        return colorFilterIt(original, new LightingColorFilter(Color.BLUE, 0));
    }

    public static Bitmap greenIt(Bitmap original) {
        return colorFilterIt(original, new LightingColorFilter(Color.GREEN, 0));
    }

    public static Bitmap yellowIt(Bitmap original) {
        return colorFilterIt(original, new LightingColorFilter(Color.YELLOW, 0));
    }


	/**
	 * Convert View to BITMAP
	 *
	 * solutions were found here:
	 * http://stackoverflow.com/questions/2801116/converting-a-view-to-bitmap-without-displaying-it-in-android
	 *
	 */
	public static Bitmap getViewBitmapFromCache(View v) {
		v.clearFocus();
		v.setPressed(false);

		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);

		// Reset the drawing cache background color to fully transparent
		// for the duration of this operation
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);

		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {
			Log.e(LOG_TAG, "failed getViewBitmap(" + v + ")", new RuntimeException());
			return null;
		}

		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);

		return bitmap;
	}

	/**
	 * Alternatively,
	 * http://stackoverflow.com/questions/5536066/convert-view-to-bitmap-on-android
	 *
	 * @param view
	 * @return
	 */
	public static Bitmap getViewBitmap(View view) {
		//Define a bitmap with the same size as the view
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		//Bind a canvas to it
		Canvas canvas = new Canvas(returnedBitmap);
		//Get the view's background
		Drawable bgDrawable = view.getBackground();
		if (bgDrawable!=null)
			//has background drawable, then draw it on the canvas
			bgDrawable.draw(canvas);
		else
			//does not have background drawable, then draw white background on the canvas
			canvas.drawColor(Color.WHITE);
		// draw the view on the canvas
		view.draw(canvas);
		//return the bitmap
		return returnedBitmap;
	}

	public static ByteArrayOutputStream bitmapToByteArrayOutputStream(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream;
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, float scale) {
		int width = Math.round(bitmap.getWidth() * scale);
		int height = Math.round(bitmap.getHeight() * scale);
		Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(target);
		canvas.scale(scale, scale);
		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return target;
	}
}
