package au.com.tyo.android.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import au.com.tyo.android.utils.CacheManager;
import au.com.tyo.io.IO;

/**
 * Created by monfee on 3/4/17.
 */

public class Loader<FileType> extends CacheManager<FileType> {

    private static final String LOG_TAG = "Loader";

    private DownloaderTask task;

    public interface Caller {
        void onDownloadFinished(Object file);
    }

    public Loader(Context context, String subdir){
        super(context, subdir);
        fileCache = new HashMap<String, SoftReference<FileType>>();
    }

    protected FileType load(String location, Caller caller) {
        FileType fileType = null;
        boolean asynchronously = caller != null;
        if (location != null) {

            try {
                fileType = fileCheck(location);
            } catch (Exception e) {
                Log.e(LOG_TAG, "having problems in loading cache.");
            }

            if(fileType == null){
                if (asynchronously) {
                    task = new DownloaderTask(caller);
                    task.execute(location);
                }
                else {
                    fileType = downloadFile(location);
                }
            }

        }

        return fileType;
    }

    public class DownloaderTask extends AsyncTask<String, Void, FileType> {

        private String location;
        private final WeakReference<Caller> reference;
        private Caller caller;

        public DownloaderTask(Caller caller) {
            this.caller = caller;
            reference = new WeakReference<Caller>(caller);
        }

        public String getLocation() {
            return location;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Thread.currentThread().setName("DownloaderTask");
        }

        /**
         * Actual download method, run in the task thread
         *
         */
        @Override
        protected FileType doInBackground(String... params) {
            // params comes from the execute() call: params[0] is the location.
            location = (String) params[0];
            FileType f = downloadFile(params[0]);
            return f;
        }

        @Override
        // Once the image is downloaded, associates it to the imageView
        protected void onPostExecute(FileType file) {
            if (null == file) {
                Log.e(LOG_TAG, "The value of downloaded file is null");
                return;
            }

            if (caller != null && file != null)
                caller.onDownloadFinished(file);

            if (isCancelled()) {
                Log.i(LOG_TAG, "Downloader task got cancelled");
                file = null;
            }
        }
    }

    /**
     * cancel a download (internal only)
     *
     * @param url
     * @param caller
     * @return
     */
    protected boolean cancelPotentialDownload(String url, Caller caller) {
        if (task != null) {
            String rl = task.getLocation();
            if ((rl == null) || (!rl.equals(url))) {
                task.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    /**
     * The actual loading logic
     */
    public FileType downloadFile(String url) {
        FileType fileType = null;
        if (url.startsWith(File.separator))
            try {
                fileType = getFileFromLocal(url);
            } catch (Exception e) {
                Log.e(LOG_TAG, "getting local file error: " + url);
            }
        else
            fileType = downloadFileWithUrl(url);
        return fileType;
    }

    public FileType getFileFromLocal(String url) throws Exception {
        return read(new File(url));
    }

    public FileType downloadFileWithUrl(String url) {
        FileType fileType = null;
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpClient client = new DefaultHttpClient(params);

        HttpGet getRequest = null;
        try {
            getRequest = new HttpGet(url);

            InputStream inputStream = null;

            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("Downloader", "Error " + statusCode + " while retrieving file from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    inputStream = entity.getContent();
                    fileType = processInputStream(inputStream);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
            if (getRequest != null) getRequest.abort();
            Log.w("Downloader", "Error while retrieving file from " + url + e.toString());
        } finally {
            if (client != null) {
                //client.close();
            }
        }
        return fileType;
    }

    protected FileType processInputStream(InputStream inputStream) {
        FileType file = null;
        try {
            file = (FileType) IO.readObject(inputStream);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Unable to read object from input stream");
        }
        return file;
    }

    public void writeFile(FileType fileType, String url) throws Exception {

        File f = locationToFile(url, false);

        SoftReference<FileType> ref = new SoftReference<FileType>(fileType);

        fileCache.put(f.getPath(), ref);

        write(fileType, f);
    }
}
