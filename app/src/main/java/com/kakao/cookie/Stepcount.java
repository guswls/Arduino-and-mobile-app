package com.kakao.cookie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class Stepcount extends AppCompatActivity {

    int wStep =0, nStep=0;//wStep = 일주일 평균 걸음 수, nStep= 오늘 걸음 수
    double k;
    private TextView result;

    private String resulttext;

    private TextView kcal;
    private String kcaltext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stepcount);

/*여기서 데이터 들어가는 부분을 숫자 설정으로 해놨는데 db에 저장 된 값 불러와야됭*/
        GraphView line_graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> line_series =
                new LineGraphSeries<DataPoint>(new DataPoint[]{
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
        line_graph.getViewport().setMaxY(1000);

        line_graph.getViewport().setScrollable(true);

        /*하루 평균 걸음
        *nStep =오늘 걸음 수 wStep= 하루 평균 걸음 */
        final DBHelper dbHelper = new DBHelper(getApplicationContext());

        wStep = 7629;
        nStep = dbHelper.getDevStep("deviceL");
        result = (TextView)findViewById(R.id.result);
        resulttext =nStep+"/"+wStep +"\n";
        result.setText(resulttext);

        /*소요 칼로리 계산 nStep = 오늘 걸은 걸음 수 */
        kcal = (TextView)findViewById(R.id.kcal);
        k= 0.023;
        float kcalresult =(float) (Math.round(nStep*k*100)/100.0);

        kcaltext =kcalresult+ "kcal";
        kcal.setText(kcaltext);
    }

}




/*public class Stepcount extends AppCompatActivity {
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stepcount);
        // we get graph view instance
        GraphView graph = (GraphView) findViewById(R.id.graph);
        // data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(10);
        viewport.setScrollable(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                for (int i = 0; i < 100; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry();
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }

    // add random data to graph
    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        series.appendData(new DataPoint(lastX++, RANDOM.nextDouble() * 10d), true, 10);
    }

}*/

