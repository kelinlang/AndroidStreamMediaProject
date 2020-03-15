//
// Created by kelinlang on 2020/3/3.
//

#ifndef STREAMMEDIAANDROIDLIB_ANDROIDMEDIACODEC_H
#define STREAMMEDIAANDROIDLIB_ANDROIDMEDIACODEC_H
#include <media/NdkMediaFormat.h>
#include <media/NdkMediaCodec.h>
#include <android/native_window.h>
#include "media/media_codec.h"
#include "ffmpeg/ffmpeg_media_format.h"
#include "media/media_queue.h"
#include "media/media_clock.h"

using namespace CommonLib;
using namespace StreamMedia::media;

namespace StreamMedia {
    namespace media {
        class AndroidMediaCodecParams :public MediaCodecParams {
        public:
            AndroidMediaCodecParams();
            ~AndroidMediaCodecParams();


        public:
            AMediaFormat* mediaFormat;

            AVRational tb;
        };
        using AndroidMediaCodecParamsPtr = std::shared_ptr<AndroidMediaCodecParams>;

        class AndroidMediaEncode : public MediaEncode {
        public:
            AndroidMediaEncode();
            ~AndroidMediaEncode();
            void init();
            void release();
            void start();
            void stop();
            //void sendFrame(MediaFramePtr& mediaFramePtr);
        public:

        };
        using AndroidMediaEncodePtr = std::shared_ptr<AndroidMediaEncode>;

        class AndroidMediaDecode : public MediaDecode {
        public:
            AndroidMediaDecode();
            ~AndroidMediaDecode();
            void init();
            void release();
            void start();
            void stop();

        protected:

        public:
            AMediaCodec* mediaCodec;

            std::thread readThread;

            MediaFrameQueuePtr mediaFrameQueuePtr;
            ClockManagerPtr clockManagerPtr;

        protected:
            ssize_t bufidx = -1;
            size_t bufsize;
            AMediaCodecBufferInfo info;
        };
        using AndroidMediaDecodePtr = std::shared_ptr<AndroidMediaDecode>;
    }
}

#endif //STREAMMEDIAANDROIDLIB_ANDROIDMEDIACODEC_H
