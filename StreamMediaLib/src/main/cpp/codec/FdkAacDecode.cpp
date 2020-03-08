//
// Created by kelinlang on 2020/3/8.
//

#include "FdkAacDecode.h"

FdkAacMediaCodecParams::FdkAacMediaCodecParams() {

}

FdkAacMediaCodecParams::~FdkAacMediaCodecParams() {

}

FdkAacMediaEncode::FdkAacMediaEncode() {

}

FdkAacMediaEncode::~FdkAacMediaEncode() {

}

void FdkAacMediaEncode::init() {

}

void FdkAacMediaEncode::release() {

}

void FdkAacMediaEncode::start() {

}

void FdkAacMediaEncode::stop() {

}

FdkAacMediaDecode::FdkAacMediaDecode() {

}

FdkAacMediaDecode::~FdkAacMediaDecode() {

}

void FdkAacMediaDecode::init() {
//    handle = aacDecoder_Open(TT_MP4_ADTS, 1);
    bool type = true;
    AAC_DECODER_ERROR err;

    handle = aacDecoder_Open(type ? TT_MP4_RAW : TT_MP4_ADTS , 1);
    if (!handle){
        LogE<<"aacDecoder_Open error"<<endl;
        return;
    }
    if(type){
        if ((err = aacDecoder_ConfigRaw(handle, &conf,&confLen)) != AAC_DEC_OK) {
            LogE<< "Unable to set extradata"<<endl;
            return ;
        }
    }
    if (aacDecoder_SetParam(handle, AAC_PCM_MAX_OUTPUT_CHANNELS,
                            2) != AAC_DEC_OK) {
        LogE<<"Unable to set output channels in the decoder"<<endl;
        return;
    }

}

void FdkAacMediaDecode::release() {

}

void FdkAacMediaDecode::start() {
    if(handle){
        LogI<<" -------------------------FdkAacMediaDecode success-----------------"<<endl;
        runFlag = true;

        codecThread = std::thread([this] {
            fileSaver.open();

            int ret;
            int countFrame = 0;

            LogI<<"workThreadFunc loop start "<<endl;
            AAC_DECODER_ERROR err;
            while (runFlag)
            {
                MediaPacketPtr mp = mediaPacketQueue.front();
                if (mp) {
                    FFmpegMediaPacketPtr fMediaPacket = std::dynamic_pointer_cast<FFmpegMediaPacket>(mp);
                    unsigned int confLen[] = {(unsigned int)fMediaPacket->getAVPacket()->size};
                    unsigned int validSize = fMediaPacket->getAVPacket()->size;
                    err=aacDecoder_Fill(handle, &fMediaPacket->getAVPacket()->data, confLen, &validSize);
                    if(err>0){
                        LogE<< "aacDecoder_Fill error"<<endl;
                    }

                    err = aacDecoder_DecodeFrame(handle, (INT_PCM *) outputBuffer, outputBufferSize / sizeof(INT_PCM), 0);
                    if (err == AAC_DEC_NOT_ENOUGH_BITS) {
                        ret = fMediaPacket->getAVPacket()->size - validSize;
                        LogI<<"AAC_DEC_NOT_ENOUGH_BITS "<<endl;
                        continue;
                    }
                    if (err != AAC_DEC_OK) {
                        LogE<< "aacDecoder_DecodeFrame error"<<endl;
                        break;
                    }

                    CStreamInfo *info = aacDecoder_GetStreamInfo(handle);
                 /*   LogI<<"channels"<<info->numChannels<<endl;
                    LogI<<"sampleRate"<<info->sampleRate<<endl;
                    LogI<<"frameSize"<<info->frameSize<<endl;
                    LogI<<"decsize"<<outputBufferSize<<endl;
                    LogI<<"decdata"<<outputBuffer[0]<<endl;*/
                    int size = info->frameSize*2;
                    uint8_t *data =  (uint8_t*)malloc(size);
                    memcpy(data,outputBuffer,size);

                    fileSaver.write(data,size);

                    MediaFrameImplPtr mediaFramePtr = std::make_shared<MediaFrameImpl>();

                    mediaFramePtr->sourceIndex = getSourceIndex();
                    mediaFramePtr->streamIndex = getStreamIndex();
                    mediaFramePtr->data = data;
                    mediaFramePtr->startPos = 0;
                    mediaFramePtr->dataLen = size;
                    mediaFramePtr->pts = 0;

                    MediaFramePtr mf = std::dynamic_pointer_cast<MediaFrame>(mediaFramePtr);
                    callbackMediaFrame(mf);
                }
            }
            fileSaver.close();

            callbackOnStop();
            LogI << "FdkAacMediaDecode finish" << endl;
        });
    }
}

void FdkAacMediaDecode::stop() {

}
