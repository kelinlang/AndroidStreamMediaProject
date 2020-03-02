package com.lib.commonlib.net.param;

/**
 * @author dengjun
 * @CreateDate 2016-08-30 18:19.
 * Description :
 */
public class ConnectionParam {
    public final static String TYPE_TCP = "TCP";
    public final static String TYPE_UDP = "UDP";

    private String mHost ;
    private int mPort;
    /* 连接socket的时候，超时时间*/
    private int mConnectTimeout = 10*1000;
    /* 读取网络数据的超时时间*/
    private int mReadTimeout = 3*60*1000;
    /*连接超时时间*/
//    private int mConnectTimeout = 10*1000;

    private boolean proxyFlag = false;
    private String mProxyHost;
    private int mProxyPort;

    private int receiveDataLength = 100*1024;


    private int bufferSize = 100*1024;


    private String sessionId;

    /* 连接类型：TCP or UDP*/
    private String type;

    private ConnectionParam(Builder builder) {
        mHost = builder.mHost;
        mPort = builder.mPort;
        type = builder.type;
        this.sessionId = builder.sessionId;

    }

    public ConnectionParam(String mHost, int mPort) {
        this.mHost = mHost;
        this.mPort = mPort;
    }

    public String getHost() {
        return mHost;
    }



    public int getPort() {
        return mPort;
    }



    public int getConnectTimeout() {
        return mConnectTimeout;
    }


    public int getReadTimeout() {
        return mReadTimeout;
    }


    public String getProxyHost() {
        return mProxyHost;
    }


    public int getProxyPort() {
        return mProxyPort;
    }


    public int getReceiveDataLength() {
        return receiveDataLength;
    }


    public boolean isProxyFlag() {
        return proxyFlag;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public String getType() {
        return type;
    }

    public String getSessionId() {
        return sessionId;
    }

    public static Builder create(){
        return new Builder();
    }

    public static class Builder{
        private String mHost ;
        private int mPort;

        private String mProxyHost;
        private int mProxyPort;

        private String type = TYPE_TCP;

        private String sessionId;

        public Builder setAddress(String mHost, int mPort){
            this.mHost = mHost;
            this.mPort = mPort;
            return this;
        }

        public Builder setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public ConnectionParam build(){
            return new ConnectionParam(this);
        }
    }
}
