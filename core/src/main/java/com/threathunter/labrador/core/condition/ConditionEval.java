package com.threathunter.labrador.core.condition;

import com.threathunter.labrador.common.model.Condition;
import com.threathunter.labrador.core.exception.DataTypeNotMatchException;

import java.util.Map;

/**
 * Created by wanbaowang on 17/11/10.
 */
public interface ConditionEval {
    public boolean isValid(Map<String,Object> kv, Condition condition) throws DataTypeNotMatchException;
}
