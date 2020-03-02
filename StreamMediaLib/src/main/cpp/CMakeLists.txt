# CMakeList.txt: StreamMediaClientDemo 的 CMake 项目，在此处包括源代码并定义
# 项目特定的逻辑。
#
#cmake_minimum_required (VERSION 3.8)
#设置工程源码根目录

#set(CommonLibRoot ${CMAKE_CURRENT_SOURCE_DIR}/src)
#set(CommonLibHeader ${CMAKE_CURRENT_SOURCE_DIR}/include)

#设置头文件目录
#include_directories(${CommonLibRoot})
#include_directories(${CommonLibHeader})
#include_directories(${CMAKE_CURRENT_SOURCE_DIR}/test)

#收集源代码
#file(GLOB CommonLib_src_list ${CommonLibRoot}/*/*.cpp ${CommonLibHeader}/*/*.h ${CommonLibHeader}/*.h )

#添加库
#add_library(commonlib STATIC ${CommonLib_src_list})
#list(APPEND LINK_LIB_LIST commonlib)

# 将源代码添加到此项目的可执行文件。
#add_subdirectory("tests")
# TODO: 如有需要，请添加测试并安装目标。
