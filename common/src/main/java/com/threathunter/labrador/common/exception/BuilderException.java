package com.threathunter.labrador.common.exception;

/**
 * 
 */
public class BuilderException extends Exception {
    public BuilderException(String message) {
        super(message);
    }

    public BuilderException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
