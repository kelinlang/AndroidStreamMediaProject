//
// Created by kelinlang on 2020/3/3.
//

#ifndef STREAMMEDIAANDROIDLIB_ANDROIDVIDEODISPLAY_H
#define STREAMMEDIAANDROIDLIB_ANDROIDVIDEODISPLAY_H

#ifdef __cplusplus
extern "C" {
#endif

#include <sys/types.h>
#include <EGL/egl.h>
#include <malloc.h>
#include <pthread.h>
#include <android/native_window_jni.h>
#include <GLES2/gl2.h>
#include <memory.h>

#ifdef __cplusplus
}
#endif


#include <android/native_window.h>
#include "media/media_format.h"
#include "media/media_display.h"
#include "ffmpeg/ffmpeg_media_format.h"
#include "opengl/cloudvoice_shader_utils.h"




using namespace CommonLib;
using namespace StreamMedia::media;

namespace StreamMedia {
    namespace media {
#define GET_STR(x) #x
        const char *vertexShaderString = GET_STR(
                attribute vec4 aPosition;
                attribute vec2 aTexCoord;
                varying vec2 vTexCoord;

                uniform         mat4 um4_ModelViewProjection;

                void main() {
                    vTexCoord=vec2(aTexCoord.x,1.0-aTexCoord.y);
                    gl_Position = um4_ModelViewProjection *aPosition;
                }
        );
        const char *fragmentShaderString = GET_STR(
                precision mediump float;
                varying vec2 vTexCoord;
                uniform sampler2D yTexture;
                uniform sampler2D uTexture;
                uniform sampler2D vTexture;
                void main() {
                    vec3 yuv;
                    vec3 rgb;
                    yuv.r = texture2D(yTexture, vTexCoord).r;
                    yuv.g = texture2D(uTexture, vTexCoord).r - 0.5;
                    yuv.b = texture2D(vTexture, vTexCoord).r - 0.5;
                    rgb = mat3(1.164,  1.164,  1.164,
                               0.0,   -0.213,  2.112,
                               1.793, -0.533,  0.0) * yuv;
                    gl_FragColor = vec4(rgb, 1.0);
                }
        );

#define SM_GLES2_MAX_PLANE 3


        class DiaplayOpaque{
        public:
            //opengl相关
            float* cacheMatrixTmp;
            ANativeWindow *aNativeWindow;

            int pitches[SM_GLES2_MAX_PLANE]; /**< in bytes, Read-only */  //像素长度
            unsigned char* pixelsY;
            unsigned char* pixelsU;
            unsigned char* pixelsV;
        };
        using DiaplayOpaquePtr = std::shared_ptr<DiaplayOpaque>;

        class AndroidVideoDisplay :public  VideoDisplay{
        public:
            void init();
            void release();
            void start();
            void stop();

        protected:
            void parseYuvData(MediaFrameImplPtr& fMediaFrame);
        protected:
            DiaplayOpaquePtr displayOpaque;
        };
        using AndroidVideoDisplayPtr = std::shared_ptr<AndroidVideoDisplay>;

    }
}

#endif //STREAMMEDIAANDROIDLIB_ANDROIDVIDEODISPLAY_H
