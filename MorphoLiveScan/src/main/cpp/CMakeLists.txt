# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Declares and names the project.

project("morpholivescan")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

set(lib_DIR ${CMAKE_SOURCE_DIR}/../../../libs)
include_directories(${lib_DIR}/include)

add_library( # Sets the name of the library.
             mls-jni

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             mls-jni.cpp )

set_target_properties(mls-jni PROPERTIES LINKER_LANGUAGE CXX)
# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

add_library(TPAPI SHARED IMPORTED)
set_property(TARGET TPAPI PROPERTY IMPORTED_LOCATION ${lib_DIR}/${ANDROID_ABI}/libTPAPI.so)

add_library(SaliJag1 SHARED IMPORTED)
set_property(TARGET SaliJag1 PROPERTY IMPORTED_LOCATION ${lib_DIR}/${ANDROID_ABI}/libSaliJag1.so)

add_library(IdxTiff SHARED IMPORTED)
set_property(TARGET IdxTiff PROPERTY IMPORTED_LOCATION ${lib_DIR}/${ANDROID_ABI}/libIdxTiff.so)

add_library(usb_usb_cl_1.0.24 SHARED IMPORTED)
set_property(TARGET usb_usb_cl_1.0.24 PROPERTY IMPORTED_LOCATION ${lib_DIR}/${ANDROID_ABI}/libusb_usb_cl_1.0.24.so)

add_library(c++_shared SHARED IMPORTED)
set_property(TARGET c++_shared PROPERTY IMPORTED_LOCATION ${lib_DIR}/${ANDROID_ABI}/libc++_shared.so)

add_library(ilvmanager SHARED IMPORTED)
set_property(TARGET ilvmanager PROPERTY IMPORTED_LOCATION ${lib_DIR}/${ANDROID_ABI}/libilvmanager.so)

add_library(mtopslim SHARED IMPORTED)
set_property(TARGET mtopslim PROPERTY IMPORTED_LOCATION ${lib_DIR}/${ANDROID_ABI}/libmtopslim.so)

add_library(usb-cl SHARED IMPORTED)
set_property(TARGET usb-cl PROPERTY IMPORTED_LOCATION ${lib_DIR}/${ANDROID_ABI}/libusb-cl.so)

add_library(SaliMorphoTopSlimV3 SHARED IMPORTED)
set_property(TARGET SaliMorphoTopSlimV3 PROPERTY IMPORTED_LOCATION ${lib_DIR}/${ANDROID_ABI}/libSaliMorphoTopSlimV3.so)

target_link_libraries( # Specifies the target library.
                       mls-jni
                       -ljnigraphics

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
                       TPAPI
                       SaliJag1
                       IdxTiff
                       usb_usb_cl_1.0.24
                       c++_shared
                       ilvmanager
                       mtopslim
                       usb-cl
                       SaliMorphoTopSlimV3 )
