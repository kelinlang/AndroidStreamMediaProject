//
// Created by kelinlang on 2020/3/8.
//

#ifndef STREAMMEDIAANDROIDLIB_FDKAACDECODE_H
#define STREAMMEDIAANDROIDLIB_FDKAACDECODE_H
#include "media/media_codec.h"
#include "ffmpeg/ffmpeg_media_format.h"
#include "fdk-aac/aacdecoder_lib.h"
#include "container/file_saver.h"
#include "media/media_queue.h"
#include "media/media_clock.h"

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
            INT_PCM					outputBuffer[2048 * sizeof(INT_PCM)*8];
            int 					outputBufferSize = 2048 * sizeof(INT_PCM)*8;

//            FileSaver fileSaver;

            MediaFrameQueuePtr mediaFrameQueuePtr;

            ClockManagerPtr clockManagerPtr;

            AVRational tb;
        };
        using  FdkAacMediaDecodePtr = std::shared_ptr< FdkAacMediaDecode>;
    }
}
#endif //STREAMMEDIA FdkAacLIB_FDKAACDECODE_H
