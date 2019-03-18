package com.threathunter.labrador.application.spl;

import com.threathunter.labrador.expression.ExpressionContext;
import com.threathunter.labrador.expression.ExpressionExecutor;
import com.threathunter.labrador.expression.ExpressionToken;
import com.threathunter.labrador.expression.IllegalExpressionException;

import java.util.*;

/**
 * 
 */
public class SplUtils {

    //解析函数
    public static Map<String, List<ExpressionToken>> parseFuncTokens(List<ExpressionToken> expTokens) {
        Map<String, List<ExpressionToken>> funcTokens = new HashMap<>();
        String curFunction = "";
        List<ExpressionToken> curTokens = new ArrayList<>();
        boolean started = false;
        for (ExpressionToken expToken : expTokens) {
            if (expToken.getTokenType() == ExpressionToken.ETokenType.ETOKEN_TYPE_FUNCTION) {
                curFunction = expToken.getTokenText();
                continue;
            } else if (expToken.getTokenType() == ExpressionToken.ETokenType.ETOKEN_TYPE_SPLITOR) {
                if (expToken.getTokenText().equals("(")) {
                    started = true;
                    continue;
                } else if (expToken.getTokenText().equals(")")) {
                    started = false;
                    List<ExpressionToken> destTokens = new ArrayList<>();
                    for (ExpressionToken token : curTokens) {
                        destTokens.add(token);
                    }
                    funcTokens.put(curFunction, destTokens);
                    curFunction = "";
                    curTokens.clear();
                    continue;
                }
            } else if (expToken.getTokenType() == ExpressionToken.ETokenType.ETOKEN_TYPE_VARIABLE ||
                    expToken.getTokenType() == ExpressionToken.ETokenType.ETOKEN_TYPE_CONSTANT) {
                if (started == true) {
                    curTokens.add(expToken);
                }
            }
        }
        return funcTokens;
    }

    private static Map<String,Set<String>> expressionContainer = new HashMap<>();

    public static Set<String> parseFunctionVars(String expression) {
        if(expressionContainer.containsKey(expression)) {
            return expressionContainer.get(expression);
        }
        synchronized (SplUtils.class) {
            Set<String> vars = new HashSet<>();
            ExpressionContext ctx = new ExpressionContext();
            ExpressionExecutor ee = new ExpressionExecutor(ctx);
            try {
                List<ExpressionToken> expTokens = ee.analyze(expression);
                Map<String, List<ExpressionToken>> funcTokens = parseFuncTokens(expTokens);
                for (Map.Entry<String, List<ExpressionToken>> entry : funcTokens.entrySet()) {
                    String funcName = entry.getKey();
                    List<ExpressionToken> tokens = entry.getValue();
                    for (ExpressionToken token : tokens) {
                        if (token.getTokenType() == ExpressionToken.ETokenType.ETOKEN_TYPE_VARIABLE) {
                            vars.add(token.getTokenText());
                        }
                    }
                }
            } catch (IllegalExpressionException e) {
                e.printStackTrace();
            }
            expressionContainer.put(expression, vars);
        }
        return expressionContainer.get(expression);
    }

    public static void main(String[] args) {
        String expression = "$CHECKNOTICE(\"ip\", c_ip, \"CRAWLER\", 3600) > 0";
        System.out.println(parseFunctionVars(expression));
    }
}
