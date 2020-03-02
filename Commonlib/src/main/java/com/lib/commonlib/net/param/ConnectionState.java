package com.lib.commonlib.net.param;

/**
 * Created by dengjun on 2015/11/27.
 * Description :
 */
public class ConnectionState {
    /**
     * The constant CONNECTION_SUCCESS.
     */
    public static final int CONNECTION_SUCCESS = 0;//连接成功
    /**
     * The constant CONNECTION_CREATE.
     */
    public static final int CONNECTION_CREATE= 1;//连接创建
    /**
     * The constant CONNECTION_FAIL.
     */
    public static final int CONNECTION_FAIL= 2;//连接失败
    /**
     * The constant CONNECTION_IDLE.
     */
    public static final int CONNECTION_OPEND = 3;
    public static final int CONNECTION_IDLE = 4;//连接休眠

    /**
     * 连接断开
     * The constant CONNECTION_OFF.
     */
    public static final int CONNECTION_OFF = 5;

    /**
     * 服务器断开连接
     */
    public static final int CONNECTION_RESET_BY_SERVER = 6;


    /**
     * 发送消息失败
     */
    public static final int SEND_MES_FAIL = 7;
    /* 心跳超时*/
    public static final int HEART_BEAT_TIME_OUT = 8;


    /**
     * The Connection state.
     */
    public int connectionState;

    /**
     * Instantiates a new Connection state.
     *
     * @param connectionState the connection state
     */
    public ConnectionState(int connectionState) {
        this.connectionState = connectionState;
    }
}
