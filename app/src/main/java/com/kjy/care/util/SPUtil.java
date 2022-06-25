package com.kjy.care.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 420041900 on 2017/3/16.
 */

public class SPUtil {

    public  final static String SPKEY="HEALTH";
    public final static  String IP="IP";
    public final static  String APP="APP";//hospital /health
    public final static  String TOKEN="token";
    public  final static String USER="USER";

    public  final static String  SINGLEMODEL="SINGLEMODEL";//单设备模式

    public  final static String  WATER_PRESS="WATER_PRESS_LEV";//水压
    public  final static String  WATER_TEMPER="WATER_TEMPER_LEV";//水温
    public  final static String  WARM_AIR="WARM_AIR_LEV";//暖风
    public  final static String  TIMER_DRY="TIMER_DRY_LEV";//定时干燥 TimerDry
    public  final static String  MASSAGE="MASSAGE_LEV";//按摩Massage
    public  final static String  AUTO="AUTO_LEV";//自动、手动模式





    public static String get(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences(SPKEY, context.MODE_PRIVATE);
        String value=pref.getString(key,"");

        return value;
    }

    public static String get(Context context, String key,String defaultValue){
        SharedPreferences pref = context.getSharedPreferences(SPKEY, context.MODE_PRIVATE);
        String value=pref.getString(key,defaultValue);

        if(value.trim().length()==0){

            value = defaultValue;
        }

        return value;
    }

    public static void set(Context context, String key, String value){

        SharedPreferences pref = context.getSharedPreferences(SPKEY, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key,value);
        editor.commit();
    }


}
