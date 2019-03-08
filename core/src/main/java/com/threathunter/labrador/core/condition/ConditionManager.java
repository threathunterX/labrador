package com.threathunter.labrador.core.condition;

import com.threathunter.labrador.common.model.Condition;
import com.threathunter.labrador.common.model.ConditionGroup;
import com.threathunter.labrador.common.model.Filter;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.exception.DataTypeNotMatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/11/10.
 */
public class ConditionManager {

    private Map<EnumUtil.Operation, ConditionEval> conditionEvalMap = new HashMap<>();

    public ConditionManager() {
        this.conditionEvalMap.put(EnumUtil.Operation.equals, new EqualsConditionEval());
    }

    public boolean isValid(Map<String,Object> kv, Filter filter) throws DataTypeNotMatchException {
        List<ConditionGroup> conditionGroups = filter.getGroups();
        List<Boolean> conditionGroupResults = new ArrayList<>();
        for(ConditionGroup conditionGroup : conditionGroups) {
            List<Boolean> conditionResults = new ArrayList<>();
            for(Condition condition : conditionGroup.getConditions()) {
                EnumUtil.Operation operation = condition.getOperation();
                ConditionEval conditionEval = conditionEvalMap.get(operation);
                conditionResults.add(conditionEval.isValid(kv, condition));
            }
            conditionGroupResults.add(computeBooleans(conditionResults, conditionGroup.getOperateType()));
        }
        return computeBooleans(conditionGroupResults, filter.getOperateType());
    }

    private boolean computeBooleans(List<Boolean> bools , EnumUtil.OperateType operateType) {
        boolean result = false;
        if(operateType == EnumUtil.OperateType.and) {
            result = true;
            for(Boolean bool : bools) {
                if(bool == false) {
                    result = false;
                    break;
                }
            }
        } else if(operateType == EnumUtil.OperateType.or){
            result = false;
            for(Boolean bool : bools) {
                if(bool == true) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

}
