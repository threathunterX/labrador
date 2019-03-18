package com.threathunter.labrador.core.transform;

import com.threathunter.geo.GeoUtil;
import com.threathunter.labrador.common.model.Condition;
import com.threathunter.labrador.common.model.ConditionGroup;
import com.threathunter.labrador.common.model.TransformResponse;
import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.exception.DataTypeNotMatchException;
import com.threathunter.model.Event;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class EventTransform {

    private static final Logger logger = LoggerFactory.getLogger(EventTransform.class);

    private PropertyDescriptor[] propertyDescriptors;

    public EventTransform() throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(Event.class, Object.class);
        this.propertyDescriptors = beanInfo.getPropertyDescriptors();
    }


    public TransformResponse transform(Event event, List<Variable> variables) throws DataTypeNotMatchException {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> eventMap = /*bean2Map(event);*/ event.genAllData();
        Map<String, Object> eventProps = eventMap.containsKey("propertyValues") ? (Map<String, Object>) eventMap.get("propertyValues") : event.getPropertyValues();
        TransformResponse transformResponse = new TransformResponse();
        for (Variable variable : variables) {
            try {
                String dimension = getDimensionProperty(variable);
                //暂未加载
                if (!result.containsKey(dimension)) {
                    fillResult(dimension, variable.getName(), result, eventMap, eventProps, event);
                }
                String functionField = variable.getElement().getFunctionField();
                if (null != functionField) {
                    if (!result.containsKey(functionField)) {
                        fillResult(functionField, variable.getName(), result, eventMap, eventProps, event);
                    }
                }

                if (null != variable.getElement().getFilter()) {
                    for (ConditionGroup conditionGroup : variable.getElement().getFilter().getGroups()) {
                        for (Condition condition : conditionGroup.getConditions()) {
                            if (!result.containsKey(condition.getObject())) {
                                fillResult(condition.getObject(), variable.getName(), result, eventMap, eventProps, event);
                            }
                        }
                    }
                }

                for (String groupKey : variable.getElement().getGroupKeys()) {
                    if (!result.containsKey(groupKey)) {
                        fillResult(groupKey, variable.getName(), result, eventMap, eventProps, event);
                    }
                }
                transformResponse.addVariable(variable);
            } catch (DataTypeNotMatchException e) {
                logger.error("datat type not matches, event is "  + event + " variable is " + variable.getName());
            }
        }
        transformResponse.setKv(result);
        return transformResponse;
    }

    private void fillResult(String key, String variableName, Map<String, Object> kv, Map<String, Object> eventMap, Map<String, Object> eventProps, Event event) throws DataTypeNotMatchException {
        if(key.equals("geo_city")) {
            String ip = String.valueOf(event.getPropertyValues().get("c_ip"));
            kv.put("geo_city", GeoUtil.getCNIPCity(ip));
        } else {
            //hard code，配置中写入的是ip，在event的properties为c_ip
            String getKey = key.equals("ip") ? "c_ip" : key;
            if (eventMap.containsKey(getKey)) {
                kv.put(key, eventMap.get(getKey));
            } else if (eventProps.containsKey(getKey)) {
                kv.put(key, eventProps.get(getKey));
            } else if (StringUtils.isBlank(key)) {
                kv.put(key, "");
            } else {
                throw new DataTypeNotMatchException("variable " + variableName + " dimension need field " + key + ", but event " + event + " doesn't match");
            }
        }
    }

    private String getDimensionProperty(Variable variable) {
        if (variable.getElement().getDimension() == EnumUtil.Dimension.others) {
            return variable.getElement().getDimensionField();
        } else {
            return variable.getElement().getDimension().name();
        }
    }


    private Map<String, Object> bean2Map(Event obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        try {
            for (PropertyDescriptor property : this.propertyDescriptors) {
                String key = property.getName();
                if (key.compareToIgnoreCase("class") == 0) {
                    continue;
                }
                Method getter = property.getReadMethod();
                Object value = getter != null ? getter.invoke(obj) : null;
                map.put(key, value);
            }
        } catch (Exception e) {
            logger.error("bean to map error, " + e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

}
