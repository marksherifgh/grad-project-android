package com.example.graduation_project_mobile_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Upload extends AppCompatActivity {
    public Intent fileIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("video/*");
        startActivityForResult(fileIntent, 10);
    }

}