package com.threathunter.labrador.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 变量转换kv的过程中，可能有些变量需要的field不满足。
 * 如:修改密码中的token_type没有，则uid__account_token_change_mobile__profile变量无需加入。
 * 即：field不满足，默认fitler为false
 */
public class TransformResponse {

    private Map<String,Object> kv;

    private List<Variable> fieldExistsVariables;

    public TransformResponse() {
        this.kv = new HashMap<>();
        this.fieldExistsVariables = new ArrayList<>();
    }

    public void addVariable(Variable variable) {
        this.fieldExistsVariables.add(variable);
    }

    public Map<String, Object> getKv() {
        return kv;
    }

    public void setKv(Map<String, Object> kv) {
        this.kv = kv;
    }

    public List<Variable> getFieldExistsVariables() {
        return this.fieldExistsVariables;
    }
}
