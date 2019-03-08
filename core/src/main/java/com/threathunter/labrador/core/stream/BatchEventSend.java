package com.threathunter.labrador.core.stream;

import com.threathunter.babel.meta.ServiceMeta;
import com.threathunter.babel.meta.ServiceMetaUtil;
import com.threathunter.babel.rpc.Service;
import com.threathunter.babel.rpc.ServiceContainer;
import com.threathunter.babel.rpc.impl.ServerContainerImpl;
import com.threathunter.model.Event;
import com.threathunter.model.EventMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wanbaowang on 17/8/25.
 */
public class BatchEventSend {
    private static final Logger logger = LoggerFactory.getLogger(BatchEventSend.class);

    private ServiceContainer server;

    private boolean redisMode;

    public BatchEventSend(boolean redisMode) {
        this.redisMode = redisMode;
        server = new ServerContainerImpl();
    }

    public void start() {
        String configName = "batch_event_send.service";
        if (this.redisMode) {
            configName = "batch_event_send_redis.service";
        }
        final ServiceMeta meta = ServiceMetaUtil.getMetaFromResourceFile(configName);
        Service stream = new Service() {

            @Override
            public Event process(Event event) {
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
        server.addService(stream);
        server.start();
        logger.warn("start:BatchEventSend");
    }

    public void stop() {
        logger.warn("stop:BatchEventSend");
        server.stop();
    }
}
