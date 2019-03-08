/*
package com.threathunter.labrador.common.util;


import com.aerospike.client.*;
import com.aerospike.client.Value.MapValue;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.task.IndexTask;
import com.threathunter.config.CommonDynamicConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AerospikeUtils {
    private static final String MERGE_MODULE = "map_ext";
    private static final String MERGE_METHOD = "putAll";
    private static String NAMESPACE = "";
    public static AerospikeClient client;
    private static final Logger LOG = LoggerFactory.getLogger(AerospikeUtils.class);
    private static transient boolean inited = false;

    public AerospikeUtils() {
    }

    public static void init(CommonDynamicConfig config) {
        if (!inited) {
            String host = getConfig(config, "aerospike_address");
            NAMESPACE = getConfig(config, "aerospike.profile.namespace");
            if (StringUtils.isBlank(host)) {
                throw new AerospikeException("aerospike.host not defined");
            }
            boolean var2 = true;
            try {
                int port = getInteger(getConfig(config, "aerospike_port"), 3000);
                System.out.println("port:" + port);
                ClientPolicy policy = new ClientPolicy();
                policy.readPolicyDefault.timeout = getInteger(getConfig(config, "aerospike.read.timeout"), 1000);
                policy.writePolicyDefault.timeout = getInteger(getConfig(config, "aerospike.write.timeout"), 1000);
                policy.writePolicyDefault.sendKey = true;
                policy.scanPolicyDefault.timeout = getInteger(getConfig(config, "aerospike.scan.timeout"), 5000);
                policy.scanPolicyDefault.sendKey = true;
                policy.queryPolicyDefault.timeout = getInteger(getConfig(config, "aerospilke.query.timeout"), 3000);
                policy.queryPolicyDefault.sendKey = true;
                policy.batchPolicyDefault.timeout = getInteger(getConfig(config, "aerospike.batch.timeout"), 3000);
                policy.maxConnsPerNode = getInteger(getConfig(config, "aerospike.num.conn"), 10);
                policy.writePolicyDefault.expiration = getInteger(getConfig(config, "aerospike.expire.seconds"), 0);
                client = new AerospikeClient(policy, host, port);
            } catch (AerospikeException var4) {
                throw var4;
            }
        }
    }

    public static String getConfig(CommonDynamicConfig config, String key) {
        return String.valueOf(config.getProperty(key));
    }

    public static String getNameSpace() {
        return NAMESPACE;
    }

    public static int getInteger(String value, int defaultValue) {
        int result = defaultValue;

        try {
            result = Integer.valueOf(value).intValue();
        } catch (Exception var4) {
            ;
        }

        return result;
    }

    public static void add(Key key, Bin... bins) throws AerospikeException {
        try {
            client.add((WritePolicy)null, key, bins);
        } catch (AerospikeException var3) {
            throw new AerospikeException(var3.getMessage(), var3);
        }
    }

    public static boolean[] batchExists(Key[] keys) throws AerospikeException {
        boolean[] exists = client.exists((BatchPolicy)null, keys);
        return exists;
    }

    public static Record[] batchReads(Key[] keys, String... binNames) throws AerospikeException {
        Record[] records = client.get((BatchPolicy)null, keys);
        return records;
    }

    public static void delete(Key key) throws AerospikeException {
        client.delete((WritePolicy)null, key);
    }

    public static Record getHeader(Key key) throws AerospikeException {
        return client.getHeader((Policy)null, key);
    }

    public static void mergeMap(Key key, Map<String, Object> map, String binName) throws AerospikeException {
        client.execute(client.getWritePolicyDefault(), key, "map_ext", "putAll", new Value[]{Value.get(binName), new MapValue(map)});
    }

    public static Record[] getHeaders(Key[] keys) throws AerospikeException {
        return client.getHeader((BatchPolicy)null, keys);
    }

    public static void careateIndex(String namespace, String setName, String indexName, String binName, IndexType indexType) {
        Policy writeIndexPolicy = client.writePolicyDefault;
        IndexTask task = client.createIndex(writeIndexPolicy, namespace, setName, indexName, binName, indexType);
        task.waitTillComplete();
    }

    public static void main(String[] args) {
        Key key = new Key("metrics", "monitor", "fx.babel.client.cost.min:1469449162799");

        while(true) {
            while(true) {
                try {
                    Record record = client.get((Policy)null, key);
                    System.out.println(record);
                    Thread.sleep(200L);
                } catch (Exception var3) {
                    System.out.println("connection fail");
                }
            }
        }
    }
}
*/
