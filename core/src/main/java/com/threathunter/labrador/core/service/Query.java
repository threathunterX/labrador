package com.threathunter.labrador.core.service;

import com.threathunter.labrador.core.exception.LabradorException;

import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/11/15.
 */
public interface Query {

    public Map<String,Object> query(String rowKey, List<String> variables) throws LabradorException ;

}
