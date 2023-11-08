cmake_minimum_required(VERSION 3.18.1)

project("SLAM_CORE")

#设置opencv android sdk的路径
set(OpenCV_DIR D:/0_proj/Holo_SDK/ORB-SLAM/OpenCV-android-sdk/sdk/native/jni)

find_package(OpenCV REQUIRED)
include_directories(${OpenCV_INCLUDE_DIRS})
add_subdirectory(orb-slam3)
include_directories(
        .
        orb-slam3
        orb-slam3/include
        orb-slam3/include/CameraModels
        orb-slam3/Thirdparty
        orb-slam3/Thirdparty/boost/include
        orb-slam3/Thirdparty/eigen3/
        orb-slam3/Thirdparty/eigen3/Eigen
        orb-slam3/Thirdparty/eigen3/Eigen/src
        orb-slam3/Thirdparty/openssl/include
        orb-slam3/Thirdparty/Sophus
)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#
# In this top level CMakeLists.txt, ${CMAKE_PROJECT_NAME} is used to define
# the target library name; in the sub-module's CMakeLists.txt, ${PROJECT_NAME}
# is preferred for the same purpose.
#
# In order to load a library into your app from Java/Kotlin, you must call
# System.loadLibrary() and pass the name of the library defined here;
# for GameActivity/NativeActivity derived applications, the same library name must be
# used in the AndroidManifest.xml file.
add_library(
        ${PROJECT_NAME}
        SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.

        native-lib.cpp
        Plane.cpp
        UIUtils.cpp
        Utils.cpp
        Matrix.cpp
)

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(
        ${PROJECT_NAME}
        # List libraries link to the target library
        orbslam3
        android
        jnigraphics
        log)