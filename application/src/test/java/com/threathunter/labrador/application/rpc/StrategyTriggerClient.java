package com.threathunter.labrador.application.rpc;

import com.threathunter.labrador.application.rpc.service.StrategyTriggerService;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.rpc.client.RpcReference;
import com.threathunter.model.Event;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/11/30.
 */
@Component
public class StrategyTriggerClient {

    @RpcReference
    private StrategyTriggerService strategyTriggerService;

    public String strategyTrigger(Map<String,Object> kv) throws LabradorException {
        return strategyTriggerService.strategyTrigger(kv);
    }

}
