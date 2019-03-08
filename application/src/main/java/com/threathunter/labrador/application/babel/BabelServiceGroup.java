package com.threathunter.labrador.application.babel;

import com.threathunter.labrador.common.util.ConfigUtil;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.exception.LabradorException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanbaowang on 17/11/2.
 */
public class BabelServiceGroup {

    private List<BabelService> services;
    private boolean redisModel = false;
    public BabelServiceGroup() throws LabradorException {
        String nebulaBabel = ConfigUtil.getString("babel_server").trim();
        try {
            EnumUtil.BabelServer babelServer = EnumUtil.BabelServer.valueOf(nebulaBabel);
            services = new ArrayList<>();
            if(babelServer == EnumUtil.BabelServer.redis) {
                redisModel = true;
            }
            this.services.add(new BabelBatchEventSendService(redisModel));
            this.services.add(new BabelStrategyService(redisModel));
            this.services.add(new BabelStreamEventSendService(redisModel));
        } catch (java.lang.IllegalArgumentException e) {
            throw new LabradorException("unknow babel model " + nebulaBabel);
        }
    }

    public void start() {
        NoticeSender.getInstance().start(this.redisModel);
        for(BabelService service : services) {
            service.start();
        }
    }

    public void stop() {
        for(BabelService service : services) {
            service.stop();
        }
    }

    public static void main(String[] args) {
        EnumUtil.BabelServer babelServer = EnumUtil.BabelServer.valueOf("abc");
        System.out.println(babelServer.name());
    }

}
