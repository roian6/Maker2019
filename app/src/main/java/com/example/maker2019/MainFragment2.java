package com.example.maker2019;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.crypto.Mac;

public class MainFragment2 extends Fragment {

    public static MainFragment2 newInstance(){
        MainFragment2 fragment = new MainFragment2();
        return fragment;
    }

    Button callBtn, messageBtn, locationBtn;

    private LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 2;

    RecyclerView rcv;
    RecycleAdapter_Main2 rcvAdap;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth firebaseAuth;

    LinearLayout uploadBtn;

    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main2, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        context = container.getContext();

        rcv = v.findViewById(R.id.main2_recycler);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

//        lm.setReverseLayout(true);
//        lm.setStackFromEnd(true);

        rcv.setLayoutManager(lm); //RecyclerView에 LayoutManager 지정

        rcvAdap = new RecycleAdapter_Main2(getContext());

        rcv.setAdapter(rcvAdap);

        databaseReference.child("macro").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MacroModel model = dataSnapshot.getValue(MacroModel.class);
                UserDB userDB = new UserDB();
                if(model.getUserkey().equals(userDB.getUserKey(context))) {
                    rcvAdap.add(model);
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

        uploadBtn = v.findViewById(R.id.main2_add);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MacroActivity.class));
            }
        });

//        callBtn = v.findViewById(R.id.call_btn);
//        callBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getContext(), "전화 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "01012345678")));
//
//            }
//        });
//
//        messageBtn = v.findViewById(R.id.message_btn);
//        messageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(Intent.ACTION_VIEW);
////                intent.setData(Uri.parse("smsto:"));
////                intent.setType("vnd.android-dir/mms-sms");
////                //intent.setDataAndType(Uri.parse("smsto:"), "vnd.android-dir/mms-sms");
////                intent.putExtra("address", "01086998406");
////                intent.putExtra("sms_body","test message");
////                startActivity(Intent.createChooser(intent, "Send sms via:"));
//
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage("01086998406", null, "test message", null, null);
//                Toast.makeText(getContext(), "메세지가 전송되었습니다.",
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//
//        locationBtn = v.findViewById(R.id.location_btn);
//        locationBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
//                Location userLocation = getMyLocation();
//                if (userLocation != null) {
//                    double latitude = userLocation.getLatitude();
//                    double longitude = userLocation.getLongitude();
////                    userVO.setLat(latitude);
////                    userVO.setLon(longitude);
//                    Toast.makeText(getContext(), latitude + " " + longitude, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });



        return v;
    }

    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
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
