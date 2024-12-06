package com.khan.fftracker.Network_Volley;

/**
 * Created by Abdul Jalil Khan on 9/6/2017.
 */
/**
 * Wrapper class for exceptions caught in the background
 */
public class BackgroundException extends RuntimeException {

    final int tid;
    final String threadName;

    /**
     * @param e original exception
     * @param tid id of the thread where exception occurred
     * @param threadName name of the thread where exception occurred
     */
    BackgroundException(Throwable e, int tid, String threadName) {
        super(e);
        this.tid = tid;
        this.threadName = threadName;
    }
}
