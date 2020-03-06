//
// Created by kelinlang on 2020/3/5.
//

#include "AndroidPusher.h"

AndroidPusher::AndroidPusher() {}

AndroidPusher::~AndroidPusher() {

}

void AndroidPusher::init() {
    openSink();
}

void AndroidPusher::release() {

}

void AndroidPusher::start() {
    ioThread = std::thread([this] {
        if (formatContext)
        {
            runFlag = true;
            int ret = -1;
            ret = avformat_write_header(formatContext, NULL);
            if (ret < 0)
            {
                LogE << "Error: Could not write output file header." << endl;
                runFlag = false;
            }
            while (runFlag)
            {
                uniqueLock(mtx);
                MediaFrameImplPtr mp = mediaPacketQueue.front();
                if (mp) {
                    AVPacket packet;
                    av_init_packet(&packet);
                    int pos = 0;
                    char startCode[4] = {0,0,0,1};
                  /*  switch (mp->packetFormat){
                        case VideoFormat::VDIEO_FORMAT_H264:
                            av_new_packet(&packet, 4+mp->dataLen);
                            memcpy(packet.data, startCode, 4);
                            memcpy(packet.data + 4, mp->data, mp->dataLen);
                            break;
                        case VideoFormat::VDIEO_FORMAT_H264_SPS_PPS:

                            break;
                        default:
                            break;
                    }*/

                    av_new_packet(&packet, mp->dataLen);
                    memcpy(packet.data , mp->data, mp->dataLen);

                    if(mp->frameType == 1){
                        packet.flags = AV_PKT_FLAG_KEY;
                    }
                    //LogT << "1 packetStreamId : " << packet->stream_index << " , inStreamId : " << mp->getStreamIndex() << " , outStreamId : " << outStreamId << ", pts : " << packet->pts << ", dts : " << packet->dts << endl;

                    packet.pts = av_rescale_q_rnd(packet.pts, videoStream->time_base, videoStream->time_base, (AVRounding)(AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
                    packet.dts = av_rescale_q_rnd(packet.dts, videoStream->time_base, videoStream->time_base, (AVRounding)(AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
                    packet.duration = av_rescale_q(packet.duration, videoStream->time_base, videoStream->time_base);
                    packet.pos = -1;
                    packet.stream_index = 0;

                    //LogT << "2 packetStreamId : " << packet.stream_index << " , inStreamId : " << mp->getStreamIndex() << " , outStreamId : " << outStreamId << ", pts : " << packet.pts << ", dts : "<< packet->dts << endl;

                    ret = av_write_frame(formatContext, &packet);
//                    LogT << "av_interleaved_write_frame , ret : " << ret << endl;
                    if (ret < 0)
                    {
                        LogE << "av_interleaved_write_frame error, ret : " << ret << endl;
                        break;
                    }
                }
                /*	if (mp && (packet = mp->getAVPacket())
                        && sourceStreamInOutMap.find(mp->getSourceIndex()) != sourceStreamInOutMap.end())
                    {

                    }*/
            }
            av_write_trailer(formatContext);
        }
        closeSink();
        callbackOnStop();
        LogI << "------------------ffmpeg out thread finish---------------------" << endl;
    });
}

void AndroidPusher::stop() {
    uniqueLock(mtx);
    if (runFlag)
    {
        LogD << " close start" << endl;
        runFlag = false;
        ioThread.join();

        mediaStreamMap.clear();
        LogD << " close finish" << endl;
    }
}

void AndroidPusher::inputMediaPacket(MediaPacketPtr &&mediaPacketPtr) {

}

void AndroidPusher::addMediaSource(MediaSourcePtr &&sourcePtr, StreamIndexList indexList) {

}

void AndroidPusher::removeMediaSource(int sourceIndex) {

}

void AndroidPusher::openSink() {
    LogD<<"formatContext : "<<formatContext<< endl;
    if (!formatContext && !url.empty() && !outFormat.empty()) {
        AVFormatContext *fc = NULL;
        int ret = -1;
        //按照文件名获取输出文件的句柄
        avformat_alloc_output_context2(&fc, NULL, outFormat.data(), url.data());

        if (!fc) {
            LogE << "Error: Could not create output context, ret :" << ret << endl;
            return;
        }
        formatContext = fc;
        outputFormat = fc->oformat;

        AVOutputFormat *ofmt = fc->oformat;
        int streamCount = 0;

        //视频
        AVCodec *codec = avcodec_find_encoder(AV_CODEC_ID_H264);
        if (!codec) {
            LogE<<"Can not find encoder!"<<endl;
            return ;
        }

        AVCodecContext* codecContext = avcodec_alloc_context3(codec);
        //编码器的ID号，这里为264编码器，可以根据video_st里的codecID 参数赋值
        codecContext->codec_id = codec->id;
        //像素的格式，也就是说采用什么样的色彩空间来表明一个像素点
        codecContext->pix_fmt = AV_PIX_FMT_YUV420P;
        //编码器编码的数据类型
        codecContext->codec_type = AVMEDIA_TYPE_VIDEO;
        //编码目标的视频帧大小，以像素为单位
        codecContext->width = 480;
       codecContext->height = 640;
        //codecContext->framerate = (AVRational) {fps, 1};
        //帧率的基本单位，我们用分数来表示，
        //codecContext->time_base = (AVRational) {1, fps};
        //目标的码率，即采样的码率；显然，采样码率越大，视频大小越大
        //codecContext->bit_rate = 400000;
        //固定允许的码率误差，数值越大，视频越小
//    codecContext->bit_rate_tolerance = 4000000;
       // codecContext->gop_size = 50;

        AVStream* outStream = avformat_new_stream(fc, codecContext->codec);//执行的时候会把outStream加到fc的流数组中
        if (!outStream)
        {
            LogE << "Error: Could not allocate output stream." << endl;
            return;
        }
        outStream->time_base.num = 1;
        outStream->time_base.den = 30;
        avcodec_parameters_from_context(outStream->codecpar, codecContext);

        videoStream = outStream;

        av_dump_format(fc, 1, url.data(), 1);
        LogI<<"push url :"<< url << "  format :"<<outFormat<<endl;
        if (!(ofmt->flags & AVFMT_NOFILE))
        {
            AVDictionary* options = NULL;
            av_dict_set(&options, "protocol_whitelist", "file,udp,rtp,rtmp,rtsp,tcp", 0);
            ret = avio_open2(&fc->pb, url.data(), AVIO_FLAG_WRITE, NULL, &options);
            if (ret < 0)
            {
                LogE << "Error: Could not open output file ："<< url.data()<<endl;
                closeSink();
            }
        }
    }
}

void AndroidPusher::closeSink() {
    if (formatContext && !(outputFormat->flags & AVFMT_NOFILE))
    {
        LogI << "close sink" << endl;
        avio_closep(&formatContext->pb);
        formatContext = nullptr;
        outputFormat = nullptr;
        options = nullptr;
    }
}

void AndroidPusher::inputMediaPacket(MediaFrameImplPtr &mediaPacketPtr) {
    if(runFlag){
        mediaPacketQueue.pushBack(mediaPacketPtr);
    }
}
