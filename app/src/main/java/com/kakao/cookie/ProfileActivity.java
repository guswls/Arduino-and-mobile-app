package com.kakao.cookie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView profileImg;
    private Button saveBtn;
    private Button cancelBtn;

    private EditText name;
    private RadioGroup gender;
    private RadioButton gen_chosen;
    private EditText age;
    private EditText height;
    private EditText weight;
    private String pImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImg = (ImageView)findViewById(R.id.Profilelmg);
        saveBtn = (Button)findViewById(R.id.SaveBtn);
        cancelBtn = (Button)findViewById(R.id.CancelBtn);

        //DB - 입력되는 정보(이름, 성별, 나이, 키, 몸무게 저장) layout 불러오기
        name = (EditText)findViewById(R.id.Edit);
        gender = (RadioGroup)findViewById(R.id.radioGroup);
        age = (EditText)findViewById(R.id.Edit2);
        height = (EditText)findViewById(R.id.Edit3);
        weight = (EditText)findViewById(R.id.Edit4);

        profileImg.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        //액션바 색상 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable((0xFF71C9CE)));


    }

    @Override
    public void onClick(View v) {
        if (v.equals(profileImg)){
            doTakeAlbumAction();
        }

        else if (v.equals(saveBtn)){
            final DBHelper dbHelper = new DBHelper(getApplicationContext());

            //사용자가 등록한 정보들을 db에 저장
            gen_chosen = (RadioButton)findViewById(gender.getCheckedRadioButtonId());
            String data1 = name.getText().toString();
            String data2 = gen_chosen.getText().toString();
            String data3 = age.getText().toString();
            String data4 = height.getText().toString();
            String data5 = weight.getText().toString();
            String data6 = pImage;
            //저장 버튼 클릭 시 DB에 저장 후 사용자 선택 화면으로 이동

            //DB - user 테이블에 데이터 추가
            dbHelper.insertUser(data1, data2, data3, data4, data5, data6);

            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if (v.equals(cancelBtn)){
            //취소 버튼 클릭 시 activity_main 화면으로 이동
            Intent intent = new Intent(ProfileActivity.this, RegisterUserActivity.class);
            startActivity(intent);
            finish();
        }
    }
    //갤러리에서 사진 가져오기
    private Uri mImageCaptureUri;
    private String absolutePath;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;
    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case  PICK_FROM_ALBUM:
            {
                mImageCaptureUri = data.getData();
                Log.d("Cookie-tmp",mImageCaptureUri.getPath().toString());
                //이미지를 가져온 이후 리사이즈할 이미지 크기 결정
                //이후 이미지 크롭 어플리케이션 호출
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                //crop할 이미지를 200*200크기로 저장
                intent.putExtra("outputX",200);//crop한 이미지의 x축 크기
                intent.putExtra("outputY",200);//crop한 이미지의 y축 크기
                intent.putExtra("aspectX",1);//crop박스의 x축 비율
                intent.putExtra("aspectY",1);//crop박스의 y축 비율
                intent.putExtra("scale",true);
                intent.putExtra("return-data",true);
                startActivityForResult(intent, CROP_FROM_IMAGE);
                break;
            }
            case CROP_FROM_IMAGE:
            {
                //크롭 이후의 이미지 넘겨받음
                //이미지뷰에 이미지를 보여주거나 부가적 작업 후 임시 파일 삭제
                if(resultCode != RESULT_OK)
                    return;
                final Bundle extras = data.getExtras();
                //crop된 이미지를 저장하기 위한 파일 경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+
                        "/Cookie_tmp/user.jpg";
                pImage = filePath;      //DB - DB에 저장하기 위한 파일이름(아니면 밑에 dirPath 변수)
                if (extras != null){
                    Bitmap photo = extras.getParcelable("data");//크롭된 비트맵
                    profileImg.setImageBitmap(photo);//레이아웃의 이미지칸에 보여줌
                    storeCropImage(photo, filePath);//crop된 이미지를 외부저장소,앨범에 저장
                    absolutePath = filePath;
                    break;
                }
                //임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists()){
                    f.delete();
                }
            }
        }
    }
    private void storeCropImage(Bitmap bitmap, String filePath){
        //Cookie_tmp폴더 생성하여 이미지 저장
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+
                "/Cookie_tmp";
        File directory_cookie = new File(dirPath);
        if(!directory_cookie.exists())
            directory_cookie.mkdir();
        File copyFile = new File(filePath);
        BufferedOutputStream out = null;
        try{
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            //크롭된 사진을 앨범에 보이도록 갱신
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));
            out.flush();
            out.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}


