package com.threathunter.labrador.core.transform;

import com.threathunter.labrador.common.model.TransformResponse;
import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.Constant;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.DataTypeNotMatchException;
import com.threathunter.model.Event;

import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/9/7.
 */
public abstract class Extract {

    public abstract List<Variable> getVariablesBySouceName(String sourceName);

    public TransformResponse extract(Event event, List<Variable> variables) throws DataTypeNotMatchException {
        TransformResponse response = Env.getTransForm().transform(event, variables);
        response.getKv().put(Constant.TIMESTAMP, event.getTimestamp());
        return response;
    }
}
