package au.com.tyo.android.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import au.com.tyo.data.ContentTypes;

public class ImageDownloader extends ResourceFetcher<Bitmap, ImageView> {

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
    protected Bitmap processInputStream(InputStream inputStream, String url) {
        if (ContentTypes.isSVG(url)) {
            SVG svg = null;
            Bitmap bitmap = null;
            try {
                svg = SVG.getFromInputStream(inputStream);
                bitmap = Bitmap.createBitmap((int) svg.getDocumentWidth(), (int) svg.getDocumentHeight(), Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(bitmap);
                canvas.drawPicture(svg.renderToPicture());

            } catch (SVGParseException e) {
                Log.e(LOG_TAG, "Can't create svg file");
            }

            return bitmap;
        }

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

