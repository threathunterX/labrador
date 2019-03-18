package com.threathunter.labrador.core.env;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.exception.LabradorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 */
public class VariableContainer {

    private Map<String, Variable> variables = new ConcurrentHashMap<String, Variable>();

    private Map<String, List<Variable>> sceneVariables = new ConcurrentHashMap<>();

    private Map<String, String> slot2ProfileMapping = new ConcurrentHashMap<>();

    private static Logger log = LoggerFactory.getLogger(VariableContainer.class);

    //维度所有的变量
    private Map<String, Set<String>> dimensionVariables = new ConcurrentHashMap<>();

    public String getVariableDimension(String name) throws LabradorException {
        Variable variable = this.variables.get(name);
        if(null == variable) {
            throw new LabradorException("variable " + name + " not found");
        }
        return variable.getElement().getDimension().name();
    }


    public void putVariableifAbsent(Variable variable) {
        variables.putIfAbsent(variable.getName(), variable);
    }

    public void putVariableIfAbsentWithDiemnsionScenes(Variable variable) {
        variables.putIfAbsent(variable.getName(), variable);
        //场景对应的所有变量
        if (sceneVariables.containsKey(variable.getElement().getSources().get(0))) {
            sceneVariables.get(variable.getElement().getSources().get(0)).add(variable);
        } else {
            List<Variable> listVariables = new ArrayList<>();
            listVariables.add(variable);
            sceneVariables.put(variable.getElement().getSources().get(0), listVariables);
        }

        if(variable.getProfileModuleType() == EnumUtil.ProfileModuleType.basic || variable.getProfileModuleType() == EnumUtil.ProfileModuleType.derived) {
            //维度对应的所有变量
            String dimension = variable.getElement().getDimension().name();
            if (dimensionVariables.containsKey(dimension)) {
                dimensionVariables.get(dimension).add(variable.getName());
            } else {
                Set<String> dimensionSet = new HashSet<>();
                dimensionSet.add(variable.getName());
                dimensionVariables.put(dimension, dimensionSet);
            }
        }
    }

    public void addSlot2ProfileMapping(String slot, String profile) {
        slot2ProfileMapping.putIfAbsent(slot, profile);
    }

    public String getProfileVariableFromSlot(String slot) {
        return slot2ProfileMapping.get(slot);
    }

    public List<Variable> getVariablesByScene(String scene) {
        return sceneVariables.get(scene);
    }

    public Set<String> loadDimensionVariables(String dimension) {
        return dimensionVariables.containsKey(dimension) ? dimensionVariables.get(dimension) : new HashSet<>();
    }

    public Variable getVariable(String name) {
        if (null == name) {
            return null;
        }
        return variables.get(name);
    }

    public boolean contains(String name) {
        return variables.containsKey(name);
    }


}
