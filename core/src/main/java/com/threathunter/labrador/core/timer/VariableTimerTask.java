package com.threathunter.labrador.core.timer;

import com.threathunter.labrador.core.builder.update.VariableUpdate;
import com.threathunter.labrador.core.env.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * 
 */
public class VariableTimerTask extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(VariableTimerTask.class);

    @Override
    public void run() {
        logger.warn("update variable");
        VariableUpdate variableUpdate = Env.getVariableUpdate();
        try {
            variableUpdate.update();
        } catch (Exception e) {
            logger.error("VariableTimerTask update error, ", e);
        }
    }
}
