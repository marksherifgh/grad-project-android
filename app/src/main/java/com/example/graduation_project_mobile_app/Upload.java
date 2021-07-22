package com.example.graduation_project_mobile_app;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.io.File;
import java.io.FileOutputStream;

import uk.me.berndporr.iirj.Butterworth;

class FFT {

    int n, m;

    // Lookup tables. Only need to recompute when size of FFT changes.
    double[] cos;
    double[] sin;

    public FFT(int n) {
        this.n = n;
        this.m = (int) (Math.log(n) / Math.log(2));

        // Make sure n is a power of 2
        if (n != (1 << m))
            throw new RuntimeException("FFT length must be power of 2");

        // precompute tables
        cos = new double[n / 2];
        sin = new double[n / 2];

        for (int i = 0; i < n / 2; i++) {
            cos[i] = Math.cos(-2 * Math.PI * i / n);
            sin[i] = Math.sin(-2 * Math.PI * i / n);
        }

    }

    public void fft(double[] x, double[] y) {
        int i, j, k, n1, n2, a;
        double c, s, t1, t2;

        // Bit-reverse
        j = 0;
        n2 = n / 2;
        for (i = 1; i < n - 1; i++) {
            n1 = n2;
            while (j >= n1) {
                j = j - n1;
                n1 = n1 / 2;
            }
            j = j + n1;

            if (i < j) {
                t1 = x[i];
                x[i] = x[j];
                x[j] = t1;
                t1 = y[i];
                y[i] = y[j];
                y[j] = t1;
            }
        }

        // FFT
        n1 = 0;
        n2 = 1;

        for (i = 0; i < m; i++) {
            n1 = n2;
            n2 = n2 + n2;
            a = 0;

            for (j = 0; j < n1; j++) {
                c = cos[a];
                s = sin[a];
                a += 1 << (m - i - 1);

                for (k = j; k < n; k = k + n2) {
                    t1 = c * x[k + n1] - s * y[k + n1];
                    t2 = s * x[k + n1] + c * y[k + n1];
                    x[k + n1] = x[k] - t1;
                    y[k + n1] = y[k] - t2;
                    x[k] = x[k] + t1;
                    y[k] = y[k] + t2;
                }
            }
        }
    }
}

public class Upload extends AppCompatActivity {
    public TextView zeetaText;
    public EditText zeetaInput;
    public Button toggle;
    public Spinner spinner;
    public String[] list = {"Displacement", "Velocity", "Acceleration"};
    public double[] yList;
    public double[] vList;
    public double[] aList;
    public double[] tList;
    public double[] yfreqList;
    public double[] vfreqList;
    public double[] afreqList;
    public double[] frequency;
    public DataPoint[] datafy;
    public DataPoint[] datafv;
    public DataPoint[] datafa;
    public DataPoint[] datay;
    public DataPoint[] datav;
    public DataPoint[] dataa;
    public int f = 0;
    public int domain = 0;
    Butterworth butter = new Butterworth();

    public double[] fftCalculator(double[] re, double[] im) {
        if (re.length != im.length) return null;
        FFT fft = new FFT(re.length);
        fft.fft(re, im);
        double[] fftMag = new double[re.length];
        for (int i = 0; i < re.length; i++) {
            fftMag[i] = Math.pow(re[i], 2) + Math.pow(im[i], 2);
        }
        return fftMag;
    }

    public static int findPreviousPowerOf2(int n) {
        while ((n & n - 1) != 0) {
            n = n & n - 1;
        }
        return n;
    }

    public static double findZeeta(double[] x) {
        double peak1 = 0;
        double peak2 = 0;
        double peak3 = 0;
        double peak4 = 0;
        for (int i = 1; i < x.length; i++) {
            if (x[i - 1] < x[i] && x[i] > x[i + 1]) {
                if (peak1 == 0) {
                    peak1 = x[i];
                } else if (peak2 == 0) {
                    peak2 = x[i];
                } else if (peak3 == 0) {
                    peak3 = x[i];
                } else if (peak4 == 0) {
                    peak4 = x[i];
                } else {
                    break;
                }
            }

        }
        if (peak1 > peak2) {
            return Math.round(Math.log(peak1 / peak2) * 100.0) / 100.0;
        } else if (peak2 > peak3) {
            return Math.round(Math.log(peak2 / peak3) * 100.0) / 100.0;
        }
        return Math.round(Math.log(peak3 / peak4) * 100.0) / 100.0;
    }

    public void export(View view) {
        try {
            StringBuilder data = new StringBuilder();
            data.append("Time,Displacement,Velocity,Acceleration");
            for (int i = 0; i < aList.length; i++) {
                data.append("\n" + String.valueOf(tList[i]) + "," + String.valueOf(yList[i]) + "," + String.valueOf(vList[i]) + "," + String.valueOf(aList[i]));
            }

            FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "data.csv");
            Uri path = FileProvider.getUriForFile(context, "com.asu.graduation_project_mobile_app.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));

        } catch (Exception e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        butter.lowPass(2, 19, 1);
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

//        try {
        vList = new double[yList.length - 1];
        aList = new double[vList.length - 1];
        double tCorrection = tList[0];
        for (int i = 0; i < tList.length; i++) {
            tList[i] = Math.round((tList[i] - tCorrection) * 100.0) / 100.0;
            yList[i] = Math.round(butter.filter(yList[i]) * 100.0) / 100.0;
        }

        for (int i = 0; i < tList.length - 1; i++) {
            double dx = yList[i + 1] - yList[i];
            double dt = tList[i + 1] - tList[i];
            double v = dx / dt;
            vList[i] = Math.round(butter.filter(v) * 100.0) / 100.0;
        }

        for (int i = 0; i < vList.length - 1; i++) {
            double dv = vList[i + 1] - vList[i];
            double dt = tList[i + 1] - tList[i];
            double a = dv / dt;
            aList[i] = Math.round(butter.filter(a) * 100.0) / 100.0;
        }

        // ----- Frequency Calculations ----- //
        yfreqList = new double[findPreviousPowerOf2(yList.length)];
        vfreqList = new double[findPreviousPowerOf2(vList.length)];
        afreqList = new double[findPreviousPowerOf2(aList.length)];
        System.arraycopy(yList, 0, yfreqList, 0, yfreqList.length);
        System.arraycopy(vList, 0, vfreqList, 0, vfreqList.length);
        System.arraycopy(aList, 0, afreqList, 0, afreqList.length);
        double[] yZeroList = new double[yfreqList.length];
        double[] vZeroList = new double[yfreqList.length];
        double[] aZeroList = new double[yfreqList.length];
        double[] yMagList = fftCalculator(yfreqList, yZeroList);
        double[] vMagList = fftCalculator(vfreqList, vZeroList);
        double[] aMagList = fftCalculator(afreqList, aZeroList);
        frequency = new double[yMagList.length];
        for (int i = 0; i < frequency.length; i++) {
            if (i == 0) {
                frequency[i] = 17.0 / frequency.length;
            }
            frequency[i] = (17.0 / frequency.length) * i;
        }


        // ------ Definitions ------ //
        zeetaInput = (EditText) findViewById(R.id.zeetaInput);
        zeetaText = (TextView) findViewById(R.id.zeeta);
        toggle = (Button) findViewById(R.id.toggle);
        spinner = (Spinner) findViewById(R.id.graph_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.graph_spinner, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        GraphView graph = (GraphView) findViewById(R.id.graph);

        datay = new DataPoint[yList.length];
        datav = new DataPoint[vList.length - 1];
        dataa = new DataPoint[aList.length - 1];
        datafy = new DataPoint[yMagList.length];
        datafv = new DataPoint[vMagList.length];
        datafa = new DataPoint[aMagList.length];

        for (int i = 0; i < yList.length; i++) {
            datay[i] = new DataPoint(tList[i], yList[i]);
        }
        for (int i = 0; i < vList.length - 1; i++) {
            datav[i] = new DataPoint(tList[i], vList[i]);
        }
        for (int i = 0; i < aList.length - 1; i++) {
            dataa[i] = new DataPoint(tList[i], aList[i]);
        }
        for (int i = 0; i < yMagList.length; i++) {
            datafy[i] = new DataPoint(frequency[i], yMagList[i]);
        }
        for (int i = 0; i < yMagList.length; i++) {
            datafv[i] = new DataPoint(frequency[i], vMagList[i]);
        }
        for (int i = 0; i < yMagList.length; i++) {
            datafa[i] = new DataPoint(frequency[i], aMagList[i]);
        }
        double zeeta = findZeeta(yList);
        // ------ Graph init ------- //
        LineGraphSeries<DataPoint> series_y = new LineGraphSeries<DataPoint>(datay);
        series_y.setDrawDataPoints(true);

        LineGraphSeries<DataPoint> series_v = new LineGraphSeries<DataPoint>(datav);
        series_v.setDrawDataPoints(true);

        LineGraphSeries<DataPoint> series_a = new LineGraphSeries<DataPoint>(dataa);
        series_a.setDrawDataPoints(true);

        LineGraphSeries<DataPoint> series_fy = new LineGraphSeries<DataPoint>(datafy);
        series_fy.setDrawDataPoints(true);

        LineGraphSeries<DataPoint> series_fv = new LineGraphSeries<DataPoint>(datafy);
        series_fv.setDrawDataPoints(true);

        LineGraphSeries<DataPoint> series_fa = new LineGraphSeries<DataPoint>(datafy);
        series_fa.setDrawDataPoints(true);

        // ------ Event Listeners for points ------ //
        zeetaInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        if (Double.parseDouble(v.getText().toString()) <= zeeta * 1.05 && Double.parseDouble(v.getText().toString()) >= zeeta * 0.95) {
                            zeetaText.setText("Zeeta = " + String.valueOf(zeeta));
                            zeetaText.setTextColor(Color.parseColor("#00FF00"));
                        } else {
                            zeetaText.setText("Zeeta = " + String.valueOf(zeeta));
                            zeetaText.setTextColor(Color.parseColor("#FF0000"));
                        }
                        return true; // consume.
                    }
                }
                return false; // pass on to other listeners.
            }
        });
        series_y.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(Upload.this, "X = " + dataPoint.getY() + " T = " + dataPoint.getX(), Toast.LENGTH_SHORT).show();
            }
        });

        series_v.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(Upload.this, "V = " + dataPoint.getY() + " T = " + dataPoint.getX(), Toast.LENGTH_SHORT).show();
            }
        });

        series_a.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(Upload.this, "A = " + dataPoint.getY() + " T = " + dataPoint.getX(), Toast.LENGTH_SHORT).show();
            }
        });

        series_fy.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(Upload.this, "Amplitude = " + dataPoint.getY() + " Frequency = " + dataPoint.getX(), Toast.LENGTH_SHORT).show();
            }
        });

        series_fv.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(Upload.this, "Amplitude = " + dataPoint.getY() + " Frequency = " + dataPoint.getX(), Toast.LENGTH_SHORT).show();
            }
        });

        series_fa.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(Upload.this, "Amplitude = " + dataPoint.getY() + " Frequency = " + dataPoint.getX(), Toast.LENGTH_SHORT).show();
            }
        });


        // ------ Graph Settings ------ //

        graph.getGridLabelRenderer().setGridColor(Color.WHITE);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);
        graph.addSeries(series_y);
        graph.getGridLabelRenderer().reloadStyles();

        // ------ Frequency toggling settings ------ //
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle.getText() == "Frequency domain") {
                    toggle.setText("Time domain");
                    domain = 1;
                    if (f == 0) {
                        graph.removeAllSeries();
                        graph.addSeries(series_fy);
                    } else if (f == 1) {
                        graph.removeAllSeries();
                        graph.addSeries(series_fv);
                    } else {
                        graph.removeAllSeries();
                        graph.addSeries(series_fa);
                    }
                } else {
                    toggle.setText("Frequency domain");
                    domain = 0;
                    if (f == 0) {
                        graph.removeAllSeries();
                        graph.addSeries(series_y);
                    } else if (f == 1) {
                        graph.removeAllSeries();
                        graph.addSeries(series_v);
                    } else {
                        graph.removeAllSeries();
                        graph.addSeries(series_a);
                    }
                }
            }
        });

        // ------ Spinner Settings ------ //

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                graph.removeAllSeries();
                if (list[position] == "Acceleration") {
                    if (domain == 0) {
                        f = 2;
                        graph.removeAllSeries();
                        graph.addSeries(series_a);
                    } else {
                        f = 2;
                        graph.removeAllSeries();
                        graph.addSeries(series_fa);
                    }
                } else if (list[position] == "Velocity") {
                    if (domain == 0) {
                        f = 1;
                        graph.removeAllSeries();
                        graph.addSeries(series_v);
                    } else {
                        f = 1;
                        graph.removeAllSeries();
                        graph.addSeries(series_fv);
                    }
                } else {
                    if (domain == 0) {
                        f = 0;
                        graph.removeAllSeries();
                        graph.addSeries(series_y);
                    } else {
                        f = 0;
                        graph.removeAllSeries();
                        graph.addSeries(series_fv);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
//        } catch (Exception e) {
//
//        }

    }
}


