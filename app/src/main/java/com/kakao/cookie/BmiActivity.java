package com.kakao.cookie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by hyunjin on 2017-05-16.
 */

public class BmiActivity extends AppCompatActivity {
    private TextView txtweight;
    private TextView txtheight;
    private TextView txtbmi;
    private TextView txtresult;
    private TextView stresult;
    private Button resultbtn;
    private double bmi = 0;
    private double valueheight = 0;
    private double valueweight = 0;
    private String resulttext;
    private String bmitext;
    private String statustext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        txtweight = (TextView)findViewById(R.id.weight);
        txtheight = (TextView)findViewById(R.id.height);

        final DBHelper dbHelper = new DBHelper(getApplicationContext());
        String[] bodysize = dbHelper.getBodysize().split(",");

        /*valueheight =Float.parseFloat(txtheight.getText().toString());
        valueweight =Float.parseFloat(txtweight.getText().toString());  --- 현진언니의 임시코드*/
        valueheight = Double.parseDouble(bodysize[0]);
        valueweight = Double.parseDouble(bodysize[1]);
        txtheight.setText(bodysize[0]);
        txtweight.setText(bodysize[1]);

        initControls();
        /*TextView tv = new TextView(this);
        tv.setText("BMI Calculator");
        setContentView(tv);*/

    }
    private void initControls() {
        txtbmi = (TextView)findViewById(R.id.result);
        txtresult = (TextView)findViewById(R.id.result2);
        resultbtn = (Button)findViewById(R.id.resultbtn);
        stresult = (TextView)findViewById(R.id.result3);
        //btnreset = (Button)findViewById(R.id.btnreset);
        resultbtn.setOnClickListener(new Button.OnClickListener() { public void onClick (View v){ calculate(); }});
        //btnreset.setOnClickListener(new Button.OnClickListener() { public void onClick (View v){ reset(); }});
    }

    private void calculate()    {

        Double valueheightmeters;

        valueheightmeters = valueheight / 100; // Converting to meters.
        bmi = (valueweight / (valueheightmeters * valueheightmeters));
        //txttipamount.setText(Double.toString(bmi));

        if (bmi >= 30) { /* obese */
            resulttext =  Float.toString((float) bmi) + " 으로";
            bmitext= "체질량 지수는";
            statustext="고도비만 입니다.";
            txtbmi.setText(bmitext);
            txtresult.setText(resulttext);
            stresult.setText(statustext);
        } else if (bmi >= 25) {
            resulttext =  Float.toString((float) bmi) + " 으로";
            bmitext= "체질량 지수는";
            statustext="비만 입니다.";
            txtbmi.setText(bmitext);
            txtresult.setText(resulttext);
            stresult.setText(statustext);
        } else if (bmi >= 23) {
            resulttext =  Float.toString((float) bmi) + " 으로";
            bmitext= "체질량 지수는";
            statustext="과체중 입니다.";
            txtbmi.setText(bmitext);
            txtresult.setText(resulttext);
            stresult.setText(statustext);
        } else if (bmi >= 18.5) {
            resulttext =  Float.toString((float) bmi) + " 으로";
            bmitext= "체질량 지수는";
            statustext="정상 입니다.";
            txtbmi.setText(bmitext);
            txtresult.setText(resulttext);
            stresult.setText(statustext);
        } else {
            resulttext =  Float.toString((float) bmi) + " 으로";
            bmitext= "체질량 지수는";
            statustext="저체중 입니다.";
            txtbmi.setText(bmitext);
            txtresult.setText(resulttext);
            stresult.setText(statustext);
        }
    }
    private void reset()
    {
        txtresult.setText("체질량 지수를 알 수 없습니다.");
        txtheight.setText("0");
        txtweight.setText("0");
    }

}
