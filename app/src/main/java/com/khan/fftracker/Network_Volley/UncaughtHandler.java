package com.khan.fftracker.Network_Volley;

/**
 * Created by Abdul Jalil Khan on 9/6/2017.
 */
public class UncaughtHandler implements Thread.UncaughtExceptionHandler {
    private final android.os.Handler mHandler;


    public UncaughtHandler(android.os.Handler handler) {
        mHandler = handler;
    }

    public void uncaughtException(Thread thread, final Throwable e) {
       //Log.v("tag", "Caught the exception in the background " + thread + " propagating it to the UI thread, e:", e);
        //final int tid = Process.myTid();
        final int tid =2;
        final String threadName = thread.getName();
        mHandler.post(new Runnable() {
            public void run() {
                throw new BackgroundException(e, tid, threadName);
            }
        });
    }
}
