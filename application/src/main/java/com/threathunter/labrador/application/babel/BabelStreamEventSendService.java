package com.threathunter.labrador.application.babel;

import com.threathunter.babel.meta.ServiceMeta;
import com.threathunter.babel.meta.ServiceMetaUtil;
import com.threathunter.babel.rpc.Service;
import com.threathunter.babel.rpc.ServiceContainer;
import com.threathunter.babel.rpc.impl.ServerContainerImpl;
import com.threathunter.labrador.common.util.Constant;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.DataTypeNotMatchException;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.StreamSendService;
import com.threathunter.labrador.core.transform.EnvExtract;
import com.threathunter.model.Event;
import com.threathunter.model.EventMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wanbaowang on 17/11/29.
 */
public class BabelStreamEventSendService implements BabelService {

    private boolean redisModel;
    private ServiceContainer container;
    private Logger logger = LoggerFactory.getLogger(BabelStreamEventSendService.class);
    private StreamSendService streamSendService = new StreamSendService(new EnvExtract());

    public BabelStreamEventSendService(boolean redisModel) {
        this.redisModel = redisModel;
        this.container = new ServerContainerImpl();
    }

    @Override
    public void start() {
        String configName = "babel/stream_event_send_mq.service";
        if(this.redisModel) {
            configName = "babel/stream_event_send_redis.service";
        }

        final ServiceMeta meta = ServiceMetaUtil.getMetaFromResourceFile(configName);
        Service log = new Service() {
            @Override
            public Event process(Event event) {
                try {
                    logger.warn("process event {}", event.toString());
                    Env.getMetricsHelper().addMetrics(Constant.METRICS_NAME_STREAM, 1D);
                    streamSendService.process(event);
                } catch (LabradorException e) {
                    logger.error("process stream event error ", e);
                } catch (DataTypeNotMatchException e) {
                    logger.error("process stream event error ", e);
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
