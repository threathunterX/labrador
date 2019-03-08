package com.threathunter.labrador.integration;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.transform.Extract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanbaowang on 17/9/8.
 */
public class SpecialVariableExtract extends Extract {

    private String variable;

    public SpecialVariableExtract(String variable) {
        this.variable = variable;
    }

    @Override
    public List<Variable> getVariablesBySouceName(String sourceName) {
        List<Variable> variables = new ArrayList<>();
        if (Env.getVariables().contains(this.variable)) {
            variables.add(Env.getVariables().getVariable(this.variable));
        } else {
            throw new IllegalStateException("variable " + this.variable + " not found");
        }
        return variables;

    }
}
