package com.stream.media.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.lib.commonlib.utils.DataJsonTranslation;
import com.lib.commonlib.utils.MLog;
import com.stream.media.camera.Camera2Impl;
import com.stream.media.camera.CameraInterface;
import com.stream.media.camera.CameraParam;
import com.stream.media.camera.PreviewDataCallback;
import com.stream.media.codec.VideoEncoder;
import com.stream.media.codec.VideoParam;
import com.stream.media.jni.MediaDataCallback;
import com.stream.media.jni.MediaJni;
import com.stream.media.jni.StreamParam;
import com.stream.media.jni.VideoData;

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraPriviewView extends SurfaceView implements SurfaceHolder.Callback, PreviewDataCallback , MediaDataCallback {
    private SurfaceHolder.Callback holdCallback;
    private CameraInterface camera;

    private VideoParam videoParam = new VideoParam();
    private VideoEncoder videoEncoder = new VideoEncoder();

    private StreamParam streamParam = new StreamParam();

    private MediaJni mediaJni;

    public CameraPriviewView(Context context) {
        super(context);
        init();
    }

    public CameraPriviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPriviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public CameraPriviewView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    void init(){
        getHolder().addCallback(this);
        camera = new Camera2Impl(getContext());
        camera.setPreviewDataCallback(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int angle = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();

        switch (angle) {
            case Surface.ROTATION_0:
                getLayoutParams().width = 480;
                getLayoutParams().height = 640;

                break;
            case Surface.ROTATION_90:
                getLayoutParams().width = 640;
                getLayoutParams().height = 480;

                break;
            case Surface.ROTATION_180:
                getLayoutParams().width = 480;
                getLayoutParams().height = 640;

                break;
            case Surface.ROTATION_270:
                getLayoutParams().width = 640;
                getLayoutParams().height = 480;

                break;
            default:
                break;
        }

        requestLayout();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        MLog.d("camera preview surfaceCreated width : "+ getWidth()+ " , height : "+ getHeight());
        videoParam.setWidth(getWidth());
        videoParam.setHeight(getHeight());
        videoParam.setFrameRate(30);
        videoParam.setBitRate(640*480*300);

        if (mediaJni != null && !TextUtils.isEmpty(streamParam.url)&& !TextUtils.isEmpty(streamParam.id)){
            MLog.d("推流参数："+ DataJsonTranslation.objectToJson(streamParam));
            mediaJni.createPushClient(streamParam.id);
            mediaJni.setPushStreamParam(streamParam.id,streamParam);
            mediaJni.initPush(streamParam.id);
            mediaJni.startPush(streamParam.id);


            videoEncoder.setVideoParam(videoParam);
            videoEncoder.setDataCallback(this);
            videoEncoder.start();
        }


        CameraParam cameraParam = new CameraParam();
        cameraParam.setPriviewWidth(getWidth());
        cameraParam.setYuvWidth(getWidth());
        cameraParam.setPriviewHeight(getHeight());
        cameraParam.setYuvHeight(getHeight());
        camera.setParam(cameraParam);
        camera.setSurface(holder.getSurface());
        camera.open();
        camera.startPreview();


        if (holdCallback != null){
            holdCallback.surfaceCreated(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        MLog.i("camera preview  surfaceChanged width : "+ width+ " , height : "+ height);


        if (holdCallback != null){
            holdCallback.surfaceChanged(holder, format,width,height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        MLog.i("camera preview  surfaceDestroyed");
        camera.stopPreview();
        camera.close();

        if (mediaJni != null && !TextUtils.isEmpty(streamParam.url)&& !TextUtils.isEmpty(streamParam.id)){
            videoEncoder.stop();
            videoEncoder.setDataCallback(null);


            mediaJni.stopPush(streamParam.id);
        }

        if (holdCallback != null){
            holdCallback.surfaceDestroyed(holder);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MLog.d("preview  当前横屏 +width : "+ getWidth()+ " , height : "+ getHeight());

            getLayoutParams().width = 640;
            getLayoutParams().height = 480;
            requestLayout();
        }else {
            MLog.d("preview 当前竖屏 +width : "+ getWidth()+ " , height : "+ getHeight());
            getLayoutParams().width = 480;
            getLayoutParams().height = 640;

            requestLayout();
        }
    }

    //摄像头预留数据回调
    @Override
    public void onData(int type, byte[] data) {
//        MLog.d("camera yuv data len : "+ data.length);
        videoEncoder.inputData(data);
    }

    //编码后回调h264数据
    @Override
    public void onData(int type, Object data) {

        if (mediaJni!= null&& !TextUtils.isEmpty(streamParam.url)&& !TextUtils.isEmpty(streamParam.id)){
            switch (type){
                case 2:
//                    MLog.d("call back  h264");
                    mediaJni.sendVideoData(streamParam.id,(VideoData)data);
                    break;
            }
        }
    }

    public void setMediaDataCallback(MediaDataCallback mediaDataCallback){
        videoEncoder.setDataCallback(mediaDataCallback);
    }


    public void setMediaJni(MediaJni mediaJni) {
        this.mediaJni = mediaJni;
    }

    public StreamParam getStreamParam() {
        return streamParam;
    }

    public void setStreamParam(StreamParam streamParam) {
        this.streamParam = streamParam;
    }

    public void setHoldCallback(SurfaceHolder.Callback holdCallback) {
        this.holdCallback = holdCallback;
    }
}
