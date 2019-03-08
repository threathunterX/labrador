package com.threathunter.labrador.core.dao;

import com.threathunter.labrador.core.transform.Group;

/**
 * Created by wanbaowang on 17/8/30.
 */
public class WriteAerospikeThread implements Runnable {

    private AbstractSendEval sendEval;
    private Group group;

    public WriteAerospikeThread(AbstractSendEval sendEval, Group group) {
        this.sendEval = sendEval;
        this.group = group;
    }

    @Override
    public void run() {
        try {
            sendEval.doEval(group);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
