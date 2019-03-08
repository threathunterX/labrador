package com.threathunter.labrador.application.rpc.service.impl;

import com.threathunter.labrador.application.relations.RelationShipService;
import com.threathunter.labrador.application.rpc.service.QueryRelationShipService;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.rpc.server.RpcService;

import java.util.Map;

@RpcService(QueryRelationShipService.class)
public class QueryRelationShipServiceImpl implements QueryRelationShipService {

    @Override
    public Map<String, Object> loadRelations(String key, String dimension) throws LabradorException {
        RelationShipService relationShipService = new RelationShipService();
        Map<String, Object> dimensionValues = relationShipService.loadRelations(key, dimension);
        return dimensionValues;
    }
}
