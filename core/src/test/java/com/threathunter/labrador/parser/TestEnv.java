package com.threathunter.labrador.parser;

import com.threathunter.labrador.common.model.Strategy;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.env.StrategyContainer;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

/**
 * 
 */
public class TestEnv {

    @Before
    public void before() throws Exception {
        Env.init();
    }

    @Test
    public void testEnvStrategies() throws Exception {
        Env.init();
        /*StrategyContainer container = Env.getStrategyContainer();
        Collection<Strategy> strategies = container.allStrategies();
        for(Strategy strategy : strategies) {
            System.out.println(strategy.getName());
        }*/
    }
}
