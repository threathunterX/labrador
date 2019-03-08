package com.threathunter.labrador.application.bloodhound;

import com.threathunter.labrador.core.message.InternalMessageQueueService;

public interface Consumer {

    public void startConsume(String queueName);

    public void stopConsume();

    public void setMessageQueueService(InternalMessageQueueService messageQueueService);

}
