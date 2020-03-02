package com.lib.commonlib.net.client;

import com.lib.commonlib.net.intefaces.IConnect;
import com.lib.commonlib.net.intefaces.IConnectCallBack;
import com.lib.commonlib.net.message.NetMessage;
import com.lib.commonlib.net.param.ConnectionParam;
import com.lib.commonlib.net.param.ConnectionState;
import com.lib.commonlib.utils.MLog;
import com.lib.commonlib.utils.bytes.ByteTools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;


/**
 * Created by dengjun on 2017/12/27.
 */

public class DefaultUdpClient  implements IConnect<NetMessage> {
    private ConnectionParam connectionParam;

    private DatagramSocket client;
    private InetSocketAddress inetSocketAddress;

    private volatile boolean isConnected = false;
    private IConnectCallBack<NetMessage> connectCallBack;

    private Thread receiveThread;


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
        if (client == null){
            try {
                MLog.i("Udp connect");
                client = new DatagramSocket();
                inetSocketAddress = new InetSocketAddress(connectionParam.getHost(),connectionParam.getPort());

                client.setReuseAddress(true);
                client.setReceiveBufferSize(connectionParam.getBufferSize());
                client.setSendBufferSize(connectionParam.getBufferSize());

            }catch (SecurityException e){
                e.printStackTrace();
                MLog.e("connect SecurityException",e);
                client = null;
                if (connectCallBack != null){
                    connectCallBack.onConnectionStatus(new ConnectionState(ConnectionState.CONNECTION_FAIL));
                }
            }catch (SocketException e) {
                e.printStackTrace();
                client = null;
                MLog.e("connect SocketException",e);
                if (connectCallBack != null){
                    connectCallBack.onConnectionStatus(new ConnectionState(ConnectionState.CONNECTION_FAIL));
                }
            }catch (Exception e){
                e.printStackTrace();
                client = null;
                MLog.e("connect udp Exception",e);
                if (connectCallBack != null){
                    connectCallBack.onConnectionStatus(new ConnectionState(ConnectionState.CONNECTION_FAIL));
                }
            }

            if (client != null){
                isConnected = true;

                startReceiveThread();

                if (connectCallBack != null){
                    connectCallBack.onConnectionStatus(new ConnectionState(ConnectionState.CONNECTION_SUCCESS));
                }
            }
        }
    }

    @Override
    public void disConnect() {
        if (client != null){
            isConnected = false;
            MLog.i("Udp disConnect");
            client.close();
            client = null;
            stopReceiveThread();
        }
        sendDatagramPacket = null;
    }

    @Override
    public void setConnectCallBack(IConnectCallBack iConnectCallBack) {
        connectCallBack = iConnectCallBack;
    }


    private DatagramPacket sendDatagramPacket;
    @Override
    public void sendData(NetMessage sendData) {
        if (client != null&& sendData.getBytes() != null){
            try {
                if (sendDatagramPacket == null){
                    sendDatagramPacket = new DatagramPacket(sendData.getBytes(),sendData.getBytes().length,InetAddress.getByName(connectionParam.getHost()),connectionParam.getPort());
                }else {
                    sendDatagramPacket.setData(sendData.getBytes());
                }
                client.send(sendDatagramPacket);
                if (connectCallBack != null){
                    connectCallBack.onSend(IConnectCallBack.SUCCESS,sendData);
                }
            } catch (IOException e) {
                e.printStackTrace();
                MLog.e("sendData IOException",e);
                if (connectCallBack != null){
                    connectCallBack.onSend(IConnectCallBack.FAIL,sendData);
                }
            }
        }
    }

    private void startReceiveThread(){

        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MLog.i("UdpReceiveThread start run");
                byte[] recvBuffer = new byte[connectionParam.getReceiveDataLength()];
                NetMessage netMessage = new NetMessage();

                DatagramPacket dataPacket = new DatagramPacket(recvBuffer, 0, recvBuffer.length);
                while (isConnected){
                    if (client != null){
                        try {
                            client.receive(dataPacket);

                            byte[] receiveData = new byte[dataPacket.getLength()];
                            System.arraycopy(dataPacket.getData(),0,receiveData,0,receiveData.length);

//                            MLog.d("len : "+receiveData.length+"  Udp receive : "+ ByteTools.bytesToHexString(receiveData));

                            if (connectCallBack != null){
                                netMessage.fromData(receiveData);
                                connectCallBack.onReceive(netMessage);
                            }
                        } catch (SocketException e) {
                            e.printStackTrace();
                            MLog.e("udp SocketException ",e);
                            if (connectCallBack != null && isConnected == true){
                                connectCallBack.onConnectionStatus(new ConnectionState(ConnectionState.CONNECTION_OFF));
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                            MLog.e("udp IOException",e);
                            if (connectCallBack != null && isConnected == true){
                                connectCallBack.onConnectionStatus(new ConnectionState(ConnectionState.CONNECTION_OFF));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            MLog.e("udp Exception",e);
                            if (connectCallBack != null && isConnected == true){
                                connectCallBack.onConnectionStatus(new ConnectionState(ConnectionState.CONNECTION_OFF));
                            }
                        }
                    }
                }
                MLog.i("UdpReceiveThread finish");
            }
        });
        receiveThread.setName("UdpReceiveThread");
        receiveThread.start();
    }

    private void stopReceiveThread(){
        if (receiveThread != null){
            try {
                receiveThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                MLog.e("",e);
            }
        }
    }

}
