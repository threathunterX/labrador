package com.threathunter.labrador.aerospike;

import com.aerospike.client.*;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.task.RegisterTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AerospikeUtil {

    public static void init(AerospikeConfig config) {
        ClientPolicy policy = new ClientPolicy();
        int readTimeoutDelay = config.getReadTimeoutDelay();
        int writeTimeoutDelay = config.getWriteTimeoutDelay();
        int scanTimeout = config.getScanTimeout();
        int queryTimeout = config.getQueryTimeout();
        int batchTimeout = config.getBatchTimeout();
        int numConns = config.getNumConns();
        int expireSecond = config.getExpireSecond();
        policy.readPolicyDefault.timeoutDelay = readTimeoutDelay;
        policy.writePolicyDefault.timeoutDelay = writeTimeoutDelay;
        policy.scanPolicyDefault.timeoutDelay = scanTimeout;
        policy.queryPolicyDefault.timeoutDelay = queryTimeout;
        policy.batchPolicyDefault.totalTimeout = batchTimeout;
        policy.writePolicyDefault.expiration = expireSecond;
        policy.maxConnsPerNode = numConns;
        policy.writePolicyDefault.sendKey = true;
        String host = config.getHost();
        int port = config.getPort();
        AerospikeClientFactory.init(host, port, policy);
    }

    //异步写aerospike
    public static void asyncPut(Key key, Bin... bins) {
        AerospikeClient client = AerospikeClientFactory.getAerospikeClient();
        client.put(AerospikeClientFactory.getEventLoop(), null, null, key, bins);
    }

    //同步写aerospike
    public static void put(Key key, Bin... bins) {
        AerospikeClient client = AerospikeClientFactory.getAerospikeClient();
        client.put(null, key, bins);
    }

    /**
     * 异步读取多条数据接口，
     * 内部使用一个EventLoop
     * 对as数据库来说，采用异步EventLoop方式进行，
     * Reactor方式进行回调
     * 对于前端查询而言，速度并不会更快。
     * return:key为查询的BatchRead.Key.key,Value为BatchRead.Key.key对应的Record
     */
    public static Map<String, Record> asyncBatchRead(List<BatchRead> reads) throws AerospikeException {
        AerospikeClient client = AerospikeClientFactory.getAerospikeClient();
        AsyncReadListener asyncReadListener = new AsyncReadListener();
        client.get(AerospikeClientFactory.getEventLoop(), asyncReadListener, null, reads);
        asyncReadListener.waitTillComplete();
        if (asyncReadListener.getException() != null) {
            throw asyncReadListener.getException();
        }
        return asyncReadListener.getRecords();
    }

    /*
        与异步处理不同的是，as通过线程池进行处理，
        每个线程通过一个Command。
        该方法如果是多个as结点，则会提升效率
    */
    public static Map<String,Record> batchRead(Key[] keys) {
        AerospikeClient client = AerospikeClientFactory.getAerospikeClient();
        Record[] records = client.get(null, keys);
        Map<String,Record> recordMap = new HashMap<>();
        for (int i = 0; i < records.length; i++) {
            Record record = records[i];
            if(null != record) {
                String key = keys[i].userKey.getObject().toString();
                recordMap.put(key, record);
            }
        }
        return recordMap;
    }

    /**
     * 通过udf写数据到as
     *
     * @param key
     * @param packageName 包名
     * @param method      方法名
     * @param values      可变数据，多个传参
     * @throws AerospikeException
     */
    public static void writeByUdf(Key key, String packageName, String method, Value... values) throws AerospikeException {
        AerospikeClient client = AerospikeClientFactory.getAerospikeClient();
        client.execute(null, key, packageName, method, values);
    }

    /**
     * 注册udf方法
     */
    public static void register(ClassLoader resourceLoader, String resourcePath, String serverPath, Language language) {
        AerospikeClient client = AerospikeClientFactory.getAerospikeClient();
        RegisterTask task = client.register(null, resourceLoader,
                "profile_write.lua", "profile_write.lua", Language.LUA);
        task.waitTillComplete();
    }

    /**
     * 根据单个key获取指定的Bin列表
     *
     * @param key
     * @param bins
     * @return
     */
    public static Record getRecordBins(Key key, String[] bins) {
        AerospikeClient client = AerospikeClientFactory.getAerospikeClient();
        return client.get(null, key, bins);
    }

}
