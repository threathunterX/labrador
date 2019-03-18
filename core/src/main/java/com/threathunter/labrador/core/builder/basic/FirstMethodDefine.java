package com.threathunter.labrador.core.builder.basic;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.common.exception.BuilderException;

/**
 * 
 */
public class FirstMethodDefine implements VariableMethodDefine {
    @Override
    public Variable define(Variable variable) throws BuilderException {
        variable.setPutMethod(EnumUtil.PutMethod.put_first);
        return variable;
    }
}
