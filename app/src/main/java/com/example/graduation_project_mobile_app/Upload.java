package com.example.graduation_project_mobile_app;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

public class Upload extends AppCompatActivity {

    public Button toggle;
    public EditText cutoff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        toggle = (Button) findViewById(R.id.toggle);
        cutoff = (EditText) findViewById(R.id.cutoffFrequency);
        Spinner spinner = (Spinner) findViewById(R.id.graph_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.graph_spinner, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        series.setDrawDataPoints(true);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Log.d("datapoint", "datapoint: " + dataPoint.toString());
                Toast.makeText(Upload.this, "X = " + dataPoint.getX() + " Y = " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle.getText() == "Frequency domain"){
                    toggle.setText("Time domain");
                    cutoff.setVisibility(View.VISIBLE);
                }
                else {
                    toggle.setText("Frequency domain");
                    cutoff.setVisibility(View.INVISIBLE);
                }
            }
        });
        graph.getGridLabelRenderer().setGridColor(Color.WHITE);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().reloadStyles();
        graph.addSeries(series);

//      TODO: Set onCharge for cutoff frequency to change the graph
//      TODO: get graphview by id
//      TODO: pass data from opencv to graphview
    }
}

