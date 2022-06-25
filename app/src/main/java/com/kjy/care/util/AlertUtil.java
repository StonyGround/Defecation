package com.kjy.care.util;

import android.content.Context;
import android.widget.Toast;

import com.kjy.care.activity.BaseApp;

public class AlertUtil {

    public static void show(String msg){


        Toast.makeText(BaseApp.getAppContext(),msg,Toast.LENGTH_LONG).show();
    }

    public static void show(String msg, Context context){


        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }





}
