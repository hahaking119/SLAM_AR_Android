#ifndef EQ_SLAM_SLAMSYSTEMNATIVE_H
#define EQ_SLAM_SLAMSYSTEMNATIVE_H

#include <android/log.h>
#define  LOG_TAG    "SlamSystemNative"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#include <jni.h>

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eqgis_slam_core_SlamSystemNative_init(JNIEnv *env, jclass clazz, jstring voc_file_path,
                                               jstring settings_file_path, jint type);

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eqgis_slam_core_SlamSystemNative_dispose(JNIEnv *env, jclass clazz);

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_eqgis_slam_core_SlamSystemNative_trackMonocular(JNIEnv *env, jclass clazz,
                                                         jlong mat_native_obj_addr);

extern "C"
JNIEXPORT jint JNICALL
Java_com_eqgis_slam_core_SlamSystemNative_getTrackingState(JNIEnv *env, jclass clazz);


#endif //EQ_SLAM_SLAMSYSTEMNATIVE_H