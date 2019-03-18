package com.threathunter.labrador.common.model;

import com.threathunter.labrador.common.util.EnumUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class Filter {

    private List<ConditionGroup> groups;
    private EnumUtil.OperateType operateType;

    public Filter() {
        groups = new ArrayList<>();
    }


    public List<ConditionGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ConditionGroup> groups) {
        this.groups = groups;
    }

    public EnumUtil.OperateType getOperateType() {
        return operateType;
    }

    public void setOperateType(EnumUtil.OperateType operateType) {
        this.operateType = operateType;
    }

    public void addGroups(ConditionGroup conditionGroup) {
        this.groups.add(conditionGroup);
    }
}
