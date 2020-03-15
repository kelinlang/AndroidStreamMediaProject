//
// Created by kelinlang on 2020/3/7.
//

#include "OpenSlAudioPlay.h"

AudioPlayer::AudioPlayer() {}

AudioPlayer::~AudioPlayer() {

}

void AudioPlayer::init() {
//    LogI<<"init"<<endl;
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
    SLDataLocator_AndroidSimpleBufferQueue android_queue={SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,10};
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

    result = ( * pcmPlayerObject) -> GetInterface(pcmPlayerObject,SL_IID_ANDROIDCONFIGURATION, &playerConfig);
    if (result != SL_RESULT_SUCCESS) {
        LogI<<"config GetInterface failed with result : "<<result<<endl;

    }

    //����ӿڻص�
    (*pcmBufferQueue)->RegisterCallback(pcmBufferQueue, pcmBufferCallBack, this);
//    ��ȡ�����ӿ�
    (*pcmPlayerObject)->GetInterface(pcmPlayerObject, SL_IID_VOLUME, &pcmPlayerVolume);

    internalQueuePtr = std::make_shared<MediaFrameQueue>(DEVICE_SHADOW_BUFFER_QUEUE_LEN);

   /* uint8_t * data = (uint8_t *)malloc(1024);
    (*pcmBufferQueue)->Enqueue(pcmBufferQueue, data, 1024);*/
//    pcmBufferCallBack(pcmBufferQueue, this);

    LogT<<"--------init success----------" <<endl;
//    fileSaver.open();
}

void AudioPlayer::start() {
//    ��ȡ����״̬�ӿ�


    slientMediaFramePtr = new MediaFrameImpl();
    slientMediaFramePtr->silentFlag = true;

    slientMediaFramePtr->dataLen = 1024*2*2;
    slientMediaFramePtr->data = (uint8_t *)malloc(slientMediaFramePtr->dataLen);
    memset(slientMediaFramePtr->data, 0, slientMediaFramePtr->dataLen);

    (*pcmBufferQueue)->Enqueue(pcmBufferQueue, slientMediaFramePtr->data, slientMediaFramePtr->dataLen);
    internalQueuePtr->push(slientMediaFramePtr);//������ͬʱ�����ڴ����

    (*pcmPlayerPlay)->SetPlayState(pcmPlayerPlay, SL_PLAYSTATE_PLAYING);

    runFlag = true;
}

void AudioPlayer::stop() {
    fileSaver.close();
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
//        mediaFrameQueue.pushBack(mediaFramePtr);

//        pcmQueue.push(std::dynamic_pointer_cast<MediaFrameImpl>(mediaFramePtr));

        long t2= currentTimeStamp();
        int gop = t2 - this->gop;
        this->gop  = t2;
//        LogT<<"time : " <<  " , gop : "<<gop<<endl;

        MediaFrameImplPtr fMediaFrame = std::dynamic_pointer_cast<MediaFrameImpl>(mediaFramePtr);
        (*pcmBufferQueue)->Enqueue(pcmBufferQueue, fMediaFrame->data, fMediaFrame->dataLen);
    }
}



void AudioPlayer::doPutAudioData() {
    long t1= currentTimeStamp();

    MediaFrameImpl* curFrame = mediaFrameQueuePtr->peekReadable();
    if(curFrame){
        (*pcmBufferQueue)->Enqueue(pcmBufferQueue, curFrame->data, curFrame->dataLen);
        mediaFrameQueuePtr->next();
        internalQueuePtr->push(curFrame);
        audioPutTime = av_gettime_relative() / 1000000.0;
    } else{
        LogT<<"audioPlay play slient 2"<<endl;
        internalQueuePtr->push(slientMediaFramePtr);//�������ž�����
    }

    long t2= currentTimeStamp();
    int gop = t2 - this->gop;
    this->gop  = t2;
//    LogT<<"audioPlay time : "<< t2-t1<<  " , gop : "<<gop<<endl;
}

void AudioPlayer::doAudioCallback() {
    audioCallbackTime = av_gettime_relative() / 1000000.0;

    /*SLuint32 audioLatency = 0;
    SLAndroidSimpleBufferQueueState state;
    (*pcmBufferQueue)->GetState(pcmBufferQueue,&state);
    LogT<<"audioPlay play state  count : "<<state.count<< ", index :"<< state.index <<" ,audioLatency : "<<audioLatency<<endl;*/

    MediaFrameImpl* playedMediaFramePtr = internalQueuePtr->front();
    if(!playedMediaFramePtr){
        LogT<<"audio playedMediaFramePtr null error"<<endl;
        return;
    }
    internalQueuePtr->pop();
    if(!playedMediaFramePtr->silentFlag){
        LogT<<"audioPlay gop : "<< (audioCallbackTime-audioPutTime)*1000<<" ms"<<endl;
        clockManagerPtr->syncAudioTime(playedMediaFramePtr);
    }

    if( !onceSilent){
        if(mediaFrameQueuePtr->getSize() < PLAY_KICKSTART_BUFFER_COUNT){
            (*pcmBufferQueue)->Enqueue(pcmBufferQueue, slientMediaFramePtr->data, slientMediaFramePtr->dataLen);
            internalQueuePtr->push(slientMediaFramePtr);//�������ž�����
            LogT<<"audioPlay play slient  1"<<endl;
            return;
        } else{
            LogT<<"audioPlay play input  3"<<endl;
            for (int32_t idx = 0; idx < PLAY_KICKSTART_BUFFER_COUNT; idx++) {//ȡPLAY_KICKSTART_BUFFER_COUNT������buf���벥��������
                doPutAudioData();
            }
            onceSilent = true;
        }
    } else{
        doPutAudioData();
    }
}


void AudioPlayer::pcmBufferCallBack(SLAndroidSimpleBufferQueueItf bf, void *context) {
//    LogT<<"--------pcmBufferCallBack------1----" <<endl;
    AudioPlayer * audioPlayer = static_cast<AudioPlayer*>(context);
    audioPlayer->doAudioCallback();

 /*     long t2= currentTimeStamp();
      int gop = t2 - audioPlayer->gop;
      audioPlayer->gop  = t2;*/
//      LogT<<"time : "<< t2-t1<<  " , gop : "<<gop<<endl;


}



