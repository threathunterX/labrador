package com.threathunter.labrador.common.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class QueryGroup {
    private String namespace;
    private String setName;
    private List<String> variables;
    private List<String> codes;

    public QueryGroup(String namespace , String setName) {
        this.namespace = namespace;
        this.setName = setName;
        this.variables = new ArrayList<>();
        this.codes = new ArrayList<>();
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public void add(String variable, String code) {
        this.variables.add(variable);
        this.codes.add(code);
    }
}
