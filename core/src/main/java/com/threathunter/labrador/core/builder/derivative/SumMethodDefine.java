package com.threathunter.labrador.core.builder.derivative;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.exception.LabradorException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class SumMethodDefine implements ReadMethodDefine {
    @Override
    public Object read(Object sourceValue, Variable sourceVariable) throws LabradorException {
        EnumUtil.DataType dataType = sourceVariable.getElement().getFunctionObjectDataType();
        Object value = null;
        switch (dataType) {
            case map_type:
                if (!(sourceValue instanceof Map)) {
                    throw new LabradorException("Read variable " + sourceVariable.getName() + " error, parameter sourceValue is not instaceof map, sourceValue is " + sourceValue);
                }
                Map<String,Long> periodMap = (Map<String,Long>) sourceValue;
                value = readMapTypeValue(periodMap, sourceVariable);
                break;
        }
        return value;
    }

    private Object readMapTypeValue(Map<String, Long> sourceMap, Variable variable) {
        //是否是前缀匹配
        boolean isPrefix = true;
        if(variable.getElement().getPeriodType() == EnumUtil.PeriodType.last_n_days) {
            isPrefix =  true;
        } else {
            isPrefix = false;
        }
        List<String> durings = ReadMethodDefine.getDuring(variable.getElement().getPeriodType(),
                variable.getElement().getPeriodStart(), variable.getElement().getPeriodEnd());
        long sumValue = 0L;
        Set<String> hours = sourceMap.keySet();
        for (String hour : hours) {
            if(isPrefix) {
                String curDay = hour.substring(0, 8);
                if (durings.contains(curDay)) {
                    sumValue = sumValue + sourceMap.get(hour);
                }
            } else {
                if(durings.contains(hour)) {
                    sumValue = sumValue + sourceMap.get(hour);
                }
            }
        }
        return sumValue;

    }
}
