package com.threathunter.labrador.application.rpc.service.impl;

import com.threathunter.labrador.application.rpc.service.GlobalVariableQueryService;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.GlobalQueryService;
import com.threathunter.labrador.rpc.server.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RpcService(GlobalVariableQueryService.class)
@Slf4j
public class GlobalVariableQueryServiceImpl implements GlobalVariableQueryService {
    private static Logger logger = LoggerFactory.getLogger(StrategyTriggerServiceImpl.class);

    @Override
    public Map<String, Object> query(String day, List<String> variables) throws LabradorException {
        GlobalQueryService queryService = new GlobalQueryService();
        Map<String,Object> result = new HashMap<>();
        try {
            Map<String, Object> kv = queryService.query(day, variables);
            result.put("status", 200);
            result.put("success", true);
            result.put("result", kv);
            logger.info(String.format("query global variable success, day {} variables {}", day, variables));
        } catch (Exception e) {
            result.put("status", 500);
            result.put("success", false);
            result.put("message", e.getMessage());
            logger.error(String.format("query global variable failed, day {} variables {}", day, variables), e);
            e.printStackTrace();
        }
        return result;
    }
}
