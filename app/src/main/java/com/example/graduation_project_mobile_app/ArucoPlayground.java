package com.example.graduation_project_mobile_app;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.DetectorParameters;
import org.opencv.aruco.Dictionary;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import java.util.LinkedList;
import java.util.List;


public class ArucoPlayground extends Activity implements CvCameraViewListener2 {
    public Mat cameraMatrix = new Mat();
    public Mat distCoeffs = new Mat();
    public Mat rvecs = new Mat();
    public Mat tvecs = new Mat();
    public MatOfInt ids = new MatOfInt();
    public List<Mat> corners;
    public DetectorParameters parameters;
    public Mat gray = new Mat();
    public Mat frame = new Mat();
    public Dictionary dictionary;
    public CameraBridgeViewBase camera;


    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("CheckOpen", "OpenCV loaded successfully");
                    camera.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d("success", "OpenCV is intialised");
        } else {
            Log.d("fail", "OpenCV is not initialised");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Ensure that this result is for the camera permission request
        if (requestCode == 1) {
            // Check if the request was granted or denied
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // The request was granted -> tell the camera view
                camera.setCameraPermissionGranted();
            } else {
                // The request was denied -> tell the user and exit the application
                Toast.makeText(this, "Camera permission required.", Toast.LENGTH_LONG).show();
                this.finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        camera = findViewById(R.id.main_camera);
        camera.enableFpsMeter();
        camera.setVisibility(SurfaceView.VISIBLE);
        camera.setCvCameraViewListener(this);
        ActivityCompat.requestPermissions(ArucoPlayground.this,
                new String[]{Manifest.permission.CAMERA},
                1);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
        } else {
            Log.d("perm", "Permissions granted");
            camera.setCameraPermissionGranted();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug())
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        else
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (camera != null)
            camera.disableView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCameraViewStarted(int i, int i1) {
        frame = new Mat();
        corners = new LinkedList<>();
        parameters = DetectorParameters.create();
        dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_6X6_250);
    }

    @Override
    public void onCameraViewStopped() {
        frame.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame cvCameraViewFrame) {
        Imgproc.cvtColor(cvCameraViewFrame.rgba(), frame, Imgproc.COLOR_RGBA2RGB);
        gray = cvCameraViewFrame.gray();
        ids = new MatOfInt();
        corners.clear();
        Aruco.detectMarkers(gray, dictionary, corners, ids, parameters);
        if (corners.size() > 0) {
            Aruco.drawDetectedMarkers(frame, corners, ids);
            cameraMatrix = Mat.eye(3, 3, CvType.CV_64FC1);
            distCoeffs = Mat.zeros(5, 1, CvType.CV_64FC1);
            Aruco.estimatePoseSingleMarkers(corners, 0.04f, cameraMatrix, distCoeffs, rvecs, tvecs);
            for (int i = 0; i < ids.toArray().length; i++) {
                Aruco.drawAxis(frame, cameraMatrix, distCoeffs, rvecs, tvecs, 0.02f);
            }
        }
        return frame;
    }


}