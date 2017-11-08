package com.kakao.cookie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by soyoon on 2017-05-18.
 */

public class DBHelper extends SQLiteOpenHelper {
    private Context context;
    private static String DBPATH = "/data/data/com.kakao.cookie/databases/";
    private static String DBNAME = "soyoon02.db";           //생성할 db의 이름

    //생성자
    public DBHelper(Context context)   {
        super(context, DBNAME, null, 1);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists USER(" +
                "_id integer primary key autoincrement, " +
                "name text," +
                "gender text," +
                "age integer, " +
                "height integer default 0, " +
                "weight integer default 0, " +
                "pImage text, " +
                "goalStep integer default 0);");
                /*"actNo integer references act(_id)" +       //act table에서 _id 참조하기
                "on delete restrict);");    */                //부모 키 값이 변경/삭제 되지 못하도록 막음
        Log.d("TAG", "======== USER 테이블 생성! ========");


        db.execSQL("create table if not exists SNS(" +
                "_id integer primary key autoincrement, " +
                "nickName text, " +
                "userID text, " +
                "pImage text);");
        Log.d("TAG", "======== SNS 테이블 생성! ========");


        db.execSQL("CREATE TABLE IF NOT EXISTS bodysize(" +
                "_id integer primary key autoincrement," +
                "date text, " +                       //입력한 당시의 YYYY-MM-DD
                "height integer default 0, " +
                "weight integer default 0);");
        Log.d("TAG", "======== BODYSIZE 테이블 생성! ========");


        db.execSQL("CREATE TABLE IF NOT EXISTS deviceL(" +
                "_id integer primary key autoincrement, " +
                "ldata1 integer default 0, " + "ldata2 integer default 0, " + "ldata3 integer default 0, " +
                "ldata4 integer default 0, " + "ldata5 integer default 0, " +
                "steps integer default 0, " +
                "cTime text);");           //입력한 당시의 YYYY-MM-DD HH:MM:SS
        db.execSQL("CREATE TABLE IF NOT EXISTS deviceR(" +
                "_id integer primary key autoincrement, " +
                "rdata1 integer default 0, " + "rdata2 integer default 0, " + "rdata3 integer default 0, " +
                "rdata4 integer default 0, " + "rdata5 integer default 0, " +
                "steps integer default 0, " +
                "cTime text);");

        Log.d("TAG", "======== DEVICE 테이블 생성! ========");
        db.execSQL("INSERT INTO deviceL(ldata1, cTime) VALUES(0, datetime('now', 'localtime'));");
        db.execSQL("INSERT INTO deviceR(rdata1, cTime) VALUES(0, datetime('now', 'localtime'));");


        db.execSQL("CREATE TABLE IF NOT EXISTS act(" +
                "_id integer primary key autoincrement, " +
                "act_date text, " +                     //입력되는 당시의 YYYY-MM-DD
                "total_step integer);");
        Log.d("TAG", "======== ACT 테이블 생성! ========");
        //db.execSQL("INSERT INTO act VALUES(null, datetime")


        db.execSQL("CREATE TABLE IF NOT EXISTS backupdata(" +
                "_id integer primary key autoincrement, " +
                "backupDate text, " +      //insert시 null 처리
                "user_id integer, " +           //마지막 user_id만 불러오게 하기
                "bodysize_id integer, " +       //마지막 저장된 bodysize_id까지 불러오게 하기
                "act_id integer);");            //마지막 저장된 act_id까지 불러오게 하기
        Log.d("TAG", "======== BACKUP DATA 테이블 생성! ========");

        db.execSQL("CREATE TABLE myTable(" +
                "xValues integer," +
                "yValues integer);");
        Log.d("TAG", "======== 그래프 전용 테이블 생성! ========");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)    {
        if(oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS user;");
            db.execSQL("DROP TABLE IF EXISTS sns;");
            db.execSQL("DROP TABLE IF EXISTS body;");
            db.execSQL("DROP TABLE IF EXISTS device;");
            db.execSQL("DROP TABLE IF EXISTS act;");

            onCreate(db);       //테이블 재생성
            Log.d("TAG", "======== 테이블 재생성 ========");
        }
    }

    /**************** insert함수 ****************/
    public void insertUser(String name, String gender, String age, String height, String weight, String pImage)  {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO user VALUES(null, '" + name + "', " +
                                                    "'" + gender + "', " +
                                                          age + ", "  +
                                                          height + ", " +
                                                          weight + ", " +
                                                    "'" + pImage + "', " +
                                                    "0);";
        Log.d("TAG", "insertUser-sql은 이렇습니당 --> " + sql);
        db.execSQL(sql);

        String sql2 = "INSERT INTO bodysize VALUES(null, date('now', 'localtime'), " + height + ", " + weight + ");";
        Log.d("TAG", "sql2 --> " + sql2);
        db.execSQL(sql2);
        db.close();
    }  /* user : ProfileActivity.java 에서 (goalStep 제외) */

    public void insertAct(int steps)    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO act(act_date, total_step) VALUES(date('now', 'localtime', '-1 day'), " + steps + ");");
            //act에 data를 추가하는 경우는 날이 바뀌었기 때문이므로,
        db.close();
    }

    public void insertSNS(String nickName, String userID, String pImage)    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO sns VALUES(null, '" + nickName + "', "
                                                + "'" + userID + "', "
                                                + "'" + pImage + "');";
        Log.d("TAG", "insertSNS-sql은 이렇습니당 --> " + sql);
        String sql2 = "UPDATE user SET name='" + nickName + "' WHERE _id=(SELECT MAX(_id) FROM user);";

        db.execSQL(sql);
        db.execSQL(sql2);
        db.close();
    }

    public void insertBackupdata()  {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO backupdata VALUES(null, datetime('now', 'localtime'), " +
                "(select max(_id) from user), (select max(_id) from bodysize), (select max(_id) from act));";
        Log.d("TAG", "insertBackup-sql은 이렇습니당 --> " + sql);
        db.execSQL(sql);
        db.close();
    }

    //from hyunjin
    public void insertData(int x, int y){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("xValues",x);
        contentValues.put("yValues",y);

        db.insert("MyTable", null, contentValues);
    }


    public String printTable(String table) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + table + ";";
        Cursor cursor = db.rawQuery(sql, null);

        String result = null;
        cursor.moveToFirst();
        while(cursor.moveToNext())  {
            result += cursor.getString(0) + " / "
                    + cursor.getString(1) + " / "
                    + cursor.getString(2) + " / "
                    + cursor.getString(3) + " / "
                    + cursor.getString(4) + " / "
                    + cursor.getString(5) + " / "
                    + cursor.getString(6) + "\n";
        }
        cursor.close();
        return result;
    }

    public boolean checkUser()  {
        SQLiteDatabase db = getReadableDatabase();
        int flag = 0;
        String result = null;

        Cursor cursor = db.rawQuery("SELECT * FROM user;", null);
        cursor.moveToFirst();
        flag = cursor.getCount();
        cursor.close();

        Log.d("TAG", "User table 확인 flag 값은 " + flag + "입니다.");
        Log.d("TAG", "=================================\n" + printTable("user") + "\n=================================");

        if(flag > 0)    return true;
        else            return false;
    }


    /**************** update함수 ****************/
    public void updateUser(int gStep)   {       //사용 : User
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE user SET goalStep=" + gStep + " WHERE _id=(SELECT MAX(_id) FROM user);");
            //_id 값이 최대인(가장 최근에 추가된 user 데이터에서 goalStep 정보 업데이트
        db.close();
    }

    public void updateUserpImage(String path)   {
        SQLiteDatabase db = getWritableDatabase();
        // 5/25 am4:43 update보다는 insert를 하는 것이 backup에 유용할 것이라고 생각
        //db.execSQL("UPDATE user SET pImage='" + path + "' WHERE _id=(SELECT MAX(_id) FROM user);");
        //_id 값이 최대인(가장 최근에 추가된 user 데이터에서 프로필 이미지 정보 업데이트
        String[] userInfo = getUser(null).split(",");
        //userInfo에 차례대로 0(name) 1(gender) 2(age) 3(height) 4(weight) 5(pImage) 6(goalStep) 저장

        db.execSQL("INSERT INTO user VALUES(null, '" + userInfo[0] + "', " +
                                                 "'" + userInfo[1] + "', " +
                                                       userInfo[2] + ", "  +
                                                       userInfo[3] + ", " +
                                                       userInfo[4] + ", " +
                                                       "'" + path + "', " +     //path만 최신 값. 나머지는 이전 값
                                                       userInfo[6] + ");");
        db.close();
    }

    public void modifyUser(String data1, String data2, String data3, String data4, String data5, String data6)    {
        SQLiteDatabase db = getWritableDatabase();
        /*if(data6 == null)   //프로필 이미지가 수정되지 않은 경우(그대로 사용)
            db.execSQL("UPDATE user SET name='" + data1 +
                                    "', gender='" + data2 +
                                    "', age=" + data3 +
                                    ", height=" + data4 +
                                    ", weight=" + data5 + " WHERE _id=(SELECT MAX(_id) FROM user);");
        else                //프로필 이미지가 수정된 경우
            db.execSQL("UPDATE user SET name='" + data1 +
                    "', gender='" + data2 +
                    "', age=" + data3 +
                    ", height=" + data4 +
                    ", weight=" + data5 +
                    ", pImage='" + data6 + "' WHERE _id=(SELECT MAX(_id) FROM user);");
*/
        Cursor cursor = db.rawQuery("SELECT pImage FROM user WHERE _id=(SELECT MAX(_id) FROM user);",null);
        cursor.moveToFirst();
        String t_pImage = cursor.getString(0);

        Cursor cursor2 = db.rawQuery("SELECT goalStep FROM user WHERE _id=(SELECT MAX(_id) FROM user);", null);
        cursor2.moveToFirst();
        int t_goalStep = cursor2.getInt(0);


        if(data6 == null)
            db.execSQL("INSERT INTO user VALUES(null, '" + data1 + "', '" + data2 + "', " + data3 + ", "
                                                    + data4 + ", " + data5 + ", '" + t_pImage + "', " + t_goalStep + ");");
        else
            db.execSQL("INSERT INTO user VALUES(null, '" + data1 + "', '" + data2 + "', " + data3 + ", "
                    + data4 + ", " + data5 + ", '" + data6 + "', " + t_goalStep + ");");


        String sql2 = "INSERT INTO bodysize VALUES(null, date('now', 'localtime'), " + data4 + ", " + data5 + ");";
        Log.d("TAG", "sql2 in 'modifyUser' --> " + sql2);
        db.execSQL(sql2);
        db.close();
    }

    public void updateDevL(int data1, int data2, int data3, int data4, int data5, int steps)   {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "UPDATE deviceL SET ldata1=" + data1 + ", "
                + "ldata2=" + data2 + ", "
                + "ldata3=" + data3 + ", "
                + "ldata4=" + data4 + ", "
                + "ldata5=" + data5 + ", "
                + "steps=" + steps + ", "
                + "cTime=datetime('now', 'localtime') "
                + "where _id=(SELECT MAX(_id) FROM deviceL);";
        Log.d("TAG", "==왼쪽==에서 sql은 " + sql);
        db.execSQL(sql);

        db.close();
    }

    public void updateDevR(int data1, int data2, int data3, int data4, int data5, int steps)   {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "UPDATE deviceR SET rdata1=" + data1 + ", "
                + "rdata2=" + data2 + ", "
                + "rdata3=" + data3 + ", "
                + "rdata4=" + data4 + ", "
                + "rdata5=" + data5 + ", "
                + "steps=" + steps + ", "
                + "cTime=datetime('now', 'localtime') "
                + "where _id=(SELECT MAX(_id) FROM deviceR);";
        Log.d("TAG", "==오른쪽==에서 sql은 " + sql);
        db.execSQL(sql);
        db.close();
    }


    /**************** get함수 ****************/
    public String getUser(String backup) {      //사용 : RestoreActivity(backup), User(), SNS()
        SQLiteDatabase db = getReadableDatabase();
        String userInfo = null;
        String sql = null;

        if(backup != null)  sql = "SELECT * FROM user WHERE _id=" + backup + ";";   //backup 정보를 불러오는 경우
        else                sql = "SELECT * FROM user WHERE _id=(SELECT MAX(_id) FROM user);";

        Cursor cursor = db.rawQuery(sql, null);
        //_id 기준으로 내림차순 정렬(가장 마지막 데이터가 맨 위로)
        cursor.moveToFirst();
        userInfo = cursor.getString(1) + "," + cursor.getString(2) + ","
                    + cursor.getInt(3) + "," + cursor.getInt(4) + ","
                    + cursor.getInt(5) + "," + cursor.getString(6) + "," + cursor.getInt(7);
                    //1(name) 2(gender) 3(age) 4(height) 5(weight) 6(pImage) 7(goalStep)

        cursor.close();
        return userInfo;
    }

    public String getSNS()   {       //SNS
        SQLiteDatabase db = getReadableDatabase();
        String snsInfo = null;

        Cursor cursor = db.rawQuery("SELECT * FROM sns WHERE _id=(SELECT MAX(_id) FROM sns);", null);
        //_id 기준으로 내림차순 정렬(가장 마지막 데이터가 맨 위로)
        cursor.moveToFirst();
        snsInfo = cursor.getString(1) + "," + cursor.getString(2) + "," + cursor.getString(3);
                //1열(nickName) 2열(userID) 3열(pImage)

        Log.d("TAG", "snsInfo는 " + snsInfo);

        cursor.close();
        return snsInfo;
    }

    public String getBodysize()   {
        SQLiteDatabase db = getReadableDatabase();
        String bodySize = null;

        Cursor cursor = db.rawQuery("SELECT height, weight FROM bodysize WHERE _id=(SELECT MAX(_id) FROM bodysize);", null);
        //_id 기준으로 내림차순 정렬(가장 마지막 데이터가 맨 위로)
        cursor.moveToFirst();
        bodySize = cursor.getInt(0) + "," + cursor.getInt(1);  //0열(height) 값과 1열(weight)값 저장

        cursor.close();
        return bodySize;
    }

    public String getDevLastTime(String dev) {  //dev는 deviceL 혹은 deviceR
        SQLiteDatabase db = getReadableDatabase();
        String dev_cTime = null;

        Cursor cursor = db.rawQuery("SELECT cTime FROM " + dev + ";", null);    //가장 최근 데이터의 cTime 불러오기
        cursor.moveToFirst();
        dev_cTime = cursor.getString(0);
        String[] dateTime = dev_cTime.split(" ");   //0번째 index에는 날짜 / 1번째 index에는 시간

        cursor.close();
        Log.d("TAG", "dev) 현재 시간은 " + dateTime[0]);
        return dateTime[0];
    }

    public int getDevStep(String dev) {  //dev는 deviceL 혹은 deviceR / 사용 : SNS, User
        SQLiteDatabase db = getReadableDatabase();
        int devStep = 0;

        Cursor cursor = db.rawQuery("SELECT steps FROM " + dev + ";", null);    //가장 최근 데이터의 step 수 불러오기
        cursor.moveToFirst();
        devStep = cursor.getInt(0);

        cursor.close();
        return devStep;
    }

    public String getDevSignal(String dev)  {
        SQLiteDatabase db = getReadableDatabase();
        String dev_signals = null;
        String sql = null;
        if(dev == "deviceL")    sql = "SELECT ldata1, ldata2, ldata3, ldata4, ldata5 FROM " + dev + ";";
        else                    sql = "SELECT rdata1, rdata2, rdata3, rdata4, rdata5 FROM " + dev + ";";

        Cursor cursor = db.rawQuery(sql, null);    //가장 최근 데이터의 센서값들 읽어오기
        cursor.moveToFirst();
        dev_signals = String.valueOf(cursor.getInt(0)) + ","
                    + String.valueOf(cursor.getInt(1)) + ","
                    + String.valueOf(cursor.getInt(2)) + ","
                    + String.valueOf(cursor.getInt(3)) + ","
                    + String.valueOf(cursor.getInt(4));

        Log.d("TAG", "getDevSignal - dev_signals는 " + dev_signals + "입니다");
        cursor.close();
        return dev_signals;
    }

    public String getBackup()   {       //RestoreActivity
        SQLiteDatabase db = getReadableDatabase();
        String backUpInfo = null;

        Cursor cursor = db.rawQuery("SELECT * FROM backupdata WHERE _id=(SELECT MAX(_id) FROM backupdata);", null);
        //가장 마지막 데이터 검색
        cursor.moveToFirst();
        backUpInfo = cursor.getString(1) + "," + cursor.getInt(2) + ","
                    + cursor.getInt(3) + "," + cursor.getInt(4);
                    //1열(backupDate) 2열(user_id)값 3열(bodysize_id) 4열(act_id)

        cursor.close();
        return backUpInfo;
    }


    /**************** count함수 ****************/
    public int countBackup()    {       //backupdata 테이블에 저장된 데이터의 개수를 반환
        SQLiteDatabase db = getReadableDatabase();
        int count = 0;

        Cursor cursor = db.rawQuery("SELECT MAX(_id) FROM backupdata;", null);
        cursor.moveToFirst();
        count = cursor.getInt(0);

        cursor.close();
        return count;
    }

    /**************** delete함수 ****************/
    public void deleteBackup(int countBackup)  {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM backupdata WHERE _id<=(SELECT MAX(_id) FROM backupdata);");
        db.close();
    }

    public void deleteUser()  {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM user WHERE _id<=(SELECT MAX(_id) FROM user);");
        db.execSQL("DROP TABLE IF EXISTS user;");
        db.execSQL("CREATE TABLE USER(" +
                "_id integer primary key autoincrement, " +
                "name text," +
                "gender text," +
                "age integer, " +
                "height integer default 0, " +
                "weight integer default 0, " +
                "pImage text, " +
                "goalStep integer default 0);");
        db.execSQL("DELETE FROM act WHERE _id<=(SELECT MAX(_id) FROM act);");
        db.execSQL("DELETE FROM bodysize WHERE _id<=(SELECT MAX(_id) FROM bodysize);");
        db.execSQL("DELETE FROM sns WHERE _id<=(SELECT MAX(_id) FROM sns);");
        db.execSQL("DROP TABLE IF EXISTS myTable;");
        db.execSQL("CREATE TABLE myTable(" +
                "xValues integer," +
                "yValues integer);");
        db.close();
    }
}
