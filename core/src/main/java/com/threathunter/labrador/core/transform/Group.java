package com.threathunter.labrador.core.transform;

import com.threathunter.labrador.common.util.EnumUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/8/29.
 */
public class Group {
    private String setName;

    private String pk;

    private long ts;

    private EnumUtil.PutMethod putMethod;

    private EnumUtil.PeriodType periodType;

    private List<Item> items = new ArrayList<>();

    private String namespace;

    private List<String> variables;

    private Map<String, Object> transformedKv;

    private String currentHour;

    public class Item {
        //aerospike中的bin名称
        private String code;
        //aerospike中的bin value
        private Object value;
        //变量名称
        private String name;

        private int functionSize;

        private List<String> groupKeys;

        private String functionField;

        public Item(String code, Object value, int functionSize, List<String> groupKeys, String functionField) {
            this.code = code;
            this.value = value;
            this.functionSize = functionSize;
            this.groupKeys = groupKeys;
            this.functionField = functionField;
        }

        public Item(String code, String name, Object value) {
            this.code = code;
            this.name = name;
            this.value = value;
        }

        public Item(String code, Object value, int functionSize) {
            this.code = code;
            this.value = value;
            this.functionSize = functionSize;
        }

        public String getCode() {
            return code;
        }

        public Object getValue() {
            return value;
        }


        public void setCode(String code) {
            this.code = code;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getFunctionSize() {
            return functionSize;
        }

        public void setFunctionSize(int functionSize) {
            this.functionSize = functionSize;
        }

        public List<String> getGroupKeys() {
            return groupKeys;
        }

        public void setGroupKeys(List<String> groupKeys) {
            this.groupKeys = groupKeys;
        }

        public String getFunctionField() {
            return functionField;
        }

        public void setFunctionField(String functionField) {
            this.functionField = functionField;
        }
    }

    public String getPk() {
        return pk;
    }

    public Group(String namespace, String setName, String pk, long ts) {
        this.namespace = namespace;
        this.setName = setName;
        this.pk = pk;
        this.ts = ts;
    }

    public Group(String namespace, String setName, Map<String, Object> transformedKv, EnumUtil.PutMethod putMethod, String pk) {
        this.namespace = namespace;
        this.setName = setName;
        this.transformedKv = transformedKv;
        this.putMethod = putMethod;
        this.variables = new ArrayList<>();
        this.pk = pk;
    }

    public Group(String namespace, String setName, EnumUtil.PutMethod putMethod, String pk) {
        this.namespace = namespace;
        this.setName = setName;
        this.putMethod = putMethod;
        this.pk = pk;
    }

    public void addItem(String code, Object value, int functionSize, List<String> groupKeys, String functionField) {
        Item item = new Item(code, value, functionSize, groupKeys, functionField);
        this.items.add(item);
    }

    public void addItem(String code, Object value, int periodValue) {
        Item item = new Item(code, value, periodValue);
        this.items.add(item);
    }

    public void addItem(String code, String name, Object value) {
        Item item = new Item(code, name, value);
        this.items.add(item);
    }

    public void addVariables(String variable) {
        this.variables.add(variable);
    }

    public String getSetName() {
        return setName;
    }


    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public EnumUtil.PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(EnumUtil.PeriodType periodType) {
        this.periodType = periodType;
    }


    public EnumUtil.PutMethod getPutMethod() {
        return putMethod;
    }

    public void setPutMethod(EnumUtil.PutMethod putMethod) {
        this.putMethod = putMethod;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Map<String, Object> getTransformedKv() {
        return transformedKv;
    }

    public void setTransformedKv(Map<String, Object> transformedKv) {
        this.transformedKv = transformedKv;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    public String getCurrentHour() {
        return currentHour;
    }

    public void setCurrentHour(String currentHour) {
        this.currentHour = currentHour;
    }

    @Override
    public String toString() {
        return this.setName + ":" + this.putMethod.name();
    }
}
