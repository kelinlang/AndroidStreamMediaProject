package com.lib.commonlib.dispatch;

import android.os.Handler;
import android.os.Looper;

import java.util.Vector;

/**
 * Created by dengjun on 2017/10/18.
 */

/**
 * 消息分发，此分发只适用于业务逻辑层，分发sdk状态，信息，错误，消息给ui
 */
public class MessageDispatch {
    private static  MessageDispatch instance = null;

    private Vector<MessageCallback> messageCallbacks = new Vector<MessageCallback>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private MessageDispatch() {
    }

    public static MessageDispatch getInstance(){
        if (instance == null){
            synchronized (MessageDispatch.class){
                if (instance == null){
                    instance = new MessageDispatch();
                }
            }
        }
        return instance;
    }


    public void addMessageCallback(MessageCallback messageCallback){
        if (!messageCallbacks.contains(messageCallback)){
            messageCallbacks.add(messageCallback);
        }
    }

    /**
     * messageCallback == null 时清除所有
     * @param messageCallback
     */
    public void removeMessageCallback(MessageCallback messageCallback){
        if (messageCallback == null){
            messageCallbacks.clear();
        }else {
            messageCallbacks.remove(messageCallback);
        }
    }

    public void dispatchMessage(final int msgCode,final Object msg){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (MessageCallback messageCallback : messageCallbacks){
                    messageCallback.onMessage(msgCode,msg);
                }
            }
        });
    }

    public void dispatchError(final int infoId,final Object info){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (MessageCallback messageCallback : messageCallbacks){
                    messageCallback.onError(infoId,info);
                }
            }
        });
    }

    public void dispatchMessage(final boolean status,final int msgCode,final Object msg){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (MessageCallback messageCallback : messageCallbacks){
                    messageCallback.onSend(status,msgCode,msg);
                }
            }
        });
    }

    public void dispatchStatus(final int status,final Object msgCode){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (MessageCallback messageCallback : messageCallbacks){
                    messageCallback.onStatus(status,msgCode);
                }
            }
        });
    }
}
