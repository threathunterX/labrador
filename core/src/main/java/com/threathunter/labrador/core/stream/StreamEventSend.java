package com.threathunter.labrador.core.stream;

import com.threathunter.babel.meta.ServiceMeta;
import com.threathunter.babel.meta.ServiceMetaUtil;
import com.threathunter.babel.rpc.Service;
import com.threathunter.babel.rpc.ServiceContainer;
import com.threathunter.babel.rpc.impl.ServerContainerImpl;
import com.threathunter.labrador.core.exception.DataTypeNotMatchException;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.StreamSendService;
import com.threathunter.labrador.core.transform.EnvExtract;
import com.threathunter.model.Event;
import com.threathunter.model.EventMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 实时流写入
 */
public class StreamEventSend {

    private static final Logger logger = LoggerFactory.getLogger(StreamEventSend.class);

    private ServiceContainer server;
    StreamSendService service = new StreamSendService(new EnvExtract());

    private boolean redisMode;

    public StreamEventSend(boolean redisMode) {
        this.redisMode = redisMode;
        server = new ServerContainerImpl();
    }

    public void start() {
        String configName = "stream_event_send.service";
        if (this.redisMode) {
            configName = "stream_event_send_redis.service";
        }
        final ServiceMeta meta = ServiceMetaUtil.getMetaFromResourceFile(configName);
        Service stream = new Service() {

            @Override
            public Event process(Event event) {
                try {
                    service.process(event);
                } catch (DataTypeNotMatchException e) {
                    e.printStackTrace();
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
        server.addService(stream);
        server.start();
        logger.warn("start:StreamEventSend");
    }

    public void stop() {
        logger.warn("stop:StreamEventSend");
        server.stop();
    }

}
