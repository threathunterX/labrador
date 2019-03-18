package com.threathunter.labrador.application.babel;

import com.threathunter.babel.meta.ServiceMeta;
import com.threathunter.babel.meta.ServiceMetaUtil;
import com.threathunter.babel.rpc.Service;
import com.threathunter.babel.rpc.ServiceContainer;
import com.threathunter.babel.rpc.impl.ServerContainerImpl;
import com.threathunter.labrador.common.util.Constant;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.BatchSendService;
import com.threathunter.model.Event;
import com.threathunter.model.EventMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class BabelBatchEventSendService implements BabelService {

    private boolean redisMode;
    private ServiceContainer container;
    private BatchSendService batchSendService = new BatchSendService();
    private static Logger logger = LoggerFactory.getLogger(BabelBatchEventSendService.class);

    public BabelBatchEventSendService(boolean redisMode) {
        this.redisMode = redisMode;
        this.container = new ServerContainerImpl();
    }

    @Override
    public void start() {
        String configName = "babel/batch_event_send.service";
        if (redisMode) {
            configName = "babel/batch_event_send_redis.service";
        }

        final ServiceMeta meta = ServiceMetaUtil.getMetaFromResourceFile(configName);
        Service log = new Service() {
            @Override
            public Event process(Event event) {
                try {
                    Env.getMetricsHelper().addMetrics(Constant.METRICS_NAME_BATCH, 1D);
                    logger.warn("process Batch event {}", event.toString());
                    batchSendService.process(event);
                } catch (LabradorException e) {
                    e.printStackTrace();
                }
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
        logger.info("start:the profile event send");
    }

    @Override
    public void stop() {

    }
}
