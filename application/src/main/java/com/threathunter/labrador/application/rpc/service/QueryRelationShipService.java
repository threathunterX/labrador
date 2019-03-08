package com.threathunter.labrador.application.rpc.service;

import com.threathunter.labrador.core.exception.LabradorException;

import java.util.Map;

public interface QueryRelationShipService {

    public Map<String, Object> loadRelations(String key, String dimension) throws LabradorException;

}
