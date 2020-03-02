package com.lib.commonlib.net.intefaces;


import com.lib.commonlib.net.param.ConnectionState;

/**
 * Created by dengjun on 2015/11/27.
 * Description :
 */
public interface IConnectCallBack<T> {
    boolean SUCCESS = true;
    boolean FAIL = false;

    //接收数据回调
    void onReceive(T messageReceived);

    void onSend(boolean status, T messageSend);

    void onConnectionStatus(ConnectionState connectionState);
}
