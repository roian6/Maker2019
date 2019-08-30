package com.example.maker2019;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    private BluetoothSPP bt;
    Button deviceBtn, sendBtn;

    LinearLayout connectBtn, bluetoothCard;

    TextView deviceTxt;

    Intent intent;
    SpeechRecognizer mRecognizer;

    TextToSpeech tts;

    String input = "", toSend = "";

    private LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 2;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth firebaseAuth;

    List<MacroModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO)
                .check();

        bluetoothCard = findViewById(R.id.bluetooth_card);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottombar);
        bottomNavigationView.setBackgroundColor(Color.WHITE);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_1:
                        selectedFragment = MainFragment1.newInstance();
                        bluetoothCard.setVisibility(View.VISIBLE);
                        break;
                    case R.id.action_2:
                        selectedFragment = MainFragment2.newInstance();
                        bluetoothCard.setVisibility(View.GONE);
                        break;
                    case R.id.action_3:
                        selectedFragment = MainFragment3.newInstance();
                        bluetoothCard.setVisibility(View.GONE);
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, MainFragment1.newInstance());
        transaction.commit();
        bluetoothCard.setVisibility(View.VISIBLE);

        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        tts.setPitch(1.5f); //1.5톤 올려서
        tts.setSpeechRate(1.0f); //1배속으로 읽기


        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        sendBtn = findViewById(R.id.send);

        deviceTxt = findViewById(R.id.device_txt);
        deviceBtn = findViewById(R.id.device_dis_btn);
        deviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.disconnect();
            }
        });

        bt = new BluetoothSPP(this);
        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(this
                    , "블루투스 사용이 불가능합니다."
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                Log.d("test", "input: "+message);
                switch (message) {
                    case "back":
                        if (!input.equals("")) {
                            input = input.substring(0, input.length() - 1);
                            Log.d("test", input);
                        }
                        break;
                    case "call":
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getNum().equals("#"+input)) {
                                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    Toast.makeText(MainActivity.this, "전화 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                input="";
                                Log.d("test", input);
                                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + list.get(i).getPhone())));
                            }
                        }
                        break;
                    case "sns":
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getNum().equals("#"+input)) {

                                input="";
                                Log.d("test", input);
                                toSend = list.get(i).getPhone();
                                mRecognizer.startListening(intent);
                            }
                        }
                        break;
                    case "emergency":

                        locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
                        Location userLocation = getMyLocation();
                        double latitude, longitude;

                        latitude = userLocation.getLatitude();
                        longitude = userLocation.getLongitude();
                        //Toast.makeText(MainActivity.this, latitude + " " + longitude, Toast.LENGTH_SHORT).show();

                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage("01086998406", null, latitude + " " + longitude, null, null);
                        tts.speak("긴급 메세지가 전송되었습니다.", TextToSpeech.QUEUE_FLUSH, null, null);
                        Toast.makeText(MainActivity.this, "긴급 메세지가 전송되었습니다.",
                                Toast.LENGTH_LONG).show();
                    default:
                        input = input.concat(message);
                        Log.d("test", input);
                        break;
                }
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                deviceTxt.setText(name);
                deviceBtn.setVisibility(View.VISIBLE);
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(MainActivity.this
                        , "연결이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                deviceTxt.setText("연결된 디바이스가 없습니다.");
                deviceBtn.setVisibility(View.GONE);
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(MainActivity.this
                        , "연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

//        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
//            public void onDataReceived(byte[] data, String message) {
//                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
//            }
//        });

        connectBtn = findViewById(R.id.connectBtn); //connect
        connectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }
        });

        databaseReference.child("macro").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MacroModel model = dataSnapshot.getValue(MacroModel.class);
                UserDB userDB = new UserDB();
                if (model.getUserkey().equals(userDB.getUserKey(MainActivity.this))) {
                    list.add(model);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지

        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }

        if (mRecognizer != null) {
            mRecognizer.destroy();
            mRecognizer.cancel();
            mRecognizer = null;
        }
    }


    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
    }


    public void setup() {
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("test message", true);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(this
                        , "블루투스가 활성화되지 않았습니다."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {
            tts.speak("천천히 다시 말해주세요.", TextToSpeech.QUEUE_FLUSH, null, null);
            Toast.makeText(MainActivity.this, "천천히 다시 말해주세요.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(toSend, null, rs[0], null, null);
            tts.speak("메세지가 전송되었습니다.", TextToSpeech.QUEUE_FLUSH, null, null);
            Toast.makeText(MainActivity.this, "메세지가 전송되었습니다.",
                    Toast.LENGTH_LONG).show();

        }
    };

    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            getMyLocation();
        }
        else {
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lng = currentLocation.getLongitude();
                double lat = currentLocation.getLatitude();
            }
        }
        return currentLocation;
    }
}
