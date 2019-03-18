package com.threathunter.labrador.core.env;

import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.exception.LabradorException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.threathunter.model.Event;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 
 */
public class EventFieldContainer {

    private Map<String, Map<String, String>> eventFields = new HashMap<>();
    private ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private Lock writeLock = reentrantReadWriteLock.writeLock();
    private Lock readLock = reentrantReadWriteLock.readLock();

    private Map<String, String> eventBasicType = new HashMap<>();

    public EventFieldContainer() {
        Class clazz = Event.class;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            String lowcaseSimpleName = field.getType().getSimpleName().toLowerCase();
            eventBasicType.put(name, lowcaseSimpleName);
        }
    }

    public void update(Map<String, Map<String, String>> eventFields) {
        try {
            writeLock.lock();
            this.eventFields = eventFields;
        } finally {
            writeLock.unlock();
        }
    }

    public boolean containsEventSource(String sourceName) {
        try {
            readLock.lock();
            return eventFields.containsKey(sourceName);
        } finally {
            readLock.unlock();
        }
    }

    public boolean containsEventSourceField(String sourceName, String field) {
        if (eventBasicType.containsKey(field)) {
            return true;
        }
        try {
            readLock.lock();
            if (!eventFields.containsKey(sourceName)) {
                return false;
            }
            if (!eventFields.get(sourceName).containsKey(field)) {
                return false;
            }
            return true;
        } finally {
            readLock.unlock();
        }
    }

    public String getEventSourceFieldType(String sourceName, String field) {
        if(StringUtils.isBlank(field)) {
            return "";
        }
        if (eventBasicType.containsKey(field)) {
            return eventBasicType.get(field);
        }
        try {
            readLock.lock();
            if (!eventFields.containsKey(sourceName)) {
                return null;
            }
            if (!eventFields.get(sourceName).containsKey(field)) {
                return null;
            }
            return eventFields.get(sourceName).get(field);
        } finally {
            readLock.unlock();
        }
    }


}
