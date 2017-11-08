package com.kakao.cookie;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;


public class Bluetooth extends AppCompatActivity{
    private BluetoothAdapter mBTAdapter = null;

    //다이알로그
    AlertDialog dlg;
    View dialog;
    //리스트뷰
    ListView mListMember;
    ArrayList<String> mArGeneral;
    ArrayAdapter<String> adapter;
    //선택된 블루투스
    TextView crDevice;//선택된 블루투스 디바이스
    String selectName;//선택된 왼쪽 블루투스 디바이스의 이름
    String selectRName;//선택된 오른쪽 블루투스 디바이스의 이름
    int select=0;//왼쪽이 선택이 되었는지?
    int select2=0;//오른쪽이 선택이 되었는지?
    //블루투스 연결
    private static final String BTNAME = "BTNAME";

    private final int BT_STATE_SUCCESS = 0;
    private final int BT_STATE_FAILURE = -1;
    private final int BT_STATE_NOTFOUND = -2;
    private final int BT_STATE_DISABLED = -3;

    private final int RES_CODE_BT_ENABLE = 1000;
    private final int RES_CODE_BT_LIST = 1001;

    private final int HANDLE_SEND_MSG = 0;
    private final int HANDLE_CONN_NAME = 1;

    private final UUID BT_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    private String mBTAddress = null;
    /*private BTAcceptThread mBTAcceptThread = null;
    private BTConnectThread mBTConnectThread = null;
    private BTMessageControlThread mBTMessageControlThread =
            null;
    private BluetoothAdapter mBTAdapter2 = null;
    private Handler mHandler = null;
    private int mBTState = BT_STATE_NOTFOUND;*/
    //여기 까지

    //2는 오른쪽에 대한 변수
    private final static int DEVICES_DIALOG = 1;
    private final static int ERROR_DIALOG = 2;
    static BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothSocket mmSocket2;
    BluetoothDevice mmDevice;
    BluetoothDevice mmDevice2;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    OutputStream mmOutputStream2;
    InputStream mmInputStream2;
    Thread workerThread;
    Thread workerThread2;
    byte[] readBuffer;
    byte[] readBuffer2;
    int readBufferPosition;
    int readBufferPosition2;
    int counter;
    volatile boolean stopWorker;
    volatile boolean stopWorker2;
    TextView chat,chat2;

    //step수와 관련된 변수 선언
    //아래 3줄은 DB에 저장할 것 !!!!!!!!!!!!!!!!!!!!!!!!!!!!
    int Ldata1,Ldata2,Ldata3,Ldata4,Ldata5;//왼쪽발
    int Rdata1,Rdata2,Rdata3,Rdata4,Rdata5;//오른쪽발
    int steps;         //DB - 왼쪽발의 step, 오른쪽발의 step 각각
    String date = null;                 //DB - 현재 날짜가 저장될 변수(YYYY-MM-DD)
    DBHelper dbHelper;






    private MyAsyncTask myAsyncTask;
    private MyAsyncTaskR myAsyncTaskR;
    boolean over=false;
    boolean over2=false;

    //버튼이 왼쪽발 버튼을 눌렀는지 오른쪽 발 버튼을 눌렀는지
    int first_s,second_s;


    //실험중


    private final BroadcastReceiver mBTReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                adapter.add(device.getName() + "\n" + device.getAddress());
                //adapter.add(device.getName());
                adapter.notifyDataSetChanged();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                Toast.makeText(context,
                        "블루투스 검색이 완료되었습니다.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        Button btn = (Button)findViewById(R.id.Device);
        Button btn2 = (Button)findViewById(R.id.DeviceR);


        //chat = (TextView)findViewById(R.id.LfootSensor);
        //chat2 = (TextView)findViewById(R.id.chat);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ShowDialog();
                first_s=1;
            }
        });
        btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ShowDialog2();
                second_s=1;
            }
        });


    }
    private void ShowDialog(){
        //다이알로그 정의
        dialog = View.inflate(getApplicationContext(),R.layout.activity_device_list,null);
        dlg = new AlertDialog.Builder(Bluetooth.this).setView(dialog).create();

        //이미 선택된 디바이스가 있으면 그 이름을 표시해준다.
        crDevice = (TextView) dialog.findViewById(R.id.CurrentDvlist);
        if(select==1) crDevice.setText(selectName);

        //다이알로그를 띄운다.
        dlg.show();
        //블루투스 스캔기능을 실행시킨다.
        Bluetooth();
    }
    private void ShowDialog2(){
        //다이알로그 정의
        dialog = View.inflate(getApplicationContext(),R.layout.activity_device_list2,null);
        dlg = new AlertDialog.Builder(Bluetooth.this).setView(dialog).create();

        //이미 선택된 디바이스가 있으면 그 이름을 표시해준다.
        crDevice = (TextView) dialog.findViewById(R.id.CurrentDvlist);
        if(select2==1) crDevice.setText(selectRName);

        //다이알로그를 띄운다.
        dlg.show();
        //블루투스 스캔기능을 실행시킨다.
        Bluetooth();
    }
    private void ConnectDevice(){
        if(first_s == 1){
            Button btn = (Button)findViewById(R.id.Device);
            btn.setText("Device 변경");
            TextView first = (TextView)findViewById(R.id.first);
            TextView second = (TextView)findViewById(R.id.second);
            TextView name = (TextView)findViewById(R.id.DeviceName);
            LinearLayout li = (LinearLayout)findViewById(R.id.linear1);


            first.setVisibility(View.INVISIBLE);
            second.setVisibility(View.INVISIBLE);
            li.setVisibility(View.VISIBLE);
            name.setText(selectName);
            first_s = 0;
        }
        if(second_s==1){
            Button btn = (Button)findViewById(R.id.DeviceR);
            btn.setText("Device 변경");
            TextView first = (TextView)findViewById(R.id.firstR);
            TextView second = (TextView)findViewById(R.id.secondR);
            TextView name = (TextView)findViewById(R.id.DeviceNameR);
            LinearLayout li = (LinearLayout)findViewById(R.id.linear1R);


            first.setVisibility(View.INVISIBLE);
            second.setVisibility(View.INVISIBLE);
            li.setVisibility(View.VISIBLE);
            name.setText(selectRName);
            second_s=0;
        }
        /*if(select==1&&select2==1)
        {
            Button MeasureBtn = (Button)findViewById(R.id.StartMeasurement);
            TextView MeasureTxt = (TextView)findViewById(R.id.infor);
            MeasureBtn.setVisibility(View.VISIBLE);
            MeasureTxt.setText("버튼을 누르면 측정이 시작됩니다.");
            MeasureBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {




                }
            });
        }*/




    }

    private void Bluetooth(){
        //블루투스
        mArGeneral = new ArrayList<String>();


        IntentFilter filter = new IntentFilter(
                BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBTReceiver, filter);

        filter = new IntentFilter(
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBTReceiver, filter);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices =
                mBTAdapter.getBondedDevices();


        //이미 페어링 된 리스트
        mArGeneral.add("[Bonded Devices]");
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                //mArGeneral.add(device.getName());
                mArGeneral.add(device.getName() + "\n" +
                        device.getAddress());
            }
        }

        //페어링 되지 않은 디바이스
        mArGeneral.add("[Scaned Devices]");
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mArGeneral);
        mListMember = (ListView)dialog.findViewById(R.id.list);
        mListMember.setAdapter(adapter);
        mListMember.setOnItemClickListener(mItemClickListener);


        doDiscovery();

    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String item = mArGeneral.get(position);

            String[] btInfoStr = item.split("\\n");
            mBTAddress = btInfoStr[1];
            if(first_s==1)select = 1;//왼쪽 블루투스가 선택이 됨
            else if(second_s==1)select2=1;//오른쪽 블루투스가 선택이 됨
            if(first_s==1) selectName = btInfoStr[0];//(왼발)아이템의 이름 저장
            else selectRName = btInfoStr[0];//(오른발)


            startConnectBluetooth();
            dlg.dismiss();//다이알로그 종료
            ConnectDevice();
        }
    };


    void initListView(){
        String[] strTextList = {"abc","aegd","ewgadsg","aegag","1234","!24757","dgrh5","eeqe","agec","Aegqb","545454","egdvd"};
        mArGeneral = new ArrayList<String>();
        for(int i=0;i<12;i++)
            mArGeneral.add(strTextList[i]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mArGeneral);
        mListMember = (ListView)dialog.findViewById(R.id.list);
        mListMember.setAdapter(adapter);
    }


    private void doDiscovery()
    {
        if (mBTAdapter.isDiscovering())
        {
            mBTAdapter.cancelDiscovery();
        }

        mBTAdapter.startDiscovery();
    }

    //새 블루투스
    private void startConnectBluetooth()
    {
        BluetoothDevice device = mBTAdapter.getRemoteDevice(mBTAddress);
        if(first_s==1) {
            doConnect(device);
        }
        if(second_s==1){
            doConnect2(device);
        }
    }

    //13. 백버튼이 눌러지거나, ConnectTask에서 예외발생시
    //데이터 수신을 위한 스레드를 종료시키고 CloseTask를 실행하여 입출력 스트림을 닫고,
    //소켓을 닫아 통신을 종료합니다.


    public void doClose() {
        workerThread.interrupt();
        new CloseTask().execute();
    }
    public void doClose2() {
        workerThread2.interrupt();
        new CloseTask2().execute();
    }

    public void doConnect(BluetoothDevice device) {
        mmDevice = device;

        //Standard SerialPortService ID
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            // 4. 지정한 블루투스 장치에 대한 특정 UUID 서비스를 하기 위한 소켓을 생성합니다.
            // 여기선 시리얼 통신을 위한 UUID를 지정하고 있습니다.
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            // 5. 블루투스 장치 검색을 중단합니다.
            //mBluetoothAdapter.cancelDiscovery();
            // 6. ConnectTask를 시작합니다.
            new ConnectTask().execute();
        } catch (IOException e) {
            Log.e("", e.toString(), e);
        }
    }
    public void doConnect2(BluetoothDevice device) {
        mmDevice2 = device;

        //Standard SerialPortService ID
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            // 4. 지정한 블루투스 장치에 대한 특정 UUID 서비스를 하기 위한 소켓을 생성합니다.
            // 여기선 시리얼 통신을 위한 UUID를 지정하고 있습니다.
            mmSocket2 = mmDevice2.createRfcommSocketToServiceRecord(uuid);
            // 5. 블루투스 장치 검색을 중단합니다.
            //mBluetoothAdapter.cancelDiscovery();
            // 6. ConnectTask를 시작합니다.
            new ConnectTask2().execute();
        } catch (IOException e) {
            Log.e("", e.toString(), e);
        }
    }

    private class ConnectTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                //7. 블루투스 장치로 연결을 시도합니다.
                mmSocket.connect();

                //8. 소켓에 대한 입출력 스트림을 가져옵니다.
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();

                //9. 데이터 수신을 대기하기 위한 스레드를 생성하여 입력스트림로부터의 데이터를 대기하다가
                //   들어오기 시작하면 버퍼에 저장합니다.
                //  '\n' 문자가 들어오면 지금까지 버퍼에 저장한 데이터를 UI에 출력하기 위해 핸들러를 사용합니다.
                beginListenForData();


            } catch (Throwable t) {
                Log.e( "", "connect? "+ t.getMessage() );
                doClose();
                return t;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Object result) {
            //10. 블루투스 통신이 연결되었음을 화면에 출력합니다.
            if (result instanceof Throwable)
            {
                Log.d("","ConnectTask "+result.toString() );

            }
        }
    }
    private class ConnectTask2 extends AsyncTask<Void, Void, Object> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                //7. 블루투스 장치로 연결을 시도합니다.
                mmSocket2.connect();

                //8. 소켓에 대한 입출력 스트림을 가져옵니다.
                mmOutputStream2 = mmSocket2.getOutputStream();
                mmInputStream2 = mmSocket2.getInputStream();

                //9. 데이터 수신을 대기하기 위한 스레드를 생성하여 입력스트림로부터의 데이터를 대기하다가
                //   들어오기 시작하면 버퍼에 저장합니다.
                //  '\n' 문자가 들어오면 지금까지 버퍼에 저장한 데이터를 UI에 출력하기 위해 핸들러를 사용합니다.
                beginListenForData2();



            } catch (Throwable t) {
                Log.e( "", "connect? "+ t.getMessage() );
                doClose2();
                return t;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Object result) {
            //10. 블루투스 통신이 연결되었음을 화면에 출력합니다.
            if (result instanceof Throwable)
            {
                Log.d("","ConnectTask2 "+result.toString() );

            }
        }
    }
    private class CloseTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground(Void... params) {
            try {
                try{mmOutputStream.close();}catch(Throwable t){/*ignore*/}
                try{mmInputStream.close();}catch(Throwable t){/*ignore*/}
                mmSocket.close();
            } catch (Throwable t) {
                return t;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Throwable) {
                Log.e("",result.toString(),(Throwable)result);
            }
        }
    }
    private class CloseTask2 extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground(Void... params) {
            try {
                try{mmOutputStream2.close();}catch(Throwable t){/*ignore*/}
                try{mmInputStream2.close();}catch(Throwable t){/*ignore*/}
                mmSocket2.close();
            } catch (Throwable t) {
                return t;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Throwable) {
                Log.e("",result.toString(),(Throwable)result);
            }
        }
    }


    void beginListenForData()
    {
        final Handler handler = new Handler(Looper.getMainLooper());

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == '\n')
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");

                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            //chat.setText(data);
                                            strtokDataL(data);

                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }
    void beginListenForData2()
    {
        final Handler handler = new Handler(Looper.getMainLooper());

        stopWorker2 = false;
        readBufferPosition2 = 0;
        readBuffer2 = new byte[1024];
        workerThread2 = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream2.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream2.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == '\n')
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition2];
                                    System.arraycopy(readBuffer2, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");

                                    readBufferPosition2 = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            //chat2.setText(data);
                                            strtokDataR(data);

                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer2[readBufferPosition2++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker2 = true;
                    }
                }
            }
        });

        workerThread2.start();
    }

    public class MyAsyncTask extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if(Ldata5<100&&over==false)
                over = true;
            if(over==true && Ldata5>200) {
                steps++;
                over=false;
            }
            dbHelper = new DBHelper(getApplicationContext());
            dbHelper.updateDevL(Ldata1, Ldata2, Ldata3, Ldata4, Ldata5, steps);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


        }

    }
    public class MyAsyncTaskR extends AsyncTask<String,Void,String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if(Rdata5<100&&over2==false)
                over2 = true;
            if(over2==true && Rdata5>200) {
                steps++;
                over2=false;
            }
            dbHelper = new DBHelper(getApplicationContext());
            dbHelper.updateDevR(Rdata1, Rdata2, Rdata3, Rdata4, Rdata5, steps);
            //2017-06-15
            //User myUser = new User()
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

    }
    void strtokDataL(String data){
        StringTokenizer f = new StringTokenizer(data,"\r");//계행 빼기
        StringTokenizer s = new StringTokenizer(f.nextToken()," ");//데이터 나누기
        Ldata1 = Integer.parseInt(s.nextToken());
        Ldata2 = Integer.parseInt(s.nextToken());
        Ldata3 = Integer.parseInt(s.nextToken());
        Ldata4 = Integer.parseInt(s.nextToken());
        Ldata5 = Integer.parseInt(s.nextToken());

        //DB -
        //final DBHelper dbHelper = new DBHelper(getApplicationContext());
        //Log.d("Bluetooth", "------ 현재 날짜는 "+date+" ------");
        date = "2017-06-15";

/*        if(dbHelper.getDevLastTime("deviceL") != date) {
            dbHelper.insertAct(steps);      //DB - Lsteps + Rsteps로 act 테이블에 저장;
            steps = 0;    //초기화해주기
        }
*/
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();

        //dbHelper.updateDevL(Ldata1, Ldata2, Ldata3, Ldata4, Ldata5, Lsteps);
    }

    void strtokDataR(String data){
        StringTokenizer f = new StringTokenizer(data,"\r");//계행 빼기
        StringTokenizer s = new StringTokenizer(f.nextToken()," ");//데이터 나누기
        Rdata1 = Integer.parseInt(s.nextToken());
        Rdata2 = Integer.parseInt(s.nextToken());
        Rdata3 = Integer.parseInt(s.nextToken());
        Rdata4 = Integer.parseInt(s.nextToken());
        Rdata5 = Integer.parseInt(s.nextToken());

        //DB -
        //final DBHelper dbHelper = new DBHelper(getApplicationContext());
        //Log.d("Bluetooth", "------ 현재 날짜는 "+date+" ------");
        date = "2017-06-15";

        /*if(dbHelper.getDevLastTime("deviceR") != date) {
            dbHelper.insertAct(steps);      //DB - Lsteps + Rsteps로 act 테이블에 저장;
            //초기화해주기
        }*/

        myAsyncTaskR = new MyAsyncTaskR();
        myAsyncTaskR.execute();

        //dbHelper.updateDevR(Rdata1, Rdata2, Rdata3, Rdata4, Rdata5, Rsteps);
    }




}
