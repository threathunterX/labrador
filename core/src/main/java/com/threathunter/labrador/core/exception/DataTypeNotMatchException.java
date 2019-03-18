package com.threathunter.labrador.core.exception;

/**
 * 
 */
public class DataTypeNotMatchException extends Exception {

    public DataTypeNotMatchException(String message) {
        super(message);
    }

    public DataTypeNotMatchException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
