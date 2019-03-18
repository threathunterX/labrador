package com.threathunter.labrador.core.service;

import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.threathunter.labrador.aerospike.AerospikeUtil;
import com.threathunter.labrador.common.model.QueryGroup;
import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 
 */
public class QueryService implements Query {

    private static final Logger logger = LoggerFactory.getLogger(QueryService.class);

    public Map<String, Object> query(String rowKey, List<String> variables) throws LabradorException {
        Map<String, Object> result = queryFillDefault(rowKey, variables, true);
        return result;
    }

    public Map<String, Object> queryFillDefault(String rowKey, List<String> variables, boolean fillDefault) throws LabradorException {
        Set<Variable> basicVars = new HashSet<>();
        Set<Variable> reqVars = new HashSet<>();
        /**1. 将查询的string转换为Variable
         2. 区分request variable 和 查询变量所需要的所有基础变量
         **/

        for (String variable : variables) {
            Variable curVariable = Env.getVariables().getVariable(variable);
            if (null == curVariable) {
                throw new LabradorException("variable " + variable + " not exists");
            }
            reqVars.add(curVariable);

            if (EnumUtil.isBasicModuleType(curVariable.getProfileModuleType())) {
                basicVars.add(curVariable);
            } else {
                List<String> sources = curVariable.getElement().getSources();
                for (String source : sources) {
                    Variable sourceVariable = Env.getVariables().getVariable(source);
                    //部分source会为ACCOUNT_LONG之类的事件，并不是真正的变量
                    if (null == sourceVariable) {
                        continue;
                    }
                    if (EnumUtil.isBasicModuleType(sourceVariable.getProfileModuleType())) {
                        basicVars.add(sourceVariable);
                    }
                }
            }
        }

        //将变量按set进行分组
        Map<String, QueryGroup> groupMap = new HashMap<>();

        for (Variable variable : basicVars) {
            String namespace = Env.getAerospikeDao().getNamespace();
            String setName = Env.getAerospikeDao().getSetNames(variable.getElement().getDimension());
            String variableName = variable.getName();
            int code = Env.getVariableUpdate().getVariableIdGenerator().loadId(variableName);
            groupMap.computeIfAbsent(setName, k -> new QueryGroup(namespace, setName)).add(variableName, String.valueOf(code));
        }

        Map<String, Object> basicResults = new HashMap<>();

        //从as中读取基础变量的值
        for (QueryGroup queryGroup : groupMap.values()) {
            String namespace = queryGroup.getNamespace();
            String setName = queryGroup.getSetName();
            List<String> codes = queryGroup.getCodes();

            Key key = new Key(namespace, setName, rowKey);
            Record record = AerospikeUtil.getRecordBins(key, codes.toArray(new String[0]));
            if (null != record) {
                for (String code : codes) {
                    Object obj = record.getValue(code);
                    if (null != obj) {
                        String variableName = Env.getVariableUpdate().getVariableIdGenerator().loadName(Integer.valueOf(code));
                        if (StringUtils.isNotBlank(variableName)) {
                            basicResults.put(variableName, obj);
                        }
                    }
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        for (Variable reqVar : reqVars) {
            String reqName = reqVar.getName();
            if (basicResults.containsKey(reqName)) {
                result.put(reqName, basicResults.get(reqName));
            } else if (reqVar.getProfileModuleType() == EnumUtil.ProfileModuleType.derived) {
                String sourceVariableName = reqVar.getElement().getSources().get(0);
                Object sourceValue = basicResults.get(sourceVariableName);
                //基本变量值为空
                if (null == sourceValue) {
                    result.put(reqName, getDefaultValue(reqVar.getElement().getValueType()));
                } else {
                    try {
                        Object evalObject = Env.getReadDefineRegistry().eval(sourceValue, reqVar);
                        result.put(reqName, evalObject);
                    } catch (LabradorException e) {
                        logger.error("query variable " + reqVar.getName() + " error, exception message is : ", e);
                        result.put(reqName, getDefaultValue(reqVar.getElement().getValueType()));
                    }
                }
            } else {
                if(fillDefault == true) {
                    result.put(reqName, getDefaultValue(reqVar.getElement().getValueType()));
                }
            }
        }
        return result;
    }

    private Object getDefaultValue(EnumUtil.DataType dataType) {
        Object defaultValue;
        switch (dataType) {
            case double_type:
                defaultValue = 0.0d;
                break;
            case long_type:
                defaultValue = 0L;
                break;
            case string_type:
                defaultValue = "";
                break;
            case list_type:
                defaultValue = new ArrayList<>();
                break;
            case map_type:
                defaultValue = new HashMap<>();
                break;
            case mmap_type:
                defaultValue = new HashMap<String, HashMap<String, Object>>();
                break;
            case mlist_type:
                defaultValue = new HashMap<String, List<Object>>();
                break;
            case empty_type:
                defaultValue = "";
                break;
            default:
                defaultValue = "";
                break;
        }
        return defaultValue;
    }


}
