package com.threathunter.labrador.core.service;

import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.threathunter.labrador.aerospike.AerospikeUtil;
import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class GlobalQueryService {

    public Map<String, Object> query(String day, List<String> variables) throws LabradorException {
        //验证变量是否有效
        validVariables(variables);
        String namespace = Env.getAerospikeDao().getNamespace();
        String set = EnumUtil.Dimension.global.name();
        Set<Key> keys = new HashSet<>();
        //构建基础变量去重列表
        for (int i = 0; i < variables.size(); i++) {
            Variable variable = Env.getVariables().getVariable(variables.get(i));
            String basicVariableName = variables.get(i);
            if(variable.getProfileModuleType() == EnumUtil.ProfileModuleType.derived) {
                if(variable.getElement().getSources().size() > 0) {
                    basicVariableName = variable.getElement().getSources().get(0);
                }
            }
            keys.add(new Key(namespace, set, String.format("%s%s", basicVariableName, day)));
        }

        Map<String, Object> result = new HashMap<>();
        //返回查询变量结量，key为变量名，value为record
        Map<String,Record> queryRecords = AerospikeUtil.batchRead(keys.toArray(new Key[0]));
        Map<String,Record> unboxQueryRecords = unbox(queryRecords);

        for (int i = 0; i < variables.size(); i++) {
            String variableName = variables.get(i);
            Variable variable = Env.getVariables().getVariable(variableName);
            //衍生变量
            if(variable.getProfileModuleType() == EnumUtil.ProfileModuleType.derived) {
                if(variable.getElement().getSources().size() > 1) {
                    throw new LabradorException("derived variable " + variableName + " source variable size greater than 1");
                }
                String sourceName = variable.getElement().getSources().get(0);
                if(unboxQueryRecords.containsKey(sourceName)) {
                    Object evalObject = Env.getReadDefineRegistry().eval(unboxQueryRecords.get(sourceName).getBins(), variable);
                    result.put(variableName, evalObject);
                }
            } else {
                result.put(variableName, unboxQueryRecords.get(variableName).getBins());
            }
        }
        return result;
    }

    //拆包，去除key中的时间
    private Map<String,Record> unbox(Map<String, Record> queryRecords) {
        Map<String,Record> unbox = new HashMap<>();
        queryRecords.forEach((key, record) -> {
            unbox.put(StringUtils.substring(key, 0, key.length() - 8), record);
        });
        return unbox;
    }

    /**
     * 合法性验证，判断传入的variables是否已定义，
     * 且均为global变量
     *
     * @param variables
     * @throws LabradorException
     */
    private void validVariables(List<String> variables) throws LabradorException {
        for (String curVar : variables) {
            Variable objVariable = Env.getVariables().getVariable(curVar);
            if (null == objVariable) {
                throw new LabradorException("variable " + curVar + " not exists");
            }
            EnumUtil.Dimension variableDimension = objVariable.getElement().getDimension();
            if (variableDimension != EnumUtil.Dimension.global) {
                throw new LabradorException("variable " + curVar + " is not global dimension");
            }
        }
    }

}
