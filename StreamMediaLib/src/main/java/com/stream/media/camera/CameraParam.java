package com.stream.media.camera;

import android.graphics.ImageFormat;

public class CameraParam {
    int cameraID = 1;
    int priviewWidth = 640;
    int priviewHeight = 480;

    int yuvFormat = ImageFormat.YUV_420_888;
    int yuvWidth = 640;
    int yuvHeight = 480;


    public int getCameraID() {
        return cameraID;
    }

    public void setCameraID(int cameraID) {
        this.cameraID = cameraID;
    }

    public int getPriviewWidth() {
        return priviewWidth;
    }

    public void setPriviewWidth(int priviewWidth) {
        this.priviewWidth = priviewWidth;
    }

    public int getPriviewHeight() {
        return priviewHeight;
    }

    public void setPriviewHeight(int priviewHeight) {
        this.priviewHeight = priviewHeight;
    }

    public int getYuvFormat() {
        return yuvFormat;
    }

    public void setYuvFormat(int yuvFormat) {
        this.yuvFormat = yuvFormat;
    }

    public int getYuvWidth() {
        return yuvWidth;
    }

    public void setYuvWidth(int yuvWidth) {
        this.yuvWidth = yuvWidth;
    }

    public int getYuvHeight() {
        return yuvHeight;
    }

    public void setYuvHeight(int yuvHeight) {
        this.yuvHeight = yuvHeight;
    }
}
