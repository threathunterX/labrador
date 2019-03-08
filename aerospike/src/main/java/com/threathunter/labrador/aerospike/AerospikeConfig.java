package com.threathunter.labrador.aerospike;

public class AerospikeConfig {

    private String host;

    private int port;

    private int readTimeoutDelay;

    private int writeTimeoutDelay;

    private int scanTimeout;

    private int queryTimeout;

    private int batchTimeout;

    private int expireSecond;

    private int numConns;

    public AerospikeConfig(String host, int port, int readTimeoutDelay, int writeTimeoutDelay, int scanTimeout,
                           int queryTimeout, int batchTimeout, int expireSecond, int numConns) {
        this.host = host;
        this.port = port;
        this.readTimeoutDelay = readTimeoutDelay;
        this.writeTimeoutDelay = writeTimeoutDelay;
        this.scanTimeout = scanTimeout;
        this.queryTimeout = queryTimeout;
        this.batchTimeout = batchTimeout;
        this.expireSecond = expireSecond;
        this.numConns = numConns;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getReadTimeoutDelay() {
        return readTimeoutDelay;
    }

    public void setReadTimeoutDelay(int readTimeoutDelay) {
        this.readTimeoutDelay = readTimeoutDelay;
    }

    public int getWriteTimeoutDelay() {
        return writeTimeoutDelay;
    }

    public void setWriteTimeoutDelay(int writeTimeoutDelay) {
        this.writeTimeoutDelay = writeTimeoutDelay;
    }

    public int getScanTimeout() {
        return scanTimeout;
    }

    public void setScanTimeout(int scanTimeout) {
        this.scanTimeout = scanTimeout;
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public int getBatchTimeout() {
        return batchTimeout;
    }

    public void setBatchTimeout(int batchTimeout) {
        this.batchTimeout = batchTimeout;
    }

    public int getExpireSecond() {
        return expireSecond;
    }

    public void setExpireSecond(int expireSecond) {
        this.expireSecond = expireSecond;
    }

    public int getNumConns() {
        return numConns;
    }

    public void setNumConns(int numConns) {
        this.numConns = numConns;
    }
}
