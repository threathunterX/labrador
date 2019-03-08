package com.threathunter.labrador.application.mysql;

import com.threathunter.labrador.application.mysql.domain.Notice;
import com.threathunter.labrador.application.mysql.service.MysqlService;
import com.threathunter.labrador.rpc.constant.RpcConstant;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wanbaowang on 17/11/1.
 */
public class TestMysql {

    private ApplicationContext applicationContext;

    @Before
    public void init() {
        this.applicationContext = new ClassPathXmlApplicationContext(RpcConstant.RPC_SERVER_PATH);
    }

    @Test
    public void testNotice() {
        MysqlService mysqlService = this.applicationContext.getBean(MysqlService.class);
        Notice src = new Notice();
        src.setStrategyName("用户多次登录失败");
        long current = System.currentTimeMillis();
        //5天的毫秒
        int interval = 1000 * 60 * 60 * 24 * 5;
        src.setCheckType("IP");
        src.setKey("test_key4");
        src.setTimestamp(current - interval);
        int count = mysqlService.noticeCheck(src);
        System.out.println(count);
    }

}
