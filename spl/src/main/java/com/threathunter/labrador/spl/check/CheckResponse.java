package com.threathunter.labrador.spl.check;

import java.io.Serializable;

/**
 * Created by wanbaowang on 17/10/25.
 */
public class CheckResponse  implements Serializable{
    private String expression;
    private boolean success;
    private String message;

    public CheckResponse(String expression, boolean success, String message) {
        this.expression = expression;
        this.success = success;
        this.message = message;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
