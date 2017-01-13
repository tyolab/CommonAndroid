package au.com.tyo.android.services;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;

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

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import au.com.tyo.android.utils.CacheManager;

public abstract class Downloader<FileType, ContainerType> extends CacheManager<FileType>
	implements DownloaderInterface<FileType, ContainerType> {
	
	private static final String LOG_TAG = "Downloader";
	
	protected DownloadListener<FileType> caller;
	
	protected HashMap<ContainerType, DownloaderTask> tasks;
	
	protected Handler handler;
	
	public interface DownloadListener<FileType> {
		void onDownloadFinished(FileType file);
	}
	
	private static class DownloadPair<FileType, ContainerType> {
		
		ContainerType container;
		
		FileType file;
		
		public DownloadPair(ContainerType container, FileType file) {
			this.container = container;
			this.file = file;
		}
		
		public ContainerType getContainer() { return this.container; }
		public FileType getFile() { return this.file; }
	}
	
	public static class MessageHandler<FileType, ContainerType> extends Handler {
		
		DownloaderInterface<FileType, ContainerType> downloader;
		
		public MessageHandler(DownloaderInterface<FileType, ContainerType> downloader) {
			this.downloader = downloader;
		}
		
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				DownloadPair<FileType, ContainerType> pair = (DownloadPair<FileType, ContainerType>) msg.obj;
				downloader.handleResult(pair.container, pair.file);
			}
		}
		
	}
	
	public Downloader(Context context, String subdir){
		super(context, subdir);
		fileCache = new HashMap<String, SoftReference<FileType>>();
		setCaller(null);
		tasks = new HashMap<ContainerType, DownloaderTask>();
		handler = new MessageHandler<FileType, ContainerType>(this);
	}
	
	public void handleResult(ContainerType container, FileType file) {
		
		if (container instanceof ImageView && file instanceof Bitmap)
			((ImageView) container).setImageBitmap((Bitmap) file);
		
	}

	public DownloadListener<FileType> getCaller() {
		return caller;
	}

	public void setCaller(DownloadListener<FileType> caller) {
		this.caller = caller;
	}
	
	public FileType download(String url) {
		return download(url, null, false);
	}
	
	protected FileType download(String url, ContainerType container, boolean asynchronously) {
		FileType fileType = null;
	     if (url != null && cancelPotentialDownload(url, container)) {
	    	 
	    	 //Caching code right here
//	    	 SoftReference<FileType> bitmapRef = (SoftReference<FileType>)fileCachgianna michaelse.get(f.getPath());
	    	 try {
				fileType = fileCheck(url);
			} catch (Exception e) {
				Log.e(LOG_TAG, "having problems in loading image cache.");
			}
	    	 
	    	  //No? download it
	    	  if(fileType == null){
	    		  if (asynchronously) {
		    		  DownloaderTask task = new DownloaderTask(container);
		    		  tasks.put(container, task);
		    		  task.execute(url);
	    		  }
	    		  else {
	    			  fileType = downloadFile(container, url);
	    			  if (fileType != null) {
		    			  if (!url.startsWith(File.separator))
			    			  try {
			    				  if (fileType != null)
			    					  writeFile(fileType, url);
			    			  } catch (Exception e) {
			    				  Log.e(LOG_TAG, "failed download image: " + url);
			    			  }
		    			  handleResult(container, fileType);
	    			  }
	    		  }
	    	  }
	    	  
	     }
	     
//		  //Yes? set it
		  if (container != null && fileType != null)
			  handleResult(container, fileType);
		  
	     return fileType;
	}
	
	public FileType download(String url, ContainerType container) {
		return download(url, container, true);
	}
	
	protected DownloaderTask getDownloaderTask(ContainerType container) {
		return tasks.get(container);
	}
	
    public class DownloaderTask extends AsyncTask<String, Void, FileType> {

		private String url;
        private final WeakReference<ContainerType> reference;

        public DownloaderTask(ContainerType container) {
            reference = new WeakReference<ContainerType>(container);
        }
        
        public String getUrl() {
        	return url;
        }
        
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			Thread.currentThread().setName("DownloaderTask");
		}

        @Override
        // Actual download method, run in the task thread
        protected FileType doInBackground(String... params) {
             // params comes from the execute() call: params[0] is the url.
        	 url = (String) params[0];
             return downloadFile(reference.get(), params[0]);
        }

        @Override
        // Once the image is downloaded, associates it to the imageView
        protected void onPostExecute(FileType file) {
        	// I don't care what happen next, but surely have ot get rid of task in the hashmap
            ContainerType container = reference.get();
            
            DownloaderTask fileDownloaderTask = getDownloaderTask(container);
            
        	tasks.remove(container);
        	
        	if (caller !=null && file != null) 
        		caller.onDownloadFinished(file);
        	
            if (isCancelled()) {
                file = null;
            }
            // Change bitmap only if this process is still associated with it
            if (this == fileDownloaderTask) {
            	if (container != null) {
                    Message msg = Message.obtain();
                    msg.what = 99;
                    msg.obj = new DownloadPair<FileType, ContainerType>(container, file);
                	handler.sendMessage(msg);
            	}
                
                //cache the image
                try {
					writeFile(file, url);
				} catch (Exception e) {
					Log.e(LOG_TAG, "saving cache error.");
				}
            }
        }
    }
    
	//cancel a download (internal only)
	protected boolean cancelPotentialDownload(String url, ContainerType imageView) {
	    DownloaderTask bitmapDownloaderTask = getDownloaderTask(imageView);

	    if (bitmapDownloaderTask != null) {
	        String bitmapUrl = bitmapDownloaderTask.getUrl();
	        if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
	            bitmapDownloaderTask.cancel(true);
	        } else {
	            // The same URL is already being downloaded.
	            return false;
	        }
	    }
	    return true;
	}
	
    //the actual download code
    public FileType downloadFile(ContainerType container, String url) {
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

	protected abstract FileType processInputStream(InputStream inputStream);

	public void writeFile(FileType fileType, String url) throws Exception {

   	 	File f = urlToFile(url, false);
   	 	
   	    SoftReference<FileType> bitmapRef = new SoftReference<FileType>(fileType); 
   	 	
   	 	fileCache.put(f.getPath(), bitmapRef);
   	 	
        write(fileType, f);
	}
}
