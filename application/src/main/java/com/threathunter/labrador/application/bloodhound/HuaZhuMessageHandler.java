package com.threathunter.labrador.application.bloodhound;

import com.threathunter.labrador.core.message.IInternalMessageHandler;

import java.util.List;

public class HuaZhuMessageHandler implements IInternalMessageHandler {
    @Override
    public void handleMassage(List messages) {
        System.out.println(messages.size());
        System.out.println();
        System.out.println("start process----");
        for(Object message : messages) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(message);
        }

        System.out.println("end process------");
    }
}
