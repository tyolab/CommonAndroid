/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.com.tyo.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.*;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapUtils {
	
	private static final String LOG_TAG = "BitmapUtils";

	public static Bitmap getBitmapFromURL(String urlStr) {
		Bitmap bitmap = null;
	    try {
	        URL url = new URL(urlStr);
	        
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        
	        InputStream input = connection.getInputStream();
	        bitmap = BitmapFactory.decodeStream(input);
	    } catch (IOException e) {
	    	Log.e(LOG_TAG, "cannot get the image from link :" + urlStr);
	    }
        return bitmap;
	}
	
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
	 * @param bytes
	 * @return
	 */
	public static Bitmap bytesToBitmap(byte[] bytes) {
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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
}
