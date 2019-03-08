package com.threathunter.labrador.core.builder.basic;

import com.threathunter.labrador.common.exception.BuilderException;
import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;

/**
 * Created by wanbaowang on 17/11/13.
 */
public class SumMethodDefine implements VariableMethodDefine {
    @Override
    public Variable define(Variable variable) throws BuilderException {
        //有时间分区,period视为无时间分区
        if (variable.getElement().getPeriodType() != null) {
            if (variable.getElement().getGroupKeys().size() == 1) {
                variable.setPutMethod(EnumUtil.PutMethod.sum_map_period);
            } else if (variable.getElement().getGroupKeys().size() == 2) {
                variable.setPutMethod(EnumUtil.PutMethod.sum_map_period_second_index);
            } else {
                throw new BuilderException("variable " + variable.getName() + " too many group_keys");
            }
        } else {
            if (variable.getElement().getGroupKeys().size() == 1) {
                variable.setPutMethod(EnumUtil.PutMethod.sum);
            } else if(variable.getElement().getGroupKeys().size() == 2) {
                variable.setPutMethod(EnumUtil.PutMethod.sum_map);
            } else {
                throw new BuilderException("variable " + variable.getName() + " too many group_keys");
            }
        }
        return variable;
    }
}
