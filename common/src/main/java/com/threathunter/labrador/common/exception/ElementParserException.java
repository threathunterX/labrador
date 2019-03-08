package com.threathunter.labrador.common.exception;

/**
 * Created by wanbaowang on 17/8/22.
 */
public class ElementParserException extends Exception {
    public ElementParserException(String message) {
        super(message);
    }

    public ElementParserException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
