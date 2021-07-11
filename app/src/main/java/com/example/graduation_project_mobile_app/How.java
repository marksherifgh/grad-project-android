package com.example.graduation_project_mobile_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class How extends AppCompatActivity {

    public Button getAruco;
    public VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how);
        getAruco = (Button) findViewById(R.id.getAruco);
        video = (VideoView) findViewById(R.id.videoview);
        video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.help);
        video.start();
        getAruco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://chev.me/arucogen/"));
                startActivity(intent);
            }
        });

    }

}