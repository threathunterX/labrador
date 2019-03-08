package com.threathunter.labrador.core.transform;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.Constant;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.common.util.ThreadLocalDateUtil;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/9/11.
 */
public class StreamGroupingHandler implements GroupingHandler {
    @Override
    public List<Group> grouping(Map<String, Object> transedKv, List<Variable> variables) throws LabradorException {
        Map<String, Group> collectGroups = new HashMap<>();
        for (Variable variable : variables) {
            String dimension = String.valueOf(transedKv.get(variable.getElement().getDimension().name()));
            EnumUtil.PutMethod putMethod = variable.getPutMethod();
            String groupKey = String.format("%s:%s", dimension, putMethod);
            EnumUtil.PeriodType periodType = null;
            if (variable.getElement().getPeriodType() != null) {
                periodType = variable.getElement().getPeriodType();
                groupKey = groupKey + "_" + periodType.name();
            }
            Group _group = null;
            if (collectGroups.containsKey(groupKey)) {
                _group = collectGroups.get(groupKey);
            } else {
                String pk = String.valueOf(transedKv.get(variable.getElement().getDimensionField()));
                String setName = Env.getAerospikeDao().getSetNames(variable.getElement().getDimension());
                String namespace = Env.getAerospikeDao().getNamespace();
                _group = new Group(namespace, setName, transedKv, putMethod, pk);
                _group.setTs((Long) transedKv.get(Constant.TIMESTAMP));
                _group.setCurrentHour(ThreadLocalDateUtil.formatDayHourByTimestamp(_group.getTs()));
                if (null != periodType) {
                    _group.setPeriodType(periodType);
                }
                collectGroups.put(groupKey, _group);
            }
            String field = variable.getElement().getFunctionField();
            String code = String.valueOf(variable.getCode());
            Object functionObjectValue = StringUtils.isBlank(field) ? "" : transedKv.get(field);
            switch (variable.getElement().getFunctionObjectDataType().name()) {
                case "string_type":
                    if (!(functionObjectValue instanceof String)) {
                        throw new LabradorException("StreamEventSend grouping fail, variable " + variable.getName() + "  functionField need string, but functionFieldValue is " + functionObjectValue);
                    }
                    break;
                case "long_type":
                    if (!(functionObjectValue instanceof Long) && !(functionObjectValue instanceof Integer)) {
                        throw new LabradorException("StreamEventSend grouping fail, variable " + variable.getName() + "  functionField need int/long, but functionFieldValue is " + functionObjectValue);
                    }
                    break;
                case "double_type":
                    if (!(functionObjectValue instanceof Double)) {
                        throw new LabradorException("StreamEventSend grouping fail, variable " + variable.getName() + "  functionField need double, but functionFieldValue is " + functionObjectValue);
                    }
                    break;
                case "empty_type":
                    if (!StringUtils.equals(functionObjectValue.toString().trim(), "")) {
                        throw new LabradorException("StreamEventSend grouping fail, variable " + variable.getName() + "  functionField need int/long, but functionFieldValue is " + functionObjectValue);
                    }
                    break;
                default:
                    throw new LabradorException("StreamEventSend grouping fail, variable " + variable.getName() + " unknown functionField datatype whit " + functionObjectValue.getClass().getSimpleName());

            }

            int functionSize = variable.getElement().getFunctionSize();
            List<String> groupKeys = variable.getElement().getGroupKeys();
            String functionField = variable.getElement().getFunctionField();
            _group.addItem(code, functionObjectValue, functionSize, groupKeys, functionField);
            _group.addVariables(variable.getName());
        }
        return new ArrayList<>(collectGroups.values());
    }
}
