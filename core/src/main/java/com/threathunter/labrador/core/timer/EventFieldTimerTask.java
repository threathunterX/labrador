package com.threathunter.labrador.core.timer;

import com.threathunter.labrador.common.exception.BuilderException;
import com.threathunter.labrador.core.builder.update.BaseUpdate;
import com.threathunter.labrador.core.builder.update.EventFieldUpdate;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.exception.UpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.TimerTask;

/**
 * 
 */
public class EventFieldTimerTask extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(EventFieldTimerTask.class);

    @Override
    public void run() {
        logger.warn("EventFieldTask update");
        BaseUpdate baseUpdate = new EventFieldUpdate();
        try {
            baseUpdate.update();
        } catch (Exception e) {
            logger.error("Event Field Timer Update error, ", e);
        }
    }
}
