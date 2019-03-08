package com.threathunter.labrador.application.rpc.service.impl;

import com.threathunter.labrador.application.rpc.service.VariableQueryService;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.QueryService;
import com.threathunter.labrador.rpc.server.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/11/16.
 */
@RpcService(VariableQueryService.class)
public class VariableQueryServiceImpl implements VariableQueryService {
    private static Logger logger = LoggerFactory.getLogger(VariableQueryServiceImpl.class);

    @Override
    public Map<String, Object> query(String rowKey, List<String> variables) {
        QueryService queryService = new QueryService();
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> kv = queryService.query(rowKey, variables);
            result.put("status", 200);
            result.put("success", true);
            result.put("result", kv);
        } catch (Exception e) {
            result.put("status", 500);
            result.put("success", false);
            result.put("message", e.getMessage());
            logger.error(String.format("query variable rowkey:%s, variables:%s fail.", rowKey, variables.toString()));
            e.printStackTrace();
        }
        return result;
    }
}
