package com.stream.media.codec;

import android.graphics.ImageFormat;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

/**
 * Created by dengjun on 2018/6/28.
 */

public class VideoParam {
    private int width = 640;
    private int height = 480;

    /* 压缩格式，默认h264*/
    private String mediaFormat = MediaFormat.MIMETYPE_VIDEO_AVC;

    /* 码率*/
    private int bitRate = 640*480*30;
    /* 帧率*/
    private int frameRate = 30;

    /* I帧间隔*/
    private int iFrameInterval = 1;



    private int previewFormat = ImageFormat.NV21;

    private int colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getMediaFormat() {
        return mediaFormat;
    }

    public void setMediaFormat(String mediaFormat) {
        this.mediaFormat = mediaFormat;
    }


    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public int getiFrameInterval() {
        return iFrameInterval;
    }

    public void setiFrameInterval(int iFrameInterval) {
        this.iFrameInterval = iFrameInterval;
    }

    public int getPreviewFormat() {
        return previewFormat;
    }

    public void setPreviewFormat(int previewFormat) {
        this.previewFormat = previewFormat;
    }


    public int getColorFormat() {
        return colorFormat;
    }

    public void setColorFormat(int colorFormat) {
        this.colorFormat = colorFormat;
    }
}
