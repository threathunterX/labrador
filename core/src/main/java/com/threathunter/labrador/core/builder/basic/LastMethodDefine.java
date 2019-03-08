package com.threathunter.labrador.core.builder.basic;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.common.exception.BuilderException;

/**
 * Created by wanbaowang on 17/9/6.
 */
public class LastMethodDefine implements VariableMethodDefine {

    @Override
    public Variable define(Variable variable) throws BuilderException {
        variable.setPutMethod(EnumUtil.PutMethod.put);
        return variable;
    }
}
