package com.threathunter.labrador.babel;

import com.threathunter.model.Event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BabelSenderTest {
    public static void main(String[] args) {
        Event event = new Event();
        event.setId("123");
        event.setName("ACCOUNT_TOKEN_CHANGE");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("hour", "2017092816");
        Map<String, Object> uidKv = new HashMap<>();
        uidKv.put("user__visit__open_resume_count__slot", 20);
        uidKv.put("user__visit__ip_detail_distinct__slot", Arrays.asList("192.168.1.1", "192.168.1.2", "192.168.1.3"));
        Map<String, Object> uidDimension = new HashMap<>();
        uidDimension.put("123", uidKv);
        kv.put("uid", uidDimension);
        event.setPropertyValues(kv);



    }
}
