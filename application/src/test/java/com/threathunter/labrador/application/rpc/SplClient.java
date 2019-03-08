package com.threathunter.labrador.application.rpc;

import com.threathunter.labrador.application.rpc.service.SplService;
import com.threathunter.labrador.rpc.client.RpcReference;
import com.threathunter.labrador.spl.check.CheckResponse;
import org.springframework.stereotype.Component;

/**
 * Created by wanbaowang on 17/11/2.
 */
@Component
public class SplClient {
    @RpcReference
    private SplService splService;

    public void check(String expression) {
        CheckResponse checkResponse = splService.check(expression);
        if(checkResponse.isSuccess()) {
            System.out.println("check successful");
        } else {
            System.out.println("check fail, cause " + checkResponse.getMessage());
        }
    }

}
