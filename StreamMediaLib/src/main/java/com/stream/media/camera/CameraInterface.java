package com.stream.media.camera;

import android.view.Surface;

public interface CameraInterface {
    void setParam(CameraParam param);
    CameraParam getParam();

    void open();

    void setSurface(Surface surface);
    void setPreviewDataCallback(PreviewDataCallback previewDataCallback);
    void startPreview();
    void stopPreview();



    void close();
}
