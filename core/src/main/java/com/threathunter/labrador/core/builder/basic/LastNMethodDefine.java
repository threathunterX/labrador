package com.threathunter.labrador.core.builder.basic;

import com.threathunter.labrador.common.exception.BuilderException;
import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;

/**
 * 
 */
public class LastNMethodDefine implements VariableMethodDefine {
    @Override
    public Variable define(Variable variable) throws BuilderException {
        if (null == variable.getElement().getPeriodType()) {
            variable.setPutMethod(EnumUtil.PutMethod.put_list_n);
        } else {
            variable.setPutMethod(EnumUtil.PutMethod.put_map_list);
        }
        return variable;
    }
}
