package com.threathunter.labrador.core.message;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 内部消息队列
 * 从队列的视脚处理任务
 * 队列: addQueue的方法进行队列新增
 * 生产者：通过addMessages，增加待处理的任务
 * 消费者：InternalDispatchQueueProcessThread类的popMessage
 * 生产者1个线程，消费者多个线程。
 * 1. 消费者和生产者是通过wait/notify机制进行通信
 * 2. 消费者和生产者通过lock进行读写同步
 * <p>
 * 功能：
 * 1. 从调用类的角度看，创建内部队列，提供内部队列的处理器(IInternalMessageHandler)
 * 2. 生产/消费者的抽象模型,通过阻塞平衡生产/消费者。
 * 3. 对标flume，性能得到提升。
 * 4. 通过参数化配置，有效控制QPS（如果需要控制)
 */
@Slf4j
public class InternalMessageQueueService {

    //队列的Dispatch类，启动处理线程，增加任务
    private Map<String, InternalDispatchQueueWrapper> threadMap = new ConcurrentHashMap<>();

    private static AtomicLong count = new AtomicLong();

    /**
     * 1.是否使用线程池。
     * 2.控制睡眠时间是否更精确
     */

    public void setSimpleMode(boolean simpleMode) {
        this.simpleMode = simpleMode;
    }

    private static boolean simpleMode = true;

    /**
     * 增加队列，可以包含多个任务队列
     *
     * @param messageConfig  队列的相关配置信息
     * @param messageHandler 每个队列对应的handler处理实现
     */
    public void addQueue(MessageConfig messageConfig, IInternalMessageHandler messageHandler) {
        InternalDispatchQueueWrapper internalDispatchQueueWrapper;
        if (simpleMode) {
            internalDispatchQueueWrapper = new SimpleInternalDispatchQueueWrapper(messageConfig, messageHandler);
        } else {
            internalDispatchQueueWrapper = new InternalDispatchThreadPoolQueueWrapper(messageConfig, messageHandler);
        }
        threadMap.put(messageConfig.queueName, internalDispatchQueueWrapper);
    }


    /**
     * 增加任务待处理，这个方法是客户端阻塞的，
     * 当队列满的时候，直到消费完一部分，才会notify
     *
     * @param queueName
     * @param messages
     */
    public void addMessages(String queueName, List messages) {
        InternalDispatchQueueWrapper internalDispatchQueueWrapper = threadMap
                .get(queueName);
        internalDispatchQueueWrapper.addMessages(messages);
    }

    //启动所有队列任务，准备消费
    public void start() {
        log.warn("start begin...");
        for (InternalDispatchQueueWrapper internalDispatchQueueWrapper : threadMap
                .values()) {
            internalDispatchQueueWrapper.start();
        }
        log.warn("start end...");
    }

    //停止所有任务

    /**
     * TODO:待完善任务的退出方法
     * 当前停止时，直接退出，
     * 更严谨些的作法是执行完所有的当前任务，方可停止。
     * 应该在停止时，禁止提交新的任务。
     */
    public void stop() {
        for (InternalDispatchQueueWrapper internalDispatchQueueWrapper : threadMap
                .values()) {
            internalDispatchQueueWrapper.destory();
        }
    }

    private static interface InternalDispatchQueueWrapper {

        //启动队列，准备消费
        public void start();

        //增加任务，调用此方法时，应该通过队列名找到了对应的InternalDispatchQueueWrapper实现
        public void addMessages(List messages);

        //停止
        public void destory();
    }

    @Slf4j
    private static class SimpleInternalDispatchQueueWrapper implements InternalDispatchQueueWrapper {

        //队列名
        private String queueName;

        //线程数
        private int threadNum;


        private List<InternalDispatchQueueProcessThread> internalDispatchQueueProcessThreads;

        //构建线程数据
        public SimpleInternalDispatchQueueWrapper(MessageConfig messageConfig,
                                                  IInternalMessageHandler messageHandler) {
            this.queueName = messageConfig.queueName;
            this.threadNum = messageConfig.threadNum;
            log.warn("queueName : " + queueName + " | threadNum : " + this.threadNum);
            internalDispatchQueueProcessThreads = new ArrayList<>();

            for (int i = 0; i < this.threadNum; i++) {
                InternalDispatchQueueProcessThread internalDispatchQueueProcessThread = new InternalDispatchQueueProcessThread(messageConfig,
                        queueName, messageHandler, i);
                internalDispatchQueueProcessThreads
                        .add(internalDispatchQueueProcessThread);
            }

        }

        //启动线程数据，等待消费
        public void start() {
            for (InternalDispatchQueueProcessThread thread : internalDispatchQueueProcessThreads) {
                thread.start();
            }
        }

        //增加任务列表
        public void addMessages(List messages) {
            Random random = new Random();
            int index = random.nextInt(threadNum);
            InternalDispatchQueueProcessThread internalDispatchQueueProcessThread = internalDispatchQueueProcessThreads
                    .get(index);
            internalDispatchQueueProcessThread.addMessage(messages);
        }

        //停止所有的任务
        public void destory() {
            for (InternalDispatchQueueProcessThread thread : internalDispatchQueueProcessThreads) {
                thread.destory();
            }
        }
    }

    //线程调度处理，每个队列对应一个实例
    @Slf4j
    private static class InternalDispatchQueueProcessThread extends Thread {

        private String queueName;

        //无界队列
        private Queue queue;

        //线程id
        private int index;

        //每次pop最多个数
        private int fetchSize = 100;

        //队列长度，超出长度时，新增任务堵塞
        private int maxSize = 1000;

        //两次pop的时间间隔,为0表示不间隔
        private long popInterval = 0;

        //上次pop的时间
        private long lastPopTime = 0L;

        private boolean workTag;

        //任务处理类
        private IInternalMessageHandler messageHandler;

        private ReentrantLock lock = new ReentrantLock();

        private Condition emptyCondition = lock.newCondition();

        private Condition fullCondition = lock.newCondition();

        //任务处理线程
        private ThreadPoolExecutor threadPool;


        public void setThreadPool(ThreadPoolExecutor threadPool) {
            this.threadPool = threadPool;
        }

        public InternalDispatchQueueProcessThread(MessageConfig messageConfig, String queueName,
                                                  IInternalMessageHandler messageHandler, int index) {
            this.queueName = queueName;
            queue = new ConcurrentLinkedDeque();
            this.messageHandler = messageHandler;
            this.index = index;

            //如果config中没有设置，均为默认，通常config只需要设置线程数
            if (messageConfig != null) {
                this.fetchSize = messageConfig.fetchSize != null ? messageConfig.fetchSize : this.fetchSize;
                this.maxSize = messageConfig.maxSize != null ? messageConfig.maxSize : this.maxSize;
                this.popInterval = messageConfig.popInterval != null ? messageConfig.popInterval : this.popInterval;
            }

        }

        //消费线程通知主线程，可以继续接受新的任务
        public void finishNotify() {
            lock.lock();
            try {
                fullCondition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void run() {

            workTag = true;
            log.debug("queueName : " + queueName + " | " + index + " start ...");
            while (workTag) {

                List messages = null;
                long c = 0L;

                lock.lock();
                try {

                    //开始消费
                    messages = popMessage();

                    fullCondition.signalAll();

                    if (CollectionUtils.isEmpty(messages)) {
                        emptyCondition.await();
                    }

                    if (log.isDebugEnabled()) {

                        c = count.addAndGet(messages.size());

                        log.debug("queueName : " + queueName + " | index :  "
                                + index + " | pop size : " + messages.size()
                                + " | count :  " + c + " | queue size :"
                                + queue.size());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
                //处理任务
                if (CollectionUtils.isNotEmpty(messages)) {
                    try {
                        log.debug("queueName : " + queueName + " | index :  "
                                + index + " pre handle message");
                        messageHandler.handleMassage(messages);
                        log.debug("queueName : " + queueName + " | index :  "
                                + "finish handle");
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
                if (simpleMode) {
                    messages.clear();
                }
            }
        }

        //控制QPS
        private void maybeSleep() {
            if (popInterval != 0L) {
                long nowTime = System.currentTimeMillis();
                long interval = popInterval;
                if (lastPopTime != 0) {
                    interval = popInterval - (nowTime - lastPopTime);
                }

                if (interval > 0) {
                    try {
                        sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                lastPopTime = System.currentTimeMillis();
            }
        }

        //增加任务
        public void addMessage(List messages) {

            if (CollectionUtils.isEmpty(messages)) {
                return;
            }

            lock.lock();
            try {
                //超出最大限定，或者每个线程待处理任务超过100个，暂不允许增加任务
                while (queue.size() >= maxSize || (threadPool != null && threadPool.getQueue().size() >= threadPool.getMaximumPoolSize() * 100)) {
                    fullCondition.await();
                }
                queue.addAll(messages);
                emptyCondition.signalAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public List popMessage() {
            List results = new LinkedList<>();
            try {
                int count = 0;

                long startTs = System.currentTimeMillis();
                while (true) {
                    count++;
                    Object obj = queue.poll();
                    if (null != obj) {
                        results.add(obj);
                    }
                    if (count >= fetchSize) {
                        break;
                    }

                    if (System.currentTimeMillis() - startTs > 1000) {
                        if (results.size() > 0) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            maybeSleep();
            return results;
        }

        public void destory() {
            workTag = false;
        }

    }


    //Dispatch包装类，包装消费线程，处理线程的线程池等
    private static class InternalDispatchThreadPoolQueueWrapper implements InternalDispatchQueueWrapper {

        private String queueName;

        private int threadNum;

        private ThreadPoolExecutor threadPool = null;

        private InternalDispatchQueueProcessThread internalDispatchQueueProcessThread;

        private InternalDispatchThreadPoolQueueWrapperMessageHandler internalDispatchThreadPoolQueueWrapperMessageHandler;


        public InternalDispatchThreadPoolQueueWrapper(MessageConfig messageConfig,
                                                      IInternalMessageHandler messageHandler) {
            this.queueName = messageConfig.queueName;
            this.threadNum = messageConfig.threadNum;
            threadPool = new ThreadPoolExecutor(1, threadNum, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

            internalDispatchThreadPoolQueueWrapperMessageHandler = new InternalDispatchThreadPoolQueueWrapperMessageHandler(threadPool, messageHandler);

            internalDispatchQueueProcessThread = new InternalDispatchQueueProcessThread(messageConfig, queueName, internalDispatchThreadPoolQueueWrapperMessageHandler, threadNum);
            internalDispatchQueueProcessThread.setThreadPool(threadPool);

            internalDispatchThreadPoolQueueWrapperMessageHandler.setInternalDispatchQueueProcessThread(internalDispatchQueueProcessThread);

        }

        public void start() {
            internalDispatchQueueProcessThread.start();
        }

        public void addMessages(List messages) {
            internalDispatchQueueProcessThread.addMessage(messages);
        }

        public void destory() {
            internalDispatchQueueProcessThread.destory();
            threadPool.shutdown();
        }
    }

    //任务处理线程包装器
    private static class InternalDispatchThreadPoolQueueWrapperMessageHandler implements IInternalMessageHandler {

        private ThreadPoolExecutor threadPool;

        private IInternalMessageHandler handler;

        private InternalDispatchQueueProcessThread internalDispatchQueueProcessThread;

        private InternalDispatchThreadPoolQueueWrapperMessageHandler(ThreadPoolExecutor threadPool, IInternalMessageHandler handler) {
            this.threadPool = threadPool;
            this.handler = handler;
        }

        public void setInternalDispatchQueueProcessThread(InternalDispatchQueueProcessThread internalDispatchQueueProcessThread) {
            this.internalDispatchQueueProcessThread = internalDispatchQueueProcessThread;
        }

        @Override
        public void handleMassage(List messages) {
            MessageHandlerRunnable runnable = new MessageHandlerRunnable(messages, handler, internalDispatchQueueProcessThread);
            threadPool.execute(runnable);
        }

    }

    //任务处理线程
    @Slf4j
    private static class MessageHandlerRunnable implements Runnable {

        private List message;

        IInternalMessageHandler messageHandler;

        private InternalDispatchQueueProcessThread internalDispatchQueueProcessThread;

        private MessageHandlerRunnable(List message, IInternalMessageHandler messageHandler, InternalDispatchQueueProcessThread internalDispatchQueueProcessThread) {
            this.message = message;
            this.messageHandler = messageHandler;
            this.internalDispatchQueueProcessThread = internalDispatchQueueProcessThread;
        }

        @Override
        public void run() {
            try {
                messageHandler.handleMassage(message);
                internalDispatchQueueProcessThread.finishNotify();
                message.clear();
                message = null;
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


}
