package com.threathunter.labrador.core.service;

import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.model.Event;

/**
 * Created by wanbaowang on 17/9/11.
 */
public interface Send {
    public Event process(Event event) throws Exception;
}
