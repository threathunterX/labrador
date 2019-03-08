package com.threathunter.labrador.application.rpc.service;

import com.threathunter.labrador.core.exception.LabradorException;

import java.util.List;
import java.util.Map;

public interface GlobalVariableQueryService {
    public Map<String,Object> query(String day, List<String> variables) throws LabradorException;
}
