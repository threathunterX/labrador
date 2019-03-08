package com.threathunter.labrador.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by wanbaowang on 17/8/22.
 * 读写互斥的hashMap
 */
public class WriteReadMap<K, V> {

    private volatile Map<K, V> map = new HashMap<K, V>();

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private Lock writeLock = readWriteLock.writeLock();

    private Lock readLock = readWriteLock.readLock();

    public V get(K k) {
        readLock.lock();
        try {
            return map.get(k);
        } finally {
            readLock.unlock();
        }
    }

    public V put(K k, V v) {
        writeLock.lock();
        try {
            return map.put(k, v);
        } finally {
            writeLock.unlock();
        }
    }

    public V putIfAbsent(K k, V v) {
        writeLock.lock();
        try {
            return map.putIfAbsent(k, v);
        } finally {
            writeLock.unlock();
        }
    }

    public boolean contains(K k) {
        readLock.lock();
        try {
            return map.containsKey(k);
        } finally {
            readLock.unlock();
        }
    }

}
