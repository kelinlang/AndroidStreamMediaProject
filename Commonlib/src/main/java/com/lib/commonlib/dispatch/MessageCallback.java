package com.lib.commonlib.dispatch;

/**
 * Created by dengjun on 2017/11/27.
 */

public interface MessageCallback {
    //状态回调方法
    void onStatus(int status, Object statusInfo);

    /* 错误信息回调*/
    void onError(int errorCode,Object errorInfo);

    //接收消息回调方法
    void onMessage(int msgCode, Object msg);
    //发送消息回调方法
    void onSend(boolean status, int msgCode, Object msg);
}
