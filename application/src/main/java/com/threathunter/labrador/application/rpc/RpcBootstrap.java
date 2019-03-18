package com.threathunter.labrador.application.rpc;

import com.threathunter.labrador.rpc.server.RpcServer;
import com.threathunter.labrador.rpc.util.Config;

/**
 * 
 */
public class RpcBootstrap {

    public void startRpc() {
        int port = Config.getInt("rpc.server.port");
        new RpcServer(port).start();
    }

    public static void main(String[] args) {
        new RpcBootstrap().startRpc();
    }
}
