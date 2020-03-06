//
// Created by kelinlang on 2020/3/6.
//
#include <memory>
#include "log/log.h"
#include "com_stream_media_jni_MediaJni.h"
#include "push/AndroidPusher.h"


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_init
        (JNIEnv *env, jobject object) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_setMediaStatusCallback
        (JNIEnv *env, jobject object, jobject) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_setParam
        (JNIEnv *env, jobject object, jobject) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_release
        (JNIEnv *env, jobject object) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_resume
        (JNIEnv *env, jobject object) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_pause
        (JNIEnv *env, jobject object) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_createPlayer
        (JNIEnv *env, jobject object, jstring) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_setVideoSurface
        (JNIEnv *env, jobject object, jstring, jobject) {

}

extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_setVideoMatrix
        (JNIEnv *env, jobject object, jstring, jfloatArray) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_setPlayerParam
        (JNIEnv *env, jobject object, jstring, jobject) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_startPlay
        (JNIEnv *env, jobject object, jstring) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_stopPlay
        (JNIEnv *env, jobject object, jstring) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_setPullStreamParam
        (JNIEnv *env, jobject object, jstring, jobject) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_startPull
        (JNIEnv *env, jobject object, jstring) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_stopPull
        (JNIEnv *env, jobject object, jstring) {

}

static AndroidPusherPtr pusherPtr;
extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_createPushClient
        (JNIEnv *env, jobject object, jstring id) {
    LogI<<"create push-----------------------"<<endl;
    pusherPtr = std::make_shared<AndroidPusher>();
}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_setPushStreamParam
        (JNIEnv *env, jobject object, jstring id, jobject streamParam) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_startPush
        (JNIEnv *env, jobject object, jstring id) {

}

extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_stopPush
        (JNIEnv *env, jobject object, jstring id) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_sendVideoData
        (JNIEnv *env, jobject object, jstring id , jobject videoData) {

}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_sendAudioData
        (JNIEnv *env, jobject object, jstring, jobject audioData) {

}