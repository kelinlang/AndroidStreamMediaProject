//
// Created by kelinlang on 2020/3/7.
//

#include "OpenSlAudioPlay.h"

AudioPlayer::AudioPlayer() {}

AudioPlayer::~AudioPlayer() {

}

void AudioPlayer::init() {
    LogI<<"init"<<endl;
    //第一步，创建引擎
    SLresult result;
    result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);

    //第二步，创建混音器
    const SLInterfaceID mids[1] = {SL_IID_ENVIRONMENTALREVERB};
    const SLboolean mreq[1] = {SL_BOOLEAN_FALSE};
    result = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1, mids, mreq);
    (void)result;
    result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    (void)result;
    result = (*outputMixObject)->GetInterface(outputMixObject, SL_IID_ENVIRONMENTALREVERB, &outputMixEnvironmentalReverb);
    if (SL_RESULT_SUCCESS == result) {
        result = (*outputMixEnvironmentalReverb)->SetEnvironmentalReverbProperties(
                outputMixEnvironmentalReverb, &reverbSettings);
        (void)result;
    }
    SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSnk = {&outputMix, NULL};


    // 第三步，配置PCM格式信息
    SLDataLocator_AndroidSimpleBufferQueue android_queue={SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,2};
    SLDataFormat_PCM pcm={
            SL_DATAFORMAT_PCM,//播放pcm格式的数据
            2,//2个声道（立体声）
            SL_SAMPLINGRATE_48,//44100hz的频率
            SL_PCMSAMPLEFORMAT_FIXED_16,//位数 16位
            SL_PCMSAMPLEFORMAT_FIXED_16,//和位数一致就行
            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,//立体声（前左前右）
            SL_BYTEORDER_LITTLEENDIAN//结束标志
    };
    SLDataSource slDataSource = {&android_queue, &pcm};


    const SLInterfaceID ids[3] = {SL_IID_BUFFERQUEUE, SL_IID_EFFECTSEND, SL_IID_VOLUME};
    const SLboolean req[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};

    result = (*engineEngine)->CreateAudioPlayer(engineEngine, &pcmPlayerObject,
            &slDataSource, &audioSnk, 3, ids, req);
    //初始化播放器
    (*pcmPlayerObject)->Realize(pcmPlayerObject, SL_BOOLEAN_FALSE);

//    得到接口后调用  获取Player接口
    (*pcmPlayerObject)->GetInterface(pcmPlayerObject, SL_IID_PLAY, &pcmPlayerPlay);

//    注册回调缓冲区 获取缓冲队列接口
    (*pcmPlayerObject)->GetInterface(pcmPlayerObject, SL_IID_BUFFERQUEUE, &pcmBufferQueue);


    //缓冲接口回调
    (*pcmBufferQueue)->RegisterCallback(pcmBufferQueue, pcmBufferCallBack, this);
//    获取音量接口
    (*pcmPlayerObject)->GetInterface(pcmPlayerObject, SL_IID_VOLUME, &pcmPlayerVolume);

//    获取播放状态接口
    (*pcmPlayerPlay)->SetPlayState(pcmPlayerPlay, SL_PLAYSTATE_PLAYING);

//    主动调用回调函数开始工作
    runFlag = true;

    uint8_t * data = (uint8_t *)malloc(1024);
    (*pcmBufferQueue)->Enqueue(pcmBufferQueue, data, 1024);
//    pcmBufferCallBack(pcmBufferQueue, this);

    LogT<<"--------init success----------" <<endl;
}

void AudioPlayer::start() {

}

void AudioPlayer::stop() {

}

void AudioPlayer::release() {
    if (pcmPlayerObject != NULL) {
        (*pcmPlayerObject)->Destroy(pcmPlayerObject);
        pcmPlayerObject = NULL;
        pcmPlayerPlay = NULL;
        pcmPlayerVolume = NULL;

    }

    // destroy output mix object, and invalidate all associated interfaces
    if (outputMixObject != NULL) {
        (*outputMixObject)->Destroy(outputMixObject);
        outputMixObject = NULL;
    }

    // destroy engine object, and invalidate all associated interfaces
    if (engineObject != NULL) {
        (*engineObject)->Destroy(engineObject);
        engineObject= NULL;
        engineEngine = NULL;
    }
}

void AudioPlayer::intputFrame(MediaFramePtr &mediaFramePtr) {
    if(runFlag){
//        LogT<<"--------intputFrame------1----" <<endl;
        mediaFrameQueue.pushBack(mediaFramePtr);
    }
}

void AudioPlayer::pcmBufferCallBack(SLAndroidSimpleBufferQueueItf bf, void *context) {
    LogT<<"--------pcmBufferCallBack------1----" <<endl;
    AudioPlayer * audioPlayer = static_cast<AudioPlayer*>(context);

    MediaFramePtr mf = audioPlayer->mediaFrameQueue.front();
    while (!mf){
        mf = audioPlayer->mediaFrameQueue.front();
    }

    if (mf && audioPlayer->runFlag) {
        MediaFrameImplPtr fMediaFrame = std::dynamic_pointer_cast<MediaFrameImpl>(mf);

        LogT<<"--------pcmBufferCallBack----------dataLen : "<<fMediaFrame->dataLen <<endl;
        uint8_t * data = (uint8_t *)malloc(fMediaFrame->dataLen);
        memcpy(data,fMediaFrame->data, fMediaFrame->dataLen);
        (*audioPlayer->pcmBufferQueue)->Enqueue(audioPlayer->pcmBufferQueue, data, fMediaFrame->dataLen);
//        fMediaFrame->data = nullptr;
//        fMediaFrame->dataLen = 0;
    }
}
