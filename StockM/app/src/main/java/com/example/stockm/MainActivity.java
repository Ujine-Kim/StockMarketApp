package com.example.stockm;

import com.example.stockm.UrlRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    public static class getRequestThread implements Runnable{
        StringBuilder infoStockMarket= new StringBuilder();

        @Override
        public void run() {
            try {
                infoStockMarket=UrlRequest.GetReq("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=IBM&apikey=demo");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public StringBuilder returnValue(){
            return infoStockMarket;
        }
    }
    public static class JSONParserMetaData {
        public static JSONObject getData(String response) throws JSONException {
            JSONObject dataJSON = new JSONObject(response);
            JSONObject metaData = dataJSON.getJSONObject("Meta Data");
          return metaData;
        };

    }
    public static class JSONParserStockPrice {
        public static JSONObject getData(StringBuilder response) throws JSONException {
            JSONObject dataJSON = new JSONObject(String.valueOf(response));
            JSONObject metaData = dataJSON.getJSONObject("Time Series (Daily)");
            return metaData;
        };
    }
    private TextView mainTextView;
    private TextView mainTextView3;
    private LineChart lineChart;
    public static double getRandomIntegerBetweenRange(double min, double max){
        double x = (int)(Math.random()*((max-min)+1))+min;
        return x;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        JSONObject metaData = null;
        JSONObject stockPrice=null;
        Iterator<?> keys= null;
        getRequestThread newThread = new getRequestThread();
        Thread getRequestApi = new Thread(newThread);
        getRequestApi.start();
        try {
            getRequestApi.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StringBuilder infoStockMarket =  newThread.returnValue();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainTextView = findViewById(R.id.main_textview);
        mainTextView3 = findViewById(R.id.main_textview3);
        try {
            metaData =JSONParserMetaData.getData(infoStockMarket.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            stockPrice= JSONParserStockPrice.getData(infoStockMarket);
            keys = stockPrice.keys();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            mainTextView.setText(mainTextView.getText()+metaData.getString("2. Symbol").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            mainTextView3.setText(mainTextView3.getText()+metaData.getString("5. Time Zone").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ArrayList<String> xAxisLabel = new ArrayList<>();

        lineChart= findViewById(R.id.lineChart1);
        XAxis xAxis = lineChart.getXAxis();
        String ApiSting = "0SKL531QJM1EDB5U";
       // lineChart.setOnChartGestureListener((OnChartGestureListener) MainActivity.this);
       // lineChart.setOnChartValueSelectedListener((OnChartValueSelectedListener) MainActivity.this);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        ArrayList <Entry> values=new ArrayList<>();
        int x=0;
        while (x<5) {
            String key= null;
            if (keys.hasNext()) {
                key = (String) keys.next();
            }
            try {
                JSONObject data1 = (JSONObject) stockPrice.get(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Log.d("Creation", stockPrice.getJSONObject(key).getString("2. high"));
                Log.d("Creation", (String) keys.next());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                values.add(new Entry(x, (float) stockPrice.getJSONObject(key).getDouble("2. high"))); // add one entry per hour
                xAxisLabel.add(key);
                x++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        lineChart.getDescription().setEnabled(false);
        xAxis.setValueFormatter(new ValueFormatter() {
                                    @Override
                                    public String getFormattedValue(float value) {
                                        return xAxisLabel.get((int) value);
                                    }
                                });
                xAxis.setGranularity(1f); // one hour
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
        LineDataSet set1 = new LineDataSet(values,"Data Set 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineChart.setBackgroundColor(Color.WHITE);
        set1.setValueTextColor(Color.WHITE);
        set1.setFillAlpha(110);
        ArrayList<ILineDataSet> dataSets=new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);



    }

}