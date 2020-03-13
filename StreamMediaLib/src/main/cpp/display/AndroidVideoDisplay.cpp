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
    LogD<<"eglInitialize ret : "<<ret<<endl;

    ret = eglChooseConfig(eglDisp, configSpec, &eglConf, 1, &numConfigs);
    LogD<<"eglChooseConfig ret : "<<ret<<endl;

    eglWindow = eglCreateWindowSurface(eglDisp, eglConf,aNativeWindow, NULL);

    eglQuerySurface(eglDisp,eglWindow,EGL_WIDTH,&viewWidth);
    eglQuerySurface(eglDisp,eglWindow,EGL_HEIGHT,&viewHeight);
    const EGLint ctxAttr[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL_NONE
    };
    eglCtx = eglCreateContext(eglDisp, eglConf,EGL_NO_CONTEXT, ctxAttr);

    ret = eglMakeCurrent(eglDisp, eglWindow, eglWindow, eglCtx);
    LogD<<"eglMakeCurrent ret : "<<ret<<endl;

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
    LogD<<"3 left : "<<left<<" ,top : "<<top<<" , viewWidth : "<<viewWidth<<" ,viewHeight :  "<<viewHeight<<endl;

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


    glGenTextures(1,&uTextureId);
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D,uTextureId);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glUniform1i(textureSamplerHandleU,1);

    glGenTextures(1,&vTextureId);
    glActiveTexture(GL_TEXTURE2);
    glBindTexture(GL_TEXTURE_2D,vTextureId);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glUniform1i(textureSamplerHandleV,2);


    glUniformMatrix4fv(um4_mvp, 1, GL_FALSE, matrix);




    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, yTextureId);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE,
                 videoWidth,videoHeight,
                 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, pixelsY);

    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, uTextureId);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE,
                 videoWidth/2, videoHeight/2,
                 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, pixelsU);

    glActiveTexture(GL_TEXTURE2);
    glBindTexture(GL_TEXTURE_2D, vTextureId);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE,
                 videoWidth/2, videoHeight/2,
                 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, pixelsV);

    /***
    * 纹理更新完成后开始绘制
    ***/
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    checkGlError("glClear");
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
        while (runFlag) {

            MediaFramePtr mf = mediaFrameQueue.front();
            if (mf) {
                MediaFrameImplPtr fMediaFrame = std::dynamic_pointer_cast<MediaFrameImpl>(mf);
                glWrapper.draw(fMediaFrame->data);
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
