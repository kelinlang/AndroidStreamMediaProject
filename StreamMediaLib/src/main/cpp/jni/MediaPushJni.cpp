//
// Created by kelinlang on 2020/3/6.
//
#include <memory>
#include "log/log.h"
#include "com_stream_media_jni_MediaJni.h"
#include "push/AndroidPusher.h"
#include "media/media_format.h"


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
    LogI<<"-----------------setPushStreamParam----------------------"<<endl;
    if(pusherPtr){
        char* idString = (char*)env->GetStringUTFChars(id ,NULL);

        jclass streamParamClass = env->FindClass("com/stream/media/jni/StreamParam");
        jfieldID  jtype = env->GetFieldID(streamParamClass,"type","I");
        jfieldID  jUrl = env->GetFieldID(streamParamClass,"url","Ljava/lang/String;");

        int type = env->GetIntField(streamParam,jtype);
        jstring url = (jstring)env->GetObjectField(streamParam, jUrl);
        char *urlString = (char*)env->GetStringUTFChars(url ,NULL);
        pusherPtr->setUrl(urlString);
        pusherPtr->setUrl("rtmp://192.168.1.7:1935/live/test");
        pusherPtr->setOutFormat("flv");

        env->ReleaseStringUTFChars(url, urlString);
        env->ReleaseStringUTFChars(id, idString);
    }
}

extern "C" void Java_com_stream_media_jni_MediaJni_initPush(JNIEnv *, jobject, jstring) {
    LogI<<"-----------------initPush----------------------"<<endl;
    if(pusherPtr){
        pusherPtr->init();
    }
}

extern "C" void Java_com_stream_media_jni_MediaJni_releasePush(JNIEnv *, jobject, jstring) {
    LogI<<"-----------------releasePush----------------------"<<endl;
    if(pusherPtr){
        pusherPtr->release();
    }
}

extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_startPush
        (JNIEnv *env, jobject object, jstring id) {
    LogI<<"-----------------startPush----------------------"<<endl;
    if(pusherPtr){
        pusherPtr->start();
    }
}

extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_stopPush
        (JNIEnv *env, jobject object, jstring id) {
    LogI<<"-----------------stopPush----------------------"<<endl;
    if(pusherPtr){
        pusherPtr->stop();
    }
}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_sendVideoData
        (JNIEnv *env, jobject object, jstring id , jobject videoData) {
    if(pusherPtr){
        char* idString = (char*)env->GetStringUTFChars(id ,NULL);
        jboolean isCp = JNI_FALSE;

        jclass videoDataClass = env->FindClass("com/stream/media/jni/VideoData");
        jfieldID  jId = env->GetFieldID(videoDataClass,"id","Ljava/lang/String;");
        jfieldID  jDataFormat = env->GetFieldID(videoDataClass,"dataFormat","I");
        jfieldID  jwidth = env->GetFieldID(videoDataClass,"width","I");
        jfieldID  jheight = env->GetFieldID(videoDataClass,"height","I");
        jfieldID  jframeType = env->GetFieldID(videoDataClass,"frameType","I");
        jfieldID  jtimeStamp = env->GetFieldID(videoDataClass,"timeStamp","J");
        jfieldID  jvideoDataLen = env->GetFieldID(videoDataClass,"videoDataLen","I");
        jfieldID  jvideoData = env->GetFieldID(videoDataClass,"videoData","[B");

        jfieldID  jspsLen = env->GetFieldID(videoDataClass,"spsLen","I");
        jfieldID  jspsData = env->GetFieldID(videoDataClass,"sps","[B");
        jfieldID  jppsDataLen = env->GetFieldID(videoDataClass,"ppsLen","I");
        jfieldID  jppsData = env->GetFieldID(videoDataClass,"pps","[B");

        MediaFrameImplPtr avPacket = std::make_shared<MediaFrameImpl>();

        if (avPacket) {
            int type = env->GetIntField(videoData, jDataFormat);
            avPacket->packetFormat = VideoFormat(type);
            if (avPacket->packetFormat == VideoFormat::VDIEO_FORMAT_H264) {
                jstring id = (jstring) env->GetObjectField(videoData, jId);
                char *idString = (char *) env->GetStringUTFChars(id, NULL);
                env->ReleaseStringUTFChars(id, idString);

                avPacket->frameType = env->GetIntField(videoData, jframeType);
                avPacket->pts = env->GetLongField(videoData, jtimeStamp);
                avPacket->dts = env->GetLongField(videoData, jtimeStamp);
                avPacket->dataLen = env->GetIntField(videoData, jvideoDataLen);

                jbyteArray dataArray = (jbyteArray) env->GetObjectField(videoData, jvideoData);
                jbyte *buffer = env->GetByteArrayElements(dataArray, 0);
                avPacket->data = (uint8_t *) malloc(avPacket->dataLen);//后面再考虑重用内存
                memcpy(avPacket->data, buffer, avPacket->dataLen);
                env->ReleaseByteArrayElements(dataArray, buffer, 0);
//                cloudVoiceLogD("data len : %d",avPacket->dataLen);

            } else if (avPacket->packetFormat == VideoFormat::VDIEO_FORMAT_H264_SPS_PPS) {
                avPacket->spsLen = env->GetIntField(videoData, jspsLen);
                jbyteArray spsdataArray = (jbyteArray) env->GetObjectField(videoData, jspsData);
                unsigned char *sps = (unsigned char *) env->GetByteArrayElements(spsdataArray, 0);
                avPacket->sps = (uint8_t *) malloc(avPacket->spsLen);
                memcpy(avPacket->sps, sps, avPacket->spsLen);
                env->ReleaseByteArrayElements(spsdataArray, (jbyte *) sps, 0);

                avPacket->ppsLen = env->GetIntField(videoData, jppsDataLen);
                jbyteArray ppsdataArray = (jbyteArray) env->GetObjectField(videoData, jppsData);
                unsigned char *pps = (unsigned char *) env->GetByteArrayElements(ppsdataArray, 0);
                avPacket->pps = (uint8_t *) malloc(avPacket->ppsLen);
                memcpy(avPacket->pps, pps, avPacket->ppsLen);
                env->ReleaseByteArrayElements(ppsdataArray, (jbyte *) pps, 0);
            }
        }
        pusherPtr->inputMediaPacket(avPacket);
        env->ReleaseStringUTFChars(id, idString);
    }
}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_sendAudioData
        (JNIEnv *env, jobject object, jstring, jobject audioData) {

}


