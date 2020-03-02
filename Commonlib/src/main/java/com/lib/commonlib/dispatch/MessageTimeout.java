package com.lib.commonlib.dispatch;


/**
 * Created by dengjun on 2018/1/12.
 */

public interface MessageTimeout<T> {
    void start();

    void stop();

    void addMessage(T message);

    void removeMessage(long messageId);

    void removeAllMessage();

    void setMessageTimeoutCallback(MessageTimeoutCallback messageTimeoutCallback);


    interface MessageTimeoutCallback<T>{
        void onMessage(T message);
    }
}
