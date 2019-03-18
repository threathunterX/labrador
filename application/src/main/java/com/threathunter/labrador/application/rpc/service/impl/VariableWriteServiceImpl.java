package com.threathunter.labrador.application.rpc.service.impl;

import com.threathunter.labrador.application.rpc.service.VariableWriteService;
import com.threathunter.labrador.common.util.EventUtils;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.StreamSendService;
import com.threathunter.labrador.core.transform.EnvExtract;
import com.threathunter.labrador.core.transform.Extract;
import com.threathunter.labrador.core.transform.SpecialVariableExtract;
import com.threathunter.labrador.rpc.server.RpcService;
import com.threathunter.model.Event;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 
 */
@RpcService(VariableWriteService.class)
public class VariableWriteServiceImpl implements VariableWriteService {

    @Override
    public void writeVariable(String name, String variable, Map<String, Object> kv) throws Exception {
        Event event = new Event();
        event.setId(EventUtils.randomId());
        event.setName(name);
        if(kv.containsKey("timestamp")) {
            try {
                Long timestamp = Long.valueOf(kv.remove("timestamp").toString());
                event.setTimestamp(timestamp);
            } catch (Exception e) {
                throw new LabradorException("timestamp is not Long type");
            }
        } else {
            event.setTimestamp(System.currentTimeMillis());
        }
        event.setPropertyValues(kv);
        Extract extract = null;
        if(StringUtils.isBlank(variable)) {
            extract = new EnvExtract();
        } else {
            extract = new SpecialVariableExtract(variable);
        }
        StreamSendService sendService = new StreamSendService(extract);
        sendService.process(event);
    }
}
