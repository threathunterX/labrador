package com.threathunter.labrador.batch.write;

import com.threathunter.labrador.AbstractTest;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.BatchSendService;
import com.threathunter.model.Event;
import org.junit.After;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanbaowang on 17/11/25.
 */
public class UserArchivesWrite extends AbstractTest{

    @Test
    public void uid_timestamp__visit_dynamic_count__profile() throws LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_LOGIN");
        event.setTimestamp(System.currentTimeMillis());
        Map<String,Object> dimensionMap = new HashMap<>();
        Map<String,Object> userMap = new HashMap<>();
        Map<String,Object> variableMap = new HashMap<>();
        Map<String,Object> currentVariableMap = new HashMap<>();
        dimensionMap.put("uid", userMap);
        userMap.put("456", variableMap);
        variableMap.put("uid__visit_dynamic_count__1h__slot", currentVariableMap);
        currentVariableMap.put("key", System.currentTimeMillis());
        currentVariableMap.put("value", 198);
        System.out.println(dimensionMap);
        event.setPropertyValues(dimensionMap);
        BatchSendService sendService = new BatchSendService();
        sendService.process(event);
    }

    @Test
    public void uid__alarm_count__profile() throws LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_LOGIN");
        event.setTimestamp(System.currentTimeMillis());
        Map<String,Object> dimensionMap = new HashMap<>();
        Map<String,Object> userMap = new HashMap<>();
        Map<String,Object> variableMap = new HashMap<>();
        Map<String,Object> currentVariableMap = new HashMap<>();
        dimensionMap.put("uid", userMap);
        userMap.put("456", variableMap);
        variableMap.put("uid__visit_incident_count__1h__slot", currentVariableMap);
        currentVariableMap.put("key", System.currentTimeMillis());
        currentVariableMap.put("value", 10);
        System.out.println(dimensionMap);
        event.setPropertyValues(dimensionMap);
        BatchSendService sendService = new BatchSendService();
        sendService.process(event);
    }

    @Test
    public void uid_geo_city__visit_dynamic_count__profile() throws LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_LOGIN");
        event.setTimestamp(System.currentTimeMillis());
        Map<String,Object> dimensionMap = new HashMap<>();
        Map<String,Object> userMap = new HashMap<>();
        Map<String,Object> variableMap = new HashMap<>();
        Map<String,Object> currentVariableMap = new HashMap<>();
        dimensionMap.put("uid", userMap);
        userMap.put("456", variableMap);
        variableMap.put("uid_geo_city__visit_dynamic_count_top20__1h__slot", currentVariableMap);
        currentVariableMap.put("key", System.currentTimeMillis());
        Map<String,Long> cityMap = new HashMap<>();
        cityMap.put("北京市", 10L);
        cityMap.put("上海市", 20L);
        currentVariableMap.put("value", cityMap);
        event.setPropertyValues(dimensionMap);
        BatchSendService sendService = new BatchSendService();
        sendService.process(event);
    }

    @Test
    public void uid__visit_dynamic_last_timestamp__profile() throws LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_LOGIN");
        event.setTimestamp(System.currentTimeMillis());
        Map<String,Object> dimensionMap = new HashMap<>();
        Map<String,Object> userMap = new HashMap<>();
        Map<String,Object> variableMap = new HashMap<>();
        Map<String,Object> currentVariableMap = new HashMap<>();
        dimensionMap.put("uid", userMap);
        userMap.put("456", variableMap);
        variableMap.put("uid__visit_dynamic_last_timestamp__1h__slot", currentVariableMap);
        currentVariableMap.put("key", System.currentTimeMillis());
        currentVariableMap.put("value", System.currentTimeMillis());
        event.setPropertyValues(dimensionMap);
        BatchSendService sendService = new BatchSendService();
        sendService.process(event);
    }


    @After
    public void tearDown() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
