package au.com.tyo.android.services;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import au.com.tyo.android.utils.CacheManager;
import au.com.tyo.services.HttpPool;
import au.com.tyo.utils.StringUtils;

public abstract class ResourceFetcher<FileType, ContainerType> extends CacheManager<FileType>
	implements ResourceFetchererInterface<FileType, ContainerType> {

	private static final String TAG = "ResourceFetcher";

	public interface Callback {
		void onTaskFinished(Object file);
	}
	
	private static final String LOG_TAG = "ResourceFetcher";
	
	protected TaskListener<FileType> caller;
	
	protected HashMap<ContainerType, FetcherTask> tasks;
	
	protected Handler handler;
	
	public interface TaskListener<FileType> {
		void onTaskFinished(FileType file);
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
		
		ResourceFetchererInterface<FileType, ContainerType> downloader;
		
		public MessageHandler(ResourceFetchererInterface<FileType, ContainerType> downloader) {
			this.downloader = downloader;
		}
		
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null && downloader != null) {
				DownloadPair<FileType, ContainerType> pair = (DownloadPair<FileType, ContainerType>) msg.obj;
				downloader.handleResult(pair.container, pair.file);
			}
		}
		
	}
	
	public ResourceFetcher(Context context, String subdir){
		super(context, subdir);
		fileCache = new HashMap<String, SoftReference<FileType>>();
		setCaller(null);
		tasks = new HashMap<ContainerType, FetcherTask>();
		handler = new MessageHandler<FileType, ContainerType>(this);
	}
	
	public void handleResult(Callback callback, ContainerType container, FileType file) {
        if (null != callback) {
            callback.onTaskFinished(file);
        }
		// do nothing
		// override it to handle the downloaded result
	}

	public TaskListener<FileType> getCaller() {
		return caller;
	}

	public void setCaller(TaskListener<FileType> caller) {
		this.caller = caller;
	}
	
	public FileType fetch(String url) {
		return fetch(url, null, false);
	}

    protected FileType fetch(String url, ContainerType container, boolean asynchronously) {
        return fetch(url, null, container, asynchronously);
    }
	
	protected FileType fetch(String url, Callback callback, ContainerType container, boolean asynchronously) {
		FileType fileType = null;
	     if (url != null && cancelPotentialDownload(url, container)) {
	    	 
	    	 //Caching code right here
//	    	 SoftReference<FileType> bitmapRef = (SoftReference<FileType>)fileCachgianna michaelse.get(f.getPath());
	    	 try {
				fileType = fileCheck(url);
			} catch (Exception e) {
				Log.e(LOG_TAG, "having problems in loading image cache.");
			}
	    	 
	    	  //No? fetch it
	    	  if(fileType == null){
	    		  if (asynchronously) {
		    		  FetcherTask task = new FetcherTask(callback, container);
		    		  tasks.put(container, task);
		    		  task.execute(url);
	    		  }
	    		  else {
	    			  fileType = downloadFile(callback, container, url);
	    			  if (fileType != null) {
		    			  if (!url.startsWith(File.separator))
			    			  try {
			    				  if (fileType != null)
			    					  writeFile(fileType, url);
			    			  } catch (Exception e) {
			    				  Log.e(LOG_TAG, "failed fetch image: " + url);
			    			  }
		    			  handleResult(callback, container, fileType);
	    			  }
	    		  }
	    	  }
	    	  
	     }
	     
//		  //Yes? set it
		  if (container != null && fileType != null)
			  handleResult(container, fileType);
		  
	     return fileType;
	}
	
	public FileType fetch(String url, ContainerType container) {
		return fetch(url, container, true);
	}
	
	protected FetcherTask getDownloaderTask(ContainerType container) {
		return tasks.get(container);
	}
	
    public class FetcherTask extends AsyncTask<String, Void, FileType> {

		private String url;
        private final WeakReference<ContainerType> reference;
        private Callback callback;

        public FetcherTask(Callback callback, ContainerType container) {
            this.callback = callback;
            reference = new WeakReference<ContainerType>(container);
        }
        
        public String getUrl() {
        	return url;
        }
        
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			// Thread.currentThread().setName("DownloaderTask");
		}

        @Override
        // Actual fetch method, run in the task thread
        protected FileType doInBackground(String... params) {
            // params comes from the execute() call: params[0] is the url.
            url = (String) params[0];
			FileType f = downloadFile(callback, reference.get(), params[0]);
			return f;
        }

        @Override
        // Once the image is downloaded, associates it to the imageView
        protected void onPostExecute(FileType file) {
			if (null == file) {
				Log.e(LOG_TAG, "The value of downloaded file is null");
				return;
			}

        	// I don't care what happen next, but surely have ot get rid of task in the hashmap
            ContainerType container = reference.get();
            
            FetcherTask fileDownloaderTask = getDownloaderTask(container);
            
        	tasks.remove(container);
        	
        	if (caller !=null && file != null) 
        		caller.onTaskFinished(file);
        	
            if (isCancelled()) {
				Log.i(LOG_TAG, "Downloader task got cancelled");
                file = null;
				return;
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
    
	//cancel a fetch (internal only)
	protected boolean cancelPotentialDownload(String url, ContainerType imageView) {
	    FetcherTask bitmapDownloaderTask = getDownloaderTask(imageView);

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

	/**
	 * The actual loading logic
	 */
    public FileType downloadFile(Callback callback, ContainerType container, String url) {
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
		InputStream inputStream = null;
//    	HttpParams params = new BasicHttpParams();
//    	params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//    	HttpClient client = new DefaultHttpClient(params);
//
//    	HttpGet getRequest = null;
    	try {
//	        getRequest = new HttpGet(url);
//
//            HttpResponse response = client.execute(getRequest);
//            final int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode != HttpStatus.SC_OK) {
//                Log.w("Downloader", "Error " + statusCode + " while retrieving file from " + url);
//                return null;
//            }
//
//            final HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                try {
//                    inputStream = entity.getContent();
//                    fileType = processInputStream(inputStream, url);
//                } finally {
//                    if (inputStream != null) {
//                        inputStream.close();
//                    }
//                    entity.consumeContent();
//                }
//            }
			inputStream = HttpPool.getConnection().getAsInputStream(url);
            fileType = processInputStream(inputStream, url);
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
             // if (getRequest != null) getRequest.abort();
            Log.w(TAG, "Error while retrieving file from " + url + e.toString());
            Log.e(TAG, StringUtils.exceptionStackTraceToString(e));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return fileType;
    }

	protected abstract FileType processInputStream(InputStream inputStream, String url);

	public void writeFile(FileType fileType, String url) throws Exception {

   	 	File f = locationToFile(url, false);
   	 	
   	    SoftReference<FileType> ref = new SoftReference<FileType>(fileType);
   	 	
   	 	fileCache.put(f.getPath(), ref);
   	 	
        write(fileType, f);
	}
}
