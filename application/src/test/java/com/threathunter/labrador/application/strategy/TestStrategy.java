package com.threathunter.labrador.application.strategy;

import com.threathunter.labrador.common.util.SpringUtils;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.service.QueryService;
import com.threathunter.labrador.rpc.constant.RpcConstant;
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
 * Created by wanbaowang on 17/11/29.
 */
public class TestStrategy {

    private ApplicationContext applicationContext;
    @Before
    public void setup() {
        try {
            Env.init();
            this.applicationContext = new ClassPathXmlApplicationContext(RpcConstant.RPC_SERVER_PATH);
            SpringUtils.setApplicationContext(this.applicationContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testProfileStrategy() throws InterruptedException {
        Event event = new Event();
        event.setApp("nebula");
        event.setTimestamp(System.currentTimeMillis());
        event.setName("ACCOUNT_LOGIN");
        event.setKey("123456789");
        Map<String, Object> properties = new HashMap<>();
        properties.put("c_ip", "1.2.3.4");
        properties.put("uid", "456");
        properties.put("did", "did123");
        properties.put("strategylist", Arrays.asList("user_login_succ_$_did_exception_uid"));
        event.setPropertyValues(properties);

        QueryService queryService = new QueryService();
        StrategyService strategyService = new StrategyService(event, queryService);
        List<Event> noticeList = strategyService.processEvent();
        for (Event notice : noticeList) {
            System.out.println(notice);
        }
    }

    @Test
    public void testSpl() throws Exception {
        Event event = new Event();
        event.setApp("nebula");
        event.setTimestamp(System.currentTimeMillis());
        event.setName("ACCOUNT_LOGIN");
        event.setKey("123456789");
        Map<String, Object> properties = new HashMap<>();
        properties.put("c_ip", "1.2.3.4");
        properties.put("uid", "456");
        properties.put("did", "did123");
        properties.put("strategylist", Arrays.asList("SPL_设备关联多个IP"));
        event.setPropertyValues(properties);

        QueryService queryService = new QueryService();
        StrategyService strategyService = new StrategyService(event, queryService);
        List<Event> noticeList = strategyService.processEvent();
        for (Event notice : noticeList) {
            System.out.println(notice);
        }
    }

    @Test
    public void testProfile_1() throws Exception {
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

        QueryService queryService = new QueryService();
        StrategyService strategyService = new StrategyService(event, queryService);
        List<Event> noticeList = strategyService.processEvent();
        for (Event notice : noticeList) {
            System.out.println(notice);
        }
    }


    @Test
    public void testStrategyHuaZhuDashBoard() {
        Event event = new Event();
        event.setApp("nebula");
        event.setTimestamp(System.currentTimeMillis());
        event.setName("ORDER_SUBMIT");
        event.setKey("123456789");
        Map<String, Object> properties = new HashMap<>();
        properties.put("c_ip", "1.2.3.4");
        properties.put("uid", "56789");
        properties.put("did", "did123");
        properties.put("strategylist", Arrays.asList("华住dashboard"));
        event.setPropertyValues(properties);

        QueryService queryService = new QueryService();
        StrategyService strategyService = new StrategyService(event, queryService);
        List<Event> noticeList = strategyService.processEvent();
        for (Event notice : noticeList) {
            System.out.println("==========");
            System.out.println(notice);
            System.out.println("==========");
        }
    }


}
