package com.lib.commonlib.net.message;



/**
 * @author dengjun
 * @CreateDate 2016-12-20 11:37.
 * Description :  网络消息包基类，具体协议的消息包继承此类
 */

public  class NetMessage {
    protected byte[] messageArray = null;

    public NetMessage() {
    }

    public NetMessage(byte[] messageArray) {
        this.messageArray = messageArray;
    }

    /**
     * 消息对象生成二进制消息数组
     * @return  二进制数据包
     */
    public  byte[] getBytes(){
        return messageArray;
    }

    /**
     *消息二进制数据生成消息对象
     * @param data
     */
    public  void fromData(byte[] data){
        messageArray = data;
    }


    public String toString(){
       return "";
    }
}
