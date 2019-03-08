package com.threathunter.labrador.write;

import com.threathunter.labrador.AbstractTest;
import com.threathunter.labrador.common.util.ConfigUtil;
import com.threathunter.labrador.core.builder.idgenerator.FileVariableIdGenerator;
import com.threathunter.labrador.core.builder.idgenerator.VariableIdGenerator;
import com.threathunter.labrador.core.builder.update.EventFieldUpdate;
import com.threathunter.labrador.core.builder.update.VariableUpdate;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.BatchSendService;
import com.threathunter.labrador.core.transform.EnvExtract;
import com.threathunter.labrador.core.transform.Extract;
import com.threathunter.model.Event;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanbaowang on 17/9/28.
 */
public class TestBatch extends AbstractTest {

    @Before
    public void init() throws Exception {
        String idGenerator = ConfigUtil.getString("nebula.labrador.idgenerator.type");
        if (idGenerator.equals("file")) {
            String path = ConfigUtil.getString("nebula.labrador.idgenerator.path");
            VariableIdGenerator generator = new FileVariableIdGenerator(path);
            new EventFieldUpdate().update();
            VariableUpdate variableUpdate = new VariableUpdate(generator);
            variableUpdate.update();
        } else {
            throw new LabradorException("IdGenerator " + idGenerator + " not implement yet");
        }
    }

    @Test
    public void testBatch() throws LabradorException {
        Event event = new Event();
        event.setId(randomId());
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
        System.out.println(event.toString());
        BatchSendService sendService = new BatchSendService();
        sendService.process(event);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBatch_did__visit_distinct_ip__1h__profile() throws LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_TOKEN_CHANGE");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        Map<String,Object> dimensionKv = new HashMap<>();
        kv.put("did", dimensionKv);
        Map<String,Object> didMap = new HashMap<>();
        dimensionKv.put("did123", didMap);
        Map<String,Object> varMap = new HashMap<>();
        varMap.put("key", System.currentTimeMillis());
        System.out.println(varMap.get("key"));
        varMap.put("value", 10);
        didMap.put("did__visit_dynamic_distinct_count_ip__1h__slot", varMap);
        event.setPropertyValues(kv);
        System.out.println(didMap);
        BatchSendService sendService = new BatchSendService();

        sendService.process(event);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
