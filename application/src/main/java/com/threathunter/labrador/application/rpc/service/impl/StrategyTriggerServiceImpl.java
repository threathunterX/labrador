package com.threathunter.labrador.application.rpc.service.impl;

import com.threathunter.labrador.application.rpc.service.StrategyTriggerService;
import com.threathunter.labrador.application.strategy.StrategyService;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.QueryService;
import com.threathunter.labrador.rpc.server.RpcService;
import com.threathunter.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/11/30.
 */
@RpcService(StrategyTriggerService.class)
public class StrategyTriggerServiceImpl implements StrategyTriggerService {

    private static Logger logger = LoggerFactory.getLogger(StrategyTriggerServiceImpl.class);

    @Override
    public String strategyTrigger(Map<String,Object> kv) throws LabradorException {
        QueryService queryService = new QueryService();
        Event event = new Event();
        event.setApp("nebula");
        event.setName("ACCOUNT_LOGIN");
        event.setKey("123456789");
        if(kv.containsKey("timestamp")) {
            Object timestampObject = kv.remove("timestamp");
            if( ! (timestampObject instanceof Long) ) {
                throw new LabradorException("timestamp is not insanceof long");
            }
            event.setTimestamp(Long.valueOf(timestampObject.toString()));
        } else {
            event.setTimestamp(System.currentTimeMillis());
        }
        event.setPropertyValues(kv);
        StrategyService strategyService = new StrategyService(event, queryService);
        List<Event> results = strategyService.processEvent();
        logger.warn("results..." + results);
        return results.toString();
    }
}
