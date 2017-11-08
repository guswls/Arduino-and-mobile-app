package com.kakao.cookie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class User extends Fragment implements View.OnClickListener{

    int gStep=0, cStep=0;//목표 걸음수, 현재 걸음수
    //final Context context = getActivity();

    private LinearLayout ly;
    private ImageView iv;
    MainView mainView;
    float c, a, b;//도달 퍼센트 계산 변수


    //프로플 관련 변수
    //DB에서 가져온 내용 setText해줘야 함
    TextView name, gender, age, height, weight, changetxt;
    ImageView changeimg;
    Button Devicebtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        final DBHelper dbHelper = new DBHelper(getActivity().getApplicationContext());



        int db_gStep = 0;

        //프로플 관련
        name = (TextView)getView().findViewById(R.id.username);
        gender = (TextView)getView().findViewById(R.id.usergender);
        age = (TextView)getView().findViewById(R.id.userage);
        height = (TextView)getView().findViewById(R.id.userheight);
        weight = (TextView)getView().findViewById(R.id.userweight);
        changetxt = (TextView)getView().findViewById(R.id.changetxt);
        changeimg = (ImageView)getView().findViewById(R.id.changeimg);
        Devicebtn = (Button) getView().findViewById(R.id.Devicebtn);


        if(dbHelper.checkUser()) {
            String[] userInfo = dbHelper.getUser(null).split(",");
            //userInfo에 차례대로 0(name) 1(gender) 2(age) 3(height) 4(weight) 5(pImage) 6(goalStep) 저장

            name.setText(userInfo[0]);
            gender.setText(userInfo[1]);
            age.setText(userInfo[2] + " 세");
            height.setText(userInfo[3] + " cm");
            weight.setText(userInfo[4] + " kg");

            db_gStep = Integer.parseInt(userInfo[6]);

        }
        /************************************************/
        //db에서 저장된 목표치, 걸음수 불러와 저장

        int devLstep = dbHelper.getDevStep("deviceL");  //DB - db에 저장된 현재 스텝수 값을 불러와 setText에 넣어줌
        int devRstep = dbHelper.getDevStep("deviceR");  //두 step의 default 값은 0

        //gStep = 목표 걸음수 / cStep = 현재 걸음수  (현진언니:null값 예외처리 필요 --> 소:생략 가능
                                                /*if(db_gStep.equals("")) gStep=0;
                                                * else gStep = db_gStep;
                                                * if(db_cStep.equals("")) cStep=0;
                                                * else cStep = db_cStep;*/

        int db_cStep = devLstep;

        //gStep=1000;
        //cStep=459;

        if(db_gStep > 0)    gStep = db_gStep;
        else                gStep = 0;

        if(db_cStep > 0)    cStep = db_cStep;
        else                cStep = 0;
        ///////////////////DB -

        changetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Modify.class);
                startActivity(intent);
            }
        });
        changeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Modify.class);
                startActivity(intent);
            }
        });
        Devicebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Bluetooth.class);
                startActivity(intent);
                Log.e("bluetooth","");
            }
        });

        if(Integer.toString(gStep).equals("")) a = 0;
        else a = gStep;
        if(Integer.toString(cStep).equals("")) b = 0;
        else b = cStep;

        if(a==0) a=1;
        c = (b * 360 / a);
        if(c>360) c = 360;
        c = c*100/360;

        //mainView.invalidate();

        iv = (ImageView)getView().findViewById(R.id.imageView);
        ly = (LinearLayout)getView().findViewById(R.id.ly);
        mainView = new MainView(getActivity());
        ly.addView(mainView);

        ly.setVisibility(LinearLayout.VISIBLE);
        iv.setVisibility(ImageView.VISIBLE);

        ly.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.equals(ly)){
            //목표설정 팝업
            setGoal();
        }
    }

    public void setGoal(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_goal,null);

        final EditText goalStep = (EditText)dialogView.findViewById(R.id.gSteps);
        final TextView curStep = (TextView)dialogView.findViewById(R.id.cSteps);

        goalStep.setText(Integer.toString(gStep));
        curStep.setText(Integer.toString(cStep));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("목표를 설정해주세요 :)")
                .setView(dialogView)
                .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(goalStep.getText().toString().equals("")) gStep = 0;
                        else gStep = Integer.parseInt(goalStep.getText().toString());

                        //DB - 사용자가 설정한 gStep값을 db에 저장***********************************/
                        final DBHelper dbHelper = new DBHelper(getActivity().getApplicationContext());
                        dbHelper.updateUser(gStep);

                        ///////////////////////////////
                        if(Integer.toString(gStep).equals("")) a = 0;
                        else a = gStep;
                        if(Integer.toString(cStep).equals("")) b = 0;
                        else b = cStep;

                        if(a==0) a=1;
                        c = (b * 360 / a);
                        if(c>360) c = 360;
                        c = c*100/360;

                        mainView.invalidate();
                        ///////////////////////////////
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    protected class MainView extends View {

        public MainView(Context context) {
            super(context);
        }

        public void onDraw(Canvas canvas) {
            final float zero = -90f;
            final float dotone = 3.6f;
            float degree = 0;

            // Paint 생성 & 속성 지정
            Paint pnt = new Paint();
            pnt.setStyle(Paint.Style.STROKE);
            pnt.setStrokeWidth(5);
            pnt.setAntiAlias(true);
            pnt.setAlpha(0x00);

            // 타원 배경 그리기
            RectF rect = new RectF(ly.getWidth()/2-250, ly.getHeight()/2-250,
                    ly.getWidth()/2+250, ly.getHeight()/2+250);

            if ((Integer.toString(cStep).equals("")||Integer.toString(cStep).equals("0"))){
                pnt.setColor(0xFFEDF2F6);
                canvas.drawArc(rect,zero,360,false,pnt);
                c=0;
            }
            else if (Integer.toString(gStep).equals("")||Integer.toString(gStep).equals("0")){
                pnt.setColor(0xFFFABC41);
                canvas.drawArc(rect,zero,360,false,pnt);
                c=100;
            }
            else {
                degree = c * dotone;

                //달성량
                pnt.setColor(0xFFFABC41);
                canvas.drawArc(rect, zero, degree, false, pnt);
                pnt.setColor(0xFFEDF2F6);
                canvas.drawArc(rect, zero+degree, 360-degree, false, pnt);
            }

            c=(float)(Math.round(c*10)/10.0);
            pnt.setColor(0xFFCDCDCD);
            pnt.setTextSize(50);
            canvas.drawText(Float.toString(c),ly.getWidth()/2-80,ly.getHeight()/2+70,pnt);
            canvas.drawText("%",ly.getWidth()/2+50,ly.getHeight()/2+70,pnt);
        }

    }
}
