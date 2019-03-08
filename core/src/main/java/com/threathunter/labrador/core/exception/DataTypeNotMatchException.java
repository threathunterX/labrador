package com.threathunter.labrador.core.exception;

/**
 * Created by wanbaowang on 17/8/25.
 */
public class DataTypeNotMatchException extends Exception {

    public DataTypeNotMatchException(String message) {
        super(message);
    }

    public DataTypeNotMatchException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
