package com.threathunter.labrador.common.model;

import com.threathunter.labrador.common.util.EnumUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanbaowang on 17/11/10.
 */
public class ConditionGroup {

    private List<Condition> conditions;

    private EnumUtil.OperateType operateType;

    public ConditionGroup() {
        conditions = new ArrayList<>();
        operateType = EnumUtil.OperateType.and;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public EnumUtil.OperateType getOperateType() {
        return operateType;
    }

    public void setOperateType(EnumUtil.OperateType operateType) {
        this.operateType = operateType;
    }

    public void addCondition(Condition condition) {
        this.conditions.add(condition);
    }
}
