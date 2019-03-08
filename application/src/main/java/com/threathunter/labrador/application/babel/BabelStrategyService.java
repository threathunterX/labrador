package com.threathunter.labrador.application.babel;

import com.threathunter.babel.meta.ServiceMeta;
import com.threathunter.babel.meta.ServiceMetaUtil;
import com.threathunter.babel.rpc.Service;
import com.threathunter.babel.rpc.ServiceContainer;
import com.threathunter.babel.rpc.impl.ServerContainerImpl;
import com.threathunter.labrador.application.spl.SplExecutor;
import com.threathunter.labrador.application.strategy.StrategyProcessThread;
import com.threathunter.labrador.common.util.Constant;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.service.QueryService;
import com.threathunter.model.Event;
import com.threathunter.model.EventMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanbaowang on 17/11/28.
 */
public class BabelStrategyService implements BabelService {

    private static Logger logger = LoggerFactory.getLogger(BabelStrategyService.class);

    private boolean redisMode;
    private ServiceContainer container;
    private ThreadPoolExecutor pool;

    private class SplReject implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            SplExecutor splExecutor = (SplExecutor) r;
            logger.warn("reject expression:" + splExecutor.getExpression() + " with event : " + splExecutor.getEvent().toString());
        }
    }

    private QueryService queryService;

    public BabelStrategyService(boolean redisMode) {
        this.redisMode = redisMode;
        this.container = new ServerContainerImpl();
        pool = new ThreadPoolExecutor(5,//核心数量
                10,//最大数据
                60,//时间
                TimeUnit.SECONDS,//单位
                new ArrayBlockingQueue<Runnable>(20),//队列
                new SplReject());
        queryService = new QueryService();
    }

    @Override
    public void start() {
        String configName = "babel/profilenoticechecker_mq.service";
        if(redisMode) {
            configName = "babel/profilenoticechecker_redis.service";
        }

        final ServiceMeta meta = ServiceMetaUtil.getMetaFromResourceFile(configName);

        Service log = new Service() {
            @Override
            public Event process(Event event) {
                logger.warn("Process Strategy event {}", event.toString());
                Env.getMetricsHelper().addMetrics(Constant.METRICS_NAME_STRATEGY, 1D);
                pool.execute(new StrategyProcessThread(event, queryService));
                return null;
            }

            @Override
            public ServiceMeta getServiceMeta() {
                return meta;
            }

            @Override
            public EventMeta getRequestEventMeta() {
                return null;
            }

            @Override
            public EventMeta getResponseEventMeta() {
                return null;
            }

            @Override
            public void close() {

            }
        };
        this.container.addService(log);
        this.container.start();
        logger.info("Babel start strategy service");
    }

    @Override
    public void stop() {

    }
}
