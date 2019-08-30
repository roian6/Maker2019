package com.example.maker2019;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MacroActivity extends AppCompatActivity {

    EditText name, phone, num;
    TextView t1, t2, t3;

    LinearLayout macroBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macro);

        Toolbar toolbar = findViewById(R.id.macro_toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setTitle("매크로 등록");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getElements();

        EditText.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                switch (view.getId()) {
                    case R.id.macro_name:
                        t1.setTextColor(b ? getColor(R.color.colorPrimary) : Color.BLACK);
                        name.setTextColor(b ? getColor(R.color.colorPrimary) : Color.BLACK);
                        break;
                    case R.id.macro_phone:
                        t2.setTextColor(b ? getColor(R.color.colorPrimary) : Color.BLACK);
                        phone.setTextColor(b ? getColor(R.color.colorPrimary) : Color.BLACK);
                        break;
                    case R.id.macro_num:
                        t3.setTextColor(b ? getColor(R.color.colorPrimary) : Color.BLACK);
                        num.setTextColor(b ? getColor(R.color.colorPrimary) : Color.BLACK);
                        break;
                }
            }
        };

        name.setOnFocusChangeListener(onFocusChangeListener);
        phone.setOnFocusChangeListener(onFocusChangeListener);
        num.setOnFocusChangeListener(onFocusChangeListener);

        macroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserDB userDB = new UserDB();

                MacroModel model = new MacroModel(userDB.getUserKey(MacroActivity.this),
                        name.getText().toString(), phone.getText().toString(), "#"+num.getText().toString());
                databaseReference.child("macro").push().setValue(model);

                finish();

            }
        });

    }

    void getElements() {
        name = findViewById(R.id.macro_name);
        phone = findViewById(R.id.macro_phone);
        num = findViewById(R.id.macro_num);

        t1 = findViewById(R.id.macro_txt1);
        t2 = findViewById(R.id.macro_txt2);
        t3 = findViewById(R.id.macro_txt3);

        macroBtn = findViewById(R.id.macro_btn);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


}
