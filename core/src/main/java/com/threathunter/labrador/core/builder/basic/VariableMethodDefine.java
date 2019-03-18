package com.threathunter.labrador.core.builder.basic;

import com.threathunter.labrador.common.exception.BuilderException;
import com.threathunter.labrador.common.model.Variable;

/**
 * 
 */
public interface VariableMethodDefine {

    public Variable define(Variable variable) throws BuilderException;

}
