package com.threathunter.labrador.core.builder.basic;

import com.threathunter.labrador.common.exception.BuilderException;
import com.threathunter.labrador.common.model.Variable;

/**
 * Created by wanbaowang on 17/11/25.
 */
public class LastValueMethodDefine implements VariableMethodDefine{

    @Override
    public Variable define(Variable variable) throws BuilderException {

        return variable;
    }
}
