package com.kakao.cookie;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.kakaostory.KakaoStoryService;
import com.kakao.kakaostory.callback.StoryResponseCallback;
import com.kakao.kakaostory.request.PostRequest;
import com.kakao.kakaostory.response.model.MyStoryInfo;
import com.kakao.kakaotalk.KakaoTalkService;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.KakaoParameterException;
import com.kakao.util.helper.log.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.kakao.auth.Session;

import org.w3c.dom.Text;


public class Sns extends Fragment implements View.OnClickListener{
    private ImageButton loginBtn;//로그인버튼
    private TextView isLogin;//로그인 여부
    private Button logoutBtn;//로그아웃

    private Button linkBtn;//프로필 연동

    private TextView goalStepsTxt;//목표 스텝수
    private TextView goalTimeTxt;//목표 시간
    private TextView progStepsTxt;//진행 걸음
    private TextView progTimeTxt;//진행 시간

    private Button refreshBtn;//카카오스토리 사용자 확인 새로고침
    private TextView isStoryUserTxt;//카카오스토리 사용자인지 텍스트로 보여줌

    private EditText setStoryTextTxt;//카카오스토리에 게시할 내용 추가 가능

    private Button formeBtn;//카카오톡 나에게 보내기
    private Button forstoryBtn;//카카오스토리 게시하기

    private TextView userIdTxt;//사용자 고유번호 출력
    private Button deleteBtn;//앱 탈퇴 버튼

    String content = "";//카카오스토리에 게시할 내용
    boolean CheckUser;//카카오스토리 유저면 True

    String nickName = "";//닉네임
    long userID = 0;//사용자 고유번호
    String pImage = "";//사용자 프로필 경로

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sns, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        loginBtn = (ImageButton)getView().findViewById(R.id.LoginBtn);//로그인 버튼
        isLogin = (TextView) getView().findViewById(R.id.IsLogin);//로그인 여부
        logoutBtn = (Button)getView().findViewById(R.id.LogoutBtn);
        linkBtn = (Button)getView().findViewById(R.id.LinkProfile);//프로필 연동
        goalStepsTxt = (TextView)getView().findViewById(R.id.GoalStepsTxt);
        goalTimeTxt = (TextView)getView().findViewById(R.id.GoalTimeTxt);
        progStepsTxt = (TextView)getView().findViewById(R.id.ProgStepsTxt);
        progTimeTxt = (TextView)getView().findViewById(R.id.ProgTimeTxt);
        refreshBtn = (Button)getView().findViewById(R.id.RefreshBtn);//카카오스토리 유저 새로고침
        isStoryUserTxt = (TextView)getView().findViewById(R.id.IsStoryUserTxt);//카카오스토리 유저 맞는지 텍스트
        setStoryTextTxt = (EditText)getView().findViewById(R.id.SetStoryTextTxt);//카카오스토리 게시글 세팅
        formeBtn = (Button)getView().findViewById(R.id.FormeBtn);//나에게보내기
        forstoryBtn = (Button)getView().findViewById(R.id.ForstoryBtn);//스토리 게시글
        userIdTxt = (TextView)getView().findViewById(R.id.UserIdTxt);
        deleteBtn = (Button)getView().findViewById(R.id.DeleteBtn);

        loginBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        linkBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
        formeBtn.setOnClickListener(this);
        forstoryBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
/************************************************************/
        Session.getCurrentSession().checkAccessTokenInfo();
/*************************************************************/
        final DBHelper dbHelper = new DBHelper(getActivity().getApplicationContext());
        String[] userInfo = dbHelper.getUser(null).split(",");
        //userInfo에 차례대로 0(name) 1(gender) 2(age) 3(height) 4(weight) 5(pImage) 6(goalStep) 저장
        goalStepsTxt.setText(userInfo[6]);      //DB - db에 저장된 목표 스텝수 값으로 text변경
                                                /*if(db에 저장된 값이 있을경우)
                                                    goalStepsTxt.setText(db에서 불러온 값);
                                                  else goalStepsTxt.setText("0");*/
        Log.d("TAG", "SNS-여기는 통과");
        int devLstep = dbHelper.getDevStep("deviceL");  //DB - db에 저장된 현재 스텝수 값을 불러와 setText에 넣어줌
        int devRstep = dbHelper.getDevStep("deviceR");  //두 step의 default 값은 0
        int totalStep = devLstep;
        progStepsTxt.setText(String.valueOf(totalStep));        /*if(db에 저장된 값이 있을경우)
                                                     progStepsTxt.setText(db에서 불러온 값);
                                                  else progStepsTxt.setText("0");*/
        //progStepsTxt.setText(0);


        /* 생략 -----------------------------
          db에 저장된 목표 걸은 시간 값을 불러와 setText에 넣어줌
          if(db에 저장된 값이 있을경우)
            goalTimeTxt.setText(db에서 불러온 값);
          else goalTimeTxt.setText("0");

          db에 저장된 현재 걸은 시간 값을 불러와 setText에 넣어줌
          if(db에 저장된 값이 있을경우)
            progTimeTxt.setText(db에서 불러온 값);
          else progTimeTxt.setText("0");
           ---------------------------------- */



        if(Session.getCurrentSession().isOpened()==false)
        {
            isLogin.setText("로그인을 해주세요.");
        }
        else{
            requestMe();
            requestIsStoryUser();
            isLogin.setText("로그인되었습니다.");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(loginBtn)){
            if(Session.getCurrentSession().isOpened()==false){
                //세션이 닫혔을 때만 로그인화면 이동
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                //getActivity().finish();
            }
        }
        else if (Session.getCurrentSession().isOpened()&&v.equals(logoutBtn)){
            UserManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    //세션이 닫혔을 때만 로그인화면 이동
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }
        else if (Session.getCurrentSession().isOpened()&&v.equals(linkBtn)){
            final DBHelper dbHelper = new DBHelper(getActivity().getApplicationContext());

            //DB - 정보 저장
            dbHelper.insertSNS(nickName, String.valueOf(userID), pImage);

            //사진 닉네임 고유번호 연동
            /*db 사용자 이름에 "nickName" 변수에 저장된 값 저장(String타입)*/
            /*db 고유번호에 "userID"변수에 저장된 값 저장(long타입) */
            LinkImage();//사진 db저장
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        else if (Session.getCurrentSession().isOpened()&&v.equals(refreshBtn)){
            requestIsStoryUser();
        }
        else if (Session.getCurrentSession().isOpened()&&v.equals(formeBtn)){
            if(goalStepsTxt.getText().toString().equals("")||goalTimeTxt.getText().toString().equals(""))
            {//값이 없는 경우
                Toast.makeText(getActivity().getApplicationContext(),"아직 목표를 채우지 못했습니다.",
                        Toast.LENGTH_SHORT).show();
            }
            //나에게 보내기
            else if (Integer.parseInt(goalStepsTxt.getText().toString())<=Integer.parseInt(progStepsTxt.getText().toString())
                    && Integer.parseInt(goalTimeTxt.getText().toString())<=Integer.parseInt(progTimeTxt.getText().toString())){
                //나에게 보내기 활성화
                requestSendMemo();
                Toast.makeText(getActivity().getApplicationContext(),"오늘의 목표를 채웠습니다.",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(),"아직 목표를 채우지 못했습니다.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if (Session.getCurrentSession().isOpened()&&v.equals(forstoryBtn)){
            if(goalStepsTxt.getText().toString().equals("")||goalTimeTxt.getText().toString().equals(""))
            {//값이 없는 경우
                Toast.makeText(getActivity().getApplicationContext(),"아직 목표를 채우지 못했습니다.",
                        Toast.LENGTH_SHORT).show();
            }
            else if(CheckUser == false) {
                //카카오스토리 사용자가 아닌경우
                Toast.makeText(getActivity().getApplicationContext(), "카카오스토리 사용자가 아닙니다.",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                //게시글 작성
                if (Integer.parseInt(goalStepsTxt.getText().toString()) <= Integer.parseInt(progStepsTxt.getText().toString())
                        && Integer.parseInt(goalTimeTxt.getText().toString()) <= Integer.parseInt(progTimeTxt.getText().toString())) {
                    setContent();//게시글 내용 세팅
                    //게시글 작성 활성화
                    Toast.makeText(getActivity().getApplicationContext(), "오늘의 목표를 채웠습니다.",
                            Toast.LENGTH_SHORT).show();
                    try {
                        requestPostNote();
                    } catch (KakaoParameterException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "아직 목표를 채우지 못했습니다.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (Session.getCurrentSession().isOpened()&&v.equals(deleteBtn)){
            //앱 탈퇴
            onClickUnlink();
        }
    }
    //session이 열려있을때 사용자 정보 활성화
    private void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                //redirectLoginActivity();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //redirectLoginActivity();
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Logger.d("UserProfile : " + userProfile);
                nickName = userProfile.getNickname();//닉네임
                userID = userProfile.getId();//사용자 고유번호
                pImage = userProfile.getProfileImagePath();//사용자 프로필 경로
                userIdTxt.setText(String.valueOf(userID));
            }

            @Override
            public void onNotSignedUp() {
                //showSignup();
            }
        });
    }

    Handler handler = new Handler();
    //이미지 연동
    public void LinkImage(){
        if(pImage.equals("")){
            Logger.e("no exist kakao image");
        }
        else {
            new ImageDownload().execute(pImage);
            // 인터넷 상의 이미지 보여주기

            // 1. 권한을 획득한다 (인터넷에 접근할수 있는 권한을 획득한다)  - 메니페스트 파일
            // 2. Thread 에서 웹의 이미지를 받아온다 - honeycomb(3.0) 버젼 부터 바뀜
            // 3. 외부쓰레드에서 메인 UI에 접근하려면 Handler 를 사용해야 한다.


            //Thread t = new Thread(Runnable 객체를 만든다);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {    // 오래 거릴 작업을 구현한다
                    // TODO Auto-generated method stub
                    try {
                        //final ImageView pImage = (ImageView)findViewById(R.id.ProfileImg);
                        URL url = new URL(pImage);
                        InputStream is = url.openStream();
                        final Bitmap bm = BitmapFactory.decodeStream(is);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {  // 화면에 그려줄 작업
                                //profileImg.setImageBitmap(bm);
                            }
                        });
                        //profileImg.setImageBitmap(bm); //비트맵 객체로 보여주기
                    } catch (Exception e) {

                    }

                }
            });

            t.start();
        }
    }

    //url 이미지 다운로드
    private class ImageDownload extends AsyncTask<String, Void, Void> {
        /*** 파일명*/
        private String fileName;
        /*** 저장할 폴더*/
        private final String SAVE_FOLDER = "/Cookie_tmp";

        @Override
        protected Void doInBackground(String... params) {
            //다운로드 경로를 지정
            String savePath = Environment.getExternalStorageDirectory().toString() + SAVE_FOLDER;

            File dir = new File(savePath);
            //상위 디렉토리가 존재하지 않을 경우 생성
            if (!dir.exists()) {
                dir.mkdirs();
            }

            fileName = String.valueOf(userID);

            //웹 서버 쪽 파일이 있는 경로
            String fileUrl = params[0];

            //다운로드 폴더에 동일한 파일명이 존재하는지 확인
            if (new File(savePath + "/" + fileName).exists() == false) {

            } else {}

            String localPath = savePath + "/" + fileName + ".jpg";

            try {
                URL imgUrl = new URL(fileUrl);
                //서버와 접속하는 클라이언트 객체 생성
                HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
                int len = conn.getContentLength();
                byte[] tmpByte = new byte[len];
                //입력 스트림을 구한다
                InputStream is = conn.getInputStream();
                File file = new File(localPath);
                //파일 저장 스트림 생성
                FileOutputStream fos = new FileOutputStream(file);
                int read;
                //입력 스트림을 파일로 저장
                for (;;) {
                    read = is.read(tmpByte);
                    if (read <= 0) {
                        break;
                    }
                    fos.write(tmpByte, 0, read); //file 생성
                }

                is.close();
                fos.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Logger.e("exist kakao image");
                /* 카카오톡 프로필에 사진이 있을 경우
                * db 사용자 프로필 사진에
                * Environment.getExternalStorageDirectory().toString() + "/Cookie_tmp/"+String.valueOf(userID)
                * 경로의 파일 저장*/
            final DBHelper dbHelper = new DBHelper(getActivity().getApplicationContext());
            dbHelper.updateUserpImage(Environment.getExternalStorageDirectory().toString()
                                            + "/Cookie_tmp/"+String.valueOf(userID));

            return null;
        }
    }

    //앱 탈퇴
    private void onClickUnlink() {//앱 탈퇴 처리
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(getActivity())
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        //redirectLoginActivity();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        //redirectSignupActivity();
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        Toast.makeText(getActivity().getApplicationContext(),"탈퇴되었습니다.",Toast.LENGTH_LONG).show();
                                        //redirectLoginActivity();
                                        Intent intent = new Intent(getActivity().getApplicationContext(), RegisterUserActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();//******************************************************************
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

    }

    //카카오톡 메시지 전송
    public void requestSendMemo() {
        String message = "Test for send Memo";
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("''yy년 MM월 dd일 E요일");
        Sns.KakaoTalkMessageBuilder builder = new Sns.KakaoTalkMessageBuilder();
        builder.addParam("MESSAGE", message);
        builder.addParam("DATE", sdf.format(date));

        KakaoTalkService.requestSendMemo(new Sns.KakaoTalkResponseCallback<Boolean>() {
                                             @Override
                                             public void onSuccess(Boolean result) {
                                                 Toast.makeText(getActivity().getApplicationContext(),"나에게 결과 보내기",Toast.LENGTH_LONG).show();
                                                 Logger.d("send message to my chatroom : " + result);
                                             }
                                         }
                ,"2789"
                , builder.build());

    }

    //카카오톡 메시지 내용 맵
    public class KakaoTalkMessageBuilder {
        public Map<String, String> messageParams = new HashMap<String, String>();

        public Sns.KakaoTalkMessageBuilder addParam(String key, String value) {
            messageParams.put("${" + key + "}", value);
            return this;
        }

        public Map<String, String> build() {
            return messageParams;
        }
    }

    //카카오톡
    private abstract class KakaoTalkResponseCallback<T> extends TalkResponseCallback<T> {
        @Override
        public void onNotKakaoTalkUser() {
            Logger.w("not a KakaoTalk user");
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            Toast.makeText(getActivity().getApplicationContext(),"전송 실패",Toast.LENGTH_LONG).show();
            Logger.e("failure : " + errorResult);
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            //redirectLoginActivity();

        }

        @Override
        public void onNotSignedUp() {
            //redirectSignupActivity();
        }
    }

    //카카오스토리
    private abstract class KakaoStoryResponseCallback<T> extends StoryResponseCallback<T> {

        @Override
        public void onNotKakaoStoryUser() {
            Logger.d("not KakaoStory user");
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            Logger.e("KakaoStoryResponseCallback : failure : " + errorResult);
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            //redirectLoginActivity();
        }

        @Override
        public void onNotSignedUp() {
            //redirectSignupActivity();
        }
    }

    //카카오스토리 사용자 확인
    private void requestIsStoryUser() {
        KakaoStoryService.requestIsStoryUser(new Sns.KakaoStoryResponseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Log.e("check story user : " , String.valueOf(result));
                CheckUser = result;
                Logger.e("CheckUser"+CheckUser);
                setStoryUserTxt();
            }
        });
    }

    private void setStoryUserTxt(){
        if(CheckUser == false){
            isStoryUserTxt.setText("카카오스토리 사용자가 아닙니다.");
        }
        else{
            isStoryUserTxt.setText("카카오스토리 사용자입니다.");
        }
    }

    //카카오스토리 글 포스팅 요청
    private void requestPostNote() throws KakaoParameterException {
        KakaoStoryService.requestPostNote(new Sns.KakaoStoryResponseCallback<MyStoryInfo>() {
                                              @Override
                                              public void onSuccess(MyStoryInfo result) {
                                                  Logger.d(result.toString());
                                              }
                                          }, content/*포스팅할 글 내용*/, PostRequest.StoryPermission.PUBLIC, true,
                ""/*우리 앱 안드로이드 링크*/,
                ""/*우리 앱 ios 링크*/,
                ""/*우리 앱 마켓 링크*/,
                ""/*우리 앱 ios마켓 링크*/);
    }

    //카카오스토리 게시글 세팅
    private void setContent(){
        content = "";
        content = "[app test]Cookie - 오늘의 목표 달성\n";
        content = content + "걸음 목표: "+goalStepsTxt.getText().toString()+"걸음 / 오늘 걸은 걸음: "+progStepsTxt.getText().toString()+"걸음\n";
        content = content + "시간 목표: "+goalTimeTxt.getText().toString()+"분 / 오늘 걸은 시간: "+progTimeTxt.getText().toString()+"분\n";
        if(!(setStoryTextTxt.getText().toString()).equals("")){
            content = content + setStoryTextTxt.getText().toString();
        }
    }
}