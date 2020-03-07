//
// Created by kelinlang on 2020/3/3.
//

#include "AndroidPlayer.h"
AndroidPlayer::AndroidPlayer() {
    mediaSourePtr = std::make_shared<FFmpegMediaSource>();
    videoDisplayPtr = std::make_shared<AndroidVideoDisplay>();
}


AndroidPlayer::~AndroidPlayer() {

}


void AndroidPlayer::init() {
    if(!videoDisplayParamPtr){
        LogE<<"videoDisplayParamPtr is null"<<endl;
        return;
    }

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

            AndroidMediaDecodePtr decode = std::make_shared<AndroidMediaDecode>();
            decode->setSourceIndex(mediaStreamPtr->sourceIndex);
            decode->setStreamIndex(mediaStreamPtr->getStreamId());

            AVCodecParameters* avCodecParameters = mediaStreamPtr->getStream()->codecpar;

            AndroidMediaCodecParamsPtr codecParamsPtr = std::make_shared<AndroidMediaCodecParams>();
            codecParamsPtr->mediaFormat = AMediaFormat_new();
            AMediaFormat_setString(codecParamsPtr->mediaFormat, "mime", "video/avc");
            AMediaFormat_setInt32(codecParamsPtr->mediaFormat, AMEDIAFORMAT_KEY_WIDTH, avCodecParameters->width); // 视频宽度
            AMediaFormat_setInt32(codecParamsPtr->mediaFormat, AMEDIAFORMAT_KEY_HEIGHT, avCodecParameters->height); // 视频高度
            videoDisplayParamPtr->videoWidth = avCodecParameters->width;
            videoDisplayParamPtr->videoHeight = avCodecParameters->height;

            LogI << "initDecode codec name : " << endl;

            decode->setMediaCodecParams(std::dynamic_pointer_cast<MediaCodecParams>(codecParamsPtr));
            decode->setMediaFrameCallback(mediaFrameCallback);

            decode->init();
            decodes.insert({ mediaStreamPtr->getStreamId(), std::move(decode) });

        } else if(StreamType::StreamAudio == mediaStreamPtr->getStreamType()){
            AndroidMediaDecodePtr decode = std::make_shared<AndroidMediaDecode>();
            decode->setSourceIndex(mediaStreamPtr->sourceIndex);
            decode->setStreamIndex(mediaStreamPtr->getStreamId());

            AVCodecParameters* avCodecParameters = mediaStreamPtr->getStream()->codecpar;

            AndroidMediaCodecParamsPtr codecParamsPtr = std::make_shared<AndroidMediaCodecParams>();
            codecParamsPtr->mediaFormat = AMediaFormat_new();
            AMediaFormat_setString(codecParamsPtr->mediaFormat, "mime", "audio/aac");
            AMediaFormat_setInt32(codecParamsPtr->mediaFormat, AMEDIAFORMAT_KEY_CHANNEL_COUNT, avCodecParameters->channels);
            AMediaFormat_setInt32(codecParamsPtr->mediaFormat, AMEDIAFORMAT_KEY_SAMPLE_RATE, avCodecParameters->sample_rate);
            AMediaFormat_setInt32(codecParamsPtr->mediaFormat, AMEDIAFORMAT_KEY_AAC_PROFILE, 2);


            LogI << "initDecode codec name : " << endl;

            decode->setMediaCodecParams(std::dynamic_pointer_cast<MediaCodecParams>(codecParamsPtr));
            decode->setMediaFrameCallback(mediaFrameCallback);

            decode->init();
            decodes.insert({ mediaStreamPtr->getStreamId(), std::move(decode) });
        }


        videoDisplayPtr->setVideoDisplayParam(videoDisplayParamPtr);
        videoDisplayPtr->init();
    }
}

void AndroidPlayer::start() {
    videoDisplayPtr->start();
    for(auto& decode: decodes){
        decode.second->start();
    }
    mediaSourePtr->start();
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


