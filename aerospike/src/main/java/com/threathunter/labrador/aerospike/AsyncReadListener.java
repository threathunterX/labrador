package com.threathunter.labrador.aerospike;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.BatchRead;
import com.aerospike.client.Record;
import com.aerospike.client.listener.BatchListListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AsyncReadListener implements BatchListListener {

    private Map<String,Record> records;

    private Exception exception;

    private boolean complete;

    public Map<String, Record> getRecords() {
        return records;
    }

    public AerospikeException getException() {
        return this.exception == null ? null : new AerospikeException(exception.getMessage());
    }

    public AsyncReadListener() {
        this.records = new HashMap<>();
    }

    public  void waitTillComplete() {
        while( ! complete) {
            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSuccess(List<BatchRead> records) {
        for(BatchRead read : records) {
            String key = String.valueOf(read.key.userKey.getObject());
            Record rec = read.record;
            this.records.put(key, rec);
        }
        this.complete = true;
        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public void onFailure(AerospikeException exception) {
        this.exception = exception;
        this.complete = true;
        synchronized (this) {
            this.notify();
        }
    }
}
