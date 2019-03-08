package com.threathunter.labrador.common.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 16/10/27.
 */
public class Term {

    public enum TermSide {LeftTerm, RigthTerm}

    public enum TermType {Event, Constant, FUNC, Empty}

    public enum SubType {GetVariable, SetBlackList, Spl}

    public enum Dimension {Uid, Ip, Others}

    private TermSide termSide;

    private TermType termType;

    private SubType subType;

    private Dimension dimension;

    private Map<String, Object> config;

    private String field;

    //left情况下，标记来源event
    private List<String> sourceEvents;

    private String value;

    private List<String> events;

    private List<String> variables;

    private String triggerKey = "";

    private String expression;

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public TermSide getTermSide() {
        return termSide;
    }

    public void setTermSide(TermSide termSide) {
        this.termSide = termSide;
    }

    public TermType getTermType() {
        return termType;
    }

    public void setTermType(TermType termType) {
        this.termType = termType;
    }

    public SubType getSubType() {
        return subType;
    }

    public void setSubType(SubType subType) {
        this.subType = subType;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<String> getSourceEvents() {
        return sourceEvents;
    }

    public void setSourceEvents(List<String> sourceEvents) {
        this.sourceEvents = sourceEvents;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    public String getTriggerKey() {
        return triggerKey;
    }

    public void setTriggerKey(String triggerKey) {
        this.triggerKey = triggerKey;
    }


    public static String getStringJson(String key, Map<String,Object> item) {
        if(item.containsKey(key)) {
            String value = String.valueOf(item.get(key)).trim();
            return value;
        } else {
            return "";
        }
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public static Term parseTerm(String side, Object jsonTerm) {
        Term term = new Term();
        if (side.equals("right")) {
            term.termSide = TermSide.RigthTerm;
        } else {
            term.termSide = TermSide.LeftTerm;
        }
        Map<String, Object> kv = (Map) jsonTerm;
        Map<String, Object> config = (Map) kv.get("config");
        String subType = getStringJson("subtype", kv);
        term.config = config;
        if (subType.equals("getvariable")) {
            term.subType = SubType.GetVariable;
        } else if (subType.equals("setblacklist")) {
            term.subType = SubType.SetBlackList;
        } else if (subType.equals("spl")) {
            term.subType = SubType.Spl;
            term.setExpression(config.get("expression").toString());
            return term;
        }
        if (config.containsKey("field")) {
            term.field = getStringJson("field", config);
        }

        if (config.containsKey("value")) {
            term.value = getStringJson("value", config);
        }

        if (config.containsKey("event")) {
            List<Object> configEvents = (List) config.get("event");
            List<String> events = new ArrayList<>();
            for (Object _event : configEvents) {
                if (((String) _event).trim().equals("nebula")) {
                    continue;
                }
                if (!events.contains(((String) _event).trim())) {
                    events.add(((String) _event).trim());
                }
            }
            if (term.events == null) {
                term.events = new ArrayList<>();
            }
            term.events.addAll(events);
        }

        if (config.containsKey("variable")) {
            List<Object> configVariables = (List) config.get("variable");
            List<String> variables = new ArrayList<>();
            for(Object configVariable : configVariables) {
                if (((String) configVariable).trim().equals("nebula")) {
                    continue;
                }

                if(configVariables.contains(((String) configVariable).trim())) {
                    variables.add(((String) configVariable).trim());
                }
            }
            if(term.variables == null) {
                term.variables = new ArrayList<>();
            }
            term.variables.addAll(variables);
        }

        if (config.containsKey("trigger")) {
            Map<String, Object> triggers = (Map) config.get("trigger");

            List<Object> keys = (List) triggers.get("keys");
            if (keys.size() != 0) {
                String _key = ((String) keys.get(0)).trim();
                if (_key.equals("uid")) {
                    term.dimension = Dimension.Uid;
                } else if (_key.equals("ip")) {
                    term.dimension = Dimension.Ip;
                } else if(_key.contains("USER(")) {
                    term.dimension = Dimension.Others;
                    String triggerKey = StringUtils.substringBetween(_key, "USER(", ")");
                    term.triggerKey = triggerKey;
                }
            }

            List<Object> configEvents = (List) triggers.get("event");
            List<String> events = new ArrayList<>();
            for (Object _event : configEvents) {
                if (((String) _event).trim().equals("nebula")) {
                    continue;
                }
                if (!events.contains(((String) _event).trim())) {
                    events.add(((String) _event).trim());
                }
            }
            if (term.events == null) {
                term.events = new ArrayList<>();
            }
            term.events.addAll(events);
        }

        String jsonType = ((String) kv.get("type")).trim();
        if (jsonType.equals("func")) {
            term.termType = TermType.FUNC;
        } else if (jsonType.equals("event")) {
            term.termType = TermType.Event;
        } else if (jsonType.equals("constant")) {
            term.termType = TermType.Constant;
        }
        return term;
    }


}
