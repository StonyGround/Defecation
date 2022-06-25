package com.kjy.care.activity.fragment;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.kjy.care.R;
import com.kjy.care.activity.DeviceActivity;
import com.kjy.care.activity.UserActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ControlAutoFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "ControlAuto";


    public ControlAutoFragment(){

    }




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }






    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto, container, false);


        TextView TextView_more=view.findViewById(R.id.TextView_more);
        TextView_more.setOnClickListener(this);


        RelativeLayout RelativeLayout_add=view.findViewById(R.id.RelativeLayout_add);
        RelativeLayout_add.setOnClickListener(this);


        RelativeLayout  RelativeLayout_vip=view.findViewById(R.id.RelativeLayout_vip);
        RelativeLayout_vip.setOnClickListener(this);


        return view;
    }










    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.RelativeLayout_add:
                DeviceActivity.luncher();
                break;

            case R.id.RelativeLayout_vip:
            case R.id.TextView_more:

                UserActivity.luncher();
                break;


        }

    }
}