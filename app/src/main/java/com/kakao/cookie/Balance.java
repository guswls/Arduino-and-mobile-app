package com.kakao.cookie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Balance extends AppCompatActivity implements View.OnClickListener {

    ImageView bal;
    int /*왼발*/lvn1/*상 좌단 센서*/, lvn2/*상 우단 센서*/, lvn3/*가운데*/, lvn4/*하 좌단 센서*/, lvn5/*하 우단 센서*/;
    int /*오른발*/rvn1, rvn2, rvn3, rvn4, rvn5;
    EditText v1, v2, v3, v4, v5, v6, v7, v8, v9, v10;
    float resultL, resultR, percent;
    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView textbalance;
    private TextView textpercent;
    private String resulttext;
    private String resultpercent;
    private String restarttext;
    private String textname;
    private String textname2;
    private String textname3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        bal = (ImageButton) findViewById(R.id.bal);

        Log.d("TAG", "*** lvn1 = "+lvn1 + ", lvn2 = " + lvn2 + ", lvn3 = " + lvn3 + ", lvn4 = " + lvn4 + ", lvn5 = " + lvn5 );

        text1 = (TextView)findViewById(R.id.text1);
        text2 = (TextView)findViewById(R.id.text2);
        text3 = (TextView)findViewById(R.id.text3);
        textbalance = (TextView)findViewById(R.id.balance);
        textpercent = (TextView)findViewById(R.id.percent);

        bal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        /*이부분은 edit text라서 센서값 연결하고 지워야 될까어
        lvn1 = Integer.parseInt(v1.getText().toString());
        lvn2 = Integer.parseInt(v2.getText().toString());
        lvn3 = Integer.parseInt(v3.getText().toString());
        lvn4 = Integer.parseInt(v4.getText().toString());
        lvn5 = Integer.parseInt(v5.getText().toString());
        rvn1 = Integer.parseInt(v6.getText().toString());
        rvn2 = Integer.parseInt(v7.getText().toString());
        rvn3 = Integer.parseInt(v8.getText().toString());
        rvn4 = Integer.parseInt(v9.getText().toString());
        rvn5 = Integer.parseInt(v10.getText().toString());
/*여기부터는 센서값에 따라 이미지 변화(해보고 안되는 경우의 수 있으면 말해줘
* 추가 할께 일단은 기본적인 것들만 해놨어... 너무 많아가지고 */
        final DBHelper dbHelper = new DBHelper(getApplicationContext());
        String[] lsignals = dbHelper.getDevSignal("deviceL").split(",");
        String[] rsignals = dbHelper.getDevSignal("deviceR").split(",");

        lvn1 = Integer.parseInt(lsignals[0]);
        lvn2 = Integer.parseInt(lsignals[1]);
        lvn3 = Integer.parseInt(lsignals[2]);
        lvn4 = Integer.parseInt(lsignals[3]);
        lvn5 = Integer.parseInt(lsignals[4]);
        rvn1 = Integer.parseInt(rsignals[0]);
        rvn2 = Integer.parseInt(rsignals[1]);
        rvn3 = Integer.parseInt(rsignals[2]);
        rvn4 = Integer.parseInt(rsignals[3]);
        rvn5 = Integer.parseInt(rsignals[4]);


        if (lvn1 < 200) {
            if (rvn1 < 200) {
                if (rvn2 < 300 && rvn2 > 200) {
                    bal.setImageResource(R.drawable.bal4);
                } else if (rvn2 < 400 && rvn2 > 299) {
                    bal.setImageResource(R.drawable.bal3);
                } else if (rvn2 < 500 && rvn2 > 399) {
                    bal.setImageResource(R.drawable.bal5);
                }

            } else if (rvn1 > 199) {
                bal.setImageResource(R.drawable.bal10);
            }

        }
        else if (lvn1 < 300 && lvn1 > 199) {
            if (lvn2 < 400 && lvn2 > 299) {
                bal.setImageResource(R.drawable.bal7);
            } else if (lvn2 > 499 && rvn2 < 500 && rvn2 > 399) {
                bal.setImageResource(R.drawable.bal8);
            }
            else
            {
                bal.setImageResource(R.drawable.bal6);
            }
        }
        else if (lvn1 > 400) {
            if (rvn2 > 500) {
                bal.setImageResource(R.drawable.bal9);
            }
        }
        else {
            if (lvn5 > 499 && rvn5 < 500) {
                bal.setImageResource(R.drawable.bal11);
            } else {
                bal.setImageResource(R.drawable.bal6);
            }
        }
        /*퍼센트 계산*/
        resultL = (lvn1 + lvn2 +lvn3 + lvn4 +lvn5)/5;
        resultR = (rvn1 +rvn2+ rvn3 +rvn4 +rvn5)/5;
        Log.d("TAG", "퍼센트 resultL은 " + resultL + ",,, resultR은 " + resultR);

        if(resultL> resultR)
        {
            resulttext = "왼쪽";
            percent = (resultL / (resultL+resultR))*100-50;
            resultpercent = Float.toString((float) percent) +"%";
            textname ="당신의 걸음걸이는";
            text1.setText(textname);
            textbalance.setText(resulttext);
            textname2 ="으로";
            text2.setText(textname2);
            textpercent.setText(resultpercent);
            textname3 ="더 기울었습니다.";
            text3.setText(textname3);
        }
        else if(resultL< resultR)
        {
            resulttext = "오른쪽";
            percent = (resultR / (resultL+resultR))*100-50;
            resultpercent = Float.toString((float) percent) +"%";
            textname ="당신의 걸음걸이는";
            text1.setText(textname);
            textbalance.setText(resulttext);
            textname2 ="으로";
            text2.setText(textname2);
            textpercent.setText(resultpercent);
            textname3 ="더 기울었습니다.";
            text3.setText(textname3);
        }
        else if (resultL == resultR)
        {
            restarttext = " 다시 측정해주세요";
            text3.setText(restarttext);
        }
    }



}

