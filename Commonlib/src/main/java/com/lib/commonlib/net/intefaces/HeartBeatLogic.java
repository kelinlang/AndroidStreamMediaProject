package com.lib.commonlib.net.intefaces;

import android.os.Handler;

/**
 * Created by dengjun on 2018/1/3.
 */

public interface HeartBeatLogic {
    int DEFAULT_TIMEOUT = 30*1000;

    int EVENT_SEND_HEART_BEAT_DATA = 1;
    int EVENT_TIMEOUT = 0;
    void setHandler(Handler handler);
    void setTimeout(int timeout);
    void startHeartBeat();
    void stopHeartBeat();
    void notifyRes();
    void setEventCallback(EventCallback eventCallback);


    public interface EventCallback{
        void onEvent(int event);
    }
}
