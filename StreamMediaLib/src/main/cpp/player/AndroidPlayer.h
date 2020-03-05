//
// Created by kelinlang on 2020/3/3.
//

#ifndef STREAMMEDIAANDROIDLIB_ANDROIDPLAYER_H
#define STREAMMEDIAANDROIDLIB_ANDROIDPLAYER_H

#include "media/media_display.h"
#include "media/media_format.h"
#include "ffmpeg/ffmpeg_media_source.h"
#include "display/AndroidVideoDisplay.h"
#include "codec/AndroidMediaCodec.h"
using namespace StreamMedia::media;

namespace StreamMedia{
    namespace media{
        class AndroidPlayer{
        public:
            AndroidPlayer();
            ~AndroidPlayer();

            void init();
            void start();
            void stop();
            void release();

        public:
            VideoDisplayParamPtr videoDisplayParamPtr ;
            FFmpegMediaSourcePtr mediaSourePtr;
            std::unordered_map<int, AndroidMediaDecodePtr> decodes;
            AndroidVideoDisplayPtr videoDisplayPtr;


        protected:
            MediaPacketCallback mediaPacketCallback = [this](MediaPacketPtr&& mediaPacketPtr) {
                try {
                    if(decodes.at(mediaPacketPtr->getStreamIndex())){
                        decodes.at(mediaPacketPtr->getStreamIndex())->sendPacket(mediaPacketPtr);
                    }
                }catch (std::out_of_range & error){

                }
            };
            MediaFrameCallback mediaFrameCallback = [this](MediaFramePtr& mediaFramePtr) {
                videoDisplayPtr->intputFrame(mediaFramePtr);
            };
        };
        using AndroidPlayerPtr = std::shared_ptr<AndroidPlayer>;
    }
}

#endif //STREAMMEDIAANDROIDLIB_ANDROIDPLAYER_H
