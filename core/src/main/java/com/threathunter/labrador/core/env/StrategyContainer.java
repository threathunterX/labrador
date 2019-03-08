package com.threathunter.labrador.core.env;

import com.threathunter.labrador.common.model.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by wanbaowang on 17/11/6.
 */
public class StrategyContainer {
    private static final Logger logger = LoggerFactory.getLogger(StrategyContainer.class);
//    private Map<String, List<Strategy>> sceneStrategies = new HashMap();
    private Map<String, Strategy> strategyMap = new HashMap<>();


/*    public List<Strategy> loadStrategyByScene(String scene) {
        return sceneStrategies.getOrDefault(scene, new ArrayList<>());
    }*/


    public void putStrategy(Strategy strategy) {
        /*List<String> scenes = strategy.getEvents();
        for (String scene : scenes) {
            List<Strategy> strategies = sceneStrategies.get(scene);
            if (null == strategies) {
                strategies = new ArrayList<>();
                sceneStrategies.remove(scene);
                sceneStrategies.put(scene, strategies);
            }
            strategies.add(strategy);
        }*/
        strategyMap.put(strategy.getName(), strategy);
    }

    public Collection<Strategy> allStrategies() {
        return strategyMap.values();
    }

/*    public List<Strategy> loadStrategiesByScene(String scene) {
        return sceneStrategies.get(scene);
    }*/

    public Strategy loadStrategyByName(String name) {
        return strategyMap.get(name);
    }

}
