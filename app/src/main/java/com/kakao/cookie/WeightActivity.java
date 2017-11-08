package com.kakao.cookie;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by hyunjin on 2017-05-16.
 */

public class WeightActivity extends AppCompatActivity  {
    Button button;
    EditText nTime, nWeight;
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    DBHelper myHelper;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);
       /* nTime = (EditText)findViewById(R.id.time);
        nWeight = (EditText)findViewById(R.id.weight);*/
        graph =(GraphView)findViewById(R.id.graph);

        myHelper=new DBHelper(this);
        sqLiteDatabase =myHelper.getReadableDatabase();
        myHelper.insertData(0, 0);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT _id, weight FROM user;", null);
        cursor.moveToFirst();
        int xVal = cursor.getInt(0);
        int yVal = cursor.getInt(1);
        myHelper.insertData(xVal,yVal);

        while(cursor.moveToNext())  {
            xVal = cursor.getInt(0);
            yVal = cursor.getInt(1);
            myHelper.insertData(xVal,yVal);
            Log.d("TAG", "몸무게 측정 xVal = " + xVal + ", yVal = " + yVal);
        }

        series =new LineGraphSeries<DataPoint>(getData());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(24);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(120);
        graph.addSeries(series);

        //exqButton();

    }


    private DataPoint[] getData() {
        //read data from database
        String[] columns ={"xValues", "yValues"};
        Cursor cursor = sqLiteDatabase.query("MyTable",columns,null,null,null,null,null);
        DataPoint[] dp =new DataPoint[cursor.getCount()];
        for(int i=0; i<cursor.getCount();i++){
            cursor.moveToNext();
            dp[i]=new DataPoint(cursor.getInt(0),cursor.getInt(1));
        }
        return dp;
    }

}

