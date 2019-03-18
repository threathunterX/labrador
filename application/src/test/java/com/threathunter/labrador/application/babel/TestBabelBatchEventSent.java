package com.threathunter.labrador.application.babel;

import com.threathunter.babel.meta.ServiceMeta;
import com.threathunter.babel.meta.ServiceMetaUtil;
import com.threathunter.babel.rpc.RemoteException;
import com.threathunter.babel.rpc.impl.ServiceClientImpl;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.model.Event;
import org.junit.Test;

import java.util.*;

/**
 * 
 */
public class TestBabelBatchEventSent {

    static {
        try {
            Env.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String randomId() {
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 24; i++) {
            int randomInt = r.nextInt(14);
            sb.append(chs[randomInt]);
        }
        return sb.toString();
    }


    private static char[] chs = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e'};
    private static List<String> ips = Arrays.asList("1.2.3.4", "1.2.3.5", "1.2.3.6", "1.2.3.7", "1.2.3.8", "1.2.3.9", "1.2.3.10", "1.2.3.11", "1.2.3.12", "1.2.3.13", "1.2.3.14", "1.2.3.15");


    public Event ORDER_SUBMIT_EVENT() throws Exception {
        Event event = new Event();
        event.setKey(randomId());
        event.setApp("nebula");
        event.setId(randomId());
        event.setName("ORDER_SUBMIT");
        Map<String, Object> properties = new HashMap<>();
        properties.put("c_ip", "1.2.3.4");
        properties.put("uid", "uid1801290010000");
        properties.put("did", "did234");
        properties.put("result", "T");
        properties.put("order_id", "order_345");
        properties.put("strategylist", Arrays.asList("订单profile_UID过去一小时下单成功数>=2"));
        event.setTimestamp(System.currentTimeMillis());
        event.setPropertyValues(properties);
        return event;
    }


    public Event uid_timestamp__visit_dynamic_count__profile() throws LabradorException {
        Event event = new Event();
        event.setKey(randomId());
        event.setApp("nebula");
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
        event.setPropertyValues(dimensionMap);
        return event;
    }

    public Event strategyEvent() throws LabradorException {
        Event event = new Event();
        event.setApp("nebula");
        event.setTimestamp(System.currentTimeMillis());
        event.setName("ACCOUNT_LOGIN");
        event.setKey("123456789");
        Map<String, Object> properties = new HashMap<>();
        properties.put("c_ip", "1.2.3.4");
        properties.put("uid", "456");
        properties.put("did", "did123");
        properties.put("strategylist", Arrays.asList("test_profile3"));
        event.setPropertyValues(properties);
        return event;
    }

    @Test
    public void testVisit() throws LabradorException, RemoteException {
        ServiceMeta meta = ServiceMetaUtil.getMetaFromResourceFile("babel/batch_event_send_redis.service");
        ServiceClientImpl client = new ServiceClientImpl(meta);
        client.bindService(meta);
        client.start();
        client.notify(uid_timestamp__visit_dynamic_count__profile(), meta.getName());
    }

    @Test
    public void testOrderSumbit() throws Exception {
        ServiceMeta meta = ServiceMetaUtil.getMetaFromResourceFile("babel/profilenoticechecker_redis.service");
        ServiceClientImpl client = new ServiceClientImpl(meta);
        client.bindService(meta);
        client.start();
        client.notify(ORDER_SUBMIT_EVENT(), meta.getName());
    }





    @Test
    public void test1() throws Exception {
        Event event = ORDER_SUBMIT_EVENT();
        Map map = event.genAllData();
        if(map.containsKey("order_id")) {
            System.out.println("true");
        }

    }

    @Test
    public void testStrategy() throws Exception {
        String fileName = "profilenoticechecker_redis.service";
        TestNoticeSender.getInstance().start(fileName);
        TestNoticeSender.getInstance().sendNotice(strategyEvent());
        Thread.sleep(4000);
    }



}
