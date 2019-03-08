package com.threathunter.labrador.core.transform;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.core.env.Env;

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
        List<Variable> sourceVariables = Env.getVariables().getVariablesByScene(sourceName);
        boolean sourceContainsVariable = false;
        if(null != sourceVariables) {
            for (Variable variable : sourceVariables) {
                if (variable.getName().equals(this.variable)) {
                    sourceContainsVariable = true;
                    break;
                }
            }
        }

        //TODO:ORDER_SUBMIT测试，暂时跳过
/*        if(sourceContainsVariable == false) {
            throw new IllegalStateException("variable " + this.variable + " not found in source " + sourceName);
        }*/
        if (Env.getVariables().contains(this.variable)) {
            variables.add(Env.getVariables().getVariable(this.variable));
        } else {
            throw new IllegalStateException("variable " + this.variable + " not found");
        }
        return variables;

    }
}
