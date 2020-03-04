package com.stream.media.codec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;


import com.lib.commonlib.utils.MLog;
import com.stream.media.jni.MediaDataCallback;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import com.stream.media.jni.VideoData;

/**
 * Created by dengjun on 2018/6/28.
 */

public class VideoEncoder {
    private static final long WAIT_TIME = 12000;
    private VideoParam videoParam;
    private MediaCodec mediaCodec;

    private MediaDataCallback dataCallback;

    public ArrayBlockingQueue<byte[]> yuvDataQueue = new ArrayBlockingQueue<byte[]>(30);
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

//        CQ  表示完全不控制码率，尽最大可能保证图像质量；
//        CBR 表示编码器会尽量把输出码率控制为设定值，即我们前面提到的“不为所动”；
//        VBR 表示编码器会根据图像内容的复杂度（实际上是帧间变化量的大小）来动态调整输出码率，图像复杂则码率高，图像简单则码率低；

        mediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
//        mediaFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline);
//        mediaFormat.setInteger(MediaFormat.KEY_LEVEL,  MediaCodecInfo.CodecProfileLevel.AVCLevel31);
//        mediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);

        mediaCodec = MediaCodec.createEncoderByType(videoParam.getMediaFormat());

        for (int colorFormats: mediaCodec.getCodecInfo().getCapabilitiesForType(mediaFormat.getString(MediaFormat.KEY_MIME)).colorFormats){
//            MLog.i("mediaCodec colorFormats : "+ colorFormats);
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
        videoParam.setColorFormat(tragetFormats);

        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
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
                yuvDataQueue.add(videoData);
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
        MLog.i("video encoder start");
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
                        byte[] nv21Data = null;
                        if (runFlag){
                            nv21Data = take();
                        }
                        if (nv21Data != null){
                            try {
                                //camera预留设置NV21需要转换
//                                byte[] yuv420sp = changeColorFormat(nv21Data);
                                byte[] yuv420sp = nv21Data;

                                int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
                                if (inputBufferIndex >= 0){
                                    ByteBuffer byteBuffer;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                        byteBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
                                    }else {
                                        byteBuffer = mediaCodec.getInputBuffers()[inputBufferIndex];
                                    }
                                    byteBuffer.clear();
                                    byteBuffer.put(yuv420sp);
//                                    MLog.d("inputBufferIndex : "+inputBufferIndex);
                                    mediaCodec.queueInputBuffer(inputBufferIndex,0,yuv420sp.length,computePresentationTime(generateIndex),0);
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
        byte[] nv21Data = null;
        try {
            nv21Data = yuvDataQueue.poll(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return nv21Data;
    }

    private long startTime = 0;
    private VideoData videoData = new VideoData();

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
                    byte[] configbyte = null;

                    while (runFlag){
                        try {
                            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, WAIT_TIME);
                            if (outputBufferIndex >= 0){
                                 ByteBuffer outputBuffer;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                    outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex);
                                }else {
                                    outputBuffer = mediaCodec.getOutputBuffers()[outputBufferIndex];
                                }
                                byte[] h264Data = new byte[bufferInfo.size];
                                outputBuffer.get(h264Data);

                               switch (bufferInfo.flags){
                                    case MediaCodec.BUFFER_FLAG_CODEC_CONFIG:
                                        MLog.d("config frame : "+ bufferInfo.flags + " config data len : "+ h264Data.length);
                                        configbyte = h264Data;
                                        break;
                                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                                        MLog.i("-------------output format change---------------");
                                        break;
                                    case MediaCodec.BUFFER_FLAG_KEY_FRAME:
//                                        MLog.d("  ");
//                                        MLog.d("key frame : "+ bufferInfo.flags+", dataLen: "+ h264Data.length);
                                        byte[] keyframe = new byte[bufferInfo.size + configbyte.length];
                                        System.arraycopy(configbyte, 0, keyframe, 0, configbyte.length);
                                        System.arraycopy(h264Data, 0, keyframe, configbyte.length, h264Data.length);

                                        fileStorage.wirte(keyframe);


                                        if (dataCallback != null){
                                            dataCallback.onData(0,keyframe);
                                        }

                                        if (startTime == 0) {
                                            startTime = bufferInfo.presentationTimeUs / 1000;
                                        }

                                        videoData.videoData = keyframe;
                                        videoData.videoDataLen = keyframe.length;
                                        videoData.dataFormat = VideoConstants.VDIEO_FORMAT_H264;
                                        videoData.frameType = 1;
                                        videoData.width = videoParam.getWidth();
                                        videoData.height = videoParam.getHeight();
                                        videoData.id = "test";
                                        videoData.timeStamp = bufferInfo.presentationTimeUs / 1000 - startTime;

                                        if (dataCallback != null){
                                            dataCallback.onData(2,videoData);
                                        }
                                        break;
                                    default:
//                                        MLog.d("normal frame :"+ bufferInfo.flags+", dataLen: "+ h264Data.length+ ", outputBufferIndex :"+outputBufferIndex);
                                        fileStorage.wirte(h264Data);

                                        if (dataCallback != null){
                                            dataCallback.onData(1,h264Data);
                                        }

                                        if (startTime == 0) {
                                            startTime = bufferInfo.presentationTimeUs / 1000;
                                        }
//                                        MLog.i("h264DataLen : "+ h264Data.length);
                                        videoData.videoData = h264Data;
                                        videoData.videoDataLen = h264Data.length;
                                        videoData.dataFormat = VideoConstants.VDIEO_FORMAT_H264;
                                        videoData.frameType = 0;
                                        videoData.width = videoParam.getWidth();
                                        videoData.height = videoParam.getHeight();
                                        videoData.id = "test";
                                        videoData.timeStamp = bufferInfo.presentationTimeUs / 1000 - startTime;

                                        if (dataCallback != null){
                                            dataCallback.onData(2,videoData);
                                        }
                                        break;
                                }


                                //新的实现开始
                           /*     ByteBuffer outputBuffer;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                    outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex);
                                }else {
                                    outputBuffer = mediaCodec.getOutputBuffers()[outputBufferIndex];
                                }
                                outputBuffer.position(bufferInfo.offset);
                                outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                                byte[] h264Data = new byte[outputBuffer.remaining()];
                                outputBuffer.get(h264Data);

                                if (startTime == 0) {
                                    startTime = bufferInfo.presentationTimeUs / 1000;
                                }
//                                        MLog.i("h264DataLen : "+ h264Data.length);
                                videoData.videoData = h264Data;
                                videoData.videoDataLen = h264Data.length;
                                videoData.dataFormat = VideoConstants.VDIEO_FORMAT_H264;
                                videoData.frameType = bufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME ? 1 :0;
                                videoData.width = videoParam.getWidth();
                                videoData.height = videoParam.getHeight();
                                videoData.id = "test";
                                videoData.timeStamp = bufferInfo.presentationTimeUs / 1000 - startTime;

                                if (dataCallback != null){
                                    dataCallback.onData(2,videoData);
                                }*/
                                //新的实现结束


                                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                            }else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                                MLog.i("-------------output format change---------------");
                                ByteBuffer spsb = mediaCodec.getOutputFormat().getByteBuffer("csd-0");
                                byte[] sps = new byte[spsb.remaining()];
                                spsb.get(sps,0,sps.length);

                                ByteBuffer ppsb = mediaCodec.getOutputFormat().getByteBuffer("csd-1");
                                byte[] pps = new byte[spsb.remaining()];
                                ppsb.get(pps,0,pps.length);
                                MLog.d("sps len "+ sps.length + "  pps len : "+ pps.length);

                                videoData.dataFormat = VideoConstants.VDIEO_FORMAT_H264_PPS_SPS;
                                videoData.id = "test";
                                videoData.sps = sps;
                                videoData.spsLen = sps.length;
                                videoData.pps = pps;
                                videoData.ppsLen = pps.length;
                                if (dataCallback != null){
                                    dataCallback.onData(2,videoData);
                                }
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
        yuvDataQueue.clear();
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
