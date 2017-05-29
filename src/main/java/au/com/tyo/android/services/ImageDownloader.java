package au.com.tyo.android.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ImageDownloader extends Downloader<Bitmap, ImageView> {
	
	private static final String LOG_TAG = "ImageDownloader";

	private int quality = 80;

	private int sampleSize = 2;

	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public ImageDownloader(Context context, String subdir){
		super(context, subdir);
	}

	@Override
	public Bitmap read(File f) {
		return BitmapFactory.decodeFile(f.getPath());
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}


	///////////////////////
	@Override
	public void write(Bitmap target, File f) {
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(f);
			target.compress(Bitmap.CompressFormat.PNG, quality, out);
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
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
		return BitmapFactory.decodeStream(inputStream, null, options);
	}

    @Override
    public void handleResult(ImageView container, Bitmap file) {
        if (null != container && file != null) {
            container.setImageBitmap(file);
        }

        /**
         * can't recycle it yet
         */
//        if (file != null) {
//            file.recycle();
//            file = null;
//        }
    }
}

