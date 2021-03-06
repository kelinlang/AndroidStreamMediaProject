//
// Created by kelinlang on 2020/3/3.
//

#include "AndroidMediaCodec.h"

AndroidMediaCodecParams::AndroidMediaCodecParams() {
    mediaFormat = AMediaFormat_new();
}

AndroidMediaCodecParams::~AndroidMediaCodecParams() {
    if(mediaFormat){
        AMediaFormat_delete(mediaFormat);
        mediaFormat= nullptr;
    }
}

AndroidMediaEncode::AndroidMediaEncode() {

}

AndroidMediaEncode::~AndroidMediaEncode() {

}

void AndroidMediaEncode::init() {

}

void AndroidMediaEncode::release() {

}

void AndroidMediaEncode::start() {

}

void AndroidMediaEncode::stop() {

}

AndroidMediaDecode::AndroidMediaDecode() {

}

AndroidMediaDecode::~AndroidMediaDecode() {

}

void AndroidMediaDecode::init() {
    if (!codecParams) {
        LogE << "codecParams is nullPtr " << endl;
        callbackOnError(static_cast<int>(MediaCodecErrorId::ParamInvalid), "codecParams is nullPtr");
        return;
    }
    AndroidMediaCodecParamsPtr param = std::dynamic_pointer_cast<AndroidMediaCodecParams>(codecParams);
    const char* mime;
    AMediaFormat_getString(param->mediaFormat,"mime",&mime);
    AMediaCodec* mediaCodec = AMediaCodec_createDecoderByType(mime);

    media_status_t status = AMediaCodec_configure(mediaCodec, param->mediaFormat, NULL, NULL, 0);
    if (status != 0){
        LogE<<"AMediaCodec_configure error , status : "<<status <<endl;
        AMediaCodec_stop(mediaCodec);
        free(mediaCodec);
        callbackOnError(static_cast<int>(MediaCodecErrorId::ParamInvalid), "AMediaCodec_configure error");
        return;
    }

    LogI<<" init success"<<endl;
    this->mediaCodec = mediaCodec;
}

void AndroidMediaDecode::release() {

}

void AndroidMediaDecode::start() {
    media_status_t status;
    if(mediaCodec){
        if ((status = AMediaCodec_start(mediaCodec)) != AMEDIA_OK){
            LogE<<"AMediaCodec_start error , status : "<<status <<endl;
            AMediaCodec_stop(mediaCodec);
            free(mediaCodec);
            mediaCodec = nullptr;
        } else{
            LogI<<" -------------------------mediaCodecStart success-----------------"<<endl;
            runFlag = true;
            codecThread = std::thread([this] {
                int ret;
                int countFrame = 0;

                ssize_t bufidx = -1;
                size_t bufsize;
                AMediaCodecBufferInfo info;
                LogI<<"workThreadFunc loop start "<<endl;
                while (runFlag)
                {
                    MediaPacketPtr mp = mediaPacketQueue.front();
                    if (mp) {
                        FFmpegMediaPacketPtr fMediaPacket = std::dynamic_pointer_cast<FFmpegMediaPacket>(mp);

                        AndroidMediaCodecParamsPtr param = std::dynamic_pointer_cast<AndroidMediaCodecParams>(codecParams);
                        bufidx = AMediaCodec_dequeueInputBuffer(this->mediaCodec,50);
                        if (bufidx >= 0) {
                            uint8_t* buf = AMediaCodec_getInputBuffer(mediaCodec,bufidx,&bufsize);

                            memcpy(buf,fMediaPacket->getAVPacket()->data,fMediaPacket->getAVPacket()->size);

                            AMediaCodec_queueInputBuffer(mediaCodec,bufidx,0,fMediaPacket->getAVPacket()->size,0,0);
                        }

                        //LogT << "avcodec_receive_frame ret :" <<ret<< " , countFrame : "<< countFrame++ <<endl;
                    }
                }

                callbackOnStop();
                LogI << "AndroidMediaDecode finish" << endl;
            });

            readThread = std::thread([this] {
                ssize_t bufidx = -1;
                size_t bufsize;
                AMediaCodecBufferInfo info;

                while (runFlag)
                {
                    bufidx = AMediaCodec_dequeueOutputBuffer(mediaCodec,&info,50);
                    if(bufidx >= 0) {
                        uint8_t *buf = AMediaCodec_getOutputBuffer(mediaCodec, bufidx, &bufsize);
                        uint8_t *data =  (uint8_t*)malloc(bufsize);
                        memcpy(data,buf,bufsize);

                        MediaFrameImplPtr mediaFramePtr = std::make_shared<MediaFrameImpl>();
//                        mediaFramePtr->mediaType = (int)param->codecParams->codec_type;
                        mediaFramePtr->sourceIndex = getSourceIndex();
                        mediaFramePtr->streamIndex = getStreamIndex();
                        mediaFramePtr->data = data;
                        mediaFramePtr->startPos = 0;
                        mediaFramePtr->dataLen = bufsize;
                        mediaFramePtr->pts = info.presentationTimeUs;
                        LogT << " presentationTimeUs : "<<info.presentationTimeUs << endl;

                        AMediaCodec_releaseOutputBuffer(mediaCodec, bufidx, false);

                        MediaFramePtr mf = std::dynamic_pointer_cast<MediaFrame>(mediaFramePtr);
                        callbackMediaFrame(mf);
                    }
                }
                LogI << "AndroidMediaDecode finish" << endl;
            });
        }
    }
}

void AndroidMediaDecode::stop() {
    uniqueLock(mtx);
    if (runFlag)
    {
        LogD << " close start" << endl;
        runFlag = false;
        mediaPacketQueue.clear();
        codecThread.join();
        readThread.join();

        if(mediaCodec){
            AMediaCodec_stop(mediaCodec);
            mediaCodec = nullptr;
        }
        LogD << " close finish" << endl;
    }
}
