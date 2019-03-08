package com.threathunter.labrador.core.message;

import java.util.List;

//任务处理handler，由客户端传入
public interface IInternalMessageHandler {

    public void handleMassage(List messages);

}
