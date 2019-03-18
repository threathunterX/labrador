package com.threathunter.labrador.core.builder.basic;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.common.exception.BuilderException;

/**
 * 
 */
public class CountMethodDefine implements VariableMethodDefine {

    @Override
    public Variable define(Variable variable) throws BuilderException {
        //有时间分区,period视为无时间分区
        if (variable.getElement().getPeriodType() != null) {
            if (variable.getElement().getGroupKeys().size() == 1) {
                variable.setPutMethod(EnumUtil.PutMethod.increment_map_period);
            } else if (variable.getElement().getGroupKeys().size() == 2) {
                variable.setPutMethod(EnumUtil.PutMethod.increment_map_period_second_index);
            } else {
                throw new BuilderException("variable " + variable.getName() + " too many group_keys");
            }
        } else {
            if (variable.getElement().getGroupKeys().size() == 1) {
                variable.setPutMethod(EnumUtil.PutMethod.increment);
            } else if(variable.getElement().getGroupKeys().size() == 2) {
                variable.setPutMethod(EnumUtil.PutMethod.increment_map);
            } else {
                throw new BuilderException("variable " + variable.getName() + " too many group_keys");
            }
        }
        return variable;
    }
}
