package com.stream.media.jni;

import android.view.Surface;

public class MediaJni {
    public native void init();
    public native void setMediaStatusCallback(MediaStatusCallback mediaStatusCallback);
    public native void setParam(MediaParam param);
    public native void release();

    public native void resume();
    public native void pause();

//    public native void setViewParam(String id);
    public native void createPlayer(String id);
    public native void setVideoSurface(String id,Surface surface);
    public native void setVideoMatrix(String id,float[] matrix);
    public native void setPlayerParam(String id,PlayerParam playerParam);
    public native void startPlay(String id);
    public native void stopPlay(String id);


    public native void setPullStreamParam(String id,StreamParam streamParam);
    public native void startPull(String id);
    public native void stopPull(String id);

    public native void createPushClient(String id);
    public native void setPushStreamParam(String id,StreamParam streamParam);
    public native void initPush(String id);
    public native void releasePush(String id);
    public native void startPush(String id);
    public native void stopPush(String id);
    public native void sendVideoData(String id,VideoData videoData);
    public native void sendAudioData(String id,AudioData audioData);
}
