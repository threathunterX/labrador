package com.threathunter.labrador.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.async.EventLoop;
import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.NettyEventLoops;
import com.aerospike.client.policy.ClientPolicy;
import com.threathunter.labrador.common.util.ConfigUtil;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Aerospike Client工厂类，
 * 支持通过init方式构建及默认读取配置文件属性方式构建
 * 1. 允许直接调用AerospikeUtil.init方式进行构建连接。
 * 2. 如果已构建，则直接使用连接，否则读取文件配置进行构建
 */
public class AerospikeClientFactory {

    private static AerospikeClient client;

    private static String host = ConfigUtil.getString("aerospike_address");
    private static int port = ConfigUtil.getIntDefault("aerospike_port", 3000);

    private static EventLoop eventLoop = null;

    //返回同步的client
    public static AerospikeClient getAerospikeClient() {
        if (client == null) {
            ClientPolicy policy = getPolicy();
            synchronized (AerospikeClientFactory.class) {
                if (client == null) {
                    client = new AerospikeClient(policy, host, port);
                }
            }
        }
        return client;
    }

    public static synchronized void init(String host, int port, ClientPolicy policy) {
        if(client == null) {
            initPolicyEventLoops(policy);
            client = new AerospikeClient(policy, host, port);
        }
    }

    public static EventLoop getEventLoop() {
        return eventLoop;
    }

    private static ClientPolicy getPolicy() {
        ClientPolicy policy = new ClientPolicy();
        policy.readPolicyDefault.timeoutDelay = ConfigUtil.getIntDefault("aerospike.read.timeout", 1000);
        policy.writePolicyDefault.timeoutDelay = ConfigUtil.getIntDefault("aerospike.write.timeout", 1000);
        policy.scanPolicyDefault.timeoutDelay = ConfigUtil.getIntDefault("aerospike.scan.timeout", 5000);
        policy.queryPolicyDefault.timeoutDelay = ConfigUtil.getIntDefault("aerospike.scan.timeout", 5000);
        switch (policy.batchPolicyDefault.totalTimeout = ConfigUtil.getIntDefault("aerospike.batch.timeout", 3000)) {
        }
        policy.writePolicyDefault.expiration = ConfigUtil.getIntDefault("aerospike.expire.seconds", 0);
        policy.maxConnsPerNode = 200;
        policy.writePolicyDefault.sendKey = true;
        initPolicyEventLoops(policy);
        return policy;
    }

    private static void initPolicyEventLoops(ClientPolicy policy) {
        EventLoopGroup group = new NioEventLoopGroup(1);
        EventLoops eventLoops = new NettyEventLoops(group);
        policy.eventLoops = eventLoops;
        eventLoop = eventLoops.get(0);
    }
}
