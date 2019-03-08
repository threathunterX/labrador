package com.threathunter.labrador.application.spl;

import com.threathunter.labrador.application.mysql.domain.Notice;
import com.threathunter.labrador.application.mysql.service.MysqlService;
import com.threathunter.labrador.common.util.SpringUtils;

/**
 * Created by wanbaowang on 17/10/31.
 */
public class UserDefinedFunctions {

    public int checkNotice(String keyType, String keyValue, String strategy, int interval) {
        MysqlService mysqlService = SpringUtils.getBean(MysqlService.class);
        Notice src = new Notice();
        src.setStrategyName(strategy);
        //5天的毫秒
//        int interval = 1000 * 60 * 60 * 24 * 5;
        src.setCheckType(keyType.toUpperCase());
        src.setKey(keyValue);
        long current = System.currentTimeMillis();
        src.setTimestamp(current - interval * 1000);
        int count = mysqlService.noticeCheck(src);
        return count;
    }

}
