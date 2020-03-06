//
// Created by kelinlang on 2020/3/3.
//

#include "AndroidVideoDisplay.h"


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
        /**
    *初始化egl
    **/

        EGLConfig eglConf;
        EGLSurface eglWindow;
        EGLContext eglCtx;
        int windowWidth;
        int windowHeight;
//    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);

        EGLint configSpec[] = { EGL_RED_SIZE, 8,
                                EGL_GREEN_SIZE, 8,
                                EGL_BLUE_SIZE, 8,
                                EGL_SURFACE_TYPE, EGL_WINDOW_BIT, EGL_NONE };

        EGLDisplay eglDisp = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        EGLint eglMajVers, eglMinVers;
        EGLint numConfigs;
        eglInitialize(eglDisp, &eglMajVers, &eglMinVers);
        eglChooseConfig(eglDisp, configSpec, &eglConf, 1, &numConfigs);

        eglWindow = eglCreateWindowSurface(eglDisp, eglConf,displayOpaque->aNativeWindow, NULL);

        eglQuerySurface(eglDisp,eglWindow,EGL_WIDTH,&windowWidth);
        eglQuerySurface(eglDisp,eglWindow,EGL_HEIGHT,&windowHeight);
        const EGLint ctxAttr[] = {
                EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL_NONE
        };
        eglCtx = eglCreateContext(eglDisp, eglConf,EGL_NO_CONTEXT, ctxAttr);

        eglMakeCurrent(eglDisp, eglWindow, eglWindow, eglCtx);


        /**
         * 设置opengl 要在egl初始化后进行
         * **/
        float vertexData[] = {
                1.0f, -1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f
        };

        float textureVertexData[] ={
                1.0f, 0.0f,//右下
                0.0f, 0.0f,//左下
                1.0f, 1.0f,//右上
                0.0f, 1.0f//左上
        };

        const GLfloat g_bt709[] = {
                1.164,  1.164,  1.164,
                0.0,   -0.213,  2.112,
                1.793, -0.533,  0.0,
        };

//    ShaderUtils *shaderUtils = new ShaderUtils();

        GLuint programId = ShaderUtils::createProgram(vertexShaderString,fragmentShaderString );

        GLuint aPositionHandle = (GLuint) glGetAttribLocation(programId, "aPosition");
        GLuint aTextureCoordHandle = (GLuint) glGetAttribLocation(programId, "aTexCoord");

        GLuint textureSamplerHandleY = (GLuint) glGetUniformLocation(programId, "yTexture");
        GLuint textureSamplerHandleU = (GLuint) glGetUniformLocation(programId, "uTexture");
        GLuint textureSamplerHandleV = (GLuint) glGetUniformLocation(programId, "vTexture");
        GLuint um4_mvp = glGetUniformLocation(programId,"um4_ModelViewProjection");


        //因为没有用矩阵所以就手动自适应
        int videoWidth = videoDisplayParamPtr->videoWidth;
        int videoHeight = videoDisplayParamPtr->videoHeight;

        int left,top,viewWidth,viewHeight;
        viewWidth = windowWidth;
        viewHeight = windowHeight;

        glViewport(left, top, viewWidth, viewHeight);
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
        GLuint yTextureId;
        GLuint uTextureId;
        GLuint vTextureId;

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

//    glUniformMatrix3fv(um4_mvp, 1, GL_FALSE, SM_GLES2_getColorMatrix_bt709());
        glUniformMatrix4fv(um4_mvp, 1, GL_FALSE, videoDisplayParamPtr->matrix);

        /***
         * 开始解码
         * **/
        int ret;
//        LogD<<"display 2"<<endl;
        int tmpWidth,tmpHeight;
        runFlag = true;
        while (runFlag) {
            if(viewWidth != videoDisplayParamPtr->viewWidth || viewHeight != videoDisplayParamPtr->viewHeight){
                viewWidth = videoDisplayParamPtr->viewWidth;
                viewHeight = videoDisplayParamPtr->viewHeight;
                LogD<<"4 left : "<<left<<" ,top : "<<top<<" , viewWidth : "<<viewWidth<<" ,viewHeight :  "<<viewHeight<<endl;

                glViewport(0, 0, viewWidth, viewHeight);
            }
            glUniformMatrix4fv(um4_mvp, 1, GL_FALSE, videoDisplayParamPtr->matrix);

            MediaFramePtr mf = mediaFrameQueue.front();
            if (mf) {
                MediaFrameImplPtr fMediaFrame = std::dynamic_pointer_cast<MediaFrameImpl>(mf);

//                LogD<<"display loop ------------ 2"<<endl;
                parseYuvData(fMediaFrame);

                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, yTextureId);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE,
                             videoDisplayParamPtr->videoWidth, videoDisplayParamPtr->videoHeight,
                             0, GL_LUMINANCE, GL_UNSIGNED_BYTE, displayOpaque->pixelsY);

                glActiveTexture(GL_TEXTURE1);
                glBindTexture(GL_TEXTURE_2D, uTextureId);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE,
                             videoDisplayParamPtr->videoWidth/2, videoDisplayParamPtr->videoHeight/2,
                             0, GL_LUMINANCE, GL_UNSIGNED_BYTE, displayOpaque->pixelsU);

                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, vTextureId);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE,
                             videoDisplayParamPtr->videoWidth/2, videoDisplayParamPtr->videoHeight/2,
                             0, GL_LUMINANCE, GL_UNSIGNED_BYTE, displayOpaque->pixelsV);


                /***
                * 纹理更新完成后开始绘制
                ***/
                glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

                glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

                eglSwapBuffers(eglDisp, eglWindow);
            }

//        cloudVoiceLogD("display loop  destroy avpackect");
        }
        LogD<<"display 3"<<endl;
        eglMakeCurrent(eglDisp, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
        eglDestroyContext(eglDisp, eglCtx);
        eglDestroySurface(eglDisp, eglWindow);
        eglTerminate(eglDisp);
        eglDisp = EGL_NO_DISPLAY;
        eglWindow = EGL_NO_SURFACE;
        eglCtx = EGL_NO_CONTEXT;

        LogD<<"display thread  finish"<<endl;



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

void AndroidVideoDisplay::parseYuvData(MediaFrameImplPtr &fMediaFrame) {
    displayOpaque->pitches[0] = videoDisplayParamPtr->videoWidth * videoDisplayParamPtr->videoHeight;
    displayOpaque->pitches[1] = videoDisplayParamPtr->videoWidth * videoDisplayParamPtr->videoHeight / 4;
    displayOpaque->pitches[2] = videoDisplayParamPtr->videoWidth * videoDisplayParamPtr->videoHeight / 4;


    if (displayOpaque->pixelsY == NULL) {
        //如果没有存储空间，则分配
        displayOpaque->pixelsY = (unsigned char *) malloc(displayOpaque->pitches[0]);
    }

    if (displayOpaque->pixelsU == NULL) {
        //如果没有存储空间，则分配
        displayOpaque->pixelsU = (unsigned char *) malloc(displayOpaque->pitches[1]);
    }

    if (displayOpaque->pixelsV == NULL) {
        //如果没有存储空间，则分配
        displayOpaque->pixelsV = (unsigned char *) malloc(displayOpaque->pitches[2]);
    }

    //复制数据
    memcpy(displayOpaque->pixelsY, fMediaFrame->data, displayOpaque->pitches[0]);
    memcpy(displayOpaque->pixelsU, fMediaFrame->data + displayOpaque->pitches[0], displayOpaque->pitches[1]);
    memcpy(displayOpaque->pixelsV, fMediaFrame->data + displayOpaque->pitches[0] + displayOpaque->pitches[1], displayOpaque->pitches[2]);
}
