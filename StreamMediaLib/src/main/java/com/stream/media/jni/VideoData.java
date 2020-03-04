package com.stream.media.jni;

/**
 * Created by dengjun on 2019/3/13.
 */

public class VideoData {
    public String id;

    public int dataFormat;

    public int width;
    public int height;

    public int frameType;
    public long timeStamp;

    public int videoDataLen;
    public byte[] videoData;



    public  byte[] sps;
    public int spsLen;
    public byte[] pps;
    public int ppsLen;

}
