package com.threathunter.labrador.application.rpc;

import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.model.Event;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/11/16.
 */
public class TestClient {

    private ApplicationContext applicationContext;

    @Before
    public void init() {
        applicationContext = new ClassPathXmlApplicationContext("classpath:spring/spring-rpc-client.xml");
    }

    @Test
    public void testSpl() {
        SplClient splClient = applicationContext.getBean(SplClient.class);
        System.out.println(splClient);
        String expression = "$CHECKNOTICE(\"ip\", c_ip, \"IP响应字节过大\", 3600) > 0";
        splClient.check(expression);
    }

    @Test
    public void testQueryVariable() throws LabradorException {
        VariableClient variableClient = applicationContext.getBean(VariableClient.class);
        String pk = "123";
        List<String> variables = Arrays.asList("uid__registration__account__mail__profile", "uid__registration__account__mobile__profile",
                "uid__registration__account__username__profile", "uid__account_register_timestamp__profile",
                "uid__account_token_change_mobile__profile",
                "uid__account_token_change_mail__profile", "uid__account_token_change_mail_timestamp__profile",
                "uid__account_token_change_mobile_timestamp__profile", "uid__account_token_change_mail_count__profile",
                "uid__account_token_change_mobile_count__profile", "uid__account_login_timestamp_last10__profile",
                "uid__account_login_ip_last10__profile", "uid__account_login_geocity_last10__profile");
        for (int i = 0; i < 10; i++) {
            Map<String, Object> results = variableClient.queryVariable(pk, variables);
            System.out.println(results);
        }
    }

    @Test
    public void testVariableWriteService() throws Exception {
        VariableWriteClient variableWriteClient = applicationContext.getBean(VariableWriteClient.class);
        String name = "ACCOUNT_REGISTRATION";
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "789");
        kv.put("result", "T");
        kv.put("c_ip", "1.3.5.7");
        String variable = "uid__account_register_timestamp__profile";
        variableWriteClient.writeVariable(name, "", kv);
    }

    @Test
    public void testStrategyTrigger() throws Exception {
        StrategyTriggerClient strategyTriggerClient = applicationContext.getBean(StrategyTriggerClient.class);
        Map<String,Object> properties = new HashMap<>();
        properties.put("c_ip", "1.2.3.4");
        properties.put("uid", "456");
        properties.put("did", "did123");
        properties.put("strategylist", Arrays.asList("user_login_succ_$_did_exception_uid"));
        String results =  strategyTriggerClient.strategyTrigger(properties);
        System.out.println(results);
    }


    @Test
    public void testQueryRelationShiopService() throws Exception {
        QueryRelationShipClient queryRelationShipClient = applicationContext.getBean(QueryRelationShipClient.class);
        String key = "456";
        String dimension = "uid";
        Map<String,Object> result = queryRelationShipClient.loadRelations(key, dimension);
        System.out.println(result);
    }

    @Test
    public void testGlobalVariableQuery() throws Exception {
        GlobalVariableClient globalVariableClient = applicationContext.getBean(GlobalVariableClient.class);
        String day = "20180307";
        List<String> vars = Arrays.asList("global__order_submit_web_sum_product_count__1d__profile");
        Map<String, Object> results = globalVariableClient.query(day, vars);
        System.out.println(results);
    }

}
