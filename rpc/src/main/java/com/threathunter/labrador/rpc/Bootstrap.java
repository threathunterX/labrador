package com.threathunter.labrador.rpc;

import com.threathunter.labrador.rpc.server.RpcServer;
import com.threathunter.labrador.rpc.util.Config;

/**
 * Created by wanbaowang on 17/10/30.
 */
public class Bootstrap {

    public static void main(String[] args) {
        int port = Config.getInt("rpc.server.port");
        new RpcServer(port).start();
    }

}
