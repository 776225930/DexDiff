cmake_minimum_required(VERSION 3.4.1)
#把bzip2目录下的源文件 用MYSOURCE表示
#加入编译路径 相当于添加依赖
aux_source_directory(bzip2 MYSOURCES)


add_library( # Sets the name of the library.
        bspatch_utils
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        native-lib.cpp
        bspatch.c
        ${MYSOURCES})


find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# cpp设置头文件查找路径
include_directories(bzip2)

target_link_libraries( # Specifies the target library.
        bspatch_utils
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})