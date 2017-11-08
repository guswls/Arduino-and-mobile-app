package com.kakao.cookie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by hyunjin on 2017-05-12.
 */
public class Act extends AppCompatActivity {

    int wStep =0, nStep=0;//wStep = 일주일 평균 걸음 수, nStep= 오늘 걸음 수

    double k;
    private TextView walk;
    private String walktext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act);
/*여기서 데이터 들어가는 부분을 숫자 설정으로 해놨는데 db에 저장 된 값 불러와야됨
* 여기서는 x축에는 날짜 들어가야 되고 y축에는 그날 총 스텝수 */
        GraphView line_graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> line_series =
                new LineGraphSeries<DataPoint>(new DataPoint[] {
                        new DataPoint(0, 100),
                        new DataPoint(6, 350),
                        new DataPoint(12, 250),
                        new DataPoint(18, 700),
                        new DataPoint(24, 620)
                });
        line_graph.addSeries(line_series);

        // set the bound

        // set manual X bounds
        line_graph.getViewport().setXAxisBoundsManual(true);
        line_graph.getViewport().setMinX(0);
        line_graph.getViewport().setMaxX(24);

        // set manual Y bounds
        line_graph.getViewport().setYAxisBoundsManual(true);
        line_graph.getViewport().setMinY(0);
        line_graph.getViewport().setMaxY(2000);

        line_graph.getViewport().setScrollable(true);

         /*하루 평균 걸음
        *nStep =오늘 걸음 수 wStep= 주당 하루 평균 걸음 */
        final DBHelper dbHelper = new DBHelper(getApplicationContext());
        nStep = dbHelper.getDevStep("deviceL");

        walk = (TextView)findViewById(R.id.walk);
        walktext = 7456 + nStep + "걸음";
        walk.setText(walktext);
    }
}
