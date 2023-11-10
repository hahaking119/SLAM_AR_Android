package com.eqgis.slam.core;

/**
 * SLAM系统
 * <p></p>
 * <pre>SampleCode:
 * </pre>
 *
 * @author tanyx 2023/11/8
 * @version 1.0
 **/
public class SlamSystem {
    private static SlamSystemNative mSlamSystemNative = new SlamSystemNative();
    static {
//        System.loadLibrary("slam");
        System.loadLibrary("SLAM_CORE");
    }


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
    public static boolean init(String vocFilePath,String settingsFilePath,int sensorType){
        return mSlamSystemNative.init(vocFilePath,settingsFilePath,sensorType);
    }

    /**
     * 销毁SLAM系统
     * @return
     */
    public static boolean dispose(){
        return mSlamSystemNative.dispose();
    }

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
    public static int getTrackingState(){
        return mSlamSystemNative.getTrackingState();
    }

    /**
     * 处理单目RGB相机的图像
     * <pre>
     *     Input images: RGB (CV_8UC3) or grayscale (CV_8U). RGB is converted to grayscale.
     * </pre>
     * @param matNativeObjAddr Mat的Native地址
     * @return
     */
    public static float[] trackMonocular(long matNativeObjAddr){
        return mSlamSystemNative.trackMonocular(matNativeObjAddr);
    }

    /**
     * 重置SLAM
     * @return
     */
    public static boolean resetSystem(){
        //重置系统
        return true;
    }
}
