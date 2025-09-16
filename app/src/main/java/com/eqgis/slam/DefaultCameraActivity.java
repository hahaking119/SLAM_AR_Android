package com.eqgis.slam;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Size;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.WindowDecorActionBar;
import androidx.core.app.ActivityCompat;

import com.eqgis.slam.core.SlamSystem;
import com.eqgis.slam.R;

import java.util.Collections;
import java.util.List;

/**
 * 默认
 */
public class DefaultCameraActivity extends CameraActivity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    private Mat rgba;
    private TextView textInfo;
    private Mat gray;

    public DefaultCameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
        },0);

        setContentView(R.layout.tutorial1_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
        textInfo = findViewById(R.id.text_info);

//        boolean init = SlamSystem.init("/storage/emulated/0/SLAM/VOC/ORBvoc.bin","/storage/emulated/0/SLAM/Calibration/PARAconfig.yaml",0);
        // 修改后：使用assets中的文件路径
        boolean init = SlamSystem.init("ORBvoc.txt.arm.bin","CameraSettings.yaml",0);

        Toast.makeText(this, "SLAM初始化：", Toast.LENGTH_SHORT).show();

        findViewById(R.id.test_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                float[] floats = SlamSystem.trackMonocular(rgba.getNativeObjAddr());
                tracking();
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        rgba = inputFrame.rgba();
        gray = inputFrame.gray();
        tracking();
        return rgba;
    }

    private void tracking() {
        long start = System.currentTimeMillis();
        float[] floats = SlamSystem.trackMonocular(rgba.getNativeObjAddr());
        long ms = System.currentTimeMillis() - start;
//        int trackingState = state;
        int trackingState = SlamSystem.getTrackingState();
        if (floats.length == 16){
            StringBuffer mat = new StringBuffer();
            mat.append("\n");
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    mat.append(" | " + floats[4 * i + j]);
                }
                mat.append("\n");
            }

            final String info = mat.toString() + "\n" + "state: " + trackingState
                    + "\n spend: " + ms + " ms";
            Log.e(TAG, "onCameraFrame:ikkyu2 test poseMat：" + info);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textInfo.setText(info);
                }
            });
        }else {
            final String info = "\n" + "state: " + trackingState
                    + "\n spend: " + ms + " ms";
            Log.e(TAG, "onCameraFrame:ikkyu2 test poseMat：" + info);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textInfo.setText(info);
                }
            });
        }

//        SlamSystem.processCameraFrame(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr());
    }
}
