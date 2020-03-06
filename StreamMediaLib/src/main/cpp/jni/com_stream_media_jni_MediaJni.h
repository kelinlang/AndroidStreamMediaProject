/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_stream_media_jni_MediaJni */

#ifndef _Included_com_stream_media_jni_MediaJni
#define _Included_com_stream_media_jni_MediaJni
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_init
  (JNIEnv *, jobject);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    setMediaStatusCallback
 * Signature: (Lcom/stream/media/jni/MediaStatusCallback;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_setMediaStatusCallback
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    setParam
 * Signature: (Lcom/stream/media/jni/MediaParam;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_setParam
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    release
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_release
  (JNIEnv *, jobject);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    resume
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_resume
  (JNIEnv *, jobject);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    pause
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_pause
  (JNIEnv *, jobject);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    createPlayer
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_createPlayer
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    setVideoSurface
 * Signature: (Ljava/lang/String;Landroid/view/Surface;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_setVideoSurface
  (JNIEnv *, jobject, jstring, jobject);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    setVideoMatrix
 * Signature: (Ljava/lang/String;[F)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_setVideoMatrix
  (JNIEnv *, jobject, jstring, jfloatArray);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    setPlayerParam
 * Signature: (Ljava/lang/String;Lcom/stream/media/jni/PlayerParam;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_setPlayerParam
  (JNIEnv *, jobject, jstring, jobject);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    startPlay
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_startPlay
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    stopPlay
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_stopPlay
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    setPullStreamParam
 * Signature: (Ljava/lang/String;Lcom/stream/media/jni/StreamParam;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_setPullStreamParam
  (JNIEnv *, jobject, jstring, jobject);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    startPull
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_startPull
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    stopPull
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_stopPull
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    createPushClient
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_createPushClient
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    setPushStreamParam
 * Signature: (Ljava/lang/String;Lcom/stream/media/jni/StreamParam;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_setPushStreamParam
  (JNIEnv *, jobject, jstring, jobject);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    startPush
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_startPush
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    stopPush
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_stopPush
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    sendVideoData
 * Signature: (Ljava/lang/String;Lcom/stream/media/jni/VideoData;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_sendVideoData
  (JNIEnv *, jobject, jstring, jobject);

/*
 * Class:     com_stream_media_jni_MediaJni
 * Method:    sendAudioData
 * Signature: (Ljava/lang/String;Lcom/stream/media/jni/AudioData;)V
 */
JNIEXPORT void JNICALL Java_com_stream_media_jni_MediaJni_sendAudioData
  (JNIEnv *, jobject, jstring, jobject);

#ifdef __cplusplus
}
#endif
#endif
