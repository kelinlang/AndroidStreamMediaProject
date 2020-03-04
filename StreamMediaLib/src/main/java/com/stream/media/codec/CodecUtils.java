package com.stream.media.codec;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;

import java.util.List;

/**
 * Created by dengjun on 2018/6/28.
 */

public class CodecUtils {
    public static boolean supportAvcCodec(){
        if(Build.VERSION.SDK_INT>=19){
            for(int j = MediaCodecList.getCodecCount() - 1; j >= 0; j--){
                MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(j);

                String[] types = codecInfo.getSupportedTypes();
                for (int i = 0; i < types.length; i++) {
                    if (types[i].equalsIgnoreCase("video/avc")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static void NV21ToNV12(byte[] nv21,byte[] nv12,int width,int height){
        if(nv21 == null || nv12 == null)return;
        int framesize = width*height;
        int i = 0,j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for(i = 0; i < framesize; i++){
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize/2; j+=2)
        {
            nv12[framesize + j-1] = nv21[j+framesize];
        }
        for (j = 0; j < framesize/2; j+=2)
        {
            nv12[framesize + j] = nv21[j+framesize-1];
        }
    }


   // 1、NV21转nv12
    public static void swapNV21ToNV12(byte[] nv21,byte[] nv12,int width,int height){
        if(nv21 == null || nv12 == null)return;
        int framesize = width*height;
        int i = 0,j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for (j = 0; j < framesize/2; j+=2)
        {
            nv12[framesize + j + 1] = nv21[j + framesize];
        }

        for (j = 0; j < framesize/2; j += 2)
        {
            nv12[framesize + j] = nv21[j + framesize + 1];
        }
    }

//2、YV12转I420
    public static  void swapYV12toI420(byte[] yv12bytes, byte[] i420bytes, int width, int height)
    {
        System.arraycopy(yv12bytes, 0, i420bytes, 0,width*height);
        System.arraycopy(yv12bytes, width*height+width*height/4, i420bytes, width*height,width*height/4);
        System.arraycopy(yv12bytes, width*height, i420bytes, width*height+width*height/4,width*height/4);
    }

//3、yv12转nv12
    public static void swapYV12toNV12(byte[] yv12bytes, byte[] nv12bytes, int width,int height)
    {
        int nLenY = width * height;
        int nLenU = nLenY / 4;

        System.arraycopy(yv12bytes, 0, nv12bytes, 0, width * height);
        for (int i = 0; i < nLenU; i++) {
            nv12bytes[nLenY + 2 * i + 1] = yv12bytes[nLenY + i];
            nv12bytes[nLenY + 2 * i] = yv12bytes[nLenY + nLenU + i];
        }
    }

//4、nv12转I420
    public static void  swapNV12toI420(byte[] nv12bytes, byte[] i420bytes, int width,int height) {
        int nLenY = width * height;
        int nLenU = nLenY / 4;

        System.arraycopy(nv12bytes, 0, i420bytes, 0, width * height);
        for (int i = 0; i < nLenU; i++) {
            i420bytes[nLenY + i] = nv12bytes[nLenY + 2 * i + 1];
            i420bytes[nLenY + nLenU + i] = nv12bytes[nLenY + 2 * i];
        }
    }

    //4、nv12转I420
    public static void  swapNV21toI420(byte[] nv21bytes, byte[] i420bytes, int width,int height) {
        int nLenY = width * height;
        int nLenU = nLenY / 4;
        int nLenV = nLenY / 4;

        System.arraycopy(nv21bytes, 0, i420bytes, 0, width * height);
        for (int i = 0; i < nLenU; i++) {
            i420bytes[nLenY + i] = nv21bytes[nLenY + 2 * i + 1];
            i420bytes[nLenY + nLenU + i] = nv21bytes[nLenY + 2 * i];
        }
    }

    public static boolean isArrayContain(int[] src, int target) {
        for (int color : src) {
            if (color == target) {
                return true;
            }
        }
        return false;
    }

    public static boolean isIntListContain(List<Integer> src, int target) {
        for (int color : src) {
            if (color == target) {
                return true;
            }
        }
        return false;
    }
}


