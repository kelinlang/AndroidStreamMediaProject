package com.stream.media.camera;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

import android.util.Range;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.lib.commonlib.utils.DataJsonTranslation;
import com.lib.commonlib.utils.MLog;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2Impl implements CameraInterface {
    private Context mContext;

    private CameraParam cameraParam;
    private PreviewDataCallback previewDataCallback;

    private Surface previewSurface;
    private String curCameraId;
    private CameraDevice mCameraDevice;

    private ImageReader mImageReader;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;

    private HandlerThread mHandlerThread;
    private Handler mBackgroundHandler;

    public Camera2Impl(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setParam(CameraParam param) {
        cameraParam = param;
    }

    @Override
    public CameraParam getParam() {
        return cameraParam;
    }



    @Override
    public void open() {
        MLog.i("openCamera");
        startBackgroundThread();
        CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {

            String cameraId = cameraManager.getCameraIdList()[cameraParam.getCameraID()];

            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
            curCameraId = cameraId;
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(cameraId, mStateCallback, mBackgroundHandler);

                createImageReader();
            }else {
                MLog.i("not had camera permmsion");
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            MLog.e("CameraAccessException error ",e);
        }
    }

    private void createImageReader(){
        MLog.d("createImageReader");
        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(curCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Range[] ranges = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
            Size[] sizes = characteristics.get(CameraCharacteristics.JPEG_AVAILABLE_THUMBNAIL_SIZES);
            MLog.d("尺寸 : "+ DataJsonTranslation.objectToJson(sizes));

//            sizes = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_JPEG_SIZES);
//            MLog.d("尺寸 : "+ DataJsonTranslation.objectToJson(sizes));

            Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.YUV_420_888)), new CompareSizesByArea());

            mImageReader = ImageReader.newInstance(cameraParam.getYuvWidth(), cameraParam.getYuvHeight(), ImageFormat.YUV_420_888, /*maxImages*/10);
            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            MLog.e("",e);
        }
    }

    private ImageUtil mImageUtil = new ImageUtil();
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
//            MLog.i("onImageAvailable");

            Image image = reader.acquireNextImage();
            if (image != null){
                byte[] yuvData = mImageUtil.getBytesFromImageAsType(image,ImageUtil.YUV420P);

                if (previewDataCallback != null ){
//                    MLog.i("cur thread id : "+ Thread.currentThread().getId());
                    previewDataCallback.onData(ImageUtil.YUV420P,yuvData);
                }

                image.close();
            }else {
                MLog.i("image == null");
            }
        }
    };

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    @Override
    public void setSurface(Surface surface) {
        previewSurface = surface;
    }

    @Override
    public void setPreviewDataCallback(PreviewDataCallback previewDataCallback) {
        this.previewDataCallback = previewDataCallback;
    }


    private void createCameraPreviewSession(){
        if (previewSurface != null && mCameraDevice != null){
            try {
//                mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//                mPreviewRequestBuilder.addTarget(previewSurface);

                mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mPreviewRequestBuilder.addTarget(previewSurface);
                mPreviewRequestBuilder.addTarget(mImageReader.getSurface());

                mCameraDevice.createCaptureSession(Arrays.asList(previewSurface,mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        MLog.i("onConfigured");
                        if (null == mCameraDevice) {
                            return;
                        }

                        // When the session is ready, we start displaying the preview.
                        mCaptureSession = session;

                        try {
                            // Auto focus should be continuous for camera preview.
                            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//                            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);

//                            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
                            // Flash is automatically enabled when necessary.


                            // Finally, we start displaying the camera preview.
                            mPreviewRequest = mPreviewRequestBuilder.build();
                            mCaptureSession.setRepeatingRequest(mPreviewRequest, null, mBackgroundHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                            MLog.e("CameraAccessException error ",e);
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        MLog.i("onConfigureFailed");
                    }
                },mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                MLog.e("CameraAccessException error ",e);
            }
        }
    }

    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
            MLog.i("onCaptureProgressed");
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }
    };

    @Override
    public void startPreview() {
        createCameraPreviewSession();
    }

    @Override
    public void stopPreview() {
        if (mCaptureSession != null){
            try {
                mCaptureSession.stopRepeating();
            } catch (CameraAccessException e) {
                e.printStackTrace();
                MLog.e("stopPreview error ",e);
            }
        }
    }

    @Override
    public void close() {
        if (null != mCaptureSession) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (null != mImageReader) {
            mImageReader.close();
            mImageReader = null;
        }
        previewSurface = null;
        stopBackgroundThread();
    }



    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            MLog.i("onOpened");
            mCameraDevice = cameraDevice;
//            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            MLog.i("onDisconnected");
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            MLog.i("onError");
            cameraDevice.close();
            mCameraDevice = null;
        }

    };




    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mHandlerThread = new HandlerThread("CameraBackground");
        mHandlerThread.start();
        mBackgroundHandler = new Handler(mHandlerThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        if (mHandlerThread != null){
            mHandlerThread.quitSafely();
            try {
                mHandlerThread.join();
                mHandlerThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
