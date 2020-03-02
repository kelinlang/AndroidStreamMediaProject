package com.lib.commonlib.net.client;

import com.lib.commonlib.net.intefaces.IConnect;
import com.lib.commonlib.net.intefaces.IConnectCallBack;

import com.lib.commonlib.net.param.ConnectionParam;
import com.lib.commonlib.net.param.ConnectionState;
import com.lib.commonlib.utils.MLog;



/**
 * Created by dengjun on 2017/12/27.
 */

public abstract class AbConnectImpl<T> implements IConnect<T> {
    protected ConnectionParam connectionParam;

    protected boolean isConnected = false;
    protected IConnectCallBack<T> connectCallBack;

    protected Thread receiveThread;
    protected boolean receiveThreadRunFlag = false;

    protected volatile byte[] buffer;

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void setConfigParam(ConnectionParam connectionParam) {
        this.connectionParam = connectionParam;
    }

    @Override
    public ConnectionParam getConnectionParam() {
        return connectionParam;
    }

    @Override
    public void connect() {
        if (connectionParam == null){
            throw new RuntimeException("Please run setConfigParam first");
        }
    }



    @Override
    public void setConnectCallBack(IConnectCallBack iConnectCallBack) {
        connectCallBack = iConnectCallBack;
    }



    protected void startReceiveThread(){
        receiveThreadRunFlag = true;
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MLog.i("ReceiveThread start run : "+connectionParam.getType());
                buffer = new byte[connectionParam.getBufferSize()];
                runReadLoop();
                MLog.i("ReceiveThread finish  "+connectionParam.getType());
            }
        });
        receiveThread.setName("ReceiveThread : "+connectionParam.getType());
        receiveThread.start();
    }

    /**
     * 执行读取线程loop
     */
    protected abstract void runReadLoop();

    protected void stopReceiveThread(){
        receiveThreadRunFlag = false;
        if (receiveThread != null){
            try {
                MLog.d("stopReceiveThread join start");
                receiveThread.join();
                MLog.d("stopReceiveThread join finish");
            } catch (InterruptedException e) {
                e.printStackTrace();
                MLog.e("stopReceiveThread ",e);
            }
        }
    }

    protected void callBackStatus(int connectionState){
        if (connectCallBack != null){
            connectCallBack.onConnectionStatus(new ConnectionState(connectionState));
        }
    }
}
