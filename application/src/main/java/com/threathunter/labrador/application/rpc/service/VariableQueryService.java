package com.threathunter.labrador.application.rpc.service;

import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.Query;

import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/11/16.
 */
public interface VariableQueryService {
    public Map<String,Object> query(String rowKey, List<String> variables) throws LabradorException;
}
