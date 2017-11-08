package com.kakao.cookie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.util.helper.log.Logger;

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener {
    private Button regUserBtn;
    final Context context = this;

    private final long FINSH_INTERVAL_TIME = 2000; //2초
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //db에서 정보를 확인하여 사용자가 있는경우 바로 메인 화면으로 이동
        final DBHelper dbHelper = new DBHelper(getApplicationContext());
        if(dbHelper.checkUser())    {
            Intent intent = new Intent(RegisterUserActivity.this, MainActivity.class);
            Logger.e("********** STATUS : saved user ***********");
            startActivity(intent);
        }

        regUserBtn = (Button)findViewById(R.id.RegUserBtn);
        regUserBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.equals(regUserBtn)){
            PopUp();
        }
    }

    //팝업 생성 함수
    public void PopUp(){
        final CharSequence[] items = {"사용자등록","취소"};
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        //제목 세팅
        alertDialogBuilder.setTitle("선택해주세요.");
        alertDialogBuilder.setItems(items,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        //선택 후 로그인 혹은 취소
                        Toast.makeText(getApplicationContext(),items[id],
                                Toast.LENGTH_SHORT).show();

                        if (id == 0){
                            Intent intent = new Intent(RegisterUserActivity.this, ProfileActivity.class);
                            Logger.e("STATUS : register");
                            startActivity(intent);
                        }
                        else {
                        }
                        dialog.dismiss();
                    }
                });

        //다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();

        //다이얼로그 보여주기
        alertDialog.show();
    }

    //뒤로가기 두번으로 종료
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "\'뒤로\' 버튼을 한 번 더 누르면 종료됩니다.",

                    Toast.LENGTH_SHORT).show();
        }
    }
}
