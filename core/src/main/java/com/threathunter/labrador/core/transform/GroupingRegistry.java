package com.threathunter.labrador.core.transform;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.ConfigUtil;
import com.threathunter.labrador.common.util.Constant;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class GroupingRegistry {

    private Map<String, GroupingHandler> handleGrouping = new HashMap<>();

    public GroupingRegistry() {
        handleGrouping.put(Constant.STREAM_GROUPING, new StreamGroupingHandler());
        handleGrouping.put(Constant.BATCH_GROUPING, new BatchGroupingHandler());
    }

    public List<Group> listGrouping(String groupingType, Map<String, Object> transedKv, List<Variable> variables) throws LabradorException {
        GroupingHandler groupingHandler = handleGrouping.get(groupingType);
        if (null == groupingHandler) {
            throw new LabradorException("grouping error, while no " + groupingType + " implement yet");
        }
        return groupingHandler.grouping(transedKv, variables);
    }

}
