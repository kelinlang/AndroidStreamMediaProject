# CMakeList.txt: 顶层 CMake 项目文件，在此处执行全局配置
# 并包含子项目。
#
#cmake_minimum_required (VERSION 3.10.2)

set(CMAKE_CXX_STANDARD 14)
#set(CMAKE_EXE_LINKER_FLAGS)

#设置链接库列表全局变量
#set(LINK_LIB_LIST  )

#include_directories("./ThirdLib/ffmpeg/linux/include")
#list(APPEND LINK_LIB_LIST  avcodec avdevice avfilter avformat avutil postproc swresample swscale)

#add_subdirectory ("./src/main/StreamMediaProject/CommonLib")
#add_subdirectory ("./src/main/StreamMediaProject/StreamMediaLib")
#add_subdirectory ("./src/main/cpp")


#设置工程源码根目录
#set(CommonLibRoot ${CMAKE_CURRENT_SOURCE_DIR}/src/main/StreamMediaProject/CommonLib/src)
#set(CommonLibHeader ${CMAKE_CURRENT_SOURCE_DIR}/src/main/StreamMediaProject/CommonLib/include)
#收集源代码
#file(GLOB CommonLib_src_list ${CommonLibRoot}/*/*.cpp ${CommonLibHeader}/*/*.h ${CommonLibHeader}/*.h )