package com.threathunter.labrador.application.bloodhound;

import com.threathunter.labrador.core.message.InternalMessageQueueService;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class HuaZhuConsumer implements Consumer {

    private InternalMessageQueueService messageQueueService;

    private boolean needConsume = true;

    private String queueName;

    @Override
    public void startConsume(String queueName) {
        this.queueName = queueName;
        while(needConsume) {
            messageQueueService.addMessages(queueName, Arrays.asList("a", "b", "c"));
        }
    }

    @Override
    public void stopConsume() {
        log.warn("{} will be stoped consume", this.queueName);
        this.needConsume = false;
    }

    @Override
    public void setMessageQueueService(InternalMessageQueueService messageQueueService) {
        this.messageQueueService = messageQueueService;
    }
}
