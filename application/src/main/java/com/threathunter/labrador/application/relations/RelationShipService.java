package com.threathunter.labrador.application.relations;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.QueryService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class RelationShipService {

    private QueryService queryService = new QueryService();

    public Map<String, Object> loadRelations(String key, String dimension) throws LabradorException {

        try {
            EnumUtil.Dimension dimensionEnum = EnumUtil.Dimension.valueOf(dimension);
        } catch (IllegalArgumentException e) {
            throw new LabradorException("dimension " + dimension + "is not valid");
        }

        log.warn("query key {}, dimension {}", key, dimension);
        List<String> queryVariables = new ArrayList<>();

        Map<String, EnumUtil.DataType> queryVariableValueTypeMap = new HashMap<>();

        Set<String> variables = Env.getVariables().loadDimensionVariables(dimension);
        for (String var : variables) {
            Variable variable = Env.getVariables().getVariable(var);
            if (variable.getElement().getModule() != EnumUtil.Module.profile) {
                continue;
            }

            if (!(variable.getElement().getValueCategory() == EnumUtil.Dimension.uid ||
                    variable.getElement().getValueCategory() == EnumUtil.Dimension.did ||
                    variable.getElement().getValueCategory() == EnumUtil.Dimension.ip)) {
                continue;
            }

            if (variable.getElement().getValueType() == EnumUtil.DataType.string_type) {
                queryVariableValueTypeMap.put(var, EnumUtil.DataType.string_type);
            } else if (variable.getElement().getValueType() == EnumUtil.DataType.list_type) {
                queryVariableValueTypeMap.put(var, EnumUtil.DataType.list_type);
            } else {
                continue;
            }
            queryVariables.add(var);
        }

        Map<String,Object> response = new HashMap<>();

        Map<String, Object> dimensionValues = queryService.queryFillDefault(key, queryVariables, false);

        List<Map<String, Object>> values = new ArrayList<>();
        for (String var : dimensionValues.keySet()) {
            Variable variable = Env.getVariables().getVariable(var);
            Map<String, Object> kv = new HashMap<>();
            if (queryVariableValueTypeMap.get(var) == EnumUtil.DataType.string_type) {
                kv.put("value", Arrays.asList(dimensionValues.get(var)));
            } else {
                kv.put("value", dimensionValues.get(var));
            }
            kv.put("type", variable.getElement().getValueCategory().name().toUpperCase());
            kv.put("remark", variable.getElement().getRemark());
            values.add(kv);
        }

        response.put("result", values);
        return response;
/*
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", results);
        return jsonObject.getJSONArray("result").toJSONString();*/
    }

}
