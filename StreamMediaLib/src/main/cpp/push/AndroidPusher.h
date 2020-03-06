//
// Created by kelinlang on 2020/3/5.
//

#ifndef STREAMMEDIAANDROIDLIB_ANDROIDPUSHER_H
#define STREAMMEDIAANDROIDLIB_ANDROIDPUSHER_H

#include "ffmpeg/ffmpeg_media_sink.h"
using namespace StreamMedia::media;

namespace StreamMedia{
    namespace media {
        class AndroidPusher : public  MediaSink{
        public:
            AndroidPusher();
             ~AndroidPusher();

             void init();
             void release();
             void start();
             void stop();

            void inputMediaPacket(MediaPacketPtr&& mediaPacketPtr);
            void inputMediaPacket(MediaFrameImplPtr& mediaPacketPtr);
            //加入视频源建立映射关系，主要针对多套节目输入到ts流
            void addMediaSource(MediaSourcePtr&& sourcePtr, StreamIndexList indexList);
            void removeMediaSource(int sourceIndex);

        protected:
            void openSink();
            void closeSink();


        protected:
            AVFormatContext* formatContext;
            AVOutputFormat* outputFormat;
            AVDictionary* options;

            ConcurrentQueue<MediaFrameImplPtr> mediaPacketQueue;

            int streamCount = 0;

            AVStream * videoStream;
        };

        using AndroidPusherPtr = std::shared_ptr<AndroidPusher>;
    }
}
#endif //STREAMMEDIAANDROIDLIB_ANDROIDPUSHER_H
