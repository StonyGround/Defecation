package com.kjy.care.activity.fragment;


import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

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
import com.kjy.care.util.MySqlUtil;
import com.kjy.care.util.WifiUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class BaseFragment extends Fragment {

    public static final String TAG = "TopFragment";
    public MySqlUtil mysql;

    public BaseFragment(){

    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mysql = new MySqlUtil(getActivity());
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







    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent obj){

        }






}