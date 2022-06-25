package com.kjy.care.activity.fragment;


import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kjy.care.R;
import com.kjy.care.activity.BaseApp;

import com.kjy.care.service.MessageEvent;
import com.kjy.care.util.AppUtil;
import com.kjy.care.util.DateUtil;
import com.kjy.care.util.WifiUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class TopFragment extends Fragment {

    public static final String TAG = "TopFragment";


    public TopFragment(){

    }

    public void hiddenBack(){

        if(imageView_Back!=null){
            imageView_Back.setVisibility(View.GONE);
        }


    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }





    ImageView imageView_Back,ImageView_Blue,ImageView_Wifi,ImageView_Night;

    TextView TextView_time;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top, container, false);


        imageView_Back = view.findViewById(R.id.ImageView_Back);


        imageView_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        TextView_time = view.findViewById(R.id.TextView_time);

        ImageView_Blue= view.findViewById(R.id.ImageView_Blue);
        ImageView_Blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtil.toSystemSet(getActivity(),"com.android.settings.bluetooth.BluetoothSettings");
            }
        });
        ImageView_Wifi= view.findViewById(R.id.ImageView_Wifi);
        ImageView_Wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtil.toSystemSet(getActivity(),"com.android.settings.wifi.WifiSettings");
            }
        });
        ImageView_Night= view.findViewById(R.id.ImageView_Night);;
        ImageView_Night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



        handler.sendEmptyMessage(handler_time);
        handler.sendEmptyMessage(handler_wifi);
        handler.sendEmptyMessage(handler_blue);




        return view;
    }










    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent obj){

        //Log.e("订阅",obj.getName()+"===");

        switch (obj){
            case WifiConnect:
                 handler.sendEmptyMessageDelayed(handler_wifi,2000);

                break;
            case WifiDisConnect:
                if(ImageView_Wifi!=null && ImageView_Wifi.getVisibility()==View.VISIBLE){ ImageView_Wifi.setImageResource(R.mipmap.wifi0);}
                break;
            case CheckWifi:
                handler.sendEmptyMessage(handler_wifi);
                break;

            case Minite:

                handler.sendEmptyMessage(handler_time);

                break;
            case Blue_off:

                handler.sendEmptyMessage(handler_blue_off);

                break;
            case Blue_on:

                handler.sendEmptyMessage(handler_blue_on);

                break;
        }



    }

    final  int handler_time=0;
    final  int handler_wifi=1;


    final  int handler_blue_on=2;
    final  int handler_blue_off=3;
    final  int handler_blue=4;
    Handler  handler=new Handler(){


        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case handler_time:

                    upadteTime();
                    break;
                case handler_wifi:

                    getWifiLev();
                    break;
                case handler_blue_on:

                    changeBlue(1);
                    break;
                case handler_blue_off:

                    changeBlue(0);

                    break;
                case handler_blue:
                    getBlueStatus();
                    break;
            }

        }
    };


    private void getWifiLev(){
        int type = WifiUtils.getWifiLev(getActivity());
        if(ImageView_Wifi!=null && ImageView_Wifi.getVisibility()==View.VISIBLE) {
            switch (type) {

                case 1:
                    ImageView_Wifi.setImageResource(R.mipmap.wifi1);
                    break;
                case 2:
                    ImageView_Wifi.setImageResource(R.mipmap.wifi2);
                    break;
                case 3:
                    ImageView_Wifi.setImageResource(R.mipmap.wifi3);
                    break;
                case 4:
                    ImageView_Wifi.setImageResource(R.mipmap.wifi4);
                    break;
                default:
                    ImageView_Wifi.setImageResource(R.mipmap.wifi0);
                    break;
            }
        }
    }
    private void upadteTime(){


        if(TextView_time!=null && TextView_time.VISIBLE == View.VISIBLE){
            String time = DateUtil.getUserDate("HH:mm");
            TextView_time.setText(time);
        }

    }


    private void changeBlue(int type){


        if(ImageView_Blue!=null && ImageView_Blue.VISIBLE == View.VISIBLE){
            if(type == 0){
                //未开启

                ImageView_Blue.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.blue_1_2x));

            }else{
                //已开启

                ImageView_Blue.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.blue_2x));
            }


        }

    }



    private void getBlueStatus(){


        if (BaseApp.mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            changeBlue(1);

        } else if (BaseApp.mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {

            changeBlue(0);
        }

    }


}