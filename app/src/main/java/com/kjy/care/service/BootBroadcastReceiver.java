package com.kjy.care.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.kjy.care.activity.BaseApp;
import com.kjy.care.activity.MainActivity;
import com.kjy.care.api.HttpUtil;
import com.kjy.care.bean.User;
import com.kjy.care.util.AlertUtil;
import com.kjy.care.util.AppUtil;
import com.kjy.care.util.JSONUtil;
import com.kjy.care.util.LogH;
import com.kjy.care.util.SPUtil;
import com.kjy.care.util.StringUtil;

import org.json.JSONObject;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        LogH.e("action===", action + "======");

        if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            Intent service = new Intent(context, BlutoothService.class);
                   //context.startService(service);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
                //且在service里再调用startForeground方法，不然就会出现ANR
                // startForeground(1,new Notification());
            } else {
                context.startService(service);
            }


        }


    }









}