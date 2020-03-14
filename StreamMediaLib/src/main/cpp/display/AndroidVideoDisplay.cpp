//
// Created by kelinlang on 2020/3/3.
//

#include "AndroidVideoDisplay.h"

GlWrapper::GlWrapper() {
   /* vertexData[0] = 1.0f;
    vertexData[1] = -1.0f;
    vertexData[2] = 0.0f;
    vertexData[3] = -1.0f;
    vertexData[4] = -1.0f;
    vertexData[5] = 0.0f;
    vertexData[6] = 1.0f;
    vertexData[7] = 1.0f;
    vertexData[8] = 1.0f;
    float vertexData[9]= {
            1.0f, -1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f
    };
    float textureVertexData[8]={
            1.0f, 0.0f,//右下
            0.0f, 0.0f,//左下
            1.0f, 1.0f,//右上
            0.0f, 1.0f//左上
    };*/
}

GlWrapper::~GlWrapper() {
    if(matrix){
        free(matrix);
    }
    if(pixelsY){
        free(pixelsY);
    }
    if(pixelsU){
        free(pixelsU);
    }
    if(pixelsV){
        free(pixelsV);
    }
}

int GlWrapper::init() {
    EGLint configSpec[] = { EGL_RED_SIZE, 8,
                            EGL_GREEN_SIZE, 8,
                            EGL_BLUE_SIZE, 8,
                            EGL_SURFACE_TYPE, EGL_WINDOW_BIT, EGL_NONE };


    eglDisp = eglGetDisplay(EGL_DEFAULT_DISPLAY);

    EGLint eglMajVers, eglMinVers;
    EGLint numConfigs;
    EGLBoolean ret;
    ret = eglInitialize(eglDisp, &eglMajVers, &eglMinVers);
//    LogD<<"eglInitialize ret : "<<ret<<endl;

    ret = eglChooseConfig(eglDisp, configSpec, &eglConf, 1, &numConfigs);
//    LogD<<"eglChooseConfig ret : "<<ret<<endl;

    eglWindow = eglCreateWindowSurface(eglDisp, eglConf,aNativeWindow, NULL);

    eglQuerySurface(eglDisp,eglWindow,EGL_WIDTH,&viewWidth);
    eglQuerySurface(eglDisp,eglWindow,EGL_HEIGHT,&viewHeight);
    const EGLint ctxAttr[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL_NONE
    };
    eglCtx = eglCreateContext(eglDisp, eglConf,EGL_NO_CONTEXT, ctxAttr);

    ret = eglMakeCurrent(eglDisp, eglWindow, eglWindow, eglCtx);
//    LogD<<"eglMakeCurrent ret : "<<ret<<endl;

    programId = ShaderUtils::createProgram(vertexShaderString,fragmentShaderString );

    aPositionHandle = (GLuint) glGetAttribLocation(programId, "aPosition");
    aTextureCoordHandle = (GLuint) glGetAttribLocation(programId, "aTexCoord");

    textureSamplerHandleY = (GLuint) glGetUniformLocation(programId, "yTexture");
    textureSamplerHandleU = (GLuint) glGetUniformLocation(programId, "uTexture");
    textureSamplerHandleV = (GLuint) glGetUniformLocation(programId, "vTexture");
    um4_mvp = glGetUniformLocation(programId,"um4_ModelViewProjection");

    return 0;
}

void GlWrapper::release() {
    LogD<<"display 3"<<endl;
    eglMakeCurrent(eglDisp, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    eglDestroyContext(eglDisp, eglCtx);
    eglDestroySurface(eglDisp, eglWindow);
    eglTerminate(eglDisp);
    eglDisp = EGL_NO_DISPLAY;
    eglWindow = EGL_NO_SURFACE;
    eglCtx = EGL_NO_CONTEXT;

    LogD<<"display thread  finish"<<endl;
}

void GlWrapper::draw(uint8_t *yuvData) {
    parseYuvData(yuvData);

    glViewport(left, top, viewWidth, viewHeight);
    checkGlError("glViewport");

    /***
    * 纹理更新完成后开始绘制
    ***/
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    checkGlError("glClear");

    glUseProgram(programId);
    glEnableVertexAttribArray(aPositionHandle);
    glVertexAttribPointer(aPositionHandle, 3, GL_FLOAT, GL_FALSE,
                          12, vertexData);
    glEnableVertexAttribArray(aTextureCoordHandle);
    glVertexAttribPointer(aTextureCoordHandle,2,GL_FLOAT,GL_FALSE,8,textureVertexData);

    /***
     * 初始化空的yuv纹理
     * **/

    glGenTextures(1,&yTextureId);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D,yTextureId);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glUniform1i(textureSamplerHandleY,0);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE,
                 videoWidth,videoHeight,
                 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, pixelsY);

    glGenTextures(1,&uTextureId);
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D,uTextureId);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glUniform1i(textureSamplerHandleU,1);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE,
                 videoWidth/2, videoHeight/2,
                 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, pixelsU);


    glGenTextures(1,&vTextureId);
    glActiveTexture(GL_TEXTURE2);
    glBindTexture(GL_TEXTURE_2D,vTextureId);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glUniform1i(textureSamplerHandleV,2);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE,
                 videoWidth/2, videoHeight/2,
                 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, pixelsV);

    glUniformMatrix4fv(um4_mvp, 1, GL_FALSE, matrix);


    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    checkGlError("glDrawArrays");

    eglSwapBuffers(eglDisp, eglWindow);
}

void GlWrapper::parseYuvData(uint8_t *yuvData) {
    pitches[0] = videoWidth * videoHeight;
    pitches[1] = videoWidth * videoHeight / 4;
    pitches[2] = videoWidth * videoHeight / 4;


    if (pixelsY == nullptr) {
        //如果没有存储空间，则分配
        pixelsY = (unsigned char *) malloc(pitches[0]);
    }

    if (pixelsU == nullptr) {
        //如果没有存储空间，则分配
        pixelsU = (unsigned char *) malloc(pitches[1]);
    }

    if (pixelsV == nullptr) {
        //如果没有存储空间，则分配
        pixelsV = (unsigned char *) malloc(pitches[2]);
    }

    //复制数据
    memcpy(pixelsY, yuvData, pitches[0]);
    memcpy(pixelsU, yuvData + pitches[0], pitches[1]);
    memcpy(pixelsV, yuvData + pitches[0]+pitches[1],pitches[2]);
}

void GlWrapper::checkEglError(const char *op, EGLBoolean returnVal) {
    if(returnVal != EGL_TRUE)
    {
        LogE<<op<<" error : "<<returnVal<<endl;
    }

    for(EGLint error = eglGetError(); error != EGL_SUCCESS; error
                                                                    = eglGetError())
    {


    }
}

void GlWrapper::checkGlError(const char *op) {
    for(GLint error = glGetError(); error; error
                                                   = glGetError())
    {
        LogE<<op<<" error : "<<error<<endl;
    }
}


void AndroidVideoDisplay::init() {

}

void AndroidVideoDisplay::release() {

}

void AndroidVideoDisplay::start() {
    displayThread = std::thread([this] {
        LogI<<"display thread start"<<endl;
        if(!videoDisplayParamPtr){
            LogE<<"no dipplay param error"<<endl;
            return;
        }

        glWrapper.matrix = (float *) malloc(videoDisplayParamPtr->matrixLen);
        memcpy(glWrapper.matrix,videoDisplayParamPtr->matrix,videoDisplayParamPtr->matrixLen);
        glWrapper.videoWidth = videoDisplayParamPtr->videoWidth;
        glWrapper.videoHeight = videoDisplayParamPtr->videoHeight;
        glWrapper.init();

        runFlag = true;

   /*     while (runFlag) {
            MediaFramePtr mf = mediaFrameQueue.front();
            if (mf) {
                MediaFrameImplPtr fMediaFrame = std::dynamic_pointer_cast<MediaFrameImpl>(mf);
                glWrapper.draw(fMediaFrame->data);
            }
        }*/
        while (runFlag) {
            if(mediaFrameQueuePtr->remainNumFrame() > 1){
                mediaFrameQueuePtr->next();//丢弃一帧
            } else{
                av_usleep(10*100);
            }
        }

        double remainingTime = 0.0;
        while (runFlag) {
            if(remainingTime > 0){
                av_usleep((int64_t)(remainingTime * 1000000.0));
            }
            remainingTime = 0.01;//10ms
            double time;//当前系统平台的时间，单位秒

            if(mediaFrameQueuePtr->remainNumFrame() == 0){
                if(mediaFrameQueuePtr->isShown()){
                    MediaFrameImpl* mf = mediaFrameQueuePtr->peekLast();
                    glWrapper.draw(mf->data);
                } else{
                    LogT<<"video displa sleep 10 ms"<<endl;
                    av_usleep(10*1000);
                }
            } else{
                double lastDuration, duration, delay;

                MediaFrameImpl* lastFrame = mediaFrameQueuePtr->peekLast();
                MediaFrameImpl* curFrame = mediaFrameQueuePtr->peek();

                if(curFrame->serial != clockManagerPtr->getVideoQueueSerial()){
                    mediaFrameQueuePtr->next();//不同序列的直接丢弃
                    remainingTime = 0.0;
                    continue;
                }
                if(lastFrame->serial != curFrame->serial ){
                    clockManagerPtr->refreshVideoFrameTimer();
                }

                lastDuration = clockManagerPtr->videoDuration(lastFrame,curFrame);
                delay = clockManagerPtr->computeVideoDelay(lastDuration);

                time= av_gettime_relative()/1000000.0;
                // 当前帧播放时刻(is->frame_timer+delay)大于当前时刻(time)，表示播放时刻未到，播放线程休眠remaining_time
                if(time < clockManagerPtr->getVideoFrameTimer() + delay){
                    remainingTime = FFMIN(clockManagerPtr->getVideoFrameTimer() + delay - time, remainingTime);
                    glWrapper.draw(mediaFrameQueuePtr->peekLast()->data);//再播一次之前显示的
                    continue;
                }
                clockManagerPtr->updateVideoFrameTimer(delay);
                if (delay > 0 && time - clockManagerPtr->getVideoFrameTimer() > AV_SYNC_THRESHOLD_MAX){
                    clockManagerPtr->refreshVideoFrameTimer();
                }
                if(!isnan(curFrame->printTimeStamp)){
                    clockManagerPtr->syncVideoTime(curFrame);
                }

                if(mediaFrameQueuePtr->remainNumFrame() > 1){
                    MediaFrameImpl* nextFrame = mediaFrameQueuePtr->peekNext();
                    duration = clockManagerPtr->videoDuration(curFrame,nextFrame);
                    if(time >clockManagerPtr->getVideoFrameTimer() +duration)
                    {
                        mediaFrameQueuePtr->next();//丢弃一帧
                        LogT<<"drop video frame"<<endl;
                        remainingTime = 0.0;//设置为0，里面重新循环可能再次丢帧
                        continue;
                    }
                } else{
                    glWrapper.draw(curFrame->data);//显示当前帧
                    mediaFrameQueuePtr->next();
                    break;
                }
            }
        }


        glWrapper.release();

        LogD<<"display thread  finish  2"<<endl;
    });
}

void AndroidVideoDisplay::stop() {
    uniqueLock(mtx);
    if (runFlag)
    {
        LogD << " close start" << endl;
        runFlag = false;
        mediaFrameQueue.clear();
        displayThread.join();

        LogD << " close finish" << endl;
    }
}
