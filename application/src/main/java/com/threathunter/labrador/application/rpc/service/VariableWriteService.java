package com.threathunter.labrador.application.rpc.service;


import java.util.Map;

/**
 * 
 */
public interface VariableWriteService {
    public void writeVariable(String name, String variable, Map<String,Object> kv) throws Exception;
}
