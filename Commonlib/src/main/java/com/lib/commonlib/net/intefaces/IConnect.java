package com.lib.commonlib.net.intefaces;


import com.lib.commonlib.net.param.ConnectionParam;

/**
 * @author dengjun
 * @CreateDate 2016-08-30 18:16.
 * Description :
 */
public interface IConnect<T> {
   boolean isConnected();

   void setConfigParam(ConnectionParam connectionParam);

   ConnectionParam getConnectionParam();

   void connect();

   void disConnect();

   void setConnectCallBack(IConnectCallBack iConnectCallBack);

   void sendData(T sendData);
}
