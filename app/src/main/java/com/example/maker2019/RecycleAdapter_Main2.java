package com.example.maker2019;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class RecycleAdapter_Main2 extends RecyclerView.Adapter<RecycleHolder_Main2> {

    //Main2 탭의 RecyclerView Adapter

    List<MacroModel> items = new ArrayList<>(); //RecyclerView에 들어갈 아이템 저장 ArrayList 선언

    public List<MacroModel> getItems() {
        return items;
    } //List의 아이템을 반환하는 함수

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth firebaseAuth;

    Context context;

    public RecycleAdapter_Main2(Context context) {
        this.context = context;
    }

    public void add(MacroModel data) { //리스트에 값을 추가하는 함수
        items.add(data); //리스트에 양식으로 전달받은 값 추가
        notifyDataSetChanged(); //RecyclerView 갱신
    }

    @NonNull
    @Override
    public RecycleHolder_Main2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main2, parent, false);
        return new RecycleHolder_Main2(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecycleHolder_Main2 holder, final int position) {

        MacroModel item = items.get(position);

        holder.name.setText(item.getName());
        holder.phone.setText(item.getPhone());
        holder.num.setText(item.getNum());

    }

    @Override
    public int getItemCount() {
        return items.size(); //리스트 크기 반환
    }


}
