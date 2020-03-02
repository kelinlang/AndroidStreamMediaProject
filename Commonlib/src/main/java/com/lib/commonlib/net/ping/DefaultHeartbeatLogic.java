package com.lib.commonlib.net.ping;

import android.os.Handler;

import com.lib.commonlib.net.intefaces.HeartBeatLogic;

/**
 * Created by dengjun on 2018/1/3.
 */

public class DefaultHeartbeatLogic implements HeartBeatLogic {
    private int timeout = DEFAULT_TIMEOUT;
    private Handler handler;
    private EventCallback eventCallback;

    public DefaultHeartbeatLogic() {

    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public void startHeartBeat() {
        if (handler != null){
            handler.post(heartLoopRunnable);
        }
    }

    private Runnable heartLoopRunnable = new Runnable() {
        @Override
        public void run() {
            if (eventCallback != null){
                eventCallback.onEvent(EVENT_SEND_HEART_BEAT_DATA);

                handler.postDelayed(timeOutRunnable,timeout);
                handler.postDelayed(heartLoopRunnable,timeout);
            }
        }
    };

    private Runnable timeOutRunnable = new Runnable() {
        @Override
        public void run() {
            if (eventCallback != null){
                eventCallback.onEvent(EVENT_TIMEOUT);
            }
        }
    };



    @Override
    public void stopHeartBeat() {
        if (handler != null){
            handler.removeCallbacks(timeOutRunnable);
            handler.removeCallbacks(heartLoopRunnable);
        }
    }

    @Override
    public void notifyRes() {
        if(handler != null){
            handler.removeCallbacks(timeOutRunnable);
        }
    }

    @Override
    public void setEventCallback(EventCallback eventCallback) {
        this.eventCallback = eventCallback;
    }
}
