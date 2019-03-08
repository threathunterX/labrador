package com.threathunter.labrador.core.message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageConfig {
    //一次性最大获取长度
    public Integer fetchSize;

    //队列最大长度
    public Integer maxSize;

    //pop时间间隔
    public Long popInterval;

    //处理任务线程个数
    public Integer threadNum;

    //线程名称
    public String queueName;

}
