package com.threathunter.labrador.application.rpc.service;

import com.threathunter.labrador.spl.check.CheckResponse;

/**
 * Created by wanbaowang on 17/10/30.
 */
public interface SplService {

    public CheckResponse check(String expression);

}
