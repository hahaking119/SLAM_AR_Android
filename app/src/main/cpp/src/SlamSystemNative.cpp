//
// Created by hinata on 2023/11/8.
//

#include "include/SlamSystemNative.h"
#include "System.h"
#include <jni.h>
#include <opencv2/core/eigen.hpp>
#include <opencv2/opencv.hpp>

static ORB_SLAM3::System *pSystem = nullptr;
std::chrono::steady_clock::time_point t0;
double tframe=0;


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eqgis_slam_core_SlamSystemNative_init(JNIEnv *env, jclass clazz, jstring voc_file_path,
                                               jstring settings_file_path, jint type) {
    //SLAM系统初始化
    const char *p_voc_file_path = env->GetStringUTFChars(voc_file_path, 0);
    if (!p_voc_file_path) {
        env->ReleaseStringUTFChars(voc_file_path, p_voc_file_path);
        return false;
    }
    const char *p_settings_file_path = env->GetStringUTFChars(settings_file_path, 0);
    if (!p_settings_file_path) {
        env->ReleaseStringUTFChars(settings_file_path, p_settings_file_path);
        return false;
    }
    if (!pSystem) {
        try {
            ORB_SLAM3::System::eSensor e;
            switch (type) {
                case 0:
                    e = ORB_SLAM3::System::MONOCULAR;
                    break;
                case 1:
                    e = ORB_SLAM3::System::STEREO;
                    break;
                case 2:
                    e = ORB_SLAM3::System::RGBD;
                    break;
//                case 3:
//                    e = ORB_SLAM3::System::IMU_MONOCULAR;
//                    break;
//                case 4:
//                    e = ORB_SLAM3::System::IMU_STEREO;
//                    break;
//                case 5:
//                    e = ORB_SLAM3::System::IMU_RGBD;
                    break;
            }
            pSystem = new ORB_SLAM3::System(p_voc_file_path, p_settings_file_path,
                                            e/*ORB_SLAM3::System::MONOCULAR*/);
            pSystem->Reset();
        } catch (const std::exception& e) {
            std::cerr << "An exception occurred: " << e.what() << std::endl;
            // 在这里添加适当的异常处理逻辑
            LOGE("An exception occurred:init %s\n",e.what());
            env->ReleaseStringUTFChars(voc_file_path, p_voc_file_path);
            env->ReleaseStringUTFChars(settings_file_path, p_settings_file_path);
            return false;
        }
    }
    env->ReleaseStringUTFChars(voc_file_path, p_voc_file_path);
    env->ReleaseStringUTFChars(settings_file_path, p_settings_file_path);
    return true;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eqgis_slam_core_SlamSystemNative_dispose(JNIEnv *env, jclass clazz) {
    //销毁SLAM系统
    if (pSystem){
        if (!pSystem->isShutDown()){
            pSystem->Shutdown();
            delete pSystem;
            return true;
        }
    }
    return false;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_eqgis_slam_core_SlamSystemNative_getTrackingState(JNIEnv *env, jclass clazz){
    //获取状态
//    switch(SLAM->GetTrackingState()) {
//        case -1: {cv::putText(*pMat, "SYSTEM NOT READY", cv::Point(0,400), cv::FONT_HERSHEY_SIMPLEX, 0.5, cv::Scalar(255,0,0),2); }break;
//        case 0:  {cv::putText(*pMat, "NO IMAGES YET", cv::Point(0,400), cv::FONT_HERSHEY_SIMPLEX, 0.5, cv::Scalar(255,0,0),2); }break;
//        case 1:  {cv::putText(*pMat, "SLAM NOT INITIALIZED", cv::Point(0,400), cv::FONT_HERSHEY_SIMPLEX, 0.5, cv::Scalar(255,0,0),2); }break;
//        case 2:  {cv::putText(*pMat, "SLAM ON", cv::Point(0,400), cv::FONT_HERSHEY_SIMPLEX, 0.5, cv::Scalar(0,255,0),2); break;}
//        case 3:  {cv::putText(*pMat, "SLAM LOST", cv::Point(0,400), cv::FONT_HERSHEY_SIMPLEX, 0.5, cv::Scalar(255,0,0), 2); break;}
//        default:break;
//    }
    if (pSystem){
        return pSystem->GetTrackingState();
    } else{
        //SLAM被销毁（或不存在）
        return -2;
    }
}

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_eqgis_slam_core_SlamSystemNative_trackMonocular(JNIEnv *env, jclass clazz,
                                                         jlong mat_native_obj_addr) {
    //处理单目相机获取的图像
    cv::Mat *pMat = (cv::Mat*)mat_native_obj_addr;
//    cv::Mat *srcMat = (cv::Mat*)mat_native_obj_addr;
//    cv::Mat *pMat;
//    cv::resize(*srcMat,*pMat,cv::Size(srcMat->cols / 2, srcMat->rows / 2));

    cv::Mat pose;

    std::chrono::steady_clock::time_point t1 = std::chrono::steady_clock::now();
    tframe  = std::chrono::duration_cast < std::chrono::duration < double >> (t1 - t0).count();
    // Pass the image to the SLAM system
    cout << "tframe = " << tframe << endl;
    clock_t start,end;
    start=clock();
//    pose = pSystem->TrackMonocular(*pMat,tframe); // TODO change to monocular_inertial
    Sophus::SE3f Tcw_SE3f = pSystem->TrackMonocular(*pMat,tframe); // TODO change to monocular_inertial
    Eigen::Matrix4f Tcw_Matrix = Tcw_SE3f.matrix();

    cv::eigen2cv(Tcw_Matrix, pose);
    end = clock();

    LOGI("Get Pose Use Time=%f\n",((double)end-start)/CLOCKS_PER_SEC);

    static bool instialized =false;
    static bool markerDetected =false;
    if(pSystem->MapChanged()){
        instialized = false;
        markerDetected =false;
    }

    //Plane 平面计算
    if(!pose.empty()){
        //todo 计算Plane
    }

//    // 获取矩阵的底层数据指针
//    const float* data_ptr = Tcw_Matrix.data();
//    // 获取矩阵的大小（4x4）
//    int rows = Tcw_Matrix.rows();
//    int cols = Tcw_Matrix.cols();
//    // 创建一个一维的 float 数组来存储数据
//    float Tcw_data[16]; // 4x4 矩阵有 16 个元素
//    // 复制数据到一维数组
//    for (int i = 0; i < rows; i++) {
//        for (int j = 0; j < cols; j++) {
//            Tcw_data[i * cols + j] = data_ptr[i * cols + j];
//        }
//    }

    cv::Mat ima=pose;
    jfloatArray resultArray = env->NewFloatArray(ima.rows * ima.cols);
    jfloat *resultPtr;

    resultPtr = env->GetFloatArrayElements(resultArray, 0);
    for (int i = 0; i < ima.rows; i++)
        for (int j = 0; j < ima.cols; j++) {
            float tempdata=ima.at<float>(i,j);
            resultPtr[i * ima.rows + j] =tempdata;
        }
    env->ReleaseFloatArrayElements(resultArray, resultPtr, 0);
    return resultArray;
}