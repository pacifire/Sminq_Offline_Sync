package offline_test.sminq.com.sminq_offlinelisting_test.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Pawan on 01/12/16.
 */
public class RequestQueueSingleton {

    //High priority NON-UI variables goes below....
    private final Context context;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static RequestQueueSingleton mRequestQueueInstance;


    public static RequestQueueSingleton getInstance(Context context) {
        if(mRequestQueueInstance == null)
            mRequestQueueInstance = new RequestQueueSingleton(context);

            return mRequestQueueInstance;
    }//getInstance closes here.....

    private RequestQueueSingleton(Context context) {
        this.context = context;
        this.mRequestQueue = getRequestQueue();
    }//RequestQueueSingleton constructor closes here.....

    public RequestQueue getRequestQueue() {

        if(mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(context);

        return mRequestQueue;
    }//getRequestQueue closes here....


    public void addToRequestQueue(Request request) {
        getRequestQueue().add(request);
    }//addToRequestQueue closes here....


    public ImageLoader getImageLoader(){

        if(mImageLoader == null){

            mImageLoader = new ImageLoader(mRequestQueue,
                    new ImageLoader.ImageCache() {
                        private final LruCache<String, Bitmap>
                                cache = new LruCache<String, Bitmap>(20);

                        @Override
                        public Bitmap getBitmap(String url) {
                            return cache.get(url);
                        }

                        @Override
                        public void putBitmap(String url, Bitmap bitmap) {
                            cache.put(url, bitmap);
                        }
                    });
        }//if(mImageLoader == null) closes here.....



        return mImageLoader;
    }//getImageLoader closes here....
}//RequestQueueSingleton class closes here.....
