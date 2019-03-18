package com.threathunter.labrador.core.exception;

/**
 * 
 */
public class UpdateException extends Exception {
    public UpdateException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public UpdateException(String message) {
        super(message);
    }
}
