package com.threathunter.labrador.core.service;

import com.threathunter.labrador.common.model.TransformResponse;
import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.Constant;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.DataTypeNotMatchException;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.transform.Extract;
import com.threathunter.labrador.core.transform.Group;
import com.threathunter.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/8/25.
 */
public class StreamSendService implements Send {

    private static final Logger logger = LoggerFactory.getLogger(StreamSendService.class);

    private Extract extract;

    public StreamSendService(Extract extract) {
        this.extract = extract;
    }

    public Event process(Event event) throws DataTypeNotMatchException, LabradorException {
        String sourceaName = event.getName();
        List<Variable> sourceVariables = extract.getVariablesBySouceName(sourceaName);
        if (null == sourceVariables) {
            logger.error("event " + sourceaName + " related variables is null, event is " + event.toString());
            return null;
        }
        try {
            TransformResponse transformResponse = extract.extract(event, sourceVariables);
            List<Variable> fieldExistsVariable = transformResponse.getFieldExistsVariables();
            List<Variable> validVariables = new ArrayList<>();
            Map<String,Object> transformedKv = transformResponse.getKv();
            for (Variable variable : fieldExistsVariable) {
                //如果变量没有filter属性，则变量不需要进验证
                if(null == variable.getElement().getFilter()) {
                    validVariables.add(variable);
                    continue;
                }
                if(Env.getConditionManager().isValid(transformedKv, variable.getElement().getFilter())) {
                    validVariables.add(variable);
                } else {
                    logger.warn("variable " + variable.getName() + " is not valid, transformed event is " + transformedKv.toString());
                }
            }
            if (validVariables.size() > 0) {
                List<Group> groups = Env.getGroupRegistry().listGrouping(Constant.STREAM_GROUPING, transformedKv, validVariables);
                for (Group group : groups) {
                    Env.getAerospikeDao().send(group);
                }
            }
        } catch (Exception e) {
            logger.error("process exception ", e);
            throw e;
        }
        return null;
    }

}
