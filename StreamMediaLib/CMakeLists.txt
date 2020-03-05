# CMakeList.txt: 顶层 CMake 项目文件，在此处执行全局配置
# 并包含子项目。
#
cmake_minimum_required (VERSION 3.4.2)

set(CMAKE_CXX_STANDARD 14)
#set(CMAKE_EXE_LINKER_FLAGS)


find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log
        )


#设置链接库列表全局变量
#set(LINK_LIB_LIST  )


#list(APPEND LINK_LIB_LIST  avcodec avdevice avfilter avformat avutil postproc swresample swscale)
#include_directories("./src/main/StreamMediaProject/ThirdLib/ffmpeg/include")

#add_subdirectory ("./src/main/StreamMediaProject/CommonLib")
#add_subdirectory ("./src/main/StreamMediaProject/StreamMediaLib")
#add_subdirectory ("./src/main/cpp")


#设置工程源码根目录

set(FFmpegHeader ./src/main/StreamMediaProject/ThirdLib/ffmpeg/include)

set(CommonLibHeader ./src/main/StreamMediaProject/CommonLib/include)
set(CommonLibRoot ./src/main/StreamMediaProject/CommonLib/src)

set(StreamMediaLibRoot ./src/main/StreamMediaProject/StreamMediaLib/include)
set(StreamMediaLibRoot ./src/main/StreamMediaProject/StreamMediaLib/src)

set(JniSource ./src/main/cpp)

include_directories("./src/main/StreamMediaProject/ThirdLib/ffmpeg/include")
include_directories("./src/main/StreamMediaProject/CommonLib/include")
include_directories("./src/main/StreamMediaProject/StreamMediaLib/include")
include_directories("./src/main/cpp")
include_directories("./src/main/cpp/jni")
include_directories("D:/AndroidDev/sdk/ndk-bundle/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/include")

#收集源代码
file(GLOB Src_list

        ${CommonLibRoot}/*/*.cpp
        ${StreamMediaLibRoot}/*/*.cpp

        ${JniSource}/*/*.cpp
        ${JniSource}/*/*.c
        ${JniSource}/*/*.h
        ${JniSource}/*.cpp
        ${JniSource}/*.c
        )

add_library(StreamMediaLib SHARED ${Src_list})

#add_library(openssl-crypto STATIC IMPORTED)
#set_target_properties(openssl-crypto
#        PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libcrypto.so)
#
##添加加密工具(md5, base64, des, aes , asa) des加密 part-2
#add_library(openssl-ssl STATIC IMPORTED)
#set_target_properties(openssl-ssl
#        PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libssl.so)
#
##添加压缩库
#add_library(z SHARED IMPORTED)
#set_target_properties(z
#        PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libz.so)


#FFmpeg
add_library(avcodec STATIC IMPORTED)
set_target_properties(avcodec
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavcodec.so)

add_library(avfilter STATIC IMPORTED)
set_target_properties(avfilter
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavfilter.so)

add_library(avformat STATIC IMPORTED)
set_target_properties(avformat
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavformat.so)

add_library(avutil STATIC IMPORTED)
set_target_properties(avutil
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavutil.so)

add_library(swresample STATIC IMPORTED)
set_target_properties(swresample
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libswresample.so)

add_library(swscale SHARED IMPORTED)
set_target_properties(swscale
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libswscale.so)

target_link_libraries( # Specifies the target library.
        StreamMediaLib

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        android
        mediandk
        OpenMAXAL
        OpenSLES
        GLESv2
        EGL
#
        #z
        #crypto
        #ssl
        #openssl-crypto
        #openssl-ssl

        avcodec avfilter avformat avutil  swresample swscale
        )