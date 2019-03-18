package com.threathunter.labrador.application.rpc.service;

import com.threathunter.labrador.spl.check.CheckResponse;

/**
 * 
 */
public interface SplService {

    public CheckResponse check(String expression);

}
