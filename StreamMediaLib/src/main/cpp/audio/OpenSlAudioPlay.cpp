//
// Created by kelinlang on 2020/3/7.
//

#include "OpenSlAudioPlay.h"

AudioPlayer::AudioPlayer() {}

AudioPlayer::~AudioPlayer() {

}

void AudioPlayer::init() {
    LogI<<"init"<<endl;
    //��һ������������
    SLresult result;
    result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);

    //�ڶ���������������
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


    // ������������PCM��ʽ��Ϣ
    SLDataLocator_AndroidSimpleBufferQueue android_queue={SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,2};
    SLDataFormat_PCM pcm={
            SL_DATAFORMAT_PCM,//����pcm��ʽ������
            2,//2����������������
            SL_SAMPLINGRATE_48,//44100hz��Ƶ��
            SL_PCMSAMPLEFORMAT_FIXED_16,//λ�� 16λ
            SL_PCMSAMPLEFORMAT_FIXED_16,//��λ��һ�¾���
            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,//��������ǰ��ǰ�ң�
            SL_BYTEORDER_LITTLEENDIAN//������־
    };
    SLDataSource slDataSource = {&android_queue, &pcm};


    const SLInterfaceID ids[3] = {SL_IID_BUFFERQUEUE, SL_IID_EFFECTSEND, SL_IID_VOLUME};
    const SLboolean req[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};

    result = (*engineEngine)->CreateAudioPlayer(engineEngine, &pcmPlayerObject,
            &slDataSource, &audioSnk, 3, ids, req);
    //��ʼ��������
    (*pcmPlayerObject)->Realize(pcmPlayerObject, SL_BOOLEAN_FALSE);

//    �õ��ӿں����  ��ȡPlayer�ӿ�
    (*pcmPlayerObject)->GetInterface(pcmPlayerObject, SL_IID_PLAY, &pcmPlayerPlay);

//    ע��ص������� ��ȡ������нӿ�
    (*pcmPlayerObject)->GetInterface(pcmPlayerObject, SL_IID_BUFFERQUEUE, &pcmBufferQueue);


    //����ӿڻص�
    (*pcmBufferQueue)->RegisterCallback(pcmBufferQueue, pcmBufferCallBack, this);
//    ��ȡ�����ӿ�
    (*pcmPlayerObject)->GetInterface(pcmPlayerObject, SL_IID_VOLUME, &pcmPlayerVolume);

//    ��ȡ����״̬�ӿ�
    (*pcmPlayerPlay)->SetPlayState(pcmPlayerPlay, SL_PLAYSTATE_PLAYING);

//    �������ûص�������ʼ����
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
