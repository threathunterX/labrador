package com.threathunter.labrador.core.condition;

import com.threathunter.labrador.common.model.Condition;
import com.threathunter.labrador.core.exception.DataTypeNotMatchException;

import java.util.Map;

/**
 * 
 */
public class EqualsConditionEval implements ConditionEval {

    @Override
    public boolean isValid(Map<String, Object> kv, Condition condition) throws DataTypeNotMatchException {
        String field = condition.getObject();
        if(!kv.containsKey(field)) {
            return false;
        }
        Object value = kv.get(field);
        if(!(value instanceof String)) {
            throw new DataTypeNotMatchException("field name " + field + " datatype expect String, but event datatype is " + value.getClass().getName() + " kv is  " + kv.toString());
        }
        return String.valueOf(value).equalsIgnoreCase(condition.getValue());
    }
}
