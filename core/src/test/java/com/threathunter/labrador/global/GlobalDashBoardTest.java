package com.threathunter.labrador.global;

import com.threathunter.labrador.AbstractTest;
import com.threathunter.labrador.core.service.GlobalQueryService;
import com.threathunter.labrador.core.service.GlobalSendService;
import com.threathunter.model.Event;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalDashBoardTest extends AbstractTest {


    @Test
    public void testGlobalSend() throws Exception {
        Event event = new Event();
        event.setId(randomId());
        event.setName("global");
        event.setKey("global variables");
//        event.setTimestamp(System.currentTimeMillis());
        event.setTimestamp(1516104187000L);
        Map<String, Object> propMap = new HashMap<>();
        propMap.put("global__order_submit_h5_count__1h__slot", 67);
        ImmutableMap<Object, Object> cityMap = ImmutableMap.of("北京", 15, "杭州", 11);
        propMap.put("global_product_location__order_submit_h5_count__1h__slot", cityMap);
        event.setPropertyValues(propMap);
        GlobalSendService sendService = new GlobalSendService();
        long ts = event.getTimestamp();
        Map<String,Object> kv = event.getPropertyValues();
        sendService.process(kv);
    }

    @Test
    public void testGlobalQuery() throws Exception {
        String day = "20180308";
        List<String> vars = Arrays.asList("global__incident_order_submit_web_count__hourly__profile");
        GlobalQueryService queryService = new GlobalQueryService();
        Map<String,Object> result = queryService.query(day, vars);
        System.out.println(result);
    }

}
