package com.threathunter.labrador.aerospike;

import com.aerospike.client.BatchRead;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestAerospikeAsync {
    private static final String keyPrefix = "batchkey";
    private static final String valuePrefix = "batchvalue";
    private static final String binName = "batchbin";
    private static final int size = 8;
    private static Key[] sendKeys;

    private static String namespace = "profiles";
    private static String set = "global_1";

    @Test
    public void testWrite() {
        sendKeys = new Key[size];
        for (int i = 0; i < size; i++) {
            sendKeys[i] = new Key(namespace, set, keyPrefix + (i + 1));
        }

        for (int i = 1; i <= size; i++) {
            final int j = i;
            new Thread(() -> {
                Key key = sendKeys[j - 1];
                Bin bin = new Bin(binName, valuePrefix + j);
                AerospikeUtil.asyncPut(key, bin);
                System.out.println(j);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ).start();
        }
    }


    @Test
    public void testAsyncBatchRead() throws Exception {
        List<BatchRead> readList = ImmutableList.of(new BatchRead(new Key(namespace, set, keyPrefix + "2"), true), new BatchRead(new Key(namespace, set, keyPrefix + "3"), true));
        Map<String, Record> results = AerospikeUtil.asyncBatchRead(readList);
        System.out.println(results);
    }

    @Test
    public void testBatchRead() throws Exception {
        List<Key> readList = ImmutableList.of(new Key(namespace, set, keyPrefix + "2"), new Key(namespace, set, keyPrefix + "100"), new Key(namespace, set, keyPrefix + "3"));
        Map<String, Record> records = AerospikeUtil.batchRead(readList.toArray(new Key[0]));
        records.forEach((key, record) -> {
            System.out.println("key:" + key + " record:" + record);
        });
    }

    @Test
    public void testPut() {
        Key key = new Key("profiles", "global", "global__transaction_escrow_h5_count__hourly__profile20180306");
//        Bin bin = new Bin("34", ImmutableMap.of("2018012610", Arrays.asList("merchant_1", "merchant_2"), "2018012611", Arrays.asList("merchant_1", "merchant_2")));
        Bin bin = new Bin("09", 6);
        Bin bin1 = new Bin("10", 47);
        AerospikeUtil.put(key, bin, bin1);
    }

}
