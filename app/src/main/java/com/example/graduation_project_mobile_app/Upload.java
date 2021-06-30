package com.example.graduation_project_mobile_app;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

public class Upload extends AppCompatActivity {

    public Button toggle;
    public EditText cutoff;
    public Spinner spinner;
    public String[] list = {"Displacement", "Velocity", "Acceleration"};
    public double[] yList;
    public double[] vList;
    public double[] aList;
    public double[] tList;
    public DataPoint[] datay;
    public DataPoint[] datav;
    public DataPoint[] dataa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                yList = null;
                tList = null;
            } else {
                yList = (double[]) extras.get("yList");
                tList = (double[]) extras.get("tList");
            }
        } else {
            yList = (double[]) savedInstanceState.getSerializable("yList");
            tList = (double[]) savedInstanceState.getSerializable("tList");
        }
        try {
            vList = new double[yList.length - 1];
            aList = new double[vList.length - 1];
            double tCorrection = tList[0];
            for (int i = 0; i < tList.length; i++) {
                tList[i] = tList[i] - tCorrection;
                if (i == tList.length - 1) {
                    continue;
                }
                double dx = yList[i + 1] - yList[i];
                double dt = tList[i + 1] - tList[i];
                double v = dx / dt;
                vList[i] = v;
            }
            for (int i = 0; i < vList.length; i++) {
                if (i == vList.length - 1) {
                    continue;
                }
                double dv = vList[i + 1] - vList[i];
                double dt = tList[i + 1] - tList[i];
                double a = dv / dt;
                aList[i] = a;
            }
        } catch (Exception e) {

        }
        // ------ Definitions ------ //

        toggle = (Button) findViewById(R.id.toggle);
        cutoff = (EditText) findViewById(R.id.cutoffFrequency);
        spinner = (Spinner) findViewById(R.id.graph_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.graph_spinner, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        datay = new DataPoint[yList.length];
        datav = new DataPoint[vList.length - 1];
        dataa = new DataPoint[aList.length - 1];
        for (int i = 0; i < yList.length; i++) {
            datay[i] = new DataPoint(tList[i], yList[i]);
        }
        for (int i = 0; i < vList.length - 1; i++) {
            datav[i] = new DataPoint(tList[i], vList[i]);
        }
        for (int i = 0; i < aList.length - 1; i++) {
            dataa[i] = new DataPoint(tList[i], aList[i]);
        }
        LineGraphSeries<DataPoint> series_d = new LineGraphSeries<DataPoint>(datay);
        series_d.setDrawDataPoints(true);

        LineGraphSeries<DataPoint> series_v = new LineGraphSeries<DataPoint>(datav);
        series_v.setDrawDataPoints(true);

        LineGraphSeries<DataPoint> series_a = new LineGraphSeries<DataPoint>(dataa);
        series_a.setDrawDataPoints(true);

        // ------ Event Listeners ------ //

        series_d.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Log.d("datapoint", "datapoint: " + dataPoint.toString());
                Toast.makeText(Upload.this, "X = " + dataPoint.getY() + " T = " + dataPoint.getX(), Toast.LENGTH_SHORT).show();
            }
        });

        series_v.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Log.d("datapoint", "datapoint: " + dataPoint.toString());
                Toast.makeText(Upload.this, "V = " + dataPoint.getY() + " T = " + dataPoint.getX(), Toast.LENGTH_SHORT).show();
            }
        });

        series_a.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Log.d("datapoint", "datapoint: " + dataPoint.toString());
                Toast.makeText(Upload.this, "A = " + dataPoint.getY() + " T = " + dataPoint.getX(), Toast.LENGTH_SHORT).show();
            }
        });

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle.getText() == "Frequency domain") {
                    toggle.setText("Time domain");
                    cutoff.setVisibility(View.VISIBLE);
                } else {
                    toggle.setText("Frequency domain");
                    cutoff.setVisibility(View.INVISIBLE);
                }
            }
        });

        // ------ Graph Settings ------ //

        graph.getGridLabelRenderer().setGridColor(Color.WHITE);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);
        graph.addSeries(series_d);
        graph.getGridLabelRenderer().reloadStyles();

        // ------ Spinner Settings ------ //

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                graph.removeSeries(series_d);
                if (list[position] == "Acceleration") {
                    graph.removeSeries(series_a);
                    graph.addSeries(series_v);
                } else if (list[position] == "Velocity") {
                    graph.removeSeries(series_v);
                    graph.addSeries(series_a);

                } else {
                    graph.removeSeries(series_a);
                    graph.removeSeries(series_v);
                    graph.addSeries(series_d);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

//      TODO: Set onCharge for cutoff frequency to change the graph
    }
}


