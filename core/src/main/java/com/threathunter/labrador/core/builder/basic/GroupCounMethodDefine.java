package com.threathunter.labrador.core.builder.basic;

import com.threathunter.labrador.common.exception.BuilderException;
import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;

/**
 * Created by wanbaowang on 17/11/21.
 */
public class GroupCounMethodDefine implements VariableMethodDefine {
    @Override
    public Variable define(Variable variable) throws BuilderException {
        //如果没有时间分区
        if (variable.getElement().getPeriodType() == null) {
            variable.setPutMethod(EnumUtil.PutMethod.increment_map);
        } else {
            variable.setPutMethod(EnumUtil.PutMethod.increment_map_period_second_index);
        }
        return variable;
    }
}
