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
#include "codec/FdkAacDecode.h"
#include "audio/OpenSlAudioPlay.h"
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

            AudioPlayerPtr audioPlayerPtr ;
            FdkAacMediaDecodePtr audioDecode;
        protected:
            MediaPacketCallback mediaPacketCallback = [this](MediaPacketPtr&& mediaPacketPtr) {
                switch (mediaPacketPtr->getStreamType()){
                    case StreamType::StreamVideo:
                        try {
                            if(decodes.at(mediaPacketPtr->getStreamIndex())){
                                decodes.at(mediaPacketPtr->getStreamIndex())->sendPacket(mediaPacketPtr);
                            }
                        }catch (std::out_of_range & error){

                        }
                        break;
                    case StreamType::StreamAudio:
                        if(audioDecode){
                            audioDecode->sendPacket(mediaPacketPtr);
                        }
                        break;
                }
            };
            MediaFrameCallback mediaFrameCallback = [this](MediaFramePtr& mediaFramePtr) {
                videoDisplayPtr->intputFrame(mediaFramePtr);
            };

            MediaFrameCallback audioFrameCallback = [this](MediaFramePtr& mediaFramePtr) {
                audioPlayerPtr->intputFrame(mediaFramePtr);
            };
        };
        using AndroidPlayerPtr = std::shared_ptr<AndroidPlayer>;
    }
}

#endif //STREAMMEDIAANDROIDLIB_ANDROIDPLAYER_H
