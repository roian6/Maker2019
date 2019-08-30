package com.example.maker2019;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleHolder_Main2 extends RecyclerView.ViewHolder {

    //People 탭의 RecyclerView ViewHolder

    //TextView, ImageView 선언
    public TextView name, phone, num;

    //ViewHolder
    public RecycleHolder_Main2(View itemView) {
        super(itemView);
        //각 아이템들을 RecyclerView 아이템 뷰의 항목과 연결
        name = itemView.findViewById(R.id.item_main2_name);
        phone = itemView.findViewById(R.id.item_main2_phone);
        num = itemView.findViewById(R.id.item_main2_num);
    }

}
