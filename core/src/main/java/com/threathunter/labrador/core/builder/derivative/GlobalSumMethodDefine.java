package com.threathunter.labrador.core.builder.derivative;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.exception.LabradorException;

import java.util.Map;

public class GlobalSumMethodDefine implements ReadMethodDefine {
    @Override
    public Object read(Object sourceValue, Variable sourceVariable) throws LabradorException {
        EnumUtil.DataType dataType = sourceVariable.getElement().getValueType();
        if(dataType != EnumUtil.DataType.long_type && dataType != EnumUtil.DataType.double_type) {
            return new LabradorException("illegal datatype with global_sum method");
        }
        if(dataType == EnumUtil.DataType.long_type) {
            Map sourceMap = (Map) sourceValue;
            long result = sourceMap.values().stream().mapToLong(n -> Long.valueOf(n.toString())).sum();
            return result;
        } else {
            Map sourceMap = (Map) sourceValue;
            double result = sourceMap.values().stream().mapToDouble(n -> Double.valueOf(n.toString())).sum();
            return result;
        }
    }
}
