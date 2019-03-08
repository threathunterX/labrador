package com.threathunter.labrador.application.spl;

import com.threathunter.labrador.application.spl.check.CheckException;
import com.threathunter.labrador.application.spl.check.CheckResponse;
import com.threathunter.labrador.application.spl.check.ExpressionCheckService;
import com.threathunter.labrador.common.util.SpringUtils;
import com.threathunter.labrador.expression.ExpressionEvaluator;
import com.threathunter.labrador.expression.datameta.Variable;
import com.threathunter.labrador.rpc.constant.RpcConstant;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;

/**
 * Created by wanbaowang on 17/10/31.
 */
public class TestExpressionCheckService {

    private ApplicationContext applicationContext;

    @Before
    public void init() {
        this.applicationContext = new ClassPathXmlApplicationContext(RpcConstant.RPC_SERVER_PATH);
        SpringUtils.setApplicationContext(this.applicationContext);
    }


    @Test
    public void testCheck() throws CheckException {
        String expression = "$CHECKNOTICE(\"ip\", c_ip, \"IP响应字节过大\", 3600) > 0";
        ExpressionCheckService checkService = new ExpressionCheckService(expression);
        CheckResponse checkResponse = checkService.check();
        System.out.println(checkResponse.isSuccess());
    }

    @Test
    public void testExecute() {
        String expression = "$CHECKNOTICE(\"user\", uid, \"user_login_succ_$_did_exception_uid\", 832000) > 0";
        ArrayList<Variable> variables = new ArrayList<>();
        variables.add(Variable.createVariable("uid", "456"));
        Object result = ExpressionEvaluator.evaluate(expression, variables);
        System.out.println(result);
    }

}
