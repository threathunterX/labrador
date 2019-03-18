package com.threathunter.labrador.core.timer;

import com.threathunter.labrador.common.exception.BuilderException;
import com.threathunter.labrador.core.builder.update.BaseUpdate;
import com.threathunter.labrador.core.builder.update.StrategyUpdate;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.exception.UpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.TimerTask;

/**
 * 
 */
public class StrategyTimeTask extends TimerTask {

    private static Logger logger = LoggerFactory.getLogger(StrategyTimeTask.class);

    @Override
    public void run() {
        BaseUpdate baseUpdate = new StrategyUpdate();
        try {
            baseUpdate.update();
        }  catch (Exception e) {
            logger.error("StrategyTimeTask Timer update error, ", e);
        }
    }
}
