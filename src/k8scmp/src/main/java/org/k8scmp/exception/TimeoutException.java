package org.k8scmp.exception;

/**
 * Created by anningluo on 2016/1/20.
 */
public class TimeoutException extends Exception {
    public TimeoutException(String msg) {
        super(msg);
    }
}
