package com.threathunter.labrador.core.transform;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.core.exception.LabradorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class BatchGroupingHandler implements GroupingHandler {
    @Override
    public List<Group> grouping(Map<String, Object> dimensionKv, List<Variable> variables) throws LabradorException {
        List<Group> groups = new ArrayList<>();


        for (Map.Entry<String, Object> entry : dimensionKv.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!(value instanceof Map)) {
                throw new LabradorException("grouping error, key " + key + " expect Map, but value is " + value);
            }
        }
        return groups;
    }
}
