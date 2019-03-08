package com.threathunter.labrador.core.builder.derivative;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.exception.LabradorException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalMapMergeTopn implements ReadMethodDefine {
    @Override
    public Object read(Object sourceValue, Variable sourceVariable) throws LabradorException {
        Map sourceMap = (Map) sourceValue;
        Map<String,Long> mergeMapLong = new HashMap();
        Map<String,Double> mergeMapDouble = new HashMap();
        Map sortMap = new LinkedHashMap();

        sourceMap.forEach((key, valueMap) -> {
            Map currentMap = (Map) valueMap;
            currentMap.forEach((k1, v1) -> {
                String curKey = String.valueOf(k1);
                if (sourceVariable.getElement().getValueSubType() == EnumUtil.DataType.long_type) {
                    long cur = mergeMapLong.containsKey(curKey) ?  (Long) mergeMapLong.get(curKey) + Long.valueOf(v1.toString()) : Long.valueOf(v1.toString());
                    mergeMapLong.put(curKey, cur);
                } else {
                    double cur = mergeMapDouble.containsKey(curKey) ? (Double) mergeMapDouble.get(curKey) + Double.valueOf(v1.toString()) : Double.valueOf(v1.toString());
                    mergeMapDouble.put(curKey, cur);
                }
            });
        });
        int limit = sourceVariable.getElement().getFunctionSize();
        if (sourceVariable.getElement().getValueSubType() == EnumUtil.DataType.long_type) {
            mergeMapLong.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(limit)
                    .forEachOrdered(x -> sortMap.put(x.getKey(), x.getValue()));
        } else {
            mergeMapDouble.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).limit(limit)
                    .forEachOrdered(x -> sortMap.put(x.getKey(), x.getValue()));
        }
        return sortMap;
    }
}
