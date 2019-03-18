package com.threathunter.labrador.core.builder.derivative;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.exception.LabradorException;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class ReadDefineRegistry {

    private Map<EnumUtil.Function, ReadMethodDefine> readMethodDefineMap = new HashMap();

    public ReadDefineRegistry() {
        this.readMethodDefineMap.put(EnumUtil.Function.distinct_count, new DistinctCountMethodDefine());
        this.readMethodDefineMap.put(EnumUtil.Function.max, new MaxMethodDefine());
        this.readMethodDefineMap.put(EnumUtil.Function.sum, new SumMethodDefine());
        this.readMethodDefineMap.put(EnumUtil.Function.distinct, new DistinctMethodDefine());

        this.readMethodDefineMap.put(EnumUtil.Function.global_sum, new GlobalSumMethodDefine());
        this.readMethodDefineMap.put(EnumUtil.Function.global_map_merge_topn, new GlobalMapMergeTopn());
    }

    public Object eval(Object sourceValue, Variable sourceVariable) throws LabradorException {
        ReadMethodDefine readMethodDefine = readMethodDefineMap.get(sourceVariable.getElement().getFunctionMethod());
        if(null == readMethodDefine) {
            throw new LabradorException("unknow getMethod " + sourceVariable.getElement().getFunctionMethod());
        }
        return readMethodDefine.read(sourceValue, sourceVariable);
    }

}
