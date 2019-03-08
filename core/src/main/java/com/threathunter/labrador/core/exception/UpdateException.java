package com.threathunter.labrador.core.exception;

/**
 * Created by wanbaowang on 17/8/24.
 */
public class UpdateException extends Exception {
    public UpdateException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public UpdateException(String message) {
        super(message);
    }
}
