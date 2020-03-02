package com.lib.commonlib.net.client;


import com.lib.commonlib.net.intefaces.IConnectCallBack;
import com.lib.commonlib.net.message.NetMessage;

import com.lib.commonlib.net.param.ConnectionState;
import com.lib.commonlib.utils.MLog;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;


/**
 * Created by dengjun on 2017/12/27.
 */

public  class DefaultTcpClient extends AbConnectImpl<NetMessage> {
    private Socket mSocket;
    private DataInputStream dataInputStream;

    @Override
    public void connect() {
        super.connect();
        if (mSocket == null){
            try {
                MLog.i("Tcp connect");
                mSocket = new Socket();
                mSocket.setSoTimeout(connectionParam.getReadTimeout());
                mSocket.connect(new InetSocketAddress(connectionParam.getHost(),connectionParam.getPort()), 10*1000);
                dataInputStream = new DataInputStream(mSocket.getInputStream());

            }catch (IOException e){
                e.printStackTrace();
                MLog.e("connect IOException",e);
                mSocket = null;
                if (connectCallBack != null){
                    connectCallBack.onConnectionStatus(new ConnectionState(ConnectionState.CONNECTION_FAIL));
                }
            }

            if (mSocket != null){
                isConnected = true;
                startReceiveThread();

                if (connectCallBack != null){
                    connectCallBack.onConnectionStatus(new ConnectionState(ConnectionState.CONNECTION_SUCCESS));
                }
            }
        }else {
            MLog.e("connect,  tcp  is connected ");
        }
    }

    private void doConnectServer() throws IOException {

    }

    @Override
    public void disConnect() {
        MLog.d("tcp disConnect");
        isConnected = false;
        if (mSocket != null){
            try {
                if(mSocket.getInputStream() != null){
                    mSocket.shutdownInput();
                }
                dataInputStream = null;
            } catch (IOException e) {
                MLog.e("close input stream error ",e);
                e.printStackTrace();
            }
            try {
                if (mSocket.getOutputStream() != null){
                    mSocket.shutdownOutput();
                }
            } catch (IOException e) {
                MLog.e("close output stream error ",e);
                e.printStackTrace();
            }
            try {
                MLog.i("close tcp socket");
                mSocket.close();
                MLog.i("close tcp socket finish");
            } catch (IOException e) {
                MLog.e("close socket error  ",e);
                e.printStackTrace();
            }
            stopReceiveThread();
            mSocket = null;
        }
    }

    @Override
    protected void runReadLoop() {
        while (isConnected){
            readData();
        }
    }

    private void readData(){
        if (mSocket != null && mSocket.isConnected() && isConnected){
            try {
                int receiveDataLength = dataInputStream.read(buffer);
                if (receiveDataLength > 0){
                    byte[] readData = new byte[receiveDataLength];

                    System.arraycopy(buffer,0,readData,0,readData.length);
//                    MLog.d("tcp len："+readData.length +" receiveData : "+ ByteTools.bytesToHexString(readData));

                    if (connectCallBack != null){
                        connectCallBack.onReceive(new NetMessage(readData));
                    }
                }
            } catch (SocketException e){
                MLog.e("tcp readData SocketException",e);
                e.printStackTrace();
                if (isConnected){
                    callBackStatus(ConnectionState.CONNECTION_FAIL);
                }
            } catch (Exception e) {
                MLog.e("tcp readData Exception error ",e);
                e.printStackTrace();
                if (isConnected){
                    callBackStatus(ConnectionState.CONNECTION_FAIL);
                }
            }
        }
    }

    @Override
    public void sendData(NetMessage sendData) {
        if (isConnected && mSocket != null && sendData.getBytes() != null){
            try {
//                MLog.d("tcp send dataLen: "+ sendData.getBytes().length+" data : "+ByteTools.bytesToHexString(sendData.getBytes()));
                mSocket.getOutputStream().write(sendData.getBytes());
                mSocket.getOutputStream().flush();

                if (connectCallBack != null){
                    connectCallBack.onSend(IConnectCallBack.SUCCESS,sendData);
                }
            } catch (IOException e) {
                MLog.e("error",e);
                if (connectCallBack != null){
                    connectCallBack.onSend(IConnectCallBack.FAIL,sendData);
                }
                callBackStatus(ConnectionState.CONNECTION_FAIL);
            }
        }
    }

}
