package com.kelinlang.stream.media.lib.jni;

import android.view.Surface;

public class MediaPlayerJni {
    public native void create();
    public native void setVideoSurface(Surface surface);
    public native void setParam(PlayerParam param);
    public native void init();
    public native void release();
    public native void start();
    public native void stop();
}
