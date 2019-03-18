package com.threathunter.labrador.core.builder.basic;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.common.exception.BuilderException;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class MethodDefineRegistry {

    private Map<EnumUtil.Function, VariableMethodDefine> methodDefineRegistry = new HashMap<>();

    public MethodDefineRegistry() {
        //streaming的写方法
        methodDefineRegistry.put(EnumUtil.Function.count, new CountMethodDefine());
        methodDefineRegistry.put(EnumUtil.Function.distinct, new DistinctMethodDefine());
        methodDefineRegistry.put(EnumUtil.Function.first, new FirstMethodDefine());
        methodDefineRegistry.put(EnumUtil.Function.last, new LastMethodDefine());
        methodDefineRegistry.put(EnumUtil.Function.lastn, new LastNMethodDefine());
        methodDefineRegistry.put(EnumUtil.Function.sum, new SumMethodDefine());
        methodDefineRegistry.put(EnumUtil.Function.group_count, new GroupCounMethodDefine());


        //batch的写方法
        methodDefineRegistry.put(EnumUtil.Function.last_value, new LastValueMethodDefine());
        methodDefineRegistry.put(EnumUtil.Function.merge_value, new MergeValueMethodDefine());

    }

    public Variable define(EnumUtil.Function function, Variable variable) throws BuilderException {
        VariableMethodDefine variableMethodDefine = methodDefineRegistry.get(function);
        if (null == variableMethodDefine) {
            throw new BuilderException("variable " + variable.getName() + " makeup error, no VariableMethodDefine [" + function.name() + "] found");
        }
        return variableMethodDefine.define(variable);
    }

}
