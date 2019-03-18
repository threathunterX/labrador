package com.threathunter.labrador.core.service;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.common.util.ThreadLocalDateUtil;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.transform.Group;
import com.threathunter.model.Event;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 
 */
public class BatchSendService implements Send {
    private static final Logger logger = LoggerFactory.getLogger(BatchSendService.class);

    private GlobalSendService globalSendService;

    public BatchSendService() {
        this.globalSendService = new GlobalSendService();
    }

    @Override
    public Event process(Event event) throws LabradorException {

        Map<String, Object> propertyValues = event.getPropertyValues();

        //遍历property，key为dimension,value为对应该维度的值
        //参照wiki:http://wiki.threathunter.net/pages/viewpage.action?pageId=20091690
        for (Map.Entry<String, Object> dimensionEntry : propertyValues.entrySet()) {
                //取出当前的维度(uid)
                String dimension = dimensionEntry.getKey();
                if(dimension.equals("global")) {
                    long ts = event.getTimestamp();
                    Map<String,Object> globalKv = (Map<String,Object>) dimensionEntry.getValue();
                    if(!globalKv.containsKey("__GLOBAL__")) {
                        logger.error("error process dimension {} event {}, miss __GLOBAL__ ", dimensionEntry.getKey(), event);
                        continue;
                    }
                    try {
                        Map<String,Object> globalVariablesKv = (Map<String,Object>) globalKv.get("__GLOBAL__");
                        logger.warn("process global {}", globalVariablesKv);
                        globalSendService.process(globalVariablesKv);
                    } catch (Exception e) {
                        logger.error("error process dimension {} event {} failed", dimension, event, e);
                    }
                } else {
                    try {
                    //维度对应的value
                        Map<String, Object> dimensionValue = (Map<String, Object>) dimensionEntry.getValue();
                        for (Map.Entry<String, Object> pkDimension : dimensionValue.entrySet()) {
                            //当前的uid/did/ip等
                            String pk = pkDimension.getKey();
                            Map<String, Object> variableKv = (Map<String, Object>) pkDimension.getValue();
                            String setName = Env.getAerospikeDao().getSetNames(EnumUtil.Dimension.valueOf(dimension));
                            Collection<Group> groups = batchGrouping(dimension, setName, pk, variableKv);

                            for (Group group : groups) {
                                Env.getAerospikeDao().send(group);
                            }
                        }
                    } catch (LabradorException e) {
                        logger.error("error process dimension {} event {}", dimensionEntry.getKey(), event, e);
                    } catch (Exception e) {
                        logger.error("error process dimension {} event {}", dimensionEntry.getKey(), event, e);
                    }
                }

        }

        return null;
    }

    private Collection<Group> batchGrouping(String dimension, String setName, String pk, Map<String, Object> variablesKv) throws LabradorException {
        try {
            Map<String, Group> groupMap = new HashMap<>();
            for (Map.Entry<String, Object> variablesEntry : variablesKv.entrySet()) {
                String slotVariable = variablesEntry.getKey();
                String basicVariableName = Env.getVariables().getProfileVariableFromSlot(slotVariable);
                if (StringUtils.isBlank(basicVariableName)) {
                    throw new LabradorException("variable name " + slotVariable + " doesn't has valid source variable");
                }
                if (!Env.getVariables().contains(basicVariableName)) {
                    throw new LabradorException("variable name " + basicVariableName + " source variable " + basicVariableName + " not exists");
                }
                Variable variable = Env.getVariables().getVariable(basicVariableName);
                Map<String, Object> variableKv = (Map<String, Object>) variablesEntry.getValue();

                EnumUtil.PutMethod putMethod = getPutMethod(variable);
                if (null == putMethod) {
                    throw new LabradorException("variable " + variable.getName() + " getPutmethod failed, variable function is " + variable.getElement().getFunctionMethod().name());
                }
                String groupkey = String.format("%s,%s", dimension, putMethod.name());
                Group group = groupMap.get(groupkey);
                if (null == group) {
                    String namespace = Env.getAerospikeDao().getNamespace();
                    if (!variableKv.containsKey("key")) {
                        throw new LabradorException("variable " + variable.getName() + " miss key");
                    }
                    Long ts = 0l;
                    try {
                        ts = Long.valueOf(variableKv.get("key").toString());
                    } catch (Exception e) {
                        throw new LabradorException("variable " + variable.getName() + " key is not timestamp type" + e.getMessage());
                    }
                    group = new Group(namespace, setName, pk, ts);
                    group.setVariables(new ArrayList<>(variablesKv.keySet()));
                    groupMap.put(groupkey, group);
                }
                group.setPutMethod(putMethod);
                Object value = getValue(putMethod, variable, variableKv);
                group.addItem(String.valueOf(variable.getCode()), variable.getName(), value);
            }
            return groupMap.values();
        }catch (Exception e) {
            logger.error("grouping failed, dimension: {} pk: {}", dimension, pk, e);
            return new ArrayList<Group>();
        }
    }

    private Object getValue(EnumUtil.PutMethod putMethod, Variable variable, Map<String, Object> variableKv) throws LabradorException {
        if (!variableKv.containsKey("value")) {
            throw new LabradorException("variable " + variable.getName() + " data illegal, miss [value] attribute");
        }

        if (variable.getName().equals("uid_timestamp__visit_dynamic_count__profile")) {
            Long ts = 0l;
            try {
                ts = Long.valueOf(variableKv.get("key").toString());
                String hour = ThreadLocalDateUtil.formatHour(new Date(ts));
                long value = Long.valueOf(variableKv.get("value").toString());
                Map<String, Long> valueMap = new HashMap<>();
                valueMap.put(hour, value);
                return valueMap;
            } catch (Exception e) {
                e.printStackTrace();
                throw new LabradorException("variable " + variable.getName() + " key is not timestamp type" + e.getMessage());
            }
        } else if(putMethod == EnumUtil.PutMethod.batch_merge) {
            return variableKv.get("value");
        } else if(variable.getElement().getValueType() == EnumUtil.DataType.map_type) {
            try {
                Map<String, Object> results = new HashMap<>();
                Map<String, Object> kv = (Map<String, Object>) variableKv.get("value");
                for (Map.Entry<String, Object> objectEntry : kv.entrySet()) {
                    results.put(objectEntry.getKey(), objectEntry.getValue());
                }
                return results;
            } catch (Exception e) {
                throw new LabradorException("variable " + variable.getName() + " getValue failed, " + e.getMessage());
            }
        } else {
            return variableKv.get("value");
        }
    }


    private EnumUtil.PutMethod getPutMethod(Variable variable) {
        EnumUtil.PutMethod putMethod = null;
        if(variable.getName().equals("uid_timestamp__visit_dynamic_count__profile")) {
            putMethod = EnumUtil.PutMethod.batch_merge_map_long;
            return putMethod;
        }

        if (variable.getElement().getFunctionMethod() == EnumUtil.Function.merge_value) {
            if (variable.getElement().getValueType() == EnumUtil.DataType.map_type) {
                if (variable.getElement().getValueSubType() == EnumUtil.DataType.long_type) {
                    putMethod = EnumUtil.PutMethod.batch_merge_map_long;
                    return putMethod;
                }
            }

            if (variable.getElement().getValueType() == EnumUtil.DataType.long_type) {
                putMethod = EnumUtil.PutMethod.batch_increment_long;
                return putMethod;
            }
        } else if (variable.getElement().getFunctionMethod() == EnumUtil.Function.last_value) {
            putMethod = EnumUtil.PutMethod.batch_put;
            return putMethod;
        } else if(variable.getElement().getFunctionMethod() == EnumUtil.Function.merge) {
            putMethod = EnumUtil.PutMethod.batch_merge;
        }
        return putMethod;
    }
}
