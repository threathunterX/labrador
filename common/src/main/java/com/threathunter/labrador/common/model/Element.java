package com.threathunter.labrador.common.model;

import com.threathunter.labrador.common.util.EnumUtil.*;

import java.util.List;

/**
 * Created by wanbaowang on 17/8/21.
 * 从运营配置中提取的Element
 */
public class Element {

    //原始json数据
    private String text;

    private Module module;

    private String name;

    private String remark;

    private Dimension dimension;

    private String dimensionField;

    //基础变量时，source为事件名
    private List<String> sources;

    //衍生变量时，source为基础变量名
    private String sourceVariable;

    private OperateType filterGroupTYPE;

    private PeriodType periodType;

    /*
    start和end都可以不填
    start和end都为0或者负数。0表示当前，end为0表示结束时间为当前。其中如果为0可以不用填写。
    start必须小于等于end
     */
    private int periodStart;

    private int periodEnd;

    private Function functionMethod;

    private String functionField;

    private Status status;

    private DataType valueType;

    private DataType valueSubType;

    private Dimension valueCategory;

    private DataType functionObjectDataType;

    private boolean isBasic;

    private List<String> groupKeys;

    private Filter filter;

    //在function里的param配置
    private int functionSize;

    public Element(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }


    public OperateType getFilterGroupTYPE() {
        return filterGroupTYPE;
    }

    public void setFilterGroupTYPE(OperateType filterGroupTYPE) {
        this.filterGroupTYPE = filterGroupTYPE;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public Function getFunctionMethod() {
        return functionMethod;
    }

    public void setFunctionMethod(Function functionMethod) {
        this.functionMethod = functionMethod;
    }

    public String getDimensionField() {
        return dimensionField;
    }

    public void setDimensionField(String dimensionField) {
        this.dimensionField = dimensionField;
    }

    public String getFunctionField() {
        return functionField;
    }

    public void setFunctionField(String functionField) {
        this.functionField = functionField;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSourceVariable() {
        return sourceVariable;
    }

    public void setSourceVariable(String sourceVariable) {
        this.sourceVariable = sourceVariable;
    }

    public boolean isBasic() {
        return isBasic;
    }

    public void setBasic(boolean basic) {
        isBasic = basic;
    }

    public List<String> getGroupKeys() {
        return groupKeys;
    }

    public void setGroupKeys(List<String> groupKeys) {
        this.groupKeys = groupKeys;
    }

    public DataType getValueType() {
        return valueType;
    }

    public void setValueType(DataType valueType) {
        this.valueType = valueType;
    }

    public DataType getValueSubType() {
        return valueSubType;
    }

    public void setValueSubType(DataType valueSubType) {
        this.valueSubType = valueSubType;
    }

    public Dimension getValueCategory() {
        return valueCategory;
    }

    public void setValueCategory(Dimension valueCategory) {
        this.valueCategory = valueCategory;
    }

    public DataType getFunctionObjectDataType() {
        return functionObjectDataType;
    }

    public void setFunctionObjectDataType(DataType functionObjectDataType) {
        this.functionObjectDataType = functionObjectDataType;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public int getFunctionSize() {
        return functionSize;
    }

    public void setFunctionSize(int functionSize) {
        this.functionSize = functionSize;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public int getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(int periodStart) {
        this.periodStart = periodStart;
    }

    public int getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(int periodEnd) {
        this.periodEnd = periodEnd;
    }
}
