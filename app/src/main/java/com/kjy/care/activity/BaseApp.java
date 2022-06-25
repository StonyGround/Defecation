package com.kjy.care.activity;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.kjy.blue.blutooth.BluetoothUtil;
import com.kjy.blue.blutooth.DataEvent;
import com.kjy.blue.blutooth.DeviceEnum;
import com.kjy.care.api.HttpUtil;
import com.kjy.care.bean.User;

import com.kjy.care.service.BlutoothService;
import com.kjy.care.util.AlertUtil;
import com.kjy.care.util.CrashHandler;
import com.kjy.care.util.LinkedTreeMapUtil;
import com.kjy.care.util.StringUtil;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.multidex.MultiDex;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseApp extends Application {
    private static BaseApp mApp;
    private static Activity sActivity;
    public static  int locakIndex=0;
    public static boolean isStop=false;
    public static boolean singleMolel = false;//不启用单设备模式

    public static  User User=null;

    public static BluetoothManager bluetoothManager = null;
    public static BluetoothAdapter mBluetoothAdapter = null;

    private static BluetoothUtil BLEUtil;
    public  static BluetoothUtil getBLE(){


        if(BaseApp.BLEUtil==null){
            BaseApp.BLEUtil=new BluetoothUtil(getAppContext(),new DataEvent(){

                @Override
                public void uploadDeviceData(DeviceEnum deviceEnum, Map map, boolean b) {
                    BaseApp.uploadDeviceData(deviceEnum, map, b);
                }
            });
        }
        return BaseApp.BLEUtil;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Disposable disposableTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;


        bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();







        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.d("YWK",activity+"onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d("YWK",activity+"onActivityStarted");
                sActivity=activity;

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });





        try {

            if(disposableTimer == null) {
                Intent service = new Intent(this, BlutoothService.class);
                //  startService(service);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(service);
                    //且在service里再调用startForeground方法，不然就会出现ANR
                    // startForeground(1,new Notification());
                } else {
                    startService(service);
                }
            }


        }catch (Exception e){

          //  LogH.e(e.getMessage());
        }

    }

    public static Context getAppContext() {
        return mApp;
    }

    public static Resources getAppResources() {
        return mApp.getResources();
    }

    public static Activity getActivity(){
        return sActivity;
    }


    public static String getPackgeName(){
        return mApp.getPackageName();
    }



    /**
     *
     * @param type
     * @param value
     * @param isHand 手动提交
     */
    public static  void uploadDeviceData(DeviceEnum type, Map value, boolean isHand){

        if(BaseApp.User == null){
            AlertUtil.show("上传失败,用户为空");
            return;
        }
        if(StringUtil.isEmpty(BaseApp.User.getId()) ){
            AlertUtil.show("上传失败,用户为空");
            return;
        }

        String patientNo=BaseApp.User.getId();


        if(singleMolel){


            if(!isHand){

                //  Log.e("提交","===isHand==");

                return;

            }

        }





        //   LogH.e("uploadDeviceData","===========type====="+type.getType());

        // Log.e("文件", JSONUtil.toJson(value));

        try {

            String time =  value.get("time").toString();

            // let pram={type:0,createTime:time,patientNo:patientNo,oxy:oxy,oxy_runing:1,pulse:pulse,pressure_runing:1};
            Map<String,Object> params = new HashMap<String,Object>();

            params.put("patientNo", patientNo);
            params.put("type", "0");//按参数 上传
            params.put("createTime", time);
            switch (type){

                case Oxy:
                    String oxy = value.get("oxy").toString();
                    String pulse = value.get("heart").toString();

                    params.put("oxy", oxy);
                    params.put("oxy_runing", "1");
                    params.put("pulse", pulse);
                    params.put("pressure_runing", "1");
                    break;

                case Breath:
                    String breath = value.get("breath").toString();
                    params.put("breath", breath);
                    params.put("breath_runing", "1");
                    params.put("onbed", value.get("onbed").toString());   //t_status_value     离床状态

                    break;

                case BloodPressure:
                    String heightPressure = value.get("heightPressure").toString();
                    String lowPressure = value.get("lowPressure").toString();
                    params.put("heightPressure", heightPressure);
                    params.put("lowPressure", lowPressure);
                    params.put("pressure_runing", "1");


                    break;
                case Weight:
                    String weight = value.get("weight").toString();
                    params.put("weight", weight);
                    params.put("weight_runing", "1");
                    break;
                case BloodSugar:
                    String gluc = value.get("gluc").toString();
                    params.put("gluc", gluc);
                    params.put("gluc_runing", "1");
                    break;
                case Waist:
                    String waist = value.get("waist").toString();
                    params.put("waist", waist);
                    params.put("waist_runing", "1");
                    break;
                case Smart:



                    String smart_value = value.get("temp").toString();
                    String smart_heart = value.get("heart").toString();
                    String smart_sos = value.get("sos").toString();
                    String smart_bar = value.get("bar").toString();

                    params.put("smart_value", smart_value);
                    params.put("smart_heart", smart_heart);
                    params.put("smart_sos", smart_sos);
                    params.put("smart_bar", smart_bar);
                    params.put("smart_runing", "1");


                    break;

                case Temp:
                    String temp = value.get("temp").toString();
                    params.put("templature", temp);
                    params.put("templature_runing", "1");


                    break;
                case Infusion:
                    String infusion = value.get("infusion").toString();
                    String bar = value.get("bar").toString();
                    params.put("infusion_value", infusion);
                    params.put("infusion_bar", bar);
                    params.put("infusion_runing", "1");
                    break;
                case Uric:
                    String ua = value.get("ua").toString();
                    params.put("ua", ua);
                    params.put("ua_runing", "1");
                    break;
                case Cholesterol:
                    String cho = value.get("cho").toString();
                    params.put("cho", cho);
                    params.put("cho_runing", "1");
                    break;

            }

            // Log.e("文件", JSONUtil.toJson(params));


            HttpUtil.getinstance().post(HttpUtil.submitHealthCheck,params, new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {

                    try {


                        JSONObject map = response.body();
                        if(map==null){

                            AlertUtil.show("数据异常:-1");

                            return;
                        }

                        if (LinkedTreeMapUtil.CodeOk(map))  {

                            AlertUtil.show("上传成功");
                        }

                    } catch (Exception e) {
                        //   e.printStackTrace();
                       // LogH.e("submitHealthCheck",e.getMessage());
                        AlertUtil.show("数据异常:100");


                        //上传失败 缓存起来，下次继续上传



                    }

                }


                @Override
                public void onFailure(Call<JSONObject> call, Throwable throwable) {

                    //LogH.e("submitHealthCheck",throwable.toString());
                    AlertUtil.show("网络异常:-100");
                }
            });

        }catch (Exception e){

            // e.printStackTrace();

            //LogH.e("submitHealthCheck",e.getMessage());
        }


    }
}