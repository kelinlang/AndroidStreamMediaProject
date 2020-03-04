package com.stream.media.view;

import android.content.Context;
import android.content.res.Configuration;
import android.opengl.Matrix;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.lib.commonlib.utils.MLog;
import com.stream.media.jni.MediaJni;
import com.stream.media.jni.PlayerParam;


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class VideoDisplayView extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder.Callback holdCallback;

    private MediaJni mediaJni;
    private PlayerParam playerParam;

    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    private float degree = 270.0f;

    public VideoDisplayView(Context context) {
        super(context);
        init();
    }

    public VideoDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoDisplayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public VideoDisplayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    void init(){
        getHolder().addCallback(this);
//        initMetrix();
        playerParam = new PlayerParam();
//        playerParam.matrix = mMVPMatrix;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        MLog.d("Video display view  onAttachedToWindow");
        int angle = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();

        switch (angle) {
            case Surface.ROTATION_0:
                getLayoutParams().width = 480;
                getLayoutParams().height = 640;
                degree = 270.0f;
                break;
            case Surface.ROTATION_90:
                getLayoutParams().width = 640;
                getLayoutParams().height = 480;
                degree = 0.0f;
                break;
            case Surface.ROTATION_180:
                getLayoutParams().width = 480;
                getLayoutParams().height = 640;
                degree = 90.0f;
                break;
            case Surface.ROTATION_270:
                getLayoutParams().width = 640;
                getLayoutParams().height = 480;
                degree = 180.0f;
                break;
            default:
                break;
        }



        requestLayout();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        MLog.d("player surfaceCreated width : "+ getWidth()+ " , height : "+ getHeight());
        if (mediaJni != null && !TextUtils.isEmpty(playerParam.url)&& !TextUtils.isEmpty(playerParam.id)){
            playerParam.videoWidth = 640;
            playerParam.videoHeight = 480;
            playerParam.viewWidth = getWidth();
            playerParam.viewHeight = getHeight();

            initMetrix();


            mediaJni.createPlayer(playerParam.id);
            mediaJni.setVideoSurface(playerParam.id,holder.getSurface());
            mediaJni.setPlayerParam(playerParam.id,playerParam);
            mediaJni.startPlay(playerParam.id);
        }

        if (holdCallback != null){
            holdCallback.surfaceCreated(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        MLog.i("player  surfaceChanged width : "+ width+ " , height : "+ height);
        if (mediaJni != null && !TextUtils.isEmpty(playerParam.url)&& !TextUtils.isEmpty(playerParam.id)){
            if(playerParam.viewHeight != height || playerParam.viewWidth != width){
                playerParam.viewWidth = width;
                playerParam.viewHeight = height;
                initMetrix();
                mediaJni.setPlayerParam(playerParam.id,playerParam);
            }
        }


        if (holdCallback != null){
            holdCallback.surfaceChanged(holder, format,width,height);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        MLog.i("player  surfaceDestroyed");
        if (mediaJni != null && !TextUtils.isEmpty(playerParam.url)&& !TextUtils.isEmpty(playerParam.id)){
            mediaJni.stopPlay(playerParam.id);
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

    private void initMetrix(){
        int angle = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();

        switch (angle) {
            case Surface.ROTATION_0:

                degree = 270.0f;
                break;
            case Surface.ROTATION_90:
                degree = 0.0f;
                break;
            case Surface.ROTATION_180:

                degree = 90.0f;
                break;
            case Surface.ROTATION_270:

                degree = 180.0f;
                break;
            default:

                break;
        }


        float aspectRatio = playerParam.viewWidth > playerParam.viewHeight
                ? (float) playerParam.viewWidth / (float) playerParam.viewHeight
                : (float) playerParam.viewHeight / (float) playerParam.viewWidth;

        //正交投影
        float far = 5.0f;

        Matrix.orthoM(mProjectMatrix,0,-1.0f, 1.0f, -1, 1, 1.0f, far);
        MLog.d("-----------aspectRatio : "+aspectRatio);
//        if(playerParam.viewWidth > playerParam.viewHeight){
//            Matrix.orthoM(mProjectMatrix,0,-aspectRatio, aspectRatio, -1.0f, 1.0f, 1.0f, far);
//        }else {
////            Matrix.orthoM(mProjectMatrix,0,-1.0f, 1.0f, -aspectRatio, aspectRatio, 1.0f, far);
//            Matrix.orthoM(mProjectMatrix,0,-aspectRatio, aspectRatio, -1.0f, 1.0f, 1.0f, far);
//        }

        //透视投影
     /*   if(playerParam.viewWidth > playerParam.viewHeight){
            Matrix.frustumM(mProjectMatrix,0,-aspectRatio, aspectRatio, -1.0f, 1.0f, 1.0f, 2.0f);
        }else {
            Matrix.frustumM(mProjectMatrix,0,-1.0f, 1.0f, -aspectRatio, aspectRatio, 1.0f, 2.0f);
        }*/

//            Matrix.orthoM(mProjectMatrix,0,);
        Matrix.rotateM(mProjectMatrix,0,degree,0.0f,0.0f,1.0f);

        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, far, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);

        playerParam.matrix = mMVPMatrix;
    }

    public void setHoldCallback(SurfaceHolder.Callback holdCallback) {
        this.holdCallback = holdCallback;
    }

    public void setMediaJni(MediaJni mediaJni) {
        this.mediaJni = mediaJni;
    }

    public PlayerParam getPlayerParam() {
        return playerParam;
    }
}
