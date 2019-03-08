package com.threathunter.labrador.common.util;

import com.threathunter.metrics.aggregator.MetricsAggregator;

import java.util.Collections;
import java.util.Map;

public class MetricsHelper {

    private static MetricsHelper metricsHelper = new MetricsHelper();

    private MetricsAggregator aggregator = new MetricsAggregator();

    private int expireSeconds;

    private static final String db = Constant.METRICS_DB;

    public static MetricsHelper getInstance() {
        return metricsHelper;
    }

    private MetricsHelper() {
        this.aggregator.initial(60);
        this.expireSeconds = 604800;
        this.aggregator.startAggregator();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.aggregator.stopAggregator();
        }));
    }

    public void setExpireSeconds(int expireSecond) {
        this.expireSeconds = expireSecond;
    }

    public void addMertricsTags(String metricsName, Map<String,Object> tags, Double value ) {
        this.aggregator.add(db, metricsName, tags, value, this.expireSeconds);
    }

    public void addMetrics(String metricsName, Double value) {
        this.aggregator.add(db, metricsName, Collections.emptyMap(), value, this.expireSeconds);
    }

}
