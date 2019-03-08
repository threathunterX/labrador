package com.threathunter.labrador.rpc.constant;

import com.threathunter.labrador.rpc.util.Config;
import org.apache.commons.lang3.StringUtils;

public class RpcConstant {

    public static final String RPC_SERVER_PATH  = Config.getString("web-rpc.spring.localtion");

    public static final String CURRENT_SERVER_ADDRESS = Config.getString("rpc.server.address");

    public static final String REGISTRY_TYPE = Config.getString("rpc.server.registry");
    public static final String SERIALIZE_TYPE = Config.getString("rpc.server.serialize");

    public static final String REMOTE_ADDRESS = Config.getString("rpc.remote.address");

    public static final String ZK_ADDRESS = Config.getString("rpc.zookeeper.address");
    public static final String ZK_REGISTRY_PATH = "/netty-swift";
    public static final int ZK_SESSION_TIMEOUT = Config.getInt("rpc.zookeeper.session.timeout");
    public static final int ZK_CONNECTION_TIMEOUT = Config.getInt("rpc.zookeeper.connection.timeout");

    public static RegistryType getRegistryType() {
        if (StringUtils.isEmpty(REGISTRY_TYPE)) {
            return RegistryType.SIMPLE;
        }
        if (REGISTRY_TYPE.equalsIgnoreCase(RegistryType.ZOOKEEPER.getKey())) {
            return RegistryType.ZOOKEEPER;
        } else {
            return RegistryType.SIMPLE;
        }
    }

    public static SerializeType getSerializeType() {
        if (StringUtils.isEmpty(SERIALIZE_TYPE)) {
            return SerializeType.HESSIAN;
        }
        if (SERIALIZE_TYPE.equalsIgnoreCase(SerializeType.KRYO.getKey())) {
            return SerializeType.KRYO;
        } else if (SERIALIZE_TYPE.equalsIgnoreCase(SerializeType.PROTOSTUFF.getKey())) {
            return SerializeType.PROTOSTUFF;
        } else {
            return SerializeType.HESSIAN;
        }
    }

}
