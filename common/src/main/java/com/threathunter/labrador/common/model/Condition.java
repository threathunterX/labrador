package com.threathunter.labrador.common.model;

import com.threathunter.labrador.common.util.EnumUtil;

/**
 * Created by wanbaowang on 17/11/10.
 */
public class Condition {
    private String source;
    private String object;
    private EnumUtil.DataType objectType;
    private EnumUtil.Operation operation;
    private String value;
    private EnumUtil.FilterType type;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public EnumUtil.DataType getObjectType() {
        return objectType;
    }

    public void setObjectType(EnumUtil.DataType objectType) {
        this.objectType = objectType;
    }

    public EnumUtil.Operation getOperation() {
        return operation;
    }

    public void setOperation(EnumUtil.Operation operation) {
        this.operation = operation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EnumUtil.FilterType getType() {
        return type;
    }

    public void setType(EnumUtil.FilterType type) {
        this.type = type;
    }
}
