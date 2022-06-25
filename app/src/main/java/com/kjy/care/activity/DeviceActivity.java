package com.kjy.care.activity;



import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;


import com.kjy.blue.blutooth.BluetoothDeviceInfo;
import com.kjy.blue.blutooth.BluetoothUtilEvent;
import com.kjy.blue.blutooth.DeviceEnum;
import com.kjy.care.R;
import com.kjy.care.adapter.BindDeviceAdapter;
import com.kjy.care.bean.DeviceInfo;
import com.kjy.care.service.MessageEvent;
import com.kjy.care.util.AppUtil;
import com.kjy.care.util.FastClickUtil;
import com.kjy.care.util.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DeviceActivity extends BaseActivity  implements View.OnClickListener {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        AppUtil.closeBar(this);



        initView();

        initEvent();

    }

    List<DeviceInfo> list_bind_device=new ArrayList<>();
    BindDeviceAdapter bindDeviceAdapter;


    List<DeviceInfo>  list_search_device=new ArrayList<>();
    BindDeviceAdapter searchDeviceAdapter;



    RelativeLayout  RelativeLayout_app;

    private ListView ListView_bind_device;

    private  ListView ListView_search_device;

    Button Button_cancel;

    RelativeLayout RelativeLayout_loading,RelativeLayout_search_list;


    int searchType=1;


    private boolean scanDeviceIng = false;//用户可以进行设备扫描发现




    private void initView(){
        RelativeLayout_app= findViewById(R.id.RelativeLayout_app);
        //界面旋转180度
        //RelativeLayout_app.setRotation(180);







        String macTemp =  BaseApp.getBLE().getStorageDevice(DeviceEnum.Temp);
        String macOxy=  BaseApp.getBLE().getStorageDevice(DeviceEnum.Oxy);
        String macBreath=  BaseApp.getBLE().getStorageDevice(DeviceEnum.Breath);
        String macBloodPressure=  BaseApp.getBLE().getStorageDevice(DeviceEnum.BloodPressure);
        String macBloodSugar =  BaseApp.getBLE().getStorageDevice(DeviceEnum.BloodSugar);
        String macInfusion=  BaseApp.getBLE().getStorageDevice(DeviceEnum.Infusion);

        String macWeight=  BaseApp.getBLE().getStorageDevice(DeviceEnum.Weight);

        String macWaist=  BaseApp.getBLE().getStorageDevice(DeviceEnum.Waist);

        String macSmart=  BaseApp.getBLE().getStorageDevice(DeviceEnum.Smart);

        String macThreeToOne=  BaseApp.getBLE().getStorageDevice(DeviceEnum.ThreeToOne);

        String macBloodPressure_BIOLAND=  BaseApp.getBLE().getStorageDevice(DeviceEnum.BloodPressure_BIOLAND);

        String macTemp_BIOLAND=  BaseApp.getBLE().getStorageDevice(DeviceEnum.Temp_BIOLAND);



        //,1体温 2脉搏、血氧 3呼吸 4空着 5血压 6体重 7血糖 8 腰围 9尿酸 10 胆固醇
        list_bind_device= new ArrayList<DeviceInfo>();
        list_bind_device.add(new DeviceInfo("体温","体温", DeviceEnum.Temp.getType(),macTemp));
        list_bind_device.add(new DeviceInfo("血氧","血氧",DeviceEnum.Oxy.getType(),macOxy));
        list_bind_device.add(new DeviceInfo("睡眠带","睡眠带",DeviceEnum.Breath.getType(),macBreath));
        list_bind_device.add(new DeviceInfo("血压","血压",DeviceEnum.BloodPressure.getType(),macBloodPressure));
        list_bind_device.add(new DeviceInfo("血糖","血糖",DeviceEnum.BloodSugar.getType(),macBloodSugar));

        list_bind_device.add(new DeviceInfo("体重","体重",DeviceEnum.Weight.getType(),macWeight));
        list_bind_device.add(new DeviceInfo("腰围","腰围",DeviceEnum.Waist.getType(),macWaist));
        list_bind_device.add(new DeviceInfo("手环","手环",DeviceEnum.Smart.getType(),macSmart));
        list_bind_device.add(new DeviceInfo("三合一血糖","三合一血糖",DeviceEnum.ThreeToOne.getType(),macThreeToOne));

        list_bind_device.add(new DeviceInfo("血压-bioland","血压-bioland",DeviceEnum.BloodPressure_BIOLAND.getType(),macBloodPressure_BIOLAND));
        list_bind_device.add(new DeviceInfo("体温-bioland","体温-bioland",DeviceEnum.Temp_BIOLAND.getType(),macTemp_BIOLAND));



        ListView_bind_device = findViewById(R.id.ListView_bind_device);


        bindDeviceAdapter=new BindDeviceAdapter(this);
        bindDeviceAdapter.setList(list_bind_device);
        ListView_bind_device.setAdapter(bindDeviceAdapter);

        ListView_bind_device.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                list_search_device.clear();
            //    checkPermissions();
               searchType =  list_bind_device.get(i).getType();
               scanDeviceIng = true;

             RelativeLayout_loading.setVisibility(View.VISIBLE);


            }
        });

        ListView_bind_device.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                searchType =  list_bind_device.get(i).getType();
                unBindDevice(searchType);
                return true;
            }

        });






        RelativeLayout_loading = findViewById(R.id.RelativeLayout_loading);
        RelativeLayout_loading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanDeviceIng = false;
                RelativeLayout_loading.setVisibility(View.GONE);
            }
        });
        RelativeLayout_search_list = findViewById(R.id.RelativeLayout_search_list);
        RelativeLayout_search_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanDeviceIng = false;
                RelativeLayout_search_list.setVisibility(View.GONE);
            }
        });

        Button_cancel = findViewById(R.id.Button_cancel);



        ListView_search_device =findViewById(R.id.ListView_search_device);
        searchDeviceAdapter=new BindDeviceAdapter(this,false);
        searchDeviceAdapter.setList(list_search_device);
        ListView_search_device.setAdapter(searchDeviceAdapter);

        ListView_search_device.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                /////////////////绑定设备//////////////////
                scanDeviceIng = false;
                // String mac= list_search_device.get(i).getMac();


                bindDevice(list_search_device.get(i).getDevice());



                RelativeLayout_search_list.setVisibility(View.GONE);


            }
        });

        Button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanDeviceIng = false;
                RelativeLayout_search_list.setVisibility(View.GONE);
            }
        });






    }


    public static void luncher(){

        Intent intent =new Intent();
        intent.setClass(BaseApp.getAppContext(), DeviceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getAppContext().startActivity(intent);
    }










    @Override
    public void onClick(View view) {
        if(FastClickUtil.isFastClick()){return;}
        switch (view.getId()){
            case R.id.LinearLayout_back:

                finish();
                break;
        }



       // AlertUtil.show("按键提示");




    }






    @Override
    protected void onDestroy() {
        super.onDestroy();




        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent obj){

       // Log.e("订阅",obj.getName()+"===");
        switch (obj) {
            case Com_data:

                //TextView_debug.append(obj.getData().toString()+"\r\n");



                break;

        }



      }












    private void unBindDevice(int type){

        bindDevice(null);

    }
    /**
     * 绑定mac
     * @param device
     */
    private void bindDevice(BluetoothDevice device){

        String mac ="";
        for(int i=0;i<list_bind_device.size();i++) {
            DeviceInfo info = list_bind_device.get(i);
            if(info.getType() ==searchType) {
                info.setDevice(device);

                if(device!=null){
                    mac = device.getAddress();

                    BaseApp.getBLE().connect(device);
                }



                info.setMac(mac);
                list_bind_device.set(i, info);
                break;
            }
        }


        bindDeviceAdapter.notifyDataSetChanged();
        BaseApp.getBLE().reLoadDevice(searchType,mac);


    }



    private void  initEvent(){


        BaseApp.getBLE().setOnlistening(new BluetoothUtilEvent(){
            @Override
            public void SleepData(LinkedList<float[]> heart, LinkedList<float[]> breath) {

            }

            @Override
            public void Connect(int type, String name, String mac) {
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      bindDeviceAdapter.notifyDataSetChanged();
                  }
              });
            }

            @Override
            public void DisConnect(int type, String name, String mac) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bindDeviceAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void ReciveData(int type, byte[] data0, String data1, String uuid, String mac, String... value) {

            }

            @Override
            public void FoundDevice(BluetoothDeviceInfo device) {

             //   LogH.e("list","============="+device.getDevice().getAddress());

                if(!scanDeviceIng){
                    return;
                }



                String name="Other";
                DeviceEnum o=DeviceEnum.getEnum(searchType);
                switch (o){

                    case Oxy:name="POD";break;
                    case Breath:name="J1657,J-Style Sleep";break;
                    case BloodPressure:name="BPM-188";break;
                    case Weight:name="SWAN";break;
                    case Waist:name="HX_TNS_";break;
                    case Infusion:name="KF_";break;
                    case BloodSugar:name="Bioland-BGM";break;
                    case Smart:name="Heart Rate Sensor";break;
                    case Temp:name="Thermometer,Bluetooth BP";break;
                    case ThreeToOne:name="BeneCheck";break;
                    case BloodPressure_BIOLAND:name="Bioland-BPM";break;

                    case Temp_BIOLAND:name="Bioland-IT";break;

                }

                boolean searchFind = false;
                if(!StringUtil.isEmpty(device.getDevice().getName())){
                    String[] names = name.split(",");//多种设备名称匹配
                    for(int i=0;i<names.length;i++){
                        if(device.getDevice().getName().toLowerCase().startsWith(names[i].toLowerCase())){
                            searchFind = true;

                        }
                    }

                }

                if(!searchFind){return;}


                boolean find =false;
                for(int i=0,k=list_search_device.size();i<k;i++){
                    if(list_search_device.get(i).getMac().equals(device.getDevice().getAddress())){
                        find = true;
                        list_search_device.get(i).setRssi(device.getRssi());
                        break;

                    }

                }


                if(!find) {
                    DeviceInfo item = new DeviceInfo();
                    item.setId(device.getDevice().getAddress());
                    item.setMac(device.getDevice().getAddress());
                    item.setName(device.getDevice().getName());
                    item.setType(searchType);
                    item.setRssi(device.getRssi());
                    item.setDevice(device.getDevice());

                    list_search_device.add(item);
                }



                RelativeLayout_loading.setVisibility(View.GONE);
                RelativeLayout_search_list.setVisibility(View.VISIBLE);
                searchDeviceAdapter.setList(list_search_device);
                searchDeviceAdapter.notifyDataSetChanged();








                //

            }




            @Override
            public void OnError(int typecode, String msg) {

            }

            @Override
            public void OnStopScan(BluetoothDeviceInfo deviceinfo) {

            }

            @Override
            public void Close() {

            }

            @Override
            public void NotifyReady(int type, String name, String mac, String uuid) {

            }
        } );


    }














}