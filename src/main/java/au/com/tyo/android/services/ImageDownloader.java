package au.com.tyo.android.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import au.com.tyo.android.images.svg.SVG;
import au.com.tyo.android.images.svg.SVGBuilder;
import au.com.tyo.android.images.utils.BitmapUtils;
import au.com.tyo.data.ContentTypes;

public class ImageDownloader extends ResourceFetcher<Drawable, ImageView> {
	
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
	public Drawable read(File f) {
		return new BitmapDrawable(getContext().getResources(), BitmapFactory.decodeFile(f.getPath()));
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}


	///////////////////////
	@Override
	public void write(Drawable target, File f) {
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(f);
            Bitmap bitmap = BitmapUtils.drawableToBitmap((PictureDrawable) target);
			bitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try { if (out != null ) out.close(); }
			catch(Exception ex) {} 
		}
	}

	@Override
	protected Drawable processInputStream(InputStream inputStream, String url) {
		if (ContentTypes.isSVG(url)) {
            SVG svg = new SVGBuilder()
                    .readFromInputStream(inputStream)
                    .build();

            Drawable drawable = svg.getDrawable();
            return drawable;
		}

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
		return new BitmapDrawable(getContext().getResources(), bitmap);
	}

    @Override
    public void handleResult(ImageView container, Drawable file) {
        if (null != container && file != null) {
            // container.setImageBitmap(file);
            //
            Bitmap bitmap = BitmapUtils.drawableToBitmap(file);
            container.setImageBitmap(bitmap);
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

