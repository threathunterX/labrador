package com.threathunter.labrador.core.env;

import com.threathunter.labrador.common.util.ConfigUtil;
import com.threathunter.labrador.common.util.Constant;
import com.threathunter.labrador.common.util.MetricsHelper;
import com.threathunter.labrador.core.builder.derivative.ReadDefineRegistry;
import com.threathunter.labrador.core.builder.idgenerator.FileVariableIdGenerator;
import com.threathunter.labrador.core.builder.idgenerator.VariableIdGenerator;
import com.threathunter.labrador.core.builder.update.EventFieldUpdate;
import com.threathunter.labrador.core.builder.update.StrategyUpdate;
import com.threathunter.labrador.core.builder.update.VariableUpdate;
import com.threathunter.labrador.core.condition.ConditionManager;
import com.threathunter.labrador.core.dao.AerospikeDao;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.transform.EventTransform;
import com.threathunter.labrador.core.transform.GroupingRegistry;
import com.threathunter.metrics.MetricsAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 */
public class Env {

    private static final Logger logger = LoggerFactory.getLogger(Env.class);

    private VariableContainer variables;

    private EventFieldContainer eventFields;

    private EventTransform transform;

    private GroupingRegistry groupingRegistry;

    private AerospikeDao aerospikeDao;

    private ThreadPoolExecutor writeExecutor;

    private VariableUpdate variableUpdate;

    private StrategyContainer strategyContainer;

    private ConditionManager conditionManager;

    private ReadDefineRegistry readDefineRegistry;

    private MetricsHelper metricsHelper;


    private static class EnvHolder {
        private static Env env;

        static {
            try {
                env = new Env();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static Env getEnv() {
            return env;
        }
    }

    private Env() throws Exception {
        eventFields = new EventFieldContainer();


        variables = new VariableContainer();

        strategyContainer = new StrategyContainer();
        try {
            transform = new EventTransform();
        } catch (IntrospectionException e) {
            e.printStackTrace();
            throw new LabradorException("Init EventTransform error, " + e.getMessage());
        }
        groupingRegistry = new GroupingRegistry();
        readDefineRegistry = new ReadDefineRegistry();
        aerospikeDao = new AerospikeDao();
        writeExecutor = new ThreadPoolExecutor(ConfigUtil.getInt(Constant.WRITE_POOL_CORE_SIZE), ConfigUtil.getInt(Constant.WRITE_POOL_MAX_SIZE),
                ConfigUtil.getInt(Constant.WRITE_POOL_KEEP_ALIVE_MINUTE_TIME),
                TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(ConfigUtil.getInt(Constant.WRITE_POOL_QUEUE_SIZE)), new ThreadPoolExecutor.CallerRunsPolicy());
        conditionManager = new ConditionManager();
        metricsHelper = MetricsHelper.getInstance();
    }

    public static void setVariableUpdate(VariableUpdate variableUpdate) {
        EnvHolder.env.variableUpdate = variableUpdate;
    }

    public static VariableUpdate getVariableUpdate() {
        return EnvHolder.env.variableUpdate;
    }

    public static EventFieldContainer getEventFieldContainer() {
        return EnvHolder.env.eventFields;
    }

    public static AerospikeDao getAerospikeDao() {
        return EnvHolder.getEnv().aerospikeDao;
    }

    public static VariableContainer getVariables() {
        return EnvHolder.getEnv().variables;
    }


    public static EventTransform getTransForm() {
        return EnvHolder.env.transform;
    }

    public static GroupingRegistry getGroupRegistry() {
        return EnvHolder.env.groupingRegistry;
    }

    public static ThreadPoolExecutor getWriteExecutor() {
        return EnvHolder.env.writeExecutor;
    }

    public static StrategyContainer getStrategyContainer() {
        return EnvHolder.env.strategyContainer;
    }

    public static ConditionManager getConditionManager() {
        return EnvHolder.env.conditionManager;
    }

    public static ReadDefineRegistry getReadDefineRegistry() {
        return EnvHolder.env.readDefineRegistry;
    }

    public static MetricsHelper getMetricsHelper() {
        return EnvHolder.env.metricsHelper;
    }

    public static void init() throws Exception {
        logger.warn("start init field");
        EventFieldUpdate fieldUpdate = new EventFieldUpdate();
        fieldUpdate.update();
        logger.warn("finish init field");

        logger.warn("start init strategy");
        StrategyUpdate strategyUpdate = new StrategyUpdate();
        strategyUpdate.update();
        logger.warn("finish init strategy");

        String idGenerator = ConfigUtil.getString("nebula.labrador.idgenerator.type");
        if (idGenerator.equals("file")) {
            logger.warn("start init variable");
            String path = ConfigUtil.getString("nebula.labrador.idgenerator.path");
            VariableIdGenerator generator = new FileVariableIdGenerator(path);
            VariableUpdate variableUpdate = new VariableUpdate(generator);
            variableUpdate.update();
            setVariableUpdate(variableUpdate);
            logger.warn("finish init variable");
        } else {
            throw new LabradorException("IdGenerator " + idGenerator + " not implement yet");
        }
        //logger.warn("init metrics agent");
        //MetricsAgent.getInstance().start();
        //logger.warn("finish metrics agent");

    }

}
