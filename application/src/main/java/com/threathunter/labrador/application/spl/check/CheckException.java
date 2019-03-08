package com.threathunter.labrador.application.spl.check;

/**
 * Created by wanbaowang on 17/10/27.
 */
public class CheckException extends Exception {

    public CheckException(String msg) {
        super(msg);
    }

    public CheckException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
