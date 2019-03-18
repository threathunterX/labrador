package com.threathunter.labrador.application;

import com.threathunter.labrador.application.babel.BabelServiceGroup;
import com.threathunter.labrador.application.rpc.RpcBootstrap;
import com.threathunter.labrador.common.util.SpringUtils;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.timer.LabradorTimer;
import com.threathunter.labrador.rpc.constant.RpcConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 */
public class Labrador {

    private static final Logger logger = LoggerFactory.getLogger(Labrador.class);

    private BabelServiceGroup babelServiceGroup;

    public void run() throws Exception {
        initEnv();
        logger.warn("Start BabelServiceGroup");
        startBabelServiceGroup();
        logger.warn("Start Time");
        startTimer();
        logger.warn("Start Rpc");
        startRpc();
    }

    private void startTimer() {
        new LabradorTimer().start();
    }

    private void initEnv() throws Exception {
        Env.init();
    }

    private void startBabelServiceGroup() throws LabradorException {
        this.babelServiceGroup = new BabelServiceGroup();
        this.babelServiceGroup.start();
    }

    private void startRpc() {
        RpcBootstrap rpcBootstrap = new RpcBootstrap();
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(RpcConstant.RPC_SERVER_PATH);
        SpringUtils.setApplicationContext(applicationContext);
        rpcBootstrap.startRpc();
    }

    public static void main(String[] args) throws Exception {
        try {
            Labrador labrador = new Labrador();
            labrador.run();
        } catch (Exception e) {
            logger.error(" main start failed ", e);
            System.exit(1);
        }
    }
}
