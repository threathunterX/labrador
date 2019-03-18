package com.threathunter.labrador.application.strategy;

import com.threathunter.geo.GeoUtil;
import com.threathunter.labrador.application.spl.SplUtils;
import com.threathunter.labrador.common.model.Strategy;
import com.threathunter.labrador.common.model.TermMatch;
import com.threathunter.labrador.common.util.Constant;
import com.threathunter.labrador.common.util.EventUtils;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.QueryService;
import com.threathunter.labrador.expression.ExpressionEvaluator;
import com.threathunter.model.Event;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 
 */
public class StrategyService {

    private static Logger logger = LoggerFactory.getLogger(StrategyService.class);
    private Event event;
    private QueryService queryService;

    public StrategyService(Event event, QueryService queryService) {
        this.event = event;
        this.queryService = queryService;
    }

    public List<Event> processEvent() {
        List<Event> results = new ArrayList<>();
        Map<String, Object> metricTags = new HashMap<>();
        logger.warn("process strategy " + event);
        if (event.getPropertyValues().containsKey("strategylist")) {
            List<String> strategyNames = (List) event.getPropertyValues().get("strategylist");
            List<Strategy> profileStrategies = new ArrayList<>();
            Map<String, Set<String>> dimensionVariables = new HashMap<>();
            //加载策略，读取所有策略相关的变量
            startLoad:
            for (String strategyName : strategyNames) {
                Strategy strategy = Env.getStrategyContainer().loadStrategyByName(strategyName);
                if (null == strategy) {
                    logger.error("strategy " + strategyName + " not found, event " + event);
                    continue;
                }

                if (strategy.containsMatchType(TermMatch.MatchType.Profile) || strategy.containsMatchType(TermMatch.MatchType.Spl)) {
                    Set<String> strategyVars = strategy.loadVariables();
                    if (StringUtils.isNotBlank(strategy.getSpl())) {
                        strategyVars.addAll(getStrategyVars(strategy, true));
                    }
                    for (String strategyVar : strategyVars) {
                        try {
                            String dimension = Env.getVariables().getVariableDimension(strategyVar);
                            dimensionVariables.computeIfAbsent(dimension, k -> genHashSet()).add(strategyVar);
                        } catch (LabradorException e) {
                            logger.error("strategy : " + strategyName + " load failed, variable " + strategyVar + " not found");
                            continue startLoad;
                        }
                    }
                    profileStrategies.add(strategy);
                }
            }

            Map<String, Object> variableResult = new HashMap<>();
            logger.warn("query kv " + dimensionVariables);
            if (dimensionVariables.size() > 0) {
                try {
                    variableResult = queryVariables(event, dimensionVariables);
                } catch (LabradorException e) {
                    logger.error("strategy process failed, " + e.getMessage() + " event is " + event);
                    return results;
                }
            }


            loopStrategy:
            for (Strategy strategy : profileStrategies) {
                if (!isValidProfile(strategy, variableResult)) {
                    continue;
                }
                if (!isValidSpl(strategy, variableResult)) {
                    continue;
                }
                metricTags.put("name", strategy.getName());
                results.add(loadEvent(strategy));
            }
        }

        if(metricTags.size() > 0) {
            Env.getMetricsHelper().addMertricsTags(Constant.METRICS_NAME_STRATEGY_TRIGGER, metricTags, 1D);
        }
        return results;
    }

    public boolean isValidProfile(Strategy strategy, Map<String, Object> variableResult) {
        if (!strategy.containsMatchType(TermMatch.MatchType.Profile)) {
            return true;
        }
        List<com.threathunter.labrador.expression.datameta.Variable> expVariables = new ArrayList<>();
        boolean isValid = false;
        String expression = strategy.getExpression();
        Set<String> varNames = strategy.getExpVars();
        for (String varName : varNames) {
            if (!variableResult.containsKey(varName)) {
                logger.error("execute strategy " + strategy.getName() + " failed, miss variable " + varName);
                break;
            }
            Object varValue = variableResult.get(varName);
            expVariables.add(com.threathunter.labrador.expression.datameta.Variable.createVariable(varName, varValue));
        }
        try {
            Object result = ExpressionEvaluator.evaluate(expression, expVariables);
            if (!(result instanceof Boolean)) {
                logger.error("expression: " + expression + " result : " + result + " is not instanceof boolean");
                return false;
            }
            if (((Boolean) result) == true) {
                return true;
            }
        } catch (Exception e) {
            logger.error("execute expression " + expression + " failed, expression is " + expression + " vars " + expVariables);
            e.printStackTrace();
        }
        return false;

    }

    /*
    查找策略spl相关的as变量，或者event field
     */
    private Set<String> getStrategyVars(Strategy strategy, boolean asVar) {
        Set<String> vars = new HashSet<>();
        Map<String, Object> eventDatas = this.event.genAllData();
        Set<String> strategyVars = SplUtils.parseFunctionVars(strategy.getSpl());
        for (String strategyVar : strategyVars) {
            boolean isEventVar = eventDatas.containsKey(strategyVar);
            //请求as变量时，非
            if ((asVar == true) && (isEventVar == false)) {
                //as 变量：queryAsVar = true && isBasic = false
                vars.add(strategyVar);
                continue;
            }
            //请求eventVar时，asVar为false
            if ((asVar == false) && (isEventVar == true)) {
                vars.add(strategyVar);
                continue;
            }
        }
        return vars;
    }

    private boolean isValidSpl(Strategy strategy, Map<String, Object> variableResult) {
        if (!strategy.containsMatchType(TermMatch.MatchType.Spl)) {
            return true;
        }
        String spl = strategy.getSpl();
        Set<String> vars = SplUtils.parseFunctionVars(spl);
        Map<String, com.threathunter.labrador.expression.datameta.Variable> varValues = new HashMap<>();
        Map<String, Object> eventData = event.genAllData();

        boolean allVarsExists = true;
        for (String var : vars) {
            if (eventData.containsKey(var)) {
                varValues.put(var, com.threathunter.labrador.expression.datameta.Variable.createVariable(var, eventData.get(var)));
            } else if (variableResult.containsKey(var)) {
                varValues.put(var, com.threathunter.labrador.expression.datameta.Variable.createVariable(var, variableResult.get(var)));
            } else {
                allVarsExists = false;
            }
        }

        if (allVarsExists == false) {
            return false;
        }
        logger.warn("process spl: {}, values: {}", spl, varValues);
        Object result = ExpressionEvaluator.evaluate(spl, varValues.values());
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        return false;
    }

    private Event loadEvent(Strategy strategy) {
        Event notice = new Event();
        Map<String, Object> property = strategy.getProperty(this.event);
        property.put("triggerValues", this.event);
        notice.setKey(EventUtils.getEventDimensionValue(this.event, strategy.getActionDimension()));
        notice.setTimestamp(System.currentTimeMillis());
        notice.setPropertyValues(property);
        String cip = (String) this.event.getPropertyValues().get("c_ip");
        String province = null;
        String city = null;
        if (cip != null) {
            try {
                int len = cip.split("\\.").length;
                if (len == 3) {
                    cip += ".0";
                }
                province = GeoUtil.getCNIPProvince(cip);
                city = GeoUtil.getCNIPCity(cip);
            } catch (Exception ignore) {
            }
        }
        if (province == null) {
            province = "";
        }
        if (city == null) {
            city = "";
        }
        notice.setApp(this.event.getApp());
        notice.setId(this.event.getId());
        notice.setName(this.event.getName());
        notice.getPropertyValues().put("geo_province", province);
        notice.getPropertyValues().put("geo_city", city);
        return notice;
    }


    private Map<String, Object> queryVariables(Event event, Map<String, Set<String>> dimensions) throws LabradorException {
        Map<String, Object> results = new HashMap<>();
        Set<Map.Entry<String, Set<String>>> entries = dimensions.entrySet();
        for (Map.Entry<String, Set<String>> entry : entries) {
            String dimension = entry.getKey();
            String key = EventUtils.getEventDimensionValue(event, dimension);
            logger.warn("dimension : " + dimension + " key " + key);
            QueryService service = new QueryService();
            results.putAll(service.query(key, new ArrayList<>(entry.getValue())));
        }
        return results;
    }


    private HashSet genHashSet() {
        return new HashSet();
    }


}
