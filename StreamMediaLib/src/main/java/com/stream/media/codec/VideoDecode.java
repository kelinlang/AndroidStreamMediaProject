package com.stream.media.codec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;


import androidx.annotation.RequiresApi;

import com.lib.commonlib.utils.MLog;
import com.stream.media.jni.MediaDataCallback;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by dengjun on 2018/6/28.
 */

public class VideoDecode {
    private static final long WAIT_TIME = 12000;
    private VideoParam videoParam;
    private MediaCodec mediaCodec;

    private MediaDataCallback dataCallback;

    public ArrayBlockingQueue<byte[]> h264DataQueue = new ArrayBlockingQueue<byte[]>(30);
    private Thread inputThread;
    private Thread outputThread;
    private volatile  boolean runFlag = false;

    public void setVideoParam(VideoParam videoParam) {
        this.videoParam = videoParam;
    }

    public void setDataCallback(MediaDataCallback dataCallback) {
        this.dataCallback = dataCallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initMediaCodec() throws IOException {
        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", videoParam.getWidth(),  videoParam.getHeight());
//        mediaFormat.setString(MediaFormat.KEY_MIME,videoParam.getMediaFormat());
//        mediaFormat.setInteger(MediaFormat.KEY_WIDTH, videoParam.getWidth());
//        mediaFormat.setInteger(MediaFormat.KEY_HEIGHT, videoParam.getHeight());
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, videoParam.getBitRate());
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, videoParam.getFrameRate());
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, videoParam.getiFrameInterval());
//        mediaFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline);
//        mediaFormat.setInteger(MediaFormat.KEY_LEVEL,  MediaCodecInfo.CodecProfileLevel.AVCLevel31);
//        mediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);

        mediaCodec = MediaCodec.createDecoderByType("video/avc");

        for (int colorFormats: mediaCodec.getCodecInfo().getCapabilitiesForType(mediaFormat.getString(MediaFormat.KEY_MIME)).colorFormats){
            MLog.i("mediaCodec colorFormats : "+ colorFormats);
        }

        int[] colorFormats = mediaCodec.getCodecInfo().getCapabilitiesForType(mediaFormat.getString(MediaFormat.KEY_MIME)).colorFormats;
        int tragetFormats= -1;

        if (ArrayUtils.isArrayContain(colorFormats, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar)) {
            tragetFormats = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
            MLog.i("tragetFormats = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar ");
        }
        if (tragetFormats == -1 && ArrayUtils.isArrayContain(colorFormats, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar)) {
            tragetFormats = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;
            MLog.i("tragetFormats = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar ");
        }
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, tragetFormats);
//        videoParam.setColorFormat(tragetFormats);

        mediaCodec.configure(mediaFormat, null, null, 0);
        mediaCodec.start();
    }

    private void startMediaCodec(){
        try {
            initMediaCodec();
        } catch (IOException e) {
            e.printStackTrace();
            stopMeiaCodec();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void stopMeiaCodec() {
        try {
            if (mediaCodec != null){
                MLog.i("stopMeiaCodec");
                mediaCodec.stop();
                mediaCodec.release();
            }
        } catch (Exception e){
            e.printStackTrace();
            MLog.e("error",e);
        }
    }

    public void inputData(byte[] videoData){
        if (mediaCodec != null){
            try {
                h264DataQueue.add(videoData);
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
        }
    }

    public boolean isWorking(){
        return runFlag;
    }

    public void start(){
        if (runFlag){
            return;
        }
        MLog.i("video decode start");
//        startMediaCodec();
//        if (mediaCodec == null){
//            return;
//        }
        runFlag = true;
        initSave();
        inputThread = new Thread(new Runnable() {
            @Override
            public void run() {
//                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                MLog.d(Thread.currentThread().getName()+" start run() tid=" + Thread.currentThread().getId());
                long generateIndex = 0;
                startMediaCodec();
                if (mediaCodec != null){
                    MLog.i("mediaCodec start success");
                    startOutputThread();

                    while (runFlag){
                        byte[] h264Data = null;
                        if (runFlag){
                            h264Data = take();
                        }
                        if (h264Data != null){
                            try {
                                int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
                                if (inputBufferIndex >= 0){
                                    ByteBuffer byteBuffer;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                        byteBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
                                    }else {
                                        byteBuffer = mediaCodec.getInputBuffers()[inputBufferIndex];
                                    }
                                    byteBuffer.clear();
                                    byteBuffer.put(h264Data);
//                                    MLog.d("inputBufferIndex : "+inputBufferIndex);
                                    mediaCodec.queueInputBuffer(inputBufferIndex,0,h264Data.length,computePresentationTime(generateIndex),0);
                                    generateIndex +=1;
                                }

                            }catch (IllegalStateException e){
                                e.printStackTrace();
                                MLog.e("error",e);
                            }catch (Exception e){
                                e.printStackTrace();
                                MLog.e("error",e);
                            }
                        }
                    }
                    stopOutputThread();
                    stopMeiaCodec();
                }
            }
        });
        inputThread.setName("codec  video Thread");
        inputThread.start();
    }

    private byte[]  changeColorFormat(byte[] data){
        byte[] yuv420 = null;
        if (videoParam.getColorFormat() == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar){
            yuv420 = new byte[videoParam.getWidth()*videoParam.getHeight()*3/2];
            CodecUtils.swapNV21toI420(data,yuv420,videoParam.getWidth(),videoParam.getHeight());
        }else if (videoParam.getColorFormat() == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar){
            yuv420 = new byte[videoParam.getWidth()*videoParam.getHeight()*3/2];
            CodecUtils.NV21ToNV12(data,yuv420,videoParam.getWidth(),videoParam.getHeight());
        }
        return yuv420;
    }

    private byte[] take(){
        byte[] h264Data = null;
        try {
            h264Data = h264DataQueue.poll(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return h264Data;
    }

    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / videoParam.getFrameRate();
    }

    private void startOutputThread(){
        outputThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MLog.d(Thread.currentThread().getName()+" start run() tid=" + Thread.currentThread().getId());
                if (mediaCodec != null){
                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                    while (runFlag){
                        try {
                            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, WAIT_TIME);
//                            MLog.d("decode outputBufferIndex : "+ outputBufferIndex);
                            if (outputBufferIndex >= 0){
                                ByteBuffer outputBuffer;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                    outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex);
                                }else {
                                    outputBuffer = mediaCodec.getOutputBuffers()[outputBufferIndex];
                                }
                                byte[] yuvData = new byte[bufferInfo.size];
                                outputBuffer.get(yuvData);

                                fileStorage.wirte(yuvData);
                                if (dataCallback != null){
                                    dataCallback.onData(1,yuvData);
                                }
                                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                            }
                        }catch (IllegalStateException e){
                            e.printStackTrace();
                            MLog.e("error",e);
                        }catch (Exception e){
                            e.printStackTrace();
                            MLog.e("error",e);
                        }
                    }
                }
            }
        });
        outputThread.setName("codec outpu thread");
        outputThread.start();
    }


    private void getH264data(){}

    private void stopOutputThread(){
        if (outputThread != null) {
            try {
                MLog.i("stopOutputThread start");
                outputThread.join();
                MLog.i("stopOutputThread finish");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            outputThread = null;
        }
    }

    public void stop(){
        runFlag = false;
        if (inputThread != null) {
            MLog.i("video encoder stop");
            try {
                inputThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            inputThread = null;
            MLog.i("video encoder finish");
        }
        closeSave();
        h264DataQueue.clear();
        stopMeiaCodec();
    }

    private FileStorage fileStorage;
    private int count;
    private void initSave(){
        if (fileStorage == null){
            fileStorage =
//                    new FileStorage("/sdcard/"+"h264_"+count+"_"+ DateTimeUtils.format(DateTimeUtils.format5+"_.h264"));
                    new FileStorage("/sdcard/h264tmp.h264");
            count++;

//            fileStorage.setSaveEnableFlag(true);
            fileStorage.open();
        }
    }

    private void closeSave(){
        if (fileStorage != null){
            fileStorage.close();
            fileStorage = null;
        }
    }
}
