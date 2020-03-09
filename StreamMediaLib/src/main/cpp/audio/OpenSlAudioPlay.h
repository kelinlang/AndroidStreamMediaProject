//
// Created by kelinlang on 2020/3/7.
//

#ifndef STREAMMEDIAANDROIDLIB_OPENSLAUDIOPLAY_H
#define STREAMMEDIAANDROIDLIB_OPENSLAUDIOPLAY_H
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include "ffmpeg/ffmpeg_media_format.h"
#include "log/log.h"
#include "container/PcmQueue.h"
#include "container/file_saver.h"
#include "container/xspsc_queue.h"
#include "buf_manager.h"


using namespace CommonLib;
using namespace StreamMedia::media;


namespace StreamMedia {
    namespace media {
        using x_int_queue_t = x_spsc_queue_t<MediaFrameImplPtr, 8 >;

        class AudioPlayer{
        public:
            AudioPlayer();

             ~AudioPlayer();

             void init();
             void start();
             void stop();
             void release();
             void intputFrame(MediaFramePtr& mediaFramePtr);

        protected:

            static void pcmBufferCallBack(SLAndroidSimpleBufferQueueItf bf, void * context);
        protected:
            std::mutex mtx;
            bool runFlag = false;
            std::thread displayThread;

            ConcurrentQueuePcm<MediaFramePtr> mediaFrameQueue;


            // ����ӿ�
            SLObjectItf engineObject = NULL;
            SLEngineItf engineEngine = NULL;

            //������
            SLObjectItf outputMixObject = NULL;
            SLEnvironmentalReverbItf outputMixEnvironmentalReverb = NULL;
            SLEnvironmentalReverbSettings reverbSettings = SL_I3DL2_ENVIRONMENT_PRESET_STONECORRIDOR;

            //pcm
            SLObjectItf pcmPlayerObject = NULL;
            SLPlayItf pcmPlayerPlay = NULL;
            SLVolumeItf pcmPlayerVolume = NULL;

//���������нӿ�
            SLAndroidSimpleBufferQueueItf pcmBufferQueue;

            FileSaver fileSaver;

            x_int_queue_t pcmQueue;

            long gop = 0;
        };
        using AudioPlayerPtr = std::shared_ptr<AudioPlayer>;
    }
}

#endif //STREAMMEDIAANDROIDLIB_OPENSLAUDIOPLAY_H
