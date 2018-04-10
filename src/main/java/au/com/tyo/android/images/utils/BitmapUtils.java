/*
 * Copyright (C) 2017 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 */

package au.com.tyo.android.images.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapUtils {
	
	private static final String LOG_TAG = "BitmapUtils";

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
	 * @param bmpOriginal
	 * @return
	 */
	public Bitmap toGrayscale(Bitmap bmpOriginal) {
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
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
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
