# CMakeList.txt: 顶层 CMake 项目文件，在此处执行全局配置
# 并包含子项目。
#
cmake_minimum_required (VERSION 3.4.2)

set(CMAKE_CXX_STANDARD 14)
#set(CMAKE_EXE_LINKER_FLAGS)

#设置链接库列表全局变量
#set(LINK_LIB_LIST  )


#list(APPEND LINK_LIB_LIST  avcodec avdevice avfilter avformat avutil postproc swresample swscale)
include_directories("./src/main/StreamMediaProject/ThirdLib/ffmpeg/include")

add_subdirectory ("./src/main/StreamMediaProject/CommonLib")
add_subdirectory ("./src/main/StreamMediaProject/StreamMediaLib")
add_subdirectory ("./src/main/cpp")


#设置工程源码根目录

#set(FFmpegHeader ./src/main/StreamMediaProject/CommonLib/include)
#
#set(CommonLibRoot ./src/main/StreamMediaProject/CommonLib/src)
#set(CommonLibHeader ./src/main/StreamMediaProject/CommonLib/include)
#
#set(StreamMediaLibRoot ./src/main/StreamMediaProject/StreamMediaLib/src)
#set(StreamMediaLibRoot ./src/main/StreamMediaProject/StreamMediaLib/include)
#
##收集源代码
#file(GLOB Src_list
#        ${FFmpegHeader}/*/*.h
#        ${CommonLibRoot}/*/*.cpp ${CommonLibHeader}/*/*.h ${CommonLibHeader}/*.h
#        ${StreamMediaLibRoot}/*/*.cpp ${StreamMediaLibHeader}/*/*.h
#        )
#
#add_library(StreamMediaLib STATIC ${Src_list})