# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)
add_definitions (-Wno-format-security)
#[[set(PATCHES_PATH "${CMAKE_CURRENT_LIST_DIR}/patches")
set(ASSETS_PATH "${CMAKE_CURRENT_LIST_DIR}/binray")
set(BUILD_TOOLS_PATH "${CMAKE_CURRENT_LIST_DIR}/")
set(LLVM_OBFUSCATOR_PATH "${BUILD_TOOLS_PATH}/tools/llvm5.0_obfuscator")
set(EXECUTABLE_OUTPUT_PATH "${ASSETS_PATH}/")
if (CMAKE_HOST_WIN32)
    set(EXE_SUFFIX ".exe")
elseif(CMAKE_HOST_UNIX)
    set(EXE_SUFFIX "")
else()
    message(FATAL_ERROR "Unsupported platform!")
endif()

execute_process(
        COMMAND ${CMAKE_COMMAND}
        ${CMAKE_CURRENT_LIST_DIR}/tools/llvm5.0_obfuscator
        -DLLVM_OBFUSCATOR_PATH=${LLVM_OBFUSCATOR_PATH}
        -DPATCHES_PATH=${PATCHES_PATH}
        -DBUILD_TOOLS_PATH=${BUILD_TOOLS_PATH}
        WORKING_DIRECTORY ${LLVM_OBFUSCATOR_PATH}/llvm_obfuscator
        COMMAND ${CMAKE_COMMAND}
        --build .
        WORKING_DIRECTORY ${LLVM_OBFUSCATOR_PATH}/llvm_obfuscator
)

set(CMAKE_C_COMPILER ${LLVM_OBFUSCATOR_PATH}/bin/clang${EXE_SUFFIX})
set(CMAKE_CXX_COMPILER ${LLVM_OBFUSCATOR_PATH}/bin/clang++${EXE_SUFFIX})

#set(OBFUSCATOR_FLAGS "-mllvm -sub -mllvm -sub_loop=1 -mllvm -fla -mllvm -sobf -mllvm -seed=0xdeadbeaf -mllvm -split -mllvm -split_num=2 -mllvm -bcf -mllvm -bcf_prob=10")

string(APPEND CMAKE_C_FLAGS " ${OBFUSCATOR_FLAGS}")
string(APPEND CMAKE_CXX_FLAGS " ${OBFUSCATOR_FLAGS}")

file(GLOB_RECURSE cpp_srcs "src/main/cpp/*.c" "src/main/cpp/*.cpp")
if(ANDROID)
    message(STATUS "ANDROID")
elseif(APPLE)
    message(STATUS "APPLE")
elseif(WIN32)
    message(STATUS "WIN32")
elseif(UNIX)
    message(STATUS "UNIX")
else()
    message(FATAL_ERROR "OTHER")
endif()]]
add_library(HostConf SHARED
        src/main/cpp/HostLib.cpp
        src/main/cpp/checksignature.h
        src/main/cpp/checksignature.c
        src/main/cpp/check_emulator.h
        src/main/cpp/check_emulator.c
        src/main/cpp/debugger.h
        src/main/cpp/debugger.c
        src/main/cpp/aes.h
        src/main/cpp/base64.h
        src/main/cpp/aes.c
        src/main/cpp/base64.c
        src/main/cpp/md5.cpp
        src/main/cpp/HostLib.h
        )
find_library(
        log-lib
        log)
#target_include_directories(HostConf PUBLIC
#        jni/include)

target_link_libraries(
        HostConf
        ${log-lib}
)

link_directories(
        lib
)



