package com.threathunter.labrador.common.exception;

/**
 * Created by wanbaowang on 17/8/22.
 */
public class BuilderException extends Exception {
    public BuilderException(String message) {
        super(message);
    }

    public BuilderException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
