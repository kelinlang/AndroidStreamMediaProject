//
// Created by kelinlang on 2020/3/3.
//

#include "com_stream_media_jni_MediaPlayerJni.h"
#include "player/AndroidPlayer.h"
#include "log/log.h"
#ifdef __cplusplus
extern "C" {
#endif

#include "log/cloudvoice_android_log.h"

#ifdef __cplusplus
}
#endif

using namespace StreamMedia::media;
using namespace std;

static AndroidPlayerPtr androidPlayPtr;

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

    Log::getInstance()->add(std::dynamic_pointer_cast<LogChannel>(std::make_shared<ConsoleChannel>()));
    Log::getInstance()->setLevel(LogLevel::LTrace);
    Log::getInstance()->setTag("StreamMedia");
    //此处可以动态注册本地方法
    FFmpeg::ffmpegInit();
    LogInit();

    LogI<<"---------------------JNI_OnLoad finish------------------------"<<endl;
    return JNI_VERSION_1_6;
}

extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaPlayerJni_create
        (JNIEnv *env, jobject object) {
    LogI<<"create"<<endl;
    androidPlayPtr= std::make_shared<AndroidPlayer>();
}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaPlayerJni_setVideoSurface
        (JNIEnv *env, jobject object, jobject surface) {
    LogI<<"setVideoSurface"<<endl;
    androidPlayPtr->videoDisplayPtr->displayOpaque->aNativeWindow = ANativeWindow_fromSurface(env,surface);
}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaPlayerJni_setParam
        (JNIEnv *env, jobject object, jobject playerParam) {
    LogI<<"setParam"<<endl;
    jclass playerParamClass = env->FindClass("com/stream/media/jni/PlayerParam");

    jfieldID  jviewWidth = env->GetFieldID(playerParamClass,"viewWidth","I");
    jfieldID  jviewHeight = env->GetFieldID(playerParamClass,"viewHeight","I");
    jfieldID  jgravity= env->GetFieldID(playerParamClass,"gravity","I");
    jfieldID  jdataFormat= env->GetFieldID(playerParamClass,"dataFormat","I");
    jfieldID  jdisplayFormat= env->GetFieldID(playerParamClass,"displayFormat","I");
    jfieldID  jvideoWidth= env->GetFieldID(playerParamClass,"videoWidth","I");
    jfieldID  jvideoHeight= env->GetFieldID(playerParamClass,"videoHeight","I");
    jfieldID  jUrl = env->GetFieldID(playerParamClass,"url","Ljava/lang/String;");
    jfieldID  jMatrix = env->GetFieldID(playerParamClass,"matrix","[F");

    int viewWidth = env->GetIntField(playerParam,jviewWidth);
    int viewHeight = env->GetIntField(playerParam,jviewHeight);
    int gravity = env->GetIntField(playerParam,jgravity);
    int dataFormat = env->GetIntField(playerParam,jdataFormat);
    int displayFormat = env->GetIntField(playerParam,jdisplayFormat);
    int videoWidth = env->GetIntField(playerParam,jvideoWidth);
    int videoHeight = env->GetIntField(playerParam,jvideoHeight);

//    jstring url = (jstring)env->GetObjectField(playerParam, jUrl);
    LogI<<"GetObjectField"<<endl;
//    char * urlString = (char*)env->GetStringUTFChars(url , NULL);

    jfloatArray  dataArray = (jfloatArray)env->GetObjectField(playerParam,jMatrix);
    jfloat* buffer = env->GetFloatArrayElements(dataArray, 0);
    int matrixLen = env->GetArrayLength(dataArray)* sizeof(float);
    float* matrix = (float *) malloc(matrixLen);//后面再考虑重用内存
    memcpy(matrix,buffer,matrixLen);
    env->ReleaseFloatArrayElements(dataArray, buffer, 0);

    VideoDisplayParamPtr playerParam1 = std::make_shared<VideoDisplayParam>();

    if (playerParam1){
        playerParam1->viewWidth = viewWidth;
        playerParam1->viewHeight = viewHeight;
        playerParam1->gravity = gravity;
        playerParam1->dataFormat = dataFormat;
        playerParam1->displayFormat = displayFormat;
        playerParam1->videoWidth = videoWidth;
        playerParam1->videoHeight = videoHeight;

        playerParam1->matrixLen = matrixLen;
        playerParam1->matrix = matrix;

//        playerParam1->url = urlString;
        playerParam1->url = "/storage/emulated/0/E1.mp4";
        playerParam1->url = "rtmp://192.168.1.7:1935/live/test";
        androidPlayPtr->videoDisplayParamPtr = playerParam1;
    }
}

extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaPlayerJni_init
        (JNIEnv *env, jobject object) {
    LogI<<"----init-----"<<endl;
    androidPlayPtr->init();
}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaPlayerJni_release
        (JNIEnv *env, jobject object) {
    LogI<<"----release-----"<<endl;
    androidPlayPtr->release();
}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaPlayerJni_start
        (JNIEnv *env, jobject object) {
    LogI<<"----start-----"<<endl;
    androidPlayPtr->start();
}


extern "C" JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaPlayerJni_stop
        (JNIEnv *env, jobject object) {
    LogI<<"----stop-----"<<endl;
    androidPlayPtr->stop();
}