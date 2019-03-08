package com.threathunter.labrador.application.strategy;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.threathunter.labrador.aerospike.AerospikeUtil;
import com.threathunter.labrador.application.babel.NoticeSender;
import com.threathunter.labrador.common.util.ConfigUtil;
import com.threathunter.labrador.core.service.QueryService;
import com.threathunter.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/11/28.
 */
public class StrategyProcessThread implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(StrategyProcessThread.class);
    private Event event;
    private QueryService queryService;

    public StrategyProcessThread(Event event, QueryService queryService) {
        this.queryService = queryService;
        this.event = event;
    }

    @Override
    public void run() {
        StrategyService strategyService = new StrategyService(this.event, this.queryService);
        List<Event> noticeList = strategyService.processEvent();
        postStrategy(noticeList.size() > 0 ? true : false);
        if (noticeList.size() > 0) {
            logger.warn("send notices " + noticeList);
            NoticeSender.getInstance().sendNotices(noticeList);
        }
    }

    private void postStrategy(boolean triggerRisk) {
        Map<String, Object> eventMap = event.genAllData();
        logger.warn("eventMap======" + eventMap);
        if(eventMap.containsKey("order_id")) {
            String riskSetName = ConfigUtil.getString("nebula.labrador.aerospike.risk.set_name");
            Key key = new Key("profiles", riskSetName, String.valueOf(eventMap.get("order_id")));
            Bin bin = new Bin("result", triggerRisk);
            AerospikeUtil.put(key, bin);
        }
    }

}
