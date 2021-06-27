package com.example.graduation_project_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    public Button how;
    public Button camera;
    public Button upload;
    public int REQUEST_TAKE_GALLERY_VIDEO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        how = (Button) findViewById(R.id.howTo);
        camera = (Button) findViewById(R.id.camera);
        upload = (Button) findViewById(R.id.upload);

        how.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, How.class);
                startActivity(intent);
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(intent, 1);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent videoIntent = new Intent();
                videoIntent.setType("video/*");
                videoIntent.setAction(Intent.ACTION_GET_CONTENT);
                videoIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(videoIntent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);


            }
        });
    }
//    public String getRealPathFromURI(Uri contentUri) {
//        String[] proj = { MediaStore.Images.Media.DATA };
//        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
//    public static String getFileName(String path) {
//        int idx = path.lastIndexOf("/");
//        String filename = path;
//        if (idx >= 0) {
//            filename = path.substring(idx + 1, path.length());
//        }
//        return filename;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Intent intent = new Intent(MainActivity.this, Camera.class);
                startActivity(intent);
            }
//            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
////                Uri videoUri = data.getData();
////                String videoPath = getRealPathFromURI(videoUri);
////                String videoPath = data.getData().toString();
////                String fileName = getFileName(videoPath);
////                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);
////                OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
////
////                Frame frame = grabber.grab();
////                Mat mat = converterToMat.convert(frame);
////                Log.d("filename", videoPath);
//                new OpenCVNativeLoader().init();
//                VideoCapture cap = new VideoCapture(0);
//                cap.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
//                cap.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
//                if (cap.isOpened()) {
//                    cap.read(frame);
//                    Imgproc.cvtColor(frame, gray, Imgproc.COLOR_RGB2GRAY);
//                    dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_6X6_250);
//                    parameters = DetectorParameters.create();
//                    corners = new LinkedList<>();
//                    ids = new MatOfInt();
//                    Aruco.detectMarkers(gray, dictionary, corners, ids, parameters);
//                    if (corners.size() > 0) {
//                        rvecs = new Mat();
//                        tvecs = new Mat();
//                        cameraMatrix = Mat.eye(3, 3, CvType.CV_64FC1);
//                        distCoeffs = Mat.zeros(5, 1, CvType.CV_64FC1);
//                        Aruco.estimatePoseSingleMarkers(corners, 0.05f, cameraMatrix, distCoeffs, rvecs, tvecs);
//                        for (int i = 0; i < ids.toArray().length; i++) {
//                            Aruco.drawAxis(frame, cameraMatrix, distCoeffs, rvecs.row(i), tvecs.row(i), 0.1f);
//                            double[] x = corners.get(0).get(0, 0);
//                            Log.d("displacement", x.toString());
//                        }
//                    }
//                }
//                cap.release();
//                //TODO: putExtra (Key, value)
//
//                Intent intent = new Intent(MainActivity.this, Upload.class);
//                startActivity(intent);
//            }
        }
    }
}