package com.threathunter.labrador.application.bloodhound;

import com.threathunter.labrador.core.message.InternalMessageQueueService;
import com.threathunter.labrador.core.message.MessageConfig;

import java.util.concurrent.CountDownLatch;

public class BloodHound {

    public void start() {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        //构造内部消息队列
        InternalMessageQueueService messageQueueService = new InternalMessageQueueService();
        messageQueueService.setSimpleMode(false);

        MessageConfig huazhuConfig = new MessageConfig();
        huazhuConfig.threadNum = 1;
        huazhuConfig.queueName = "huazhu";

        messageQueueService.addQueue(huazhuConfig, new HuaZhuMessageHandler());
        messageQueueService.start();

        Consumer consumer = new HuaZhuConsumer();
        consumer.setMessageQueueService(messageQueueService);
        consumer.startConsume(huazhuConfig.queueName);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BloodHound bloodHound = new BloodHound();
        bloodHound.start();
    }


}
