package com.kjy.care.util;


import android.util.Log;

import com.kjy.care.activity.BaseApp;

public class LogH {
    final static boolean DEBUG_MODE = AppUtil.isApkInDebug(BaseApp.getAppContext());
    final static int LENGTH = 4000;

    public static void e(String tag, String msg) {
        try {

            if (DEBUG_MODE) {
                int max_str_length = LENGTH - tag.length();
                //大于4000时
                while (msg.length() > max_str_length) {
                    LogH.e(tag, msg.substring(0, max_str_length));
                    msg = msg.substring(max_str_length);
                }
                //剩余部分
                Log.e(tag, msg);
            }
        }catch (Exception e){}
    }

    public static void e(String msg) {
        try {

            if (DEBUG_MODE) {
                LogH.e("健康", msg);
            }
        }catch (Exception e){

        }

    }
}
