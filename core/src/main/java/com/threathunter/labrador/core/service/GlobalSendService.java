package com.threathunter.labrador.core.service;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.threathunter.labrador.aerospike.AerospikeUtil;
import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.common.util.ThreadLocalDateUtil;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;

@Slf4j
public class GlobalSendService {

    public Event process(Map<String, Object> propertyValues) throws Exception {
//        String hour = ThreadLocalDateUtil.formatHour(new Date(ts));
//        String day = ThreadLocalDateUtil.formatDay(new Date(ts));
        String namespace = Env.getAerospikeDao().getNamespace();
//        Map<String, Object> propertyValues = event.getPropertyValues();
        propertyValues.forEach((slotVariableName, value) -> {
            log.warn("key: {} value :{}", slotVariableName, value);
            String basicVariableName = Env.getVariables().getProfileVariableFromSlot(slotVariableName);
            if (StringUtils.isBlank(basicVariableName)) {
                log.error("variable {} doesn't has valid profile variable", slotVariableName);
                return;
            }
            if (!Env.getVariables().contains(basicVariableName)) {
                log.error("variable {} doesn't exists", basicVariableName);
                return;
            }
            Variable variable = Env.getVariables().getVariable(basicVariableName);
            EnumUtil.Dimension dimension = variable.getElement().getDimension();
            if (dimension != EnumUtil.Dimension.global) {
                log.error("variable {} dimension is not global", basicVariableName);
                return;
            }
            final String setName;
            try {
                setName = Env.getAerospikeDao().getSetNames(dimension);
            } catch (LabradorException e) {
                log.error("get aerospike setName failed, variable is {}, exception is {}", basicVariableName, e);
                return;
            }
            long timestamp = Long.valueOf(((Map) value).get("key").toString());
            String hour = ThreadLocalDateUtil.formatHour(new Date(timestamp));
            String day = ThreadLocalDateUtil.formatDay(new Date(timestamp));
            String pk = String.format("%s%s", basicVariableName, day);
            Key key = new Key(namespace, setName, pk);
            Bin bin = new Bin(hour, ((Map) value).get("value"));
            AerospikeUtil.put(key, bin);
        });
        return null;
    }

    private String getDay(String ts) {
        return null;
    }

    private String getHour(String ts) {
        return null;
    }
}
