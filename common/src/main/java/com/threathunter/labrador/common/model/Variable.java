package com.threathunter.labrador.common.model;

import com.threathunter.labrador.common.util.EnumUtil;

/**
 * Created by wanbaowang on 17/8/21.
 */
public class Variable {

    private Element element;

    //是否是衍生变量
    private boolean isDerived;

    //变量名
    private String name;

    //变量的编码
    private int code;

    //put方法
    private EnumUtil.PutMethod putMethod;

    //get方法
    private EnumUtil.GetMethod getMethod;

    //操作对象数据类型
    private EnumUtil.DataType functionObjectDataType;

    private EnumUtil.ProfileModuleType profileModuleType;


    public Variable(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public boolean isDerived() {
        return isDerived;
    }

    public void setDerived(boolean derived) {
        isDerived = derived;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public EnumUtil.PutMethod getPutMethod() {
        return putMethod;
    }

    public void setPutMethod(EnumUtil.PutMethod putMethod) {
        this.putMethod = putMethod;
    }

    public EnumUtil.GetMethod getGetMethod() {
        return getMethod;
    }

    public void setGetMethod(EnumUtil.GetMethod getMethod) {
        this.getMethod = getMethod;
    }

    public EnumUtil.DataType getFunctionObjectDataType() {
        return functionObjectDataType;
    }

    public void setFunctionObjectDataType(EnumUtil.DataType functionObjectDataType) {
        this.functionObjectDataType = functionObjectDataType;
    }


    public EnumUtil.ProfileModuleType getProfileModuleType() {
        return profileModuleType;
    }

    public void setProfileModuleType(EnumUtil.ProfileModuleType profileModuleType) {
        this.profileModuleType = profileModuleType;
    }
}
