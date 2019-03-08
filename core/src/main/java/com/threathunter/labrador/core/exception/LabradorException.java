package com.threathunter.labrador.core.exception;

/**
 * Created by wanbaowang on 17/8/24.
 */
public class LabradorException extends Exception {
    public LabradorException() {
        super();
    }

    public LabradorException(String message) {
        super(message);
    }

    public LabradorException(Throwable throwable, String message) {
        super(message, throwable);
    }
}
