//
// Created by kelinlang on 2020/3/3.
//

#include "com_kelinlang_stream_media_lib_jni_MediaPlayerJni.h"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
//    cloudVoiceAndroidLogInit();
//    cloudVoiceLogI("---------------------JNI_OnLoad start------------------------");

    int retval;
    JNIEnv* env = NULL;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_6) != JNI_OK) {
//        cloudVoiceLogI("---------------------JNI_OnLoad error------------------------");
        return JNI_ERR;
    }


    //此处可以动态注册本地方法

//    cloudVoiceLogI("---------------------JNI_OnLoad finish------------------------");

    return JNI_VERSION_1_6;
}

extern "C" JNIEXPORT void JNICALL Java_com_kelinlang_stream_media_lib_jni_MediaPlayerJni_create
        (JNIEnv *env, jobject object) {

}


extern "C" JNIEXPORT void JNICALL Java_com_kelinlang_stream_media_lib_jni_MediaPlayerJni_setVideoSurface
        (JNIEnv *env, jobject object, jobject surface) {

}


extern "C" JNIEXPORT void JNICALL Java_com_kelinlang_stream_media_lib_jni_MediaPlayerJni_setParam
        (JNIEnv *env, jobject, jobject) {

}

extern "C" JNIEXPORT void JNICALL Java_com_kelinlang_stream_media_lib_jni_MediaPlayerJni_init
        (JNIEnv *env, jobject object) {

}


extern "C" JNIEXPORT void JNICALL Java_com_kelinlang_stream_media_lib_jni_MediaPlayerJni_release
        (JNIEnv *env, jobject object) {

}


extern "C" JNIEXPORT void JNICALL Java_com_kelinlang_stream_media_lib_jni_MediaPlayerJni_start
        (JNIEnv *env, jobject object) {

}


extern "C" JNIEXPORT void JNICALL Java_com_kelinlang_stream_media_lib_jni_MediaPlayerJni_stop
        (JNIEnv *env, jobject object) {

}