package com.example.graduation_project_mobile_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.content.Intent;
import android.view.View;

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

        how.setOnClickListener( new View.OnClickListener() {
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
                startActivityForResult(Intent.createChooser(videoIntent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);


            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                Uri videoUri = data.getData();
                //TODO: openCV processing in here using VideoCapture and return array to be sent to the Upload activity
                Intent intent = new Intent(MainActivity.this, Upload.class);
                //TODO: putExtra (Key, value)
                startActivity(intent);
            }
        }
    }
}