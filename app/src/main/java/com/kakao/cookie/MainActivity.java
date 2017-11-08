package com.kakao.cookie;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.kakao.auth.Session;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private PopupMenu pm;

    private final long FINSH_INTERVAL_TIME = 2000; //2초
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(this);

        this.setTitle("Cookie");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("USER"));
        tabLayout.addTab(tabLayout.newTab().setText("RECORD"));
        tabLayout.addTab(tabLayout.newTab().setText("SNS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
/*************************************************************************/
        Session.getCurrentSession().checkAccessTokenInfo();
/**************************************************************************/
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        //백업 버튼 클릭 시 백업 관리 화면으로 이동
        if(id==R.id.button1) {
            Intent intent = new Intent(getApplicationContext(), Backup.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    //뒤로가기 두번으로 종료
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
            Session.getCurrentSession().checkAccessTokenInfo();
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "\'뒤로\' 버튼을 한 번 더 누르면 종료됩니다.",

                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(toolbar)) {
            Log.v("출력", "팝업");
            pm.show();
        }
    }

   /*public void popUp(){
        pm = new PopupMenu(getApplicationContext(),toolbar);
        pm.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener(){
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case 10:
                                //인텐트 위치
                                Toast.makeText(getApplicationContext(),"백업",Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                }
        );
        //메뉴 만들기
        Menu menu = pm.getMenu();
        menu.add(0,10,0,"백업");
    }*/
}