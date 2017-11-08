package com.kakao.cookie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Backup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup);

        //액션바 색상 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable((0xFF71C9CE)));

        //현재 정보 백업 버튼 클릭 시
        final Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //다이얼로그 생성
                final AlertDialog.Builder builder = new AlertDialog.Builder(Backup.this);
                builder.setMessage("현재 정보를 백업하시겠습니까?");

                builder.setPositiveButton("예", new DialogInterface.OnClickListener() { //예 버튼 클릭 시
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /************ DB - insert 작업 ************/
                        final DBHelper dbHelper = new DBHelper(getApplicationContext());
                        dbHelper.insertBackupdata();        //backupdata 테이블에 현재 user_id

                        Toast.makeText(getApplicationContext(), "현재 정보가 백업되었습니다.", Toast.LENGTH_SHORT).show();
                    } //메세지 토스트 & DB에 백업
                });

                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        });

        //백업 정보 삭제 버튼 클릭 시
        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //다이얼로그 생성
                final AlertDialog.Builder builder = new AlertDialog.Builder(Backup.this);
                builder.setMessage("백업 정보를 삭제하시겠습니까?");

                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { //예 버튼 클릭 시
                        /************ DB - delete 작업 ************/
                        final DBHelper dbHelper = new DBHelper(getApplicationContext());
                        if(dbHelper.countBackup() > 0)  {
                            dbHelper.deleteBackup(dbHelper.countBackup());
                            Toast.makeText(getApplicationContext(), "백업 정보가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        }

                        else
                            Toast.makeText(getApplicationContext(), "백업 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }); //메세지 토스트 & DB에서 삭제

                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        });

        //사용자 삭제 버튼 클릭 시
        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //다이얼로그 생성
                final AlertDialog.Builder builder = new AlertDialog.Builder(Backup.this);
                builder.setMessage("사용자를 삭제하시겠습니까?");

                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { //예 버튼 클릭 시
                        //DB - 사용자 삭제
                        final DBHelper dbHelper = new DBHelper(getApplicationContext());
                        dbHelper.deleteUser();

                        Toast.makeText(getApplicationContext(), "사용자가 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), RegisterUserActivity.class);
                        startActivity(intent);
                    }
                }); //메세지 토스트 & activity_main 화면으로 이동


                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        });
    }
}
