package com.threathunter.labrador.common.model;

import com.threathunter.model.Event;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by wanbaowang on 17/11/2.
 */
public class Strategy {
    private static final Logger logger = LoggerFactory.getLogger(Strategy.class);

    private String status;

    private List<TermMatch> termMatches;

    private List<String> tags;

    private String app;

    private long starteffect;

    private long endeffect;

    private long modifytime;

    private int score;

    private String category;

    private String remark;

    private boolean isLock;

    private String name;

    private long version;

    private long createtime;

    private TermMatch eventMatch;

    private String expression;

    private Set<String> expVars;

    public enum StrategyType {Profile, Profile_Rt, Profile_Rt_Spl, Profile_Spl}

    private Set<TermMatch.MatchType> matchTypes = new HashSet<>();

    private String spl;

    public List<String> getStrategyEvents() {
        return null;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TermMatch> getTermMatches() {
        return termMatches;
    }

    public void setTermMatches(List<TermMatch> termMatches) {
        this.termMatches = termMatches;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public long getStarteffect() {
        return starteffect;
    }

    public void setStarteffect(long starteffect) {
        this.starteffect = starteffect;
    }

    public long getEndeffect() {
        return endeffect;
    }

    public void setEndeffect(long endeffect) {
        this.endeffect = endeffect;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getModifytime() {
        return modifytime;
    }

    public void setModifytime(long modifytime) {
        this.modifytime = modifytime;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public TermMatch getEventMatch() {
        return eventMatch;
    }

    public void setEventMatch(TermMatch eventMatch) {
        this.eventMatch = eventMatch;
    }

    public Set<String> getExpVars() {
        return expVars;
    }

    public void setExpVars(Set<String> expVars) {
        this.expVars = expVars;
    }


    public Set<TermMatch.MatchType> getMatchTypes() {
        return matchTypes;
    }

    public void setMatchTypes(Set<TermMatch.MatchType> matchTypes) {
        this.matchTypes = matchTypes;
    }

    public List<String> getEvents() {
        List<String> events = new ArrayList<>();
        for (TermMatch matcher : this.termMatches) {
            if (matcher.getMatchType() == TermMatch.MatchType.Event) {
                events = matcher.getEvents();
                break;
            }
        }
        return events;
    }

    public Set<String> loadVariables() {
        Set<String> variables = new HashSet<>();
        for (TermMatch termMatch : this.termMatches) {
            if (termMatch.getMatchType() == TermMatch.MatchType.Profile) {
                Set<String> currentVariables = termMatch.getVariables();
                for (String currentVariable : currentVariables) {
                    variables.add(currentVariable);
                }
            }
        }
        return variables;
    }

    private static String getStringJson(String key, Map<String, Object> item) {
        if (item.containsKey(key)) {
            String value = String.valueOf(item.get(key)).trim();
            return value;
        } else {
            return "";
        }
    }

    public static Strategy parseStrategy(Map<String, Object> kv) {
        Strategy strategy = new Strategy();
        String status = getStringJson("status", kv);
        strategy.setStatus(status);
        List<Object> termItems = (List) kv.get("terms");
        String expression = "";
        if (termItems.size() > 0) {
            strategy.termMatches = new ArrayList<>();
            Set<String> expVars = new HashSet<>();
            for (Object termItem : termItems) {
                TermMatch termMatch = TermMatch.parseTermMatch(termItem);
                if (termMatch.getMatchType() == TermMatch.MatchType.Event) {
                    strategy.eventMatch = termMatch;
                }

                if (termMatch.getMatchType() == TermMatch.MatchType.Profile) {
                    String curTermMatchExpression = termMatch.getExpression();
                    List<String> curExpVars = termMatch.getLeftTerm().getVariables();
                    for (String curExpVar : curExpVars) {
                        expVars.add(curExpVar);
                    }
                    if (StringUtils.isBlank(expression)) {
                        expression = curTermMatchExpression;
                    } else {
                        expression = expression + " &&" + curTermMatchExpression;
                    }
                } else if (termMatch.getMatchType() == TermMatch.MatchType.Spl) {
                    String spl = termMatch.getLeftTerm().getExpression();
                    strategy.setSpl(spl);
                }
                strategy.termMatches.add(termMatch);
                strategy.setExpVars(expVars);
            }
        }
        strategy.expression = expression;
        List<Object> tagsJson = (List) kv.get("tags");
        if (tagsJson.size() > 0) {
            strategy.tags = new ArrayList<>();
            for (Object _tag : tagsJson) {
                strategy.tags.add((String) _tag);
            }
        }
        try {
            strategy.setApp(getStringJson("app", kv));
            strategy.setStarteffect((Long) kv.get("starteffect"));
            strategy.setEndeffect(Long.valueOf(String.valueOf(kv.get("endeffect"))));
            strategy.setModifytime((Long) kv.get("modifytime"));
            strategy.setScore((Integer) kv.get("score"));
            strategy.setCategory((String) kv.get("category"));
            strategy.setRemark((String) kv.get("remark"));
            strategy.setLock(Boolean.valueOf("isLock"));
            strategy.setName((String) kv.get("name"));
            strategy.setVersion((Long) kv.get("version"));
            strategy.setCreatetime((Long) kv.get("createtime"));
        } catch (Exception e) {
            logger.error("parse strategy exception, while stratgey is " + strategy.getName());
            e.printStackTrace();
            throw new IllegalStateException("parse strategy exception, while stratgey is ", e);
        }

        Set<TermMatch.MatchType> matchTypes = new HashSet<>();
        for (TermMatch termMatch : strategy.termMatches) {
            matchTypes.add(termMatch.getMatchType());
        }

        strategy.setMatchTypes(matchTypes);

        return strategy;
    }

    public boolean containsMatchType(TermMatch.MatchType matchType) {
        return this.matchTypes.contains(matchType);
    }

    public String getActionCheckType() {
        String checkType = "";
        for (TermMatch termMatch : termMatches) {
            Term leftTerm = termMatch.getLeftTerm();
            if (leftTerm.getSubType() == Term.SubType.SetBlackList) {
                Map<String, Object> config = leftTerm.getConfig();
                checkType = (String) config.get("checktype");
            }
        }
        return checkType;
    }

    public String getActionDimension() {
        String dimension = "";
        for (TermMatch termMatch : termMatches) {
            Term leftTerm = termMatch.getLeftTerm();
            if (leftTerm.getSubType() == Term.SubType.SetBlackList) {
                Map<String, Object> config = leftTerm.getConfig();
                dimension = (String) config.get("checkvalue");
            }
        }
        return dimension;
    }

    public Map<String, Object> getProperty(Event event) {
        Map<String, Object> kv = new HashMap<>();
        for (TermMatch termMatch : termMatches) {
            Term leftTerm = termMatch.getLeftTerm();
            if (leftTerm.getSubType() == Term.SubType.SetBlackList) {
                Map<String, Object> config = leftTerm.getConfig();
                kv.put("remark", (String) config.get("remark"));
                kv.put("checkpoints", (String) config.get("checkpoints"));
                kv.put("decision", (String) config.get("decision"));
                kv.put("riskScore", this.getScore());
                if (this.getStatus().toLowerCase().equals("test")) {
                    kv.put("test", false);
                } else {
                    kv.put("test", true);
                }
                kv.put("strategyName", this.getName());
                kv.put("expire", event.getTimestamp() + ((Integer) config.get("ttl")) * 1000);
                kv.put("sceneName", this.getCategory());
                kv.put("checkType", (String) config.get("checktype"));
                kv.put("variableValues", "");
                kv.put("timestamp", System.currentTimeMillis());
                break;
            }
        }
        return kv;
    }

    public String getSpl() {
        return spl;
    }

    public void setSpl(String spl) {
        this.spl = spl;
    }
}
