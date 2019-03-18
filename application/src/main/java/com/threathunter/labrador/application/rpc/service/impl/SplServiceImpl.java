package com.threathunter.labrador.application.rpc.service.impl;

import com.threathunter.labrador.application.rpc.service.SplService;
import com.threathunter.labrador.rpc.server.RpcService;
import com.threathunter.labrador.spl.check.CheckResponse;
import com.threathunter.labrador.spl.check.ExpressionCheckService;

/**
 * 
 */
@RpcService(SplService.class)
public class SplServiceImpl implements SplService {

    @Override
    public CheckResponse check(String expression) {
        ExpressionCheckService checkService = new ExpressionCheckService(expression);
        CheckResponse checkResponse = checkService.check();
        return checkResponse;
    }
}
