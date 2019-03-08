package com.threathunter.labrador.core.builder.basic;

import com.threathunter.labrador.common.exception.BuilderException;
import com.threathunter.labrador.common.model.Variable;

/**
 * Created by wanbaowang on 17/9/6.
 */
public interface VariableMethodDefine {

    public Variable define(Variable variable) throws BuilderException;

}
