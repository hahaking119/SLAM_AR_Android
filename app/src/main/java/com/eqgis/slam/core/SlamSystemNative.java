package com.eqgis.slam.core;

import androidx.annotation.Keep;

/**
 * SLAM相关Native方法类
 * <p></p>
 * <pre>
 *     注意：不对外开放接口，同时不能被混淆
 * </pre>
 *
 * @author tanyx 2023/11/8
 * @version 1.0
 **/

@Keep
class SlamSystemNative {

    /**
     * SLAM系统初始化
     * @param vocFilePath voc文件路径
     * @param settingsFilePath 相机配置文件路径
     * @param sensorType
     * <pre>
     *     MONOCULAR=0,
     *     STEREO=1,
     *     RGBD=2,
     *     IMU_MONOCULAR=3,
     *     IMU_STEREO=4,
     *     IMU_RGBD=5,
     * </pre>
     * @return
     */
    public native boolean init(String vocFilePath,String settingsFilePath,int sensorType);

    /**
     * 销毁SLAM系统
     * @return
     */
    public native boolean dispose();

    /**
     * 获取跟踪状态
     * <pre>
     *     返回值含义：
     *     -2:"SYSTEM NOT EXIST"
     *     -1:"SYSTEM NOT READY"
     *     0:"NO IMAGES YET"
     *     1:"SLAM NOT INITIALIZED"
     *     2:"SLAM ON"
     *     3:"SLAM LOST"
     * </pre>
     * @return
     */
    public native int getTrackingState();

    /**
     * 处理单目RGB相机的图像
     * <pre>
     *     Input images: RGB (CV_8UC3) or grayscale (CV_8U). RGB is converted to grayscale.
     * </pre>
     * @param matNativeObjAddr Mat的Native地址
     * @return
     */
    public native float[] trackMonocular(long matNativeObjAddr);
}
