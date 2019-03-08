package com.threathunter.labrador.expression.test;

import com.threathunter.labrador.expression.ExpressionEvaluator;
import com.threathunter.labrador.expression.ExpressionToken;
import com.threathunter.labrador.expression.datameta.Variable;
import com.threathunter.labrador.expression.format.ExpressionParser;
import com.threathunter.labrador.expression.format.FormatException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanbaowang on 17/10/25.
 */
public class Test {
    public static void main(String[] args) throws FormatException {
/*        String expression = "$STARTSWITH(aabcbcc,\"abc\")";
        ArrayList<Variable> variables = new ArrayList<>();
        variables.add(Variable.createVariable("aabcbcc", "abc"));
        Object result = ExpressionEvaluator.evaluate(expression, variables);
        System.out.println(result);*/

/*        String expression = "$CHECKNOTICE(keytype,keyvalue,rulename,tag,scene_name,dicision,test,expired,interval_ms) > 0";
        System.out.println(ExpressionEvaluator.compile(expression));*/


/*        String expression = "$STARTSWITH(\"abc\", \"abc\")";
        ArrayList<Variable> variables = new ArrayList<>();
//        variables.add(Variable.createVariable("aabcbcc", "abc"));
        Object result = ExpressionEvaluator.evaluate(expression, variables);
        System.out.println(result);*/

/*        String expression = "$CHECKNOTICE(aabcbcc, \"abc\")";

        ExpressionParser expressionParser = new ExpressionParser();
        List<ExpressionToken> tokens = expressionParser.getExpressionTokens(expression);
        for(ExpressionToken token : tokens) {
            System.out.println(token.getTokenType() + " : " + token.toString());
        }*/




    }
}
