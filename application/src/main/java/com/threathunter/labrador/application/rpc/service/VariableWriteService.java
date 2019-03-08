package com.threathunter.labrador.application.rpc.service;


import java.util.Map;

/**
 * Created by wanbaowang on 17/11/21.
 */
public interface VariableWriteService {
    public void writeVariable(String name, String variable, Map<String,Object> kv) throws Exception;
}
