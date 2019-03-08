package com.threathunter.labrador.spl.check;

import com.threathunter.labrador.expression.ExpressionContext;
import com.threathunter.labrador.expression.ExpressionExecutor;
import com.threathunter.labrador.expression.ExpressionToken;
import com.threathunter.labrador.expression.IllegalExpressionException;
import com.threathunter.labrador.expression.function.FunctionLoader;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/10/25.
 */
public class ExpressionCheckService {

    private String expression;

    public ExpressionCheckService(String expression) {
        this.expression = expression;
    }

    public CheckResponse check() {
        ExpressionContext ctx = new ExpressionContext();
        ExpressionExecutor ee = new ExpressionExecutor(ctx);
        String message = "";
        boolean success = true;
        try {
            //解析表达式词元
            List<ExpressionToken> expTokens = ee.analyze(expression);
            Map<String, List<ExpressionToken>> funcTokens = parseFuncTokens(expTokens);
            for (Map.Entry<String, List<ExpressionToken>> entry : funcTokens.entrySet()) {
                String funcName = entry.getKey();
                FunctionLoader.Function function = FunctionLoader.getFunctionByName(funcName);
                if (null == function) {
                    message = "function name " + funcName + " not defined";
                    success = false;
                    break;
                }
                List<ExpressionToken> tokens = entry.getValue();
                CheckFunctionResponse checkFunctionResponse = checkFunction(function, tokens);
                if (!checkFunctionResponse.success) {
                    success = false;
                    message = checkFunctionResponse.message;
                    break;
                }
            }
            ee.compile(expTokens);
        } catch (IllegalExpressionException e) {
            success = false;
            message = e.getMessage();
        } catch (IllegalAccessException e) {
            success = false;
            message = e.getMessage();
        }
        CheckResponse checkResponse = new CheckResponse(this.expression, success, message);
        return checkResponse;
    }

    private CheckFunctionResponse checkFunction(FunctionLoader.Function function, List<ExpressionToken> tokens) {
        //如果没有verify函数定义，则不进行函数正确性检测
        if (StringUtils.isBlank(function.getVerifyMethod())) {
            return new CheckFunctionResponse(true);
        }
        List<String> regexList = function.getVariableNameRegex();
        List<String> typeList = function.getVariableType();
        if (tokens.size() != regexList.size()) {
            String message = String.format("parameter of function name %s size is %d, but expression size is %d", function.getName(), regexList.size(), tokens.size());
            return new CheckFunctionResponse(false, message);
        }
        for (int i = 0; i < regexList.size(); i++) {
            String regex = regexList.get(i);
            String tokenText = tokens.get(i).getTokenText();
            String type = typeList.get(i);
            if (!tokenText.matches(regex)) {
                return new CheckFunctionResponse(false, String.format("function %s regex %s not matches, token is %s", function.getName(), regex, tokenText));
            }

            if (tokens.get(i).getTokenType() == ExpressionToken.ETokenType.ETOKEN_TYPE_CONSTANT) {
                if (type.equals("variable")) {
                    return new CheckFunctionResponse(false, String.format("function %s regex %s type is variable, but token type is constant", function.getName(), regex));
                }
            } else if(tokens.get(i).getTokenType() == ExpressionToken.ETokenType.ETOKEN_TYPE_VARIABLE){
                if (type.equals("constant")) {
                    return new CheckFunctionResponse(false, String.format("function %s regex %s type is constant, but token type is variable", function.getName(), regex));
                }
            }
        }
        return new CheckFunctionResponse(true);
    }

    private Map<String, List<ExpressionToken>> parseFuncTokens(List<ExpressionToken> expTokens) throws IllegalAccessException {
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

    private class CheckFunctionResponse {
        private boolean success;
        private String message;

        public CheckFunctionResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public CheckFunctionResponse(boolean success) {
            this(success, "");
        }
    }

    public static void main(String[] args) throws CheckException {
//        String expression = "$CHECKNOTICE(\"ip\", cip, \"CRAWLER\", 938549)";
        String expression = "$CHECKNOTICE(\"ip\", c_ip, \"CRAWLER\", 3600) > 0";
        ExpressionCheckService expressionCheckService = new ExpressionCheckService(expression);
        CheckResponse checkResponse = expressionCheckService.check();
        if(!checkResponse.isSuccess()) {
            System.out.println("发生错误");
            System.out.println(checkResponse.getMessage());
        }
    }
}
