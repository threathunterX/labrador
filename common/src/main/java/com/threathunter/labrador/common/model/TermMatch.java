package com.threathunter.labrador.common.model;

import java.util.*;

/**
 * 
 */
public class TermMatch {

    private String remark;

    private String op;

    private Term leftTerm;

    private Term rightTerm;

    public Term getLeftTerm() {
        return leftTerm;
    }

    public void setLeftTerm(Term leftTerm) {
        this.leftTerm = leftTerm;
    }

    public Term getRightTerm() {
        return rightTerm;
    }

    public void setRightTerm(Term rightTerm) {
        this.rightTerm = rightTerm;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public enum MatchType {Event, Profile, Rt, Action, Spl}

    private MatchType matchType;

    private Set<String> variables;

    private String expression;

    private String scope;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public Set<String> getVariables() {
        return variables;
    }

    public void setVariables(Set<String> variables) {
        this.variables = variables;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<String> loadEvents() {
        if (this.getMatchType() != MatchType.Profile) {
            return new ArrayList<>();
        }
        return this.leftTerm.getVariables();
    }

    public List<String> getEvents() {
        if (this.matchType.equals(MatchType.Event)) {
            return this.getLeftTerm().getEvents();
        } else {
            return new ArrayList<>();
        }
    }

    private static String getStringJson(String key, Map<String,Object> item) {
        if(item.containsKey(key)) {
            String value = String.valueOf(item.get(key)).trim();
            return value;
        } else {
            return "";
        }
    }

    public static TermMatch parseTermMatch(Object jsonTerms) {
        TermMatch termMatch = new TermMatch();
        Map<String, Object> kv = (Map) jsonTerms;
        String remark = getStringJson("remark", kv);
        String op = getStringJson("op", kv);

        String scope = getStringJson("scope", kv);
        termMatch.setScope(scope);

        termMatch.op = op;
        termMatch.remark = remark;

        Object jsonRigthTerm = kv.get("right");
        Object jsonLeftTerm = kv.get("left");
        if (null != jsonRigthTerm) {
            Term rigthTerm = Term.parseTerm("right", jsonRigthTerm);
            termMatch.rightTerm = rigthTerm;
        }

        if (null != jsonLeftTerm) {
            Term leftTerm = Term.parseTerm("left", jsonLeftTerm);
            termMatch.leftTerm = leftTerm;
        }

        Set<String> variables = new HashSet<>();

        if (termMatch.leftTerm.getTermType() == Term.TermType.Event) {
            termMatch.matchType = MatchType.Event;
        } else if (termMatch.leftTerm.getTermType() == Term.TermType.FUNC && termMatch.rightTerm == null) {
            termMatch.matchType = MatchType.Action;
        } else if(termMatch.getLeftTerm().getSubType() == Term.SubType.Spl) {
            termMatch.matchType = MatchType.Spl;
        } else if(termMatch.getScope().equals("profile") && termMatch.getRightTerm() != null) {
            termMatch.matchType = MatchType.Profile;
            String variable = termMatch.getLeftTerm().getVariables().get(0);
            variables.add(variable);
            String value = termMatch.getRightTerm().getValue();
            String termExpression =  "(" + variable + " " + op + value + ")";
            termMatch.expression = termExpression;
        } else if(termMatch.getScope().equals("realtime")) {
            termMatch.matchType = MatchType.Rt;
        }
        termMatch.setVariables(variables);
        return termMatch;
    }
}
