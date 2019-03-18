package com.threathunter.labrador.core.builder.derivative;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.exception.LabradorException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class DistinctCountMethodDefine implements ReadMethodDefine {

    @Override
    public Object read(Object sourceValue, Variable sourceVariable) throws LabradorException {
        EnumUtil.DataType dataType = sourceVariable.getElement().getFunctionObjectDataType();
        Object value = null;
        switch (dataType) {
            case mlist_type:
                if (!(sourceValue instanceof Map)) {
                    throw new LabradorException("Read variable " + sourceVariable.getName() + " error, parameter sourceValue is not instaceof map, sourceValue is " + sourceValue);
                }
                Map<String, List<Object>> mapValue = (Map<String, List<Object>>) sourceValue;
                value = readMlistType(mapValue, sourceVariable);
                break;
            default:
                break;
        }
        return value;
    }


    public Object readMlistType(Map<String, List<Object>> sourceMap, Variable variable) {

        //是否是前缀匹配
        boolean isPrefix = true;
        if(variable.getElement().getPeriodType() == EnumUtil.PeriodType.last_n_days) {
            isPrefix =  true;
        } else {
            isPrefix = false;
        }
        List<String> durings = ReadMethodDefine.getDuring(variable.getElement().getPeriodType(),
                variable.getElement().getPeriodStart(), variable.getElement().getPeriodEnd());

        Set<String> hours = sourceMap.keySet();
        Set<Object> distinctValues = new HashSet<>();
        for (String hour : hours) {
            if(isPrefix) {
                String curDay = hour.substring(0, 8);
                if (durings.contains(curDay)) {
                    distinctValues.addAll(sourceMap.get(hour));
                }
            } else {
                if(durings.contains(hour)) {
                    distinctValues.addAll(sourceMap.get(hour));
                }
            }
        }
        return distinctValues.size();
    }

}
