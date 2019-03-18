package com.threathunter.labrador.application.spl;

import com.threathunter.model.Event;

/**
 * 
 */
public class SplExecutor implements Runnable {

    private Event event;
    private String expression;

    public SplExecutor(Event event) {
        this.event = event;
        this.expression = expression;
    }

    @Override
    public void run() {

    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
