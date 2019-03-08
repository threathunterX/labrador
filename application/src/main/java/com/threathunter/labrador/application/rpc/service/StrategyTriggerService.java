package com.threathunter.labrador.application.rpc.service;

import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.model.Event;

import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/11/30.
 */
public interface StrategyTriggerService {

    public String strategyTrigger(Map<String,Object> kv) throws LabradorException;

}
