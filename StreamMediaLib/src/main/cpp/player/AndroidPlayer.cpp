//
// Created by kelinlang on 2020/3/3.
//

#include "AndroidPlayer.h"
AndroidPlayer::AndroidPlayer() {
    mediaSourePtr = std::make_shared<FFmpegMediaSource>();
    videoDisplayPtr = std::make_shared<AndroidVideoDisplay>();
    audioPlayerPtr = std::make_shared<AudioPlayer>();
    clockManagerPtr = std::make_shared<ClockManager>();
    videoMediaFrameQueuePtr = std::make_shared<MediaFrameQueue>(FFMIN(VIDEO_PICTURE_QUEUE_SIZE, FRAME_QUEUE_SIZE),true);
    audioMediaFrameQueuePtr = std::make_shared<MediaFrameQueue>(FFMIN(SAMPLE_QUEUE_SIZE, FRAME_QUEUE_SIZE),true);
}


AndroidPlayer::~AndroidPlayer() {

}


void AndroidPlayer::init() {
    if(!videoDisplayParamPtr){
        LogE<<"videoDisplayParamPtr is null"<<endl;
        return;
    }
    audioPacketQueuePtr = std::make_shared<MediaPacketQueue>();
    videoPacketQueuePtr = std::make_shared<MediaPacketQueue>();
    clockManagerPtr->setAudioQueueSerial(&audioPacketQueuePtr->serial);
    clockManagerPtr->setVideoQueueSerial(&videoPacketQueuePtr->serial);
    clockManagerPtr->init();
    audioPlayerPtr->clockManagerPtr = clockManagerPtr;
    videoDisplayPtr->clockManagerPtr = clockManagerPtr;



    mediaSourePtr->videoPacketQueuePtr = videoPacketQueuePtr;
    mediaSourePtr->audioPacketQueuePtr = audioPacketQueuePtr;
    mediaSourePtr->setIndex(0);
    mediaSourePtr->setUrl(videoDisplayParamPtr->url);
    mediaSourePtr->init();
    if(!mediaSourePtr->isInited()){
        LogE<<"mediaSourePtr init fail"<<endl;
        return;
    }
    mediaSourePtr->addMediaPacketCallback(mediaPacketCallback);
    MediaStreamMap mediaStreamMap = mediaSourePtr->getMediaStreamMap();
    for (auto& streamPair : mediaStreamMap) {
        FFmpegMediaStreamPtr  mediaStreamPtr = std::dynamic_pointer_cast<FFmpegMediaStream>(streamPair.second);
        if(StreamType::StreamVideo == mediaStreamPtr->getStreamType()){
//            continue;//屏蔽视频解码

            AndroidMediaDecodePtr decode = std::make_shared<AndroidMediaDecode>();
            decode->setSourceIndex(mediaStreamPtr->sourceIndex);
            decode->setStreamIndex(mediaStreamPtr->getStreamId());
            decode->setMediaPacketQueue(videoPacketQueuePtr);

            AVCodecParameters* avCodecParameters = mediaStreamPtr->getStream()->codecpar;

            AndroidMediaCodecParamsPtr codecParamsPtr = std::make_shared<AndroidMediaCodecParams>();
            codecParamsPtr->tb.num = mediaStreamPtr->getStream()->time_base.num;//时间基
            codecParamsPtr->tb.den = mediaStreamPtr->getStream()->time_base.den;

            codecParamsPtr->mediaFormat = AMediaFormat_new();
            AMediaFormat_setString(codecParamsPtr->mediaFormat, "mime", "video/avc");
            AMediaFormat_setInt32(codecParamsPtr->mediaFormat, AMEDIAFORMAT_KEY_WIDTH, avCodecParameters->width); // 视频宽度
            AMediaFormat_setInt32(codecParamsPtr->mediaFormat, AMEDIAFORMAT_KEY_HEIGHT, avCodecParameters->height); // 视频高度
            videoDisplayParamPtr->videoWidth = avCodecParameters->width;
            videoDisplayParamPtr->videoHeight = avCodecParameters->height;
            videoDisplayParamPtr->frameRate = mediaStreamPtr->getFrameRate();//帧率
            LogI << "initDecode codec name : " << endl;

            decode->setMediaCodecParams(std::dynamic_pointer_cast<MediaCodecParams>(codecParamsPtr));
            decode->setMediaFrameCallback(mediaFrameCallback);
            decode->mediaFrameQueuePtr = videoMediaFrameQueuePtr;
            decode->clockManagerPtr = clockManagerPtr;
            decode->init();
            decodes.insert({ mediaStreamPtr->getStreamId(), std::move(decode) });

        } else if(StreamType::StreamAudio == mediaStreamPtr->getStreamType()){

            AVCodecParameters* avCodecParameters = mediaStreamPtr->getStream()->codecpar;

            //Android mediaCodec 解码
          /*  AndroidMediaDecodePtr decode = std::make_shared<AndroidMediaDecode>();
            decode->setSourceIndex(mediaStreamPtr->sourceIndex);
            decode->setStreamIndex(mediaStreamPtr->getStreamId());
            AndroidMediaCodecParamsPtr codecParamsPtr = std::make_shared<AndroidMediaCodecParams>();
            codecParamsPtr->mediaFormat = AMediaFormat_new();
            AMediaFormat_setString(codecParamsPtr->mediaFormat, "mime", "audio/mp4a-latm");
            AMediaFormat_setInt32(codecParamsPtr->mediaFormat, AMEDIAFORMAT_KEY_CHANNEL_COUNT, avCodecParameters->channels);
            AMediaFormat_setInt32(codecParamsPtr->mediaFormat, AMEDIAFORMAT_KEY_SAMPLE_RATE, avCodecParameters->sample_rate);
            AMediaFormat_setInt32(codecParamsPtr->mediaFormat, AMEDIAFORMAT_KEY_AAC_PROFILE, 1);
            AMediaFormat_setInt32(codecParamsPtr->mediaFormat, AMEDIAFORMAT_KEY_MAX_INPUT_SIZE, 16384);
            AMediaFormat_setInt32(codecParamsPtr->mediaFormat, AMEDIAFORMAT_KEY_IS_ADTS, 1);
             decode->setMediaCodecParams(std::dynamic_pointer_cast<MediaCodecParams>(codecParamsPtr));
            */


            FdkAacMediaDecodePtr decode = std::make_shared<FdkAacMediaDecode>();
            decode->conf = (uint8_t*)malloc(mediaStreamPtr->getStream()->codec->extradata_size);
            memcpy(decode->conf,mediaStreamPtr->getStream()->codec->extradata,mediaStreamPtr->getStream()->codec->extradata_size);
            LogI << "initDecode aac decode name : " << endl;

            decode->tb.num = mediaStreamPtr->getStream()->time_base.num;//时间基
            decode->tb.den = mediaStreamPtr->getStream()->time_base.den;

            decode->clockManagerPtr = clockManagerPtr;
            decode->mediaFrameQueuePtr = audioMediaFrameQueuePtr;
            decode->setMediaFrameCallback(audioFrameCallback);
            decode->setMediaPacketQueue(audioPacketQueuePtr);
            decode->init();
            audioDecode = decode;
//            decodes.insert({ mediaStreamPtr->getStreamId(), std::move(decode) });
        }

        videoDisplayPtr->mediaFrameQueuePtr = videoMediaFrameQueuePtr;
        videoDisplayPtr->setVideoDisplayParam(videoDisplayParamPtr);

        videoDisplayPtr->init();

        audioPlayerPtr->mediaFrameQueuePtr = audioMediaFrameQueuePtr;
        audioPlayerPtr->init();
    }
}

void AndroidPlayer::start() {
    videoDisplayPtr->start();
    for(auto& decode: decodes){
        decode.second->start();
    }
    mediaSourePtr->start();
    if(audioDecode){
        audioDecode->start();
    }
    if(audioPlayerPtr){
        audioPlayerPtr->start();
    }
}

void AndroidPlayer::stop() {
    mediaSourePtr->stop();
    for(auto& decode: decodes){
        decode.second->stop();
    }
    videoDisplayPtr->stop();
}

void AndroidPlayer::release() {
    mediaSourePtr->release();
    for(auto& decode: decodes){
        decode.second->release();
    }
    videoDisplayPtr->release();
}


