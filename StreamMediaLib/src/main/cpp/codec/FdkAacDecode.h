//
// Created by kelinlang on 2020/3/8.
//

#ifndef STREAMMEDIAANDROIDLIB_FDKAACDECODE_H
#define STREAMMEDIAANDROIDLIB_FDKAACDECODE_H
#include "media/media_codec.h"
#include "ffmpeg/ffmpeg_media_format.h"
#include "fdk-aac/aacdecoder_lib.h"

using namespace CommonLib;
using namespace StreamMedia::media;

namespace StreamMedia {
    namespace media {
        class FdkAacMediaCodecParams :public MediaCodecParams {
        public:
            FdkAacMediaCodecParams();
            ~FdkAacMediaCodecParams();


        public:

        };
        using FdkAacMediaCodecParamsPtr = std::shared_ptr<FdkAacMediaCodecParams>;

        class FdkAacMediaEncode : public MediaEncode {
        public:
             FdkAacMediaEncode();
            ~ FdkAacMediaEncode();
            void init();
            void release();
            void start();
            void stop();
            //void sendFrame(MediaFramePtr& mediaFramePtr);
        public:

        };
        using  FdkAacMediaEncodePtr = std::shared_ptr< FdkAacMediaEncode>;

        class  FdkAacMediaDecode : public MediaDecode {
        public:
             FdkAacMediaDecode();
            ~ FdkAacMediaDecode();
            void init();
            void release();
            void start();
            void stop();

        public:
            uint8_t* conf ;
            unsigned int confLen = 2;

            HANDLE_AACDECODER		handle = nullptr;
            INT_PCM					outputBuffer[2*2048];
            int 					outputBufferSize = 2*2048;
        };
        using  FdkAacMediaDecodePtr = std::shared_ptr< FdkAacMediaDecode>;
    }
}
#endif //STREAMMEDIA FdkAacLIB_FDKAACDECODE_H
