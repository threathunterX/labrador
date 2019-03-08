package com.threathunter.labrador.application.babel;

import com.threathunter.babel.meta.ServiceMeta;
import com.threathunter.babel.meta.ServiceMetaUtil;
import com.threathunter.babel.rpc.impl.ServiceClientImpl;
import com.threathunter.common.ShutdownHookManager;
import com.threathunter.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class TestNoticeSender {
    private static class InstanceHolder {
        static TestNoticeSender instance = new TestNoticeSender();
    }

    public static TestNoticeSender getInstance() {
        return TestNoticeSender.InstanceHolder.instance;
    }

    private static final Logger logger = LoggerFactory.getLogger(NoticeSender.class);

    private final BlockingQueue<Event> cache = new LinkedBlockingDeque<>();
    private volatile boolean running = false;
    private final TestNoticeSender.Worker worker;
    private ServiceClientImpl client;
    private ServiceMeta meta;
    private String babelFileName;

    private TestNoticeSender() {
        running = true;
        worker = new TestNoticeSender.Worker();
        worker.start();
        ShutdownHookManager.get().addShutdownHook(() -> stop(), 2);
    }

    public void start(String fileName) {
        meta = ServiceMetaUtil.getMetaFromResourceFile("babel/" + fileName);
        logger.warn("notice message: [name:" + meta.getName() + "],[delivermode:" + meta.getDeliverMode() + "]");
        client = new ServiceClientImpl(meta);
        client.bindService(meta);
        client.start();
    }

    public void stop() {
        if (!running) {
            return;
        }

        running = false;
        try {
            worker.interrupt();
            worker.join();
        } catch (InterruptedException e) {
            logger.error("", e);
            e.printStackTrace();
        } finally {
        }
    }

    public void sendNotice(Event notice) {
        if (notice != null) {
            cache.add(notice);
        }
    }

    public void sendNotices(List<Event> notices) {
        if(null != notices && notices.size() > 0) {
            cache.addAll(notices);
        }
    }

    private class Worker extends Thread {
        public Worker() {
            super("notice sender");
            this.setDaemon(true);

        }

        @Override
        public void run() {
            int idle = 0;
            while(running) {
                List<Event> events = new ArrayList<>();
                cache.drainTo(events);
                if (events.isEmpty()) {
                    idle++;
                    if (idle >= 3) {
                        // sleep after 3 times that no event is coming
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            logger.error("", e);
                            e.printStackTrace();
                        }
                    }
                } else {
                    idle = 0;
                    try {
                        if (events.size() == 1) {
                            logger.warn("notice event......" + meta.getName());
                            logger.warn("event:" + events.get(0).toString());
                            client.notify(events.get(0), meta.getName());
                        } else {
                            logger.warn("notice events......" + meta.getName() + events.size());
                            client.notify(events, meta.getName());
                        }
                    } catch (Exception ex) {
                        logger.error("", ex);
                        logger.error("rpc:fatal:fail to send notice", ex);
                    }
                }
            }
        }
    }

}
