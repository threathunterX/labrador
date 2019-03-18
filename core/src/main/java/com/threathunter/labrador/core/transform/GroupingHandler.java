package com.threathunter.labrador.core.transform;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.core.exception.LabradorException;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public interface GroupingHandler {

    List<Group> grouping(Map<String, Object> transedKv, List<Variable> variables) throws LabradorException;

}
