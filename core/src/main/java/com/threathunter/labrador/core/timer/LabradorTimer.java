package com.threathunter.labrador.core.timer;

import java.util.Timer;

/**
 * 
 */
public class LabradorTimer extends Thread {

    public void run() {
        Timer timer = new Timer();
        timer.schedule(new VariableTimerTask(), 60 * 1000, 2 * 1000 * 60);
        timer.schedule(new EventFieldTimerTask(), 60 * 1000, 5 * 1000 * 60);
        timer.schedule(new StrategyTimeTask(), 60 * 1000, 6 * 1000 * 60);
    }

}
