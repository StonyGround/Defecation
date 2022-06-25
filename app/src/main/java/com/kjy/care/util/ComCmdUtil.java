package com.kjy.care.util;

import android.content.Context;
import android.util.Log;

import com.kjy.care.activity.BaseApp;


import android_serialport_api.SerialportUtil;
import android_serialport_api.StringToAscii;

public class ComCmdUtil {


    /**
     *自动
     */
    public static void sendAutoCmd(){

        //FF 04 A1 00 A5
        String hex = "04B10101";
        String cmd = "FF"+ hex+ StringToAscii.HexSum(hex);
        Log.e("命令","cmd=>"+cmd);
        SerialportUtil.write(StringToAscii.hex2byte(cmd));
    }


    /**
     * 手动
     */
    public static void sendHandCmd(){

        //FF 04 A1 00 A5
        String hex = "04B10102";
        String cmd = "FF"+ hex+StringToAscii.HexSum(hex);
        Log.e("命令","cmd=>"+cmd);
        SerialportUtil.write(StringToAscii.hex2byte(cmd));
    }



    /**
     * 下发强吸，有返回值
     */
    public static void sendAbsorptionCmd(){

        //FF 04 A1 00 A5
        String hex = "04B200";
        String cmd = "FF"+ hex+StringToAscii.HexSum(hex);
        Log.e("命令","cmd=>"+cmd);
        SerialportUtil.write(StringToAscii.hex2byte(cmd));
    }

    /**
     * 下发大便，有返回值
     */
    public static void sendDefecateCmd(){

        //FF 04 A1 00 A5
        String hex = "04B400";
        String cmd = "FF"+ hex+StringToAscii.HexSum(hex);
        Log.e("命令","cmd=>"+cmd);
        SerialportUtil.write(StringToAscii.hex2byte(cmd));
    }


    /**
     * 下发小便，有返回值
     */
    public static void sendPeeCmd(){

        //FF 04 A1 00 A5
        String hex = "04B500";
        String cmd = "FF"+ hex+StringToAscii.HexSum(hex);
        Log.e("命令","cmd=>"+cmd);
        SerialportUtil.write(StringToAscii.hex2byte(cmd));
    }



    /**
     * 下发干燥，有返回值
     */
    public static void sendDryCmd(){

        //FF 04 A1 00 A5
        String hex = "04B700";
        String cmd = "FF"+ hex+StringToAscii.HexSum(hex);
        Log.e("命令","cmd=>"+cmd);
        SerialportUtil.write(StringToAscii.hex2byte(cmd));
    }




    /**
     * 下发停止，有返回值 01
     */
    public static void sendStopCmd(){

        //FF 04 A1 00 A5
        String hex = "04B800";
        String cmd = "FF"+ hex+StringToAscii.HexSum(hex);
        Log.e("命令","cmd=>"+cmd);
        SerialportUtil.write(StringToAscii.hex2byte(cmd));
    }



    /**
     * 下发温度，   有返回值  温度  单位 0.1
     * lev:0 / 1 / 2 / 3
     */
    public static void sendWaterTempCmd(int lev){

        String value = "0000";
        switch (lev){
            case 0:value = "0000";break;
            case 1:value = "012C";break;
            case 2:value = "014A";break;
            case 3:value = "017C";break;
        }

        //FF 04 A1 00 A5
        String hex = "04B902"+value;
        String cmd = "FF"+ hex+StringToAscii.HexSum(hex);
        Log.e("命令","cmd=>"+cmd);
        SerialportUtil.write(StringToAscii.hex2byte(cmd));
    }



    /**
     * 下发水压，   有返回值  01
     * lev:0 / 1 / 2 / 3
     */
    public static void sendWaterPressCmd(int lev){

        String value = "00";
        switch (lev){
            case 0:value = "00";break;
            case 1:value = "01";break;
            case 2:value = "02";break;
            case 3:value = "03";break;
        }

        //FF 04 A1 00 A5
        String hex = "04E001"+value;
        String cmd = "FF"+ hex+StringToAscii.HexSum(hex);
        Log.e("命令","cmd=>"+cmd);
        SerialportUtil.write(StringToAscii.hex2byte(cmd));
    }



    /**
     * 下发定时干燥，   有返回值  剩余时间上传
     * lev:0 / 1 / 2 / 3 /4
     */
    public static void sendTimerDryCmd(int lev){

        String value = "0000"; //无自定干燥
        switch (lev){
            case 0:value = "0000";break;//无自定干燥
            case 1:value = "003C";break;//60分钟
            case 2:value = "0078";break;//120分钟
            case 3:value = "00B4";break;//180分钟
            case 4:value = "00F0";break;//240分钟
        }

        //FF 04 A1 00 A5
        String hex = "04E102"+value;
        String cmd = "FF"+ hex+StringToAscii.HexSum(hex);
        Log.e("命令","cmd=>"+cmd);
        SerialportUtil.write(StringToAscii.hex2byte(cmd));
    }




    /**
     * 下发暖风，   有返回值 00 成功 / 01  当前状态未完成
     * lev:0 / 1
     */
    public static void sendWarmAirCmd(int lev){

        String value = "00"; //关闭
        switch (lev){
            case 0:value = "00";break;//关闭
            case 1:value = "01";break;//打开

        }

        //FF 04 A1 00 A5
        String hex = "04E201"+value;
        String cmd = "FF"+ hex+StringToAscii.HexSum(hex);
        Log.e("命令","cmd=>"+cmd);
        SerialportUtil.write(StringToAscii.hex2byte(cmd));
    }




    /**
     * APP下发请求当前下位机状态
     */
    public static void sendGetInfo(MySqlUtil mysql){

        String value = "";


        //FF 04 A1 00 A5
        String hex = "04A600"+value;
        String cmd = "FF"+ hex+StringToAscii.HexSum(hex);
        Log.e("命令","cmd=>"+cmd);
        SerialportUtil.write(StringToAscii.hex2byte(cmd));

        MyDataUtil.addRecordList(mysql,"同步下位机状态", BaseApp.User.getId());
    }



    /**
     * 下位机请求状态，下发当前APP状态
     */
    public static void sendSetInfo(Context context,MySqlUtil mysql){

        /*
        水压： 0 弱    1强
        水温(2字节)：当前温度
        暖风：0关闭   1开启
        定时干燥(2字节)：0关闭   1: 1h       2: 2h         3: 3h       4: 4h
        按摩:  0关闭   1开启
        自动/手动:   1自动    2手动
        保留：00
        */


        String WATER_PRESS_LEV= SPUtil.get(context,SPUtil.WATER_PRESS,"0");//水压 低
        String WATER_TEMPER_LEV=SPUtil.get(context,SPUtil.WATER_TEMPER,"0");//水温  关
        String WARM_AIR_LEV=SPUtil.get(context,SPUtil.WARM_AIR,"0");//暖风
        String TIMER_DRY_LEV=SPUtil.get(context,SPUtil.TIMER_DRY,"0");//定时干燥
        String MASSAGE_LEV=SPUtil.get(context,SPUtil.MASSAGE,"0");//按摩
        String AUTO_LEV=SPUtil.get(context,SPUtil.AUTO,"1");//自动

        /////////////需要处理   水温
        String WATER_TEMPER_VALUE="0000";
        switch (Integer.parseInt(WATER_TEMPER_LEV)){
            case 0:WATER_TEMPER_VALUE = "0000";break;
            case 1:WATER_TEMPER_VALUE = "012C";break;
            case 2:WATER_TEMPER_VALUE = "014A";break;
            case 3:WATER_TEMPER_VALUE = "017C";break;
        }

        /////////////需要处理  定时干燥
        String TIMER_DRY_VALUE = "0000"; //无自定干燥
        switch (Integer.parseInt(TIMER_DRY_LEV)){
            case 0:TIMER_DRY_VALUE = "0000";break;//无自定干燥
            case 1:TIMER_DRY_VALUE = "003C";break;//60分钟
            case 2:TIMER_DRY_VALUE = "0078";break;//120分钟
            case 3:TIMER_DRY_VALUE = "00B4";break;//180分钟
            case 4:TIMER_DRY_VALUE = "00F0";break;//240分钟
        }



        String value = "";
        value+="0"+WATER_PRESS_LEV;
        value+=WATER_TEMPER_VALUE;
        value+="0"+WARM_AIR_LEV;
        value+=TIMER_DRY_VALUE;
        value+="0"+MASSAGE_LEV;
        value+="0"+AUTO_LEV;
        value+="00";//保留

        // FF04A505  0000000000 sum
        //FF 04 A1 00 A5
        String hex = "04A509"+value;
        String cmd = "FF"+ hex+StringToAscii.HexSum(hex);
        Log.e("命令","cmd=>"+cmd);
        SerialportUtil.write(StringToAscii.hex2byte(cmd));


        MyDataUtil.addRecordList(mysql,"下发同步APP状态", BaseApp.User.getId());
    }


}
