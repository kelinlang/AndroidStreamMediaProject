package com.stream.media.jni;

import android.view.Surface;

public class MediaPlayerJni {
    static {
        try {
            System.loadLibrary("StreamMediaLib");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public native void create();
    public native void setVideoSurface(Surface surface);
    public native void setParam(PlayerParam param);
    public native void init();
    public native void release();
    public native void start();
    public native void stop();
}
