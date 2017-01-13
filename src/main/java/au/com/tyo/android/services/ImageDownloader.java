package au.com.tyo.android.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageDownloader extends Downloader<Bitmap, ImageView> {
	
	private static final String LOG_TAG = "ImageDownloader";
	
	public ImageDownloader(Context context, String subdir){
		super(context, subdir);
	}

	@Override
	public Bitmap read(File f) {
		return BitmapFactory.decodeFile(f.getPath());
	}

	///////////////////////
	@Override
	public void write(Bitmap target, File f) {
		FileOutputStream out = null;
		
		try {
			out = new FileOutputStream(f);
			target.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally { 
			try { if (out != null ) out.close(); }
			catch(Exception ex) {} 
		}
	}

	@Override
	protected Bitmap processInputStream(InputStream inputStream) {
		return BitmapFactory.decodeStream(inputStream);
	}

}

