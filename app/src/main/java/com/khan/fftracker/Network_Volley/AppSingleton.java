package com.khan.fftracker.Network_Volley;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.khan.fftracker.di.AppModule;
import com.khan.fftracker.di.NetworkModule;
import com.khan.fftracker.di.RetrofitComponent;


import java.util.Arrays;

/**
 * Created by Abdul Jalil Khan on 11/3/2016.
 */
public class AppSingleton extends Application {
    private static final String TAG=AppSingleton.class.getName();
   private static AppSingleton appSingleton;
    RequestQueue requestQueue;


    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    public RetrofitComponent retrofitComponent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }
    @Override
    public void onCreate() {
        super.onCreate();
        appSingleton=this;
        daggerCompenent();

        startCatcher();

    }

    private void daggerCompenent() {
        retrofitComponent= DaggerRetrofitComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    public RetrofitComponent getRetrofitComponent(){
        return retrofitComponent;
    }

    private void startCatcher() {

        Thread.UncaughtExceptionHandler systemUncaughtHandler = Thread.getDefaultUncaughtExceptionHandler();
        // the following handler is used to catch exceptions thrown in background threads
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtHandler(new Handler()));

        while (true) {
            try {
                Log.d("json", "Starting crash catch Looper");
                Looper.loop();
                Thread.setDefaultUncaughtExceptionHandler(systemUncaughtHandler);
                throw new RuntimeException("Main thread loop unexpectedly exited");
            } catch (BackgroundException e) {
                Log.d("json", "startCatcher: "+e.getMessage());
                Log.d("json caught", "Caught the exception in the background thread " + e.threadName + ", TID: " + e.tid, e.getCause());
                Log.d("json ERROR","--------" + Arrays.toString(e.getStackTrace()));

               // showCrashDisplayActivity(e.getCause());
            } catch (Throwable e) {
                Log.d("json ERROR","--------" + Arrays.toString(e.getStackTrace()));

                Log.d("json 4", "Caught the exception in the UI thread, e:", e);
               // showCrashDisplayActivity(e);
            }
        }
    }

    void showCrashDisplayActivity(Throwable e) {
       // e.printStackTrace();
        Log.d(TAG+"2", "showCrashDisplayActivity ww: "+e.getCause()+" cause "+e.getClass().getSimpleName()+" name "+e.getClass().getName());

//        Intent i = new Intent(this, CrashActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        i.putExtra("e", e);
//        startActivity(i);
    }


    public static synchronized AppSingleton getInstance() {
        return appSingleton;
    }

    public <T> void  addToRequestQueue(Request<T> stringRequest) {
        stringRequest.setTag(TAG);
        getRequest().add(stringRequest);
    }

    public RequestQueue getRequest() {
        if (requestQueue==null){
            requestQueue= Volley.newRequestQueue(getApplicationContext());

        }
        return requestQueue;
    }

//    private AppSingleton(Context context) {
//        mCtx = context;
//        mRequestQueue = getRequest();
//
//        mImageLoader = new ImageLoader(mRequestQueue,
//                new ImageLoader.ImageCache() {
//                    private final LruCache<String, Bitmap> cache = new LruBitmapCache(mCtx);
//
//                    @Override
//                    public Bitmap getBitmap(String url) {
//                        return cache.get(url);
//                    }
//
//                    @Override
//                    public void putBitmap(String url, Bitmap bitmap) {
//                        cache.put(url, bitmap);
//                    }
//                });
//    }
}
