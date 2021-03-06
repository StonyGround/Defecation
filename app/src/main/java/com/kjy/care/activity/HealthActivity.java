package com.kjy.care.activity;



import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import com.kjy.blue.blutooth.BLEDevice;
import com.kjy.blue.blutooth.BluetoothDeviceInfo;
import com.kjy.blue.blutooth.BluetoothUtilEvent;
import com.kjy.blue.blutooth.DeviceDataConfig;
import com.kjy.blue.blutooth.DeviceEnum;
import com.kjy.care.R;
import com.kjy.care.adapter.DeviceAdapter;
import com.kjy.care.adapter.OnItemClickListener;
import com.kjy.care.api.HttpUtil;
import com.kjy.care.bean.DeviceInfo;

import com.kjy.care.service.MessageEvent;

import com.kjy.care.util.AlertUtil;
import com.kjy.care.util.AppUtil;
import com.kjy.care.util.DateUtil;
import com.kjy.care.util.FastClickUtil;
import com.kjy.care.util.LinkedTreeMapUtil;
import com.kjy.care.util.SPUtil;
import com.kjy.care.util.StringUtil;
import com.kjy.care.widget.DatePickerPopupWindow;
import com.kjy.care.widget.DateTimeUtil;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kjy.care.activity.BaseApp.getActivity;
import static com.kjy.care.activity.BaseApp.singleMolel;

public class HealthActivity extends BaseActivity  implements View.OnClickListener {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        AppUtil.closeBar(this);



        initView();



    }



    RelativeLayout  RelativeLayout_app,RelativeLayout_input_bg,RelativeLayout_loading;
    private TextView act_healthytest_datepicker_tv,TextView_Connect_Value,TextView_Value,TextView_Report;


    LinearLayout LinearLayout_1,LinearLayout_2,LinearLayout_3;
    TextView TextView_1,TextView_2,TextView_3,TextView_type;

    EditText EditText_1,EditText_2,EditText_3;
    Button Button_yes;

    LineChartView lineChartView_chart;

    private LinearLayout LinearLayout_datepicker;

    private View Line_color,Line2_color;
    private TextView Line_name,Line2_name;
    private TextView TextView_Value_Ref;


    DeviceAdapter myAdapter;
    LinearLayoutManager linearLayoutManager;


    private void initView(){
        RelativeLayout_app= findViewById(R.id.RelativeLayout_app);
        //????????????180???
       // RelativeLayout_app.setRotation(180);






        lineChartView_chart = (LineChartView)findViewById(R.id.LineChartView_chart);
        RelativeLayout_loading= findViewById(R.id.RelativeLayout_loading);

        LinearLayout_datepicker = findViewById(R.id.LinearLayout_datepicker);
        act_healthytest_datepicker_tv= findViewById(R.id.act_healthytest_datepicker_tv);

        Line_color= findViewById(R.id.Line_color);
        Line2_color= findViewById(R.id.Line2_color);
        Line_name= findViewById(R.id.Line_name);
        Line2_name= findViewById(R.id.Line2_name);




        act_healthytest_datepicker_tv= findViewById(R.id.act_healthytest_datepicker_tv);

        TextView_Connect_Value= findViewById(R.id.TextView_Connect_Value);
        TextView_Value= findViewById(R.id.TextView_Value);
        TextView_Report= findViewById(R.id.TextView_Report);
        TextView_Value_Ref= findViewById(R.id.TextView_Value_Ref);

        RelativeLayout_input_bg= findViewById(R.id.RelativeLayout_input_bg);
        RelativeLayout_input_bg.setVisibility(View.GONE);


        LinearLayout_1  =  findViewById(R.id.LinearLayout_1);
        LinearLayout_2  =  findViewById(R.id.LinearLayout_2);
        LinearLayout_3  =  findViewById(R.id.LinearLayout_3);
        TextView_1  =  findViewById(R.id.TextView_1);
        TextView_2  =  findViewById(R.id.TextView_2);
        TextView_3  =  findViewById(R.id.TextView_3);
        EditText_1  =  findViewById(R.id.EditText_1);
        EditText_2  =  findViewById(R.id.EditText_2);
        EditText_3  =  findViewById(R.id.EditText_3);
        TextView_type =  findViewById(R.id.TextView_type);
        Button_yes =  findViewById(R.id.Button_yes);

        EditText_1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                cleanTimer();

                Button_yes.setText("??????");
            }
        });







        //,1?????? 2??????????????? 3?????? 4?????? 5?????? 6?????? 7?????? 8 ?????? 9?????? 10 ?????????
        List<DeviceInfo> listItem = new ArrayList<DeviceInfo>();
        listItem.add(new DeviceInfo("??????","??????", DeviceEnum.Temp.getType()));
        listItem.add(new DeviceInfo("??????","??????",DeviceEnum.Oxy.getType()));
        listItem.add(new DeviceInfo("?????????","?????????",DeviceEnum.Breath.getType()));
        listItem.add(new DeviceInfo("??????","??????",DeviceEnum.BloodPressure.getType()));
        listItem.add(new DeviceInfo("??????","??????",DeviceEnum.BloodSugar.getType()));

        listItem.add(new DeviceInfo("??????","??????",DeviceEnum.Weight.getType()));
        listItem.add(new DeviceInfo("??????","??????",DeviceEnum.Waist.getType()));
        listItem.add(new DeviceInfo("??????","??????",DeviceEnum.Smart.getType()));
        listItem.add(new DeviceInfo("??????","??????",DeviceEnum.Uric.getType()));
        listItem.add(new DeviceInfo("????????????","????????????",DeviceEnum.Cholesterol.getType()));




       // listItem.add(new DeviceInfo("??????","??????",DeviceEnum.Infusion.getType()));





        //  ArrayAdapter
        //????????????simpleAdapter
        myAdapter = new DeviceAdapter(this);
        myAdapter.setList(listItem);

        RecyclerView listView = (RecyclerView) findViewById(R.id.ListView_device);
         linearLayoutManager= new LinearLayoutManager(this, GridLayoutManager.HORIZONTAL,false);
        listView.setLayoutManager(linearLayoutManager);

        listView.setAdapter(myAdapter);

        myAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


                int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
                int lastItem = linearLayoutManager.findLastVisibleItemPosition();
                if (position == firstItem||position==firstItem+1) {
                    listView.smoothScrollBy(0 - (int) getActivity().getResources().getDimension(R.dimen.x144), 0);
                } else if (position==lastItem||position==lastItem-1) {
                    listView.smoothScrollBy((int) getActivity().getResources().getDimension(R.dimen.x144), 0);
                }


                myAdapter.setIndex(position);

                if( listItem.get(position).getType()!=selectType) {
                    //////////////////?????????????????????????????????///////////////////////////
                    switchDevice(listItem.get(position).getType(),false);

                }
                //   LogH.e("index","=================>"+i);

            }
        });



          myAdapter.setIndex(0);
          switchDevice(listItem.get(0).getType(),false);



    }


    public static void luncher(){

        Intent intent =new Intent();
        intent.setClass(BaseApp.getAppContext(), HealthActivity.class);
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
            case R.id.TextView_input_btn:
                showEditView();
                break;



            case  R.id.Button_cancel:
                cleanTimer();
                RelativeLayout_input_bg.setVisibility(View.GONE);


                break;
            case R.id.LinearLayout_datepicker:
                showDatePicker(view);
                break;
            case  R.id.Button_yes:
                cleanTimer();

                subForm();
                break;
            case R.id.RelativeLayout_input_bg:

                AppUtil.hideSystemKeyBoard(view);

                break;
        }



        // AlertUtil.show("????????????");




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

        // Log.e("??????",obj.getName()+"===");
        switch (obj) {
            case Com_data:

                //TextView_debug.append(obj.getData().toString()+"\r\n");



                break;

        }



    }





    final int DEVICE_CONNECT =4;
    final int DEVICE_DISCONNECT =5;
    final int DEVICE_REC_DATA =6;
    final int DEVICE_LIST =7;
    final int DEVICE_NOTIFY_REDEAY =8;

    private void initBleCallBack() {

        BaseApp.getBLE().setOnlistening(new BluetoothUtilEvent() {
            @Override
            public void SleepData(LinkedList<float[]> heart, LinkedList<float[]> breath) {
            /*   lineChartView_chart.post(new Runnable() {
                   @Override
                   public void run() {
                       lineChartView_chart.setLineChartData(ChartDataUtil.getActivityBreathLineChartData(getActivity(), heart,  getResources().getColor(R.color.day_heart_lineColor)));

                   }
               });*/
            }

            @Override
            public void Connect(int i,String name, String mac) {
                //   LogH.e("Connect","MainActivity->Connect=>"+name+"=="+mac);

                Message msg=new Message();
                msg.what = DEVICE_CONNECT;
                msg.arg1 = i;//????????????
                //  mainHandler.sendMessage(msg);


            }

            @Override
            public void DisConnect(int i,String name, String mac) {
                //  LogH.e("DisConnect","DisConnect=>"+name+"=="+mac);

                Message msg=new Message();
                msg.what = DEVICE_DISCONNECT;
                msg.arg1 = i;//????????????
                mainHandler.sendMessage(msg);
            }

            @Override
            public void ReciveData(int i,byte[] data0, String data1, String uuid, String mac,String[] value) {
                //LogH.e("ReciveData","MAIN-----ReciveData=>"+data1);

                Message msg=new Message();
                msg.what = DEVICE_REC_DATA;
                msg.arg1 = i;//????????????
                msg.obj= value;
                mainHandler.sendMessage(msg);
            }

            @Override
            public void FoundDevice(BluetoothDeviceInfo device) {
                //     LogH.e("MainActivity-ListDevice","ListDevice=>"+list.size());

                //      Message msg=new Message();
                //    msg.what = DEVICE_LIST;
                //   msg.obj= list;
                // mainHandler.sendMessage(msg);
            }

            @Override
            public void OnError(int typecode, String msg) {
                //   LogH.e("OnError","OnError=>"+msg);
            }

            @Override
            public void OnStopScan(BluetoothDeviceInfo deviceinfo) {
                // LogH.e("OnStopScan","OnStopScan=>");
            }

            @Override
            public void Close() {

                //LogH.e("Close","Close=>");
            }

            @Override
            public void NotifyReady(int i,String name, String mac, String uuid) {
                //  LogH.e("NotifyReady","NotifyReady=>"+name+"=="+mac+"=="+uuid);
                Message msg=new Message();
                msg.what = DEVICE_NOTIFY_REDEAY;
                msg.obj= uuid;
                msg.arg1 = i;
                mainHandler.sendMessage(msg);


            }
        });
    }


    final int VIEW_DATAINFO=1;//????????????????????????
    final int VIEW_DATE_SELECT=3;//???????????????


    public Handler mainHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case VIEW_DATAINFO:

                    JSONArray temp =  (JSONArray)(msg.obj);

                    updateDataInfoView(temp,msg.arg1);



                    break;



                case VIEW_DATE_SELECT:
                    //???????????????????????????
                    String date =  (String)(msg.obj);
                    getDataList(date);
                    break;

                case DEVICE_CONNECT:
                    onDeviceConnect(msg.arg1);

                    break;
                case DEVICE_DISCONNECT:
                    onDeviceDisConnect(msg.arg1);
                    break;
                case DEVICE_REC_DATA:
                    onDeviceRecData(msg.arg1,(String[])msg.obj);
                    break;
                case DEVICE_LIST:

                    break;
                case DEVICE_NOTIFY_REDEAY:
                    onDeviceNotifyReady(msg.arg1,msg.obj.toString());
                    break;


            }


        }
    };



    private  void onDeviceConnect(int type){




        ///////////////?????????????????????????????????/////////////////////
        if(selectType == type){

            TextView_Connect_Value.setText("????????????????????????");
            TextView_Value.setText("???????????????");
            TextView_Report.setText("???????????????");
        }

        /////////////??????????????????????????????////////////////////
        if(type== DeviceEnum.Temp_BIOLAND.getType()) {
            if (selectType == DeviceEnum.Temp.getType() ) {

                TextView_Connect_Value.setText("????????????????????????");
                TextView_Value.setText("???????????????");
                TextView_Report.setText("???????????????");
            }
        }


        /////////////??????????????????????????????////////////////////
        if(type== DeviceEnum.BloodPressure_BIOLAND.getType()) {
            if (selectType == DeviceEnum.BloodPressure.getType() ) {

                TextView_Connect_Value.setText("????????????????????????");
                TextView_Value.setText("???????????????");
                TextView_Report.setText("???????????????");
            }
        }



        ////////////????????????////??????////////////////////

        if(type== DeviceEnum.ThreeToOne.getType()) {
            if (selectType == DeviceEnum.BloodSugar.getType()     || selectType == DeviceEnum.Cholesterol.getType() || selectType == DeviceEnum.Uric.getType()) {

                TextView_Connect_Value.setText("????????????????????????");
                TextView_Value.setText("???????????????");
                TextView_Report.setText("???????????????");
            }
        }





    }

    private  void onDeviceDisConnect(int type){


        if(selectType == type){
            TextView_Connect_Value.setText("????????????????????????");

        }

        /////////////??????????????????????????????////////////////////
        if(type== DeviceEnum.Temp_BIOLAND.getType()) {
            if (selectType == DeviceEnum.Temp.getType() ) {

                TextView_Connect_Value.setText("????????????????????????");
            }
        }


        /////////////??????????????????????????????////////////////////
        if(type== DeviceEnum.BloodPressure_BIOLAND.getType()) {
            if (selectType == DeviceEnum.BloodPressure.getType() ) {

                TextView_Connect_Value.setText("????????????????????????");
            }
        }


        /////////////???????????????//////////////////

        if(type == DeviceEnum.ThreeToOne.getType()) {
            if (selectType == DeviceEnum.BloodSugar.getType() || selectType == DeviceEnum.Uric.getType() || selectType == DeviceEnum.Cholesterol.getType()) {
                TextView_Connect_Value.setText("????????????????????????");

            }
        }





    }


    private  void onDeviceRecData(int type,String[] data){
        DeviceEnum info = DeviceEnum.getEnum(type);

        //////////////????????????????????????/////////////
        if(info ==DeviceEnum.Infusion){

            if(data.length>0 && data[0].equals("0")){
                //////////////data[0].equals("0")//////??????????????????????????????////////////////////

                //LogH.e("onDeviceRecData","???????????????=>????????????"+data[1]);
                if(selectType == info.getType()){


                    loadSessionDeviceInfo(info,false);
                }



                return;

            }

        }
        //////////////????????????????????????/////////////
        if(info ==DeviceEnum.Smart){
            //  LogH.e("onDeviceRecData","???????????????=>"+data[2]+"==="+data[3]);
            if(data.length==4 && data[2].equals("0")){
                //////////////data[2].equals("0")//////SOS??????????????????////////////////////

                // LogH.e("onDeviceRecData","???????????????=>"+data[3]);
                if(selectType == info.getType()){


                    loadSessionDeviceInfo(info,false);
                }



                return;

            }

        }
        if(BaseApp.singleMolel && !BaseApp.isStop) {
            //??????????????????????????????????????????

            switch (info) {
                case Temp:
                case Temp_BIOLAND:
                case Oxy:
                case BloodPressure:
                case BloodSugar:
                case Weight:
                case Waist:
                case Cholesterol:
                case Uric:
                case ThreeToOne:
                    RelativeLayout_input_bg.setVisibility(View.VISIBLE);


                    cleanTimer();


                    if (inputTimerTask == null) {

                        inputTimerTask = new TimerTask() {
                            @Override
                            public void run() {
                                inputHanlder.sendEmptyMessage(0);
                            }
                        };
                    }
                    inputTimer = new Timer();
                    inputTimer.schedule(inputTimerTask, 0, 1000);
                    showTime = new Date();
                    break;

            }
        }






        //??????????????????,???????????????????????????
        switchDevice(type,true);

        //?????????????????????
        List<DeviceInfo> tempList =  myAdapter.getList();

        for(int i=0;i<tempList.size();i++){

            if(type == tempList.get(i).getType()){

                myAdapter.setIndex(i);

            }


        }



    }


    private  void onDeviceNotifyReady(int i,String uuid){

    }

    private Date showTime =new Date();
    private int maxWait=10;
    private TimerTask inputTimerTask=null;
    private Timer inputTimer=null;
    private  Handler inputHanlder=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if(RelativeLayout_input_bg.getVisibility() != View.VISIBLE) {    cleanTimer();   return;}

            Date now =new Date();

            long   n =   (now.getTime() - showTime.getTime())/1000;

            long time = maxWait -n ;
            if(time<=0){
                Button_yes.setText("??????");
                //RelativeLayout_input_bg.setVisibility(View.GONE);
                ///////?????????????????????///////

                cleanTimer();
                subForm();

                return;
            }

            Button_yes.setText("??????("+time+")");  //????????????







        }
    };

    /**
     * ????????????
     */
    private void cleanTimer(){
        if(inputTimerTask!=null){
            inputTimerTask.cancel();
            inputTimerTask=null;
            inputTimer.purge();
            inputTimer.cancel();
            inputTimer = null;

        }
    }

    private String select_day=DateTimeUtil.getNowDate();
    private String befor_select_day=DateTimeUtil.GetPreDate(DateTimeUtil.getDate(select_day), -30);

    //,1?????? 2??????????????? 3?????? 4?????? 5?????? 6?????? 7?????? 8 ?????? 9?????? 10 ????????? 20??????,21??????
    private int selectType = DeviceEnum.Temp.getType() ;

    /**
     * ???????????????????????????????????? ?????? ???????????????????????????????????????
     * @param info
     * @param playMusic
     */
    private void loadSessionDeviceInfo(DeviceEnum info,boolean playMusic){
        //,1?????? 2??????????????? 3?????? 4?????? 5?????? 6?????? 7?????? 8 ?????? 9?????? 10 ???????????? 20 ??????
        String value = SPUtil.get(BaseApp.getAppContext(),"data_"+info.getType());
        String connect ="?????????";

        String mac  =  SPUtil.get(this,"mac_"+info.getType());

        if(StringUtil.isEmpty(mac)){
            connect="?????????";
        }

        String macThreeToOne  =  SPUtil.get(this,"mac_"+DeviceEnum.ThreeToOne.getType());
        if(StringUtil.isEmpty(macThreeToOne)){
            if(info == DeviceEnum.BloodSugar){
                String macBloodSugar  =  SPUtil.get(this,"mac_"+DeviceEnum.BloodSugar.getType());
                if(!StringUtil.isEmpty(macBloodSugar)){
                    connect="?????????";
                }
            }

        }else{

            if(info == DeviceEnum.Cholesterol || info == DeviceEnum.Uric){
                connect="?????????";
            }


        }


        ///////////////?????????????????????////////////////???????????????????????????/////////////////////////////
        String macTemp_BIOLAND =  SPUtil.get(this,"mac_"+DeviceEnum.Temp_BIOLAND.getType());
        if(info == DeviceEnum.Temp){

            if(StringUtil.isEmpty(macTemp_BIOLAND)){
                String macBloodPressure  =  SPUtil.get(this,"mac_"+DeviceEnum.Temp.getType());
                if(!StringUtil.isEmpty(macBloodPressure)){
                    connect="?????????";
                }
            }else{

                connect = "?????????";

            }
        }




        ///////////////?????????????????????////////////////???????????????????????????/////////////////////////////
        String macBloodPressure_BIOLAND =  SPUtil.get(this,"mac_"+DeviceEnum.BloodPressure_BIOLAND.getType());
        if(info == DeviceEnum.BloodPressure){

            if(StringUtil.isEmpty(macBloodPressure_BIOLAND)){
                String macBloodPressure  =  SPUtil.get(this,"mac_"+DeviceEnum.BloodPressure.getType());
                if(!StringUtil.isEmpty(macBloodPressure)){
                    connect="?????????";
                }
            }else{

                connect = "?????????";

            }
        }





        BLEDevice dev =BaseApp.getBLE().getDeviceByType(info.getType());
        if(dev!=null && dev.isConnect()){ connect ="?????????"; }


        if(!StringUtil.isEmpty(macThreeToOne)){
            if(info == DeviceEnum.BloodSugar   || info == DeviceEnum.Cholesterol   || info == DeviceEnum.Uric){
                dev =BaseApp.getBLE().getDeviceByType(DeviceEnum.ThreeToOne.getType());
                if(dev!=null && dev.isConnect()){ connect ="?????????"; }
            }

        }


        if(!StringUtil.isEmpty(macBloodPressure_BIOLAND)){
            if(info == DeviceEnum.BloodPressure){
                dev =BaseApp.getBLE().getDeviceByType(DeviceEnum.BloodPressure_BIOLAND.getType());
                if(dev!=null && dev.isConnect()){ connect ="?????????"; }
            }

        }

        if(!StringUtil.isEmpty(macTemp_BIOLAND)){
            if(info == DeviceEnum.Temp){
                dev =BaseApp.getBLE().getDeviceByType(DeviceEnum.Temp_BIOLAND.getType());
                if(dev!=null && dev.isConnect()){ connect ="?????????"; }
            }

        }





        TextView_Connect_Value.setText("???????????????"+connect);

        TextView_Value.setText("???????????????");
        TextView_Report.setText("???????????????");

        int blue_light = this.getResources().getColor(R.color.blue_light);
        int black_666 = this.getResources().getColor(R.color.black_666);
        int black_999 = this.getResources().getColor(R.color.black_999);
        int black_aaa= this.getResources().getColor(R.color.black_aaa);

        String valueMsg ="";
        String reportMsg ="";
        String time ="";



        switch (info){

            case Temp:
                //??????
                TextView_Value_Ref.setText("???????????????"+ DeviceDataConfig.Temp_Ref);
                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String temp = item.get("temp").toString();

                    String report= item.get("report").toString();
                    String status=item.get("status").toString();

                    valueMsg=("??????:"+temp+"???,??????:"+status);
                    reportMsg = report;


                }


                break;
            case Oxy:
                //??????
                TextView_Value_Ref.setText("???????????????"+DeviceDataConfig.Oxy_Ref);

                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String oxy = item.get("oxy").toString();
                    String heart= item.get("heart").toString();
                    String report= item.get("report").toString();


                    valueMsg=("??????:"+oxy+"%,??????:"+heart+"???/??????");
                    reportMsg = report;


                }

                break;
            case Breath:
                //?????????
                TextView_Value_Ref.setText("???????????????"+DeviceDataConfig.Breath_Ref);

                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String breath = item.get("breath").toString();
                    String heart= item.get("heart").toString();
                    int move= Integer.parseInt(item.get("move").toString());
                    int onbed= Integer.parseInt(item.get("onbed").toString());

                    String report= item.get("report").toString();
                    String bed ="??????";
                    if(onbed==1){

                        bed= "??????";
                    }
                    valueMsg=("??????:"+breath+"???/??????,?????????"+heart+"???/??????,"+bed);
                    reportMsg = report;



                }
                // ChartDataUtil.initChartView(this.lineChartView_chart, 200.0f, 0.0f, 0.0f, 720.0f);
                break;

            case BloodPressure:
                //??????
                TextView_Value_Ref.setText("???????????????"+DeviceDataConfig.BloodPressure_Ref);

                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String heightPressure = item.get("heightPressure").toString();
                    String lowPressure= item.get("lowPressure").toString();
                    String heart= item.get("heart").toString();
                    String report= item.get("report").toString();
                    String status= item.get("status").toString();

                    valueMsg=("??????:"+heightPressure+"/"+lowPressure +"mmHg,??????:"+heart+"???/??????");
                    reportMsg = report;

                }



                break;
            case BloodSugar:
                //??????
                TextView_Value_Ref.setText("???????????????"+DeviceDataConfig.BloodSugar_Ref);
                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String gluc = item.get("gluc").toString();


                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("??????:"+gluc+"mmol/L,"+status);
                    reportMsg = report;


                }


                break;
            case Infusion:
                //??????

                TextView_Value_Ref.setText("???????????????");

                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String infusion = item.get("infusion").toString();
                    int bar = Integer.parseInt(item.get("bar").toString());
                    String report= item.get("report").toString();
                    String status= item.get("status").toString();
                    String barLess="";
                    if(bar<=16){
                        barLess=",??????????????????,???????????????";
                    }


                    valueMsg=(status+",??????:"+(bar)+"%");
                    reportMsg = report+barLess;


                }

                break;
            case Weight:
                //??????
                TextView_Value_Ref.setText("???????????????"+DeviceDataConfig.Weight_Ref);

                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String weight = item.get("weight").toString();
                    String bmi = item.get("bmi").toString();

                    String report= item.get("report").toString();


                    valueMsg=("??????:"+weight+" KG???BMI:"+bmi);
                    reportMsg = report;


                }

                break;
            case Waist:
                //??????

                TextView_Value_Ref.setText("???????????????"+DeviceDataConfig.Waist_Ref);

                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String waist = item.get("waist").toString();


                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("??????:"+waist+"CM,"+status);
                    reportMsg = report;


                }


                break;
            case Smart:
                //??????

                TextView_Value_Ref.setText("???????????????");

                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String heart = item.get("heart").toString();
                    String temp = item.get("temp").toString();
                    String sos = item.get("sos").toString();
                    String bar = item.get("bar").toString();


                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("??????:"+heart+"???/??????,??????:"+temp+"???,SOS:"+(sos.equals("0")?"???":"???")+" ,??????:"+bar+"%");
                    reportMsg = report;


                }


                break;
            case Uric:
                //??????
                TextView_Value_Ref.setText("???????????????"+DeviceDataConfig.Uric_Ref);
                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String ua = item.get("ua").toString();


                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("??????:"+ua+"mmol/L,"+status);
                    reportMsg = report;


                }


                break;

            case Cholesterol:
                //????????????
                TextView_Value_Ref.setText("???????????????"+DeviceDataConfig.Cholesterol_Ref);
                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String cho = item.get("cho").toString();


                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("????????????:"+cho+"mmol/L,"+status);
                    reportMsg = report;


                }


                break;
        }

        //playMusic  ????????????
        if(playMusic&&!StringUtil.isEmpty(reportMsg)){
          //  TextView_Report.postDelayed(new VoiceRunnable(valueMsg),1000);

            if(singleMolel && !BaseApp.isStop) {
                //??????????????????????????????????????????
                Map item = new Gson().fromJson(value, Map.class);
                showInputView(info, item);
            }

        }



        if(time.length()>0){

            long i = new Date().getTime() - DateTimeUtil.getStringToDate(time).getTime();

            if(i<1000*60){
                //????????????
                TextView_Value.setText("???????????????"+valueMsg);
                TextView_Value.setTextColor(blue_light);
                TextView_Report.setText("???????????????"+reportMsg);

            }else{

                //???????????????
                if(time.substring(0,10).equals(DateTimeUtil.getNowDate()) ) {

                    TextView_Value.setText("???????????????"+valueMsg + "(" + time + ")");
                    TextView_Value.setTextColor(black_aaa);
                    TextView_Report.setText("???????????????"+reportMsg);
                }
            }
        }

    }





    /**
     * ???????????? ?????? 1????????? ??? ????????????
     * @param time 2020-10-15
     */
    private void getDataList(String time){
        Date date = DateTimeUtil.getDate(time);
        String showDay =  time+" "+DateTimeUtil.getWeekByDate(date) +" "+ DateTimeUtil.getDay(date);
        String fromDay =DateTimeUtil.GetPreDate(date, -30);
        act_healthytest_datepicker_tv.setText(showDay);

        select_day = time;
        befor_select_day=fromDay;
        getDataInfo();
    }


    //,1?????? 2??????????????? 3?????? 4?????? 5?????? 6?????? 7?????? 8 ?????? 9?????? 10 ?????????
    /**
     * ????????????????????????
     */
    private void getDataInfo(){
        if(BaseApp.User == null){
            AlertUtil.show("????????????,????????????");
            return;
        }
        if(StringUtil.isEmpty(BaseApp.User.getId()) ){
            AlertUtil.show("????????????,????????????");
            return;
        }

        String patientNo=BaseApp.User.getId();







        int type=selectType;

        if(selectType == 20 ){
            Message msg=new Message();
            msg.what = VIEW_DATAINFO;
            msg.arg1 = type;//????????????
            msg.obj =new JSONArray();
            mainHandler.sendMessage(msg);
            return;
        }




        try {
            RelativeLayout_loading.setVisibility(View.VISIBLE);
            Map<String,Object> params = new HashMap<String,Object>();

            params.put("patientNo", patientNo);
            params.put("type", type);
            params.put("beginTime", befor_select_day);
            params.put("endTime", select_day);

            //   LogH.e("getHealthCheck", JSONUtil.toJson(params));

            HttpUtil.getinstance().post(HttpUtil.getHealthCheck,params, new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                     Log.e("getHealthCheck",response.toString());
                    RelativeLayout_loading.setVisibility(View.GONE);
                    if(type != selectType){return;}

                    try {


                        JSONObject map = response.body();
                        if(map==null){

                            AlertUtil.show("????????????:-1");
                            cleanErrorDataView(type);


                            return;
                        }
                        //    LogH.e("??????",map.toString());
                        if (LinkedTreeMapUtil.CodeOk(map)) {
                            JSONArray temp =map.getJSONArray("body");


                            Message msg=new Message();
                            msg.what = VIEW_DATAINFO;
                            msg.arg1 = type;//????????????
                            msg.obj =temp;
                            mainHandler.sendMessage(msg);
                        }else{
                            AlertUtil.show("????????????:101");
                            cleanErrorDataView(type);
                        }

                    } catch (Exception e) {
                        //   e.printStackTrace();
                        //   LogH.e("getHealthCheck",e.getMessage());
                        AlertUtil.show("????????????:100"+HttpUtil.getHealthCheck);

                        cleanErrorDataView(type);

                    }

                }


                @Override
                public void onFailure(Call<JSONObject> call, Throwable throwable) {
                    RelativeLayout_loading.setVisibility(View.GONE);
                    if(type != selectType){return;}
                    throwable.printStackTrace();
                    //  LogH.e("getHealthCheck",throwable.toString()+HttpUtil.getHealthCheck);
                    AlertUtil.show("????????????:-100");
                }
            });

        }catch (Exception e){
            RelativeLayout_loading.setVisibility(View.GONE);
            if(type != selectType){return;}
            // e.printStackTrace();

            //  LogH.e("getHealthCheck",e.getMessage());
        }


    }


    /**
     * ????????????????????????
     * @param type
     */
    private void cleanErrorDataView(int type){


        JSONArray temp =new JSONArray();

        Message msg=new Message();
        msg.what = VIEW_DATAINFO;
        msg.arg1 = type;//????????????
        msg.obj =temp;
        mainHandler.sendMessage(msg);

    }


    /**
     * ??????????????????
     * @param view
     */
    private void showDatePicker(View view) {


        DatePickerPopupWindow.show(this, view,select_day, new DatePickerPopupWindow.OnSelectListener() {
            @Override
            public void onSelect(int num, String dateStr) {
                //  setPickerTime(num, dateStr);
                //   LogH.e("onSelect","DatePickerPopupWindow=>"+dateStr+"===="+num);

                Message msg=new Message();
                msg.what = VIEW_DATE_SELECT;

                msg.obj =dateStr;
                mainHandler.sendMessage(msg);



            }
        });



    }






    /**
     * ??????????????????
     * @param list
     * @param type
     */
    private void updateDataInfoView(JSONArray list,int type){

        JSONArray data =new JSONArray();

        try {
            if (list != null && list.length() > 0) {

                for (int i = 0; i <list.length(); i++) {
                    data.put(list.getJSONObject(i));
                }

            }
        }catch (Exception e){

        }



        mPointValues = new ArrayList<PointValue>();
        mPointValues2 = new ArrayList<PointValue>();
        mAxisXValues = new ArrayList<AxisValue>();
        mPointColors = new ArrayList<Integer>();
        mPointColors2= new ArrayList<Integer>();

        ///   mPointValues.clear();   ?????????????????? ????????????


        //  String now = DateUtil.getStringDate();

        // String day=obj.getInHospitalTime().length()>10?DateUtil.getTwoDay(now,obj.getInHospitalTime())+"???":"";
        // String inHosTime=obj.getInHospitalTime().length()>10?obj.getInHospitalTime().substring(0,10).replaceAll("-","/"):obj.getInHospitalTime();
        //,1?????? 2??????????????? 3?????? 4?????? 5?????? 6?????? 7?????? 8 ?????? 9?????? 10 ?????????
        DeviceEnum info = DeviceEnum.getEnum(type);

        switch (info){

            case Temp:
                //1??????
                updateTempView(data);
                break;
            case Oxy:
                //??????
                updateOxyView(data);
                break;
            case Breath:
                //?????????
                //   upateBreathView(data);
                break;
            case BloodPressure:
                //??????
                updateXyView(data);
                break;
            case Weight:
                //??????
                updateWeightView(data);
                break;
            case BloodSugar:
                //??????
                updateGlucView(data);
                break;
            case Waist:
                //??????
                updateWaistView(data);
                break;
            case Infusion:
                //??????
                updateInfusionView(data);
                break;
            case Smart:
                //??????
                updateSamrtView(data);
                break;
            case Cholesterol:
                //??????
                updateCholesterolView(data);
                break;
            case Uric:
                //??????
                updateUricView(data);
                break;

        }
    }

    /**
     * ????????????????????????
     * @param data
     */
    private  void updateTempView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                double templature = obj.getDouble("templature");//36~37.3
                String createTime = obj.getString("createTime");

                String timeStart = createTime.substring(0, 10).replaceAll("-","/");
                String timeEnd = createTime.substring(11, 19);
                createTime = timeStart+"\n"+timeEnd;
                int color =green_dark;

                if(templature>37.3){
                    color =Color.RED;
                }

                if(templature<36.0){
                    color =orange_yellow;
                }


                mAxisXValues.add(new AxisValue(i).setLabel(createTime));

                mPointValues.add(new PointValue(i, Float.parseFloat(templature+"")));

                mPointColors.add(color);
            }catch (Exception e){

                //LogH.e("upateTempView","upateTempView->"+e.getMessage());
            }
        }

        initLineChart(1,1);

    }




    /**
     * ????????????????????????
     * @param data
     */
    private  void updateOxyView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                int oxy = obj.getInt("oxy");//94~100
                int pulse = obj.getInt("pulse");//60~100
                String createTime = obj.getString("createTime");

                String timeStart = createTime.substring(0, 10).replaceAll("-","/");
                String timeEnd = createTime.substring(11, 19);
                createTime = timeStart+"\n"+timeEnd;
                int color =green_dark;
                int color2 =green_dark;

                if(oxy<94){
                    color =orange_yellow;
                }
                if(pulse>100){
                    color2 =Color.RED;
                }
                if(pulse<60){
                    color2 =orange_yellow;
                }

                mAxisXValues.add(new AxisValue(i).setLabel(createTime));

                mPointValues.add(new PointValue(i, Float.parseFloat(oxy+"")));

                mPointValues2.add(new PointValue(i, Float.parseFloat(pulse+"")));



                mPointColors.add(color);
                mPointColors2.add(color2);
            }catch (Exception e){

              //  LogH.e("upateOxyView","upateOxyView->"+e.getMessage());
            }
        }

        initLineChart(2,0);

    }



    /**
     * ????????????????????????
     * @param data
     */
    private  void updateXyView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                int heightPressure = obj.getInt("heightPressure");//90~139
                int lowPressure = obj.getInt("lowPressure");//60~89
                String createTime = obj.getString("createTime");

                String timeStart = createTime.substring(0, 10).replaceAll("-","/");
                String timeEnd = createTime.substring(11, 19);
                createTime = timeStart+"\n"+timeEnd;
                int color =green_dark;
                int color2 =green_dark;

                if(heightPressure>139 || lowPressure>89){color =Color.RED;}

                //  if(lowPressure>=60 && lowPressure<=89){}else{ color2 =Color.RED; }

                mAxisXValues.add(new AxisValue(i).setLabel(createTime));

                mPointValues.add(new PointValue(i, Float.parseFloat(heightPressure+"")));

                mPointValues2.add(new PointValue(i, Float.parseFloat(lowPressure+"")));



                mPointColors.add(color);
                mPointColors2.add(color2);
            }catch (Exception e){

             //   LogH.e("upateXyView","upateXyView->"+e.getMessage());
            }
        }

        initLineChart(2,0);

    }





    /**
     * ????????????????????????
     * @param data
     */
    private  void updateGlucView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                double gluc = obj.getDouble("gluc");//??????3.9~6.1  ??????3.9~7.8
                String createTime = obj.getString("createTime");

                String timeStart = createTime.substring(0, 10).replaceAll("-","/");
                String timeEnd = createTime.substring(11, 19);
                createTime = timeStart+"\n"+timeEnd;
                int color =green_dark;

                if(gluc<3.9){
                    color =orange_yellow;
                }

                if(gluc>6.1){
                    color =Color.RED;
                }


                mAxisXValues.add(new AxisValue(i).setLabel(createTime));

                mPointValues.add(new PointValue(i, Float.parseFloat(gluc+"")));

                mPointColors.add(color);
            }catch (Exception e){

             //   LogH.e("updateGlucView","updateGlucView->"+e.getMessage());
            }
        }

        initLineChart(1,1);

    }


    /**
     * ??????????????????????????????
     * @param data
     */
    private  void updateCholesterolView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                double value = obj.getDouble("cho");//?????????:3.12-5.17
                String createTime = obj.getString("createTime");

                String timeStart = createTime.substring(0, 10).replaceAll("-","/");
                String timeEnd = createTime.substring(11, 19);
                createTime = timeStart+"\n"+timeEnd;
                int color =green_dark;

                if(value<3.12){
                    color =orange_yellow;
                }

                if(value>5.17){
                    color =Color.RED;
                }


                mAxisXValues.add(new AxisValue(i).setLabel(createTime));

                mPointValues.add(new PointValue(i, Float.parseFloat(value+"")));

                mPointColors.add(color);
            }catch (Exception e){

             //   LogH.e("updateCholesterolView","updateCholesterolView->"+e.getMessage());
            }
        }

        initLineChart(1,2);

    }



    /**
     * ????????????????????????
     * @param data
     */
    private  void updateUricView(JSONArray data){

        String sex ="???";
        if(BaseApp.User != null && !StringUtil.isEmpty(BaseApp.User.getId())){
            sex = BaseApp.User.getSex();
        }
        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                double value = obj.getDouble("ua");//0.149-0.416(???) 0.089-0.357(???)
                String createTime = obj.getString("createTime");

                String timeStart = createTime.substring(0, 10).replaceAll("-","/");
                String timeEnd = createTime.substring(11, 19);
                createTime = timeStart+"\n"+timeEnd;
                int color =green_dark;

                if(sex.equals("???")){
                    if(value<0.149){
                        color =orange_yellow;
                    }

                    if(value>0.416){
                        color =Color.RED;
                    }
                }else{
                    if(value<0.089){
                        color =orange_yellow;
                    }

                    if(value>0.357){
                        color =Color.RED;
                    }
                }



                mAxisXValues.add(new AxisValue(i).setLabel(createTime));

                mPointValues.add(new PointValue(i, Float.parseFloat(value+"")));

                mPointColors.add(color);
            }catch (Exception e){

               // LogH.e("updateUricView","updateUricView->"+e.getMessage());
            }
        }

        initLineChart(1,2);

    }




    /**
     * ????????????????????????
     * @param data
     */
    private  void updateWeightView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                double weight = obj.getDouble("weight");//  ?????????18.5???BMI???24???   24~28 ?????? 28 ????????????    ?????????65???????????????1???72?????????????????????=65kg/???1.72M??1.72M???=21.97
                String createTime = obj.getString("createTime");

                String timeStart = createTime.substring(0, 10).replaceAll("-","/");
                String timeEnd = createTime.substring(11, 19);
                createTime = timeStart+"\n"+timeEnd;
                int color =green_dark;

                //  if(gluc<3.9){
                //     color =orange_yellow;
                //   }

                //  if(gluc>6.1){
                //    color =Color.RED;
                //   }


                mAxisXValues.add(new AxisValue(i).setLabel(createTime));

                mPointValues.add(new PointValue(i, Float.parseFloat(weight+"")));

                mPointColors.add(color);
            }catch (Exception e){

            //    LogH.e("updateWeightView","updateWeightView->"+e.getMessage());
            }
        }

        initLineChart(1,1);

    }



    /**
     * ????????????????????????
     * @param data
     */
    private  void updateWaistView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                double waist = obj.getDouble("waist");//  ??????*0.34    cm
                String createTime = obj.getString("createTime");

                String timeStart = createTime.substring(0, 10).replaceAll("-","/");
                String timeEnd = createTime.substring(11, 19);
                createTime = timeStart+"\n"+timeEnd;
                int color =green_dark;

                // if(gluc<3.9){
                //     color =orange_yellow;
                //  }

                //  if(gluc>6.1){
                //     color =Color.RED;
                //  }


                mAxisXValues.add(new AxisValue(i).setLabel(createTime));

                mPointValues.add(new PointValue(i, Float.parseFloat(waist+"")));

                mPointColors.add(color);
            }catch (Exception e){

               // LogH.e("updateWaistView","updateWaistView->"+e.getMessage());
            }
        }

        initLineChart(1,1);

    }


    /**
     * ????????????????????????
     * @param data
     */
    private  void upateBreathView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                double breath = obj.getDouble("breath");//12~20
                String createTime = obj.getString("createTime");

                String timeStart = createTime.substring(0, 10).replaceAll("-","/");
                String timeEnd = createTime.substring(11, 19);
                createTime = timeStart+"\n"+timeEnd;
                int color =green_dark;

                if(breath>20) { color =Color.RED;}
                if(breath<12) { color =orange_yellow;}



                mAxisXValues.add(new AxisValue(i).setLabel(createTime));

                mPointValues.add(new PointValue(i, Float.parseFloat(breath+"")));

                mPointColors.add(color);
            }catch (Exception e){

              //  LogH.e("upateBreathView","upateBreathView->"+e.getMessage());
            }
        }

        initLineChart(1,0);

    }







    /**
     * ????????????????????????
     * @param data
     */
    private  void updateSamrtView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                double smart_value = obj.getDouble("smart_value");// ?????????
                int smart_heart = obj.getInt("smart_heart");//60~89
                String createTime = obj.getString("createTime");

                String timeStart = createTime.substring(0, 10).replaceAll("-","/");
                String timeEnd = createTime.substring(11, 19);
                createTime = timeStart+"\n"+timeEnd;
                int color =green_dark;
                int color2 =green_dark;

                //  if(heightPressure>=90 && heightPressure<=139){}else{ color =Color.RED;}

                //  if(lowPressure>=60 && lowPressure<=89){}else{ color2 =Color.RED; }

                mAxisXValues.add(new AxisValue(i).setLabel(createTime));

                mPointValues.add(new PointValue(i, Float.parseFloat(smart_value+"")));

                mPointValues2.add(new PointValue(i, Float.parseFloat(smart_heart+"")));



                mPointColors.add(color);
                mPointColors2.add(color2);
            }catch (Exception e){

                //LogH.e("updateSamrtView","updateSamrtView->"+e.getMessage());


                try {
                  //  LogH.e("updateSamrtView", "updateSamrtView->??????" + data.getJSONObject(i).toString());
                }catch (Exception e2){

                }
            }
        }

        initLineChart(2,1);

    }


    /**
     * ????????????????????????
     * @param data
     */
    private  void updateInfusionView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                int infusion_value = obj.getInt("infusion_value");//90~139
                int infusion_bar = obj.getInt("infusion_bar");//60~89
                String createTime = obj.getString("createTime");

                String timeStart = createTime.substring(0, 10).replaceAll("-","/");
                String timeEnd = createTime.substring(11, 19);
                createTime = timeStart+"\n"+timeEnd;
                int color =green_dark;
                int color2 =green_dark;

                //  if(heightPressure>=90 && heightPressure<=139){}else{ color =Color.RED;}

                //  if(lowPressure>=60 && lowPressure<=89){}else{ color2 =Color.RED; }

                mAxisXValues.add(new AxisValue(i).setLabel(createTime));

                mPointValues.add(new PointValue(i, Float.parseFloat(infusion_value+"")));

                mPointValues2.add(new PointValue(i, Float.parseFloat(infusion_bar+"")));



                mPointColors.add(color);
                mPointColors2.add(color2);
            }catch (Exception e){

              //  LogH.e("updateSamrtView","updateSamrtView->"+e.getMessage());
            }
        }

        initLineChart(2,0);

    }



    private List<PointValue> mPointValues = new ArrayList<PointValue>();

    private List<PointValue> mPointValues2 = new ArrayList<PointValue>();

    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    private List<Integer> mPointColors = new ArrayList<Integer>();
    private List<Integer> mPointColors2 = new ArrayList<Integer>();

    int green_dark=BaseApp.getAppResources().getColor(R.color.green_dark);
    int blue_light=BaseApp.getAppResources().getColor(R.color.blue_light);
    int blue_sky=BaseApp.getAppResources().getColor(R.color.blue_sky);

    int orange_yellow =BaseApp.getAppResources().getColor(R.color.orange_yellow);


    /**
     * ?????? lineCount=2  ??????????????????
     * @param lineCount
     *  @param digitsNumber ????????????
     */
    private void initLineChart(int lineCount,int digitsNumber) {

        int min=0;
        int max=200;

        int maxShowSize=10;//??????????????????



        List<Line> lines = new ArrayList<Line>();



        //Color.parseColor("#FFCD41")



        Line line = new Line(mPointValues).setColor(green_dark).setStrokeWidth(3);  //???????????????????????????
        line.setPointRadius(5);
        line.setShape(ValueShape.CIRCLE);//????????????????????????????????????  ??????????????? ???????????? ???ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND???
        line.setCubic(false);//?????????????????????????????????????????????
        line.setFilled(false);//???????????????????????????
        line.setHasLabels(true);//?????????????????????????????????Y ??????
        // line.setHasLabelsOnlyForSelected(true);//????????????????????????????????????????????????line.setHasLabels(true);????????????
        // line.setHasLines(true);//??????????????????????????????false ??????????????????????????????
        line.setHasPoints(true);//?????????????????? ?????????false ????????????????????????????????????????????????????????????????????????
        line.setColorValues(mPointColors);
        line.setFormatter(new SimpleLineChartValueFormatter().setDecimalDigitsNumber(digitsNumber));




        lines.add(line);

        //
        Line line2 = new Line(mPointValues2).setColor(blue_sky).setStrokeWidth(3);  //???????????????????????????
        line2.setPointRadius(5);
        line2.setShape(ValueShape.CIRCLE);//????????????????????????????????????  ??????????????? ???????????? ???ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND???
        line2.setCubic(false);//?????????????????????????????????????????????
        line2.setFilled(false);//???????????????????????????
        line2.setHasLabels(true);//?????????????????????????????????Y ??????
        // line2.setHasLabelsOnlyForSelected(true);//????????????????????????????????????????????????line.setHasLabels(true);????????????
        // line2.setHasLines(true);//??????????????????????????????false ??????????????????????????????
        line2.setHasPoints(true);//?????????????????? ?????????false ????????????????????????????????????????????????????????????????????????
        line2.setColorValues(mPointColors2);
        line2.setFormatter(new SimpleLineChartValueFormatter().setDecimalDigitsNumber(digitsNumber));
        if(lineCount==2) {
            lines.add(line2);
        }





        LineChartData data = new LineChartData();
        data.setValueLabelBackgroundEnabled(false);//?????????????????????????????????????????????
        data.setValueLabelsTextColor(Color.LTGRAY);// ????????????????????????



        data.setLines(lines);


        //X ?????????
        Axis axisX = new Axis(); //X???
        // axisX.setHasTiltedLabels(true);  //X?????????????????????????????????????????????true???????????????
        axisX.setTextColor(Color.LTGRAY);  //??????????????????
        //axisX.setName("date");  //????????????
        axisX.setTextSize(10);//??????????????????
        axisX.setMaxLabelChars(maxShowSize); //????????????X???????????????????????????????????????X????????????????????? mAxisXValues.size()
        axisX.setValues(mAxisXValues);  //??????X??????????????????

        axisX.setAutoGenerated(false);


        axisX.setHasLines(true); //x ????????????
        // axisX.setInside(true);

        data.setAxisXBottom(axisX); //x ????????????







        // Y???????????????????????????????????????Y?????????(???????????????????????????Y??????????????????????????????)
        Axis axisY = new Axis().setHasLines(true);  //Y???
        axisY.setName("");
        axisY.setTextSize(12);
        //   axisY.setFormatter(new SimpleAxisValueFormatter().setDecimalDigitsNumber(digitsNumber));



        ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();

        for (int j = 0; j < 13; j++) {

            //  axisValuesY.add(new AxisValue(j).setValue(j*20));// ??????Y?????????????????????

        }
        //axisY.setValues(axisValuesY);

        data.setAxisYLeft(axisY);

        data.setValueLabelTextSize(14);//?????? ??????????????????





        lineChartView_chart.setLineChartData(data);



        lineChartView_chart.setVisibility(View.VISIBLE);
        Viewport v2=new Viewport(lineChartView_chart.getMaximumViewport());
        //v2.top=max;
        //  v2.bottom=min;

        ////////////////////////??????????????????maxShowSize//???????????????/////////////////////////////
        if(mAxisXValues.size()<maxShowSize){
            v2.left=0;
            v2.right=maxShowSize;
            lineChartView_chart.setMaximumViewport(v2);
        }


        Viewport v = new Viewport(lineChartView_chart.getMaximumViewport());
        if(mPointValues.size()>maxShowSize) {
            v.left = v.right - maxShowSize;  //?????????????????????
            //v.left=v.left;
            v.right = v.right;//v.right ????????????

        }else{
            ////////////////////////??????????????????maxShowSize//???????????????/////////////////////////////
            v.right = maxShowSize;//v.right ????????????
        }
        lineChartView_chart.setCurrentViewport(v);



        lineChartView_chart.setScrollEnabled(true);
        lineChartView_chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        lineChartView_chart.setZoomEnabled(false);

        // lineChartView_chart.setZoomType(ZoomType.HORIZONTAL);






    }







    /**
     * ?????????????????????????????????????????????????????????1???????????????????????????????????? ????????????
     * @param type
     * @param playMusic
     */
    private void switchDevice(int type,boolean playMusic){



        DeviceEnum info = DeviceEnum.getEnum(type);

        if(!playMusic && singleMolel){
            // ????????????
            BaseApp.getBLE().setSelectItem(info);

            BaseApp.getBLE().disConnect(type,false);
        }

        selectType = info.getType();
        //,1?????? 2??????????????? 3?????? 4?????? 5?????? 6?????? 7?????? 8 ?????? 9?????? 10 ???????????? 20 ??????
        switch (info){

            case Temp:
                //1??????

                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);
                Line_name.setText("??????");
                break;
            case Oxy:
                //??????

                Line2_color.setVisibility(View.VISIBLE);
                Line2_name.setVisibility(View.VISIBLE);

                Line_name.setText("??????");
                Line2_name.setText("??????");
                break;
            case Breath:
                //?????????

                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);

                Line_name.setText("??????");
                break;

            case BloodPressure:
                //??????

                Line2_color.setVisibility(View.VISIBLE);
                Line2_name.setVisibility(View.VISIBLE);

                Line_name.setText("?????????");
                Line2_name.setText("?????????");
                break;
            case BloodSugar:
                //??????

                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);

                Line_name.setText("??????");
                break;
            case Infusion:
                //??????
                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);

                Line_name.setText("??????");
                break;
            case Weight:
                //??????
                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);

                Line_name.setText("??????");
                break;

            case Waist:
                //??????
                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);

                Line_name.setText("??????");
                break;
            case Smart:
                //??????
                Line2_color.setVisibility(View.VISIBLE);
                Line2_name.setVisibility(View.VISIBLE);

                Line_name.setText("??????");
                Line2_name.setText("??????");
                break;
            case Uric:
                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);

                Line_name.setText("??????");
                break;
            case Cholesterol:

                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);

                Line_name.setText("????????????");
                break;
        }

        loadSessionDeviceInfo(info,playMusic);

        ///////////////////////???????????????????????????/////////////////////////////

        select_day=DateTimeUtil.getNowDate();  //?????? ????????????


        this.getDataList(select_day);




    }

    private DeviceEnum lastType = null;
    private Map lastResult = null;
    /**
     * ????????????????????????????????????
     */
    private void subForm(){
        String value_1 = EditText_1.getText().toString();
        String value_2 = EditText_2.getText().toString();
        String value_3 = EditText_3.getText().toString();
        lastResult.put("time", DateUtil.getStringDate());

        if(lastType == DeviceEnum.Temp){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>=0 && Float.parseFloat(value_1)<=44){

                lastResult.put("temp",value_1);


            }else{

                AlertUtil.show("?????????????????????,????????????0~44");
                return;
            }
        } else if(lastType == DeviceEnum.Oxy){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>0 && Float.parseFloat(value_1)<=100){
                lastResult.put("oxy",value_1);
            }else{

                AlertUtil.show("?????????????????????,????????????0~100");
                return;
            }

            if(!StringUtil.isEmpty(value_2) && Float.parseFloat(value_2)>=0 && Float.parseFloat(value_2)<=280){
                lastResult.put("heart",value_2);
            }else{

                AlertUtil.show("?????????????????????,????????????0~280");
                return;
            }
        }else  if(lastType == DeviceEnum.BloodPressure){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>0 && Float.parseFloat(value_1)<=300){
                lastResult.put("heightPressure",value_1);
            }else{

                AlertUtil.show("????????????????????????,????????????1~300");
                return;
            }

            if(!StringUtil.isEmpty(value_2) && Float.parseFloat(value_2)>0 && Float.parseFloat(value_2)<=300){
                lastResult.put("lowPressure",value_2);
            }else{

                AlertUtil.show("????????????????????????,????????????1~300");
                return;
            }

            if(!StringUtil.isEmpty(value_3) && Float.parseFloat(value_3)>=0 && Float.parseFloat(value_3)<=280){
                lastResult.put("heart",value_3);
            }else{

                AlertUtil.show("?????????????????????,????????????0~280");
                return;
            }
        }else if(lastType == DeviceEnum.BloodSugar){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>0 && Float.parseFloat(value_1)<=34){
                lastResult.put("gluc",value_1);
            }else{

                AlertUtil.show("?????????????????????,????????????0~34");
                return;
            }


        }else if(lastType == DeviceEnum.Weight){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>0 && Float.parseFloat(value_1)<=250){
                lastResult.put("weight",value_1);
            }else{

                AlertUtil.show("?????????????????????,????????????0~250");
                return;
            }


        }else if(lastType == DeviceEnum.Waist){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>0 && Float.parseFloat(value_1)<=200){
                lastResult.put("waist",value_1);
            }else{

                AlertUtil.show("?????????????????????,????????????0~200");
                return;
            }


        }else if(lastType == DeviceEnum.Uric){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>=0 && Float.parseFloat(value_1)<=1.2){
                lastResult.put("ua",value_1);
            }else{

                AlertUtil.show("?????????????????????,????????????0~1.2");
                return;
            }


        }else  if(lastType == DeviceEnum.Cholesterol){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>=0 && Float.parseFloat(value_1)<=11){
                lastResult.put("cho",value_1);
            }else{

                AlertUtil.show("???????????????????????????,????????????0~11");
                return;
            }


        }

        switch (lastType){

            case Temp:
                //??????
            case Oxy:
                //??????
            case BloodPressure:
                //??????
            case BloodSugar:
                //?????? 0~34mmg/l
            case Weight:
                //??????0~250
            case Waist:
                //?????? 0~250
            case Uric:
                //??????0~1.2
            case Cholesterol:
                //????????????0~11
                BaseApp.uploadDeviceData(lastType, lastResult,true);
                RelativeLayout_input_bg.setVisibility(View.GONE);
                break;
        }




    }



    @Override
    protected void onResume() {
        super.onResume();

        initBleCallBack();
        DeviceEnum info = DeviceEnum.getEnum(selectType);
        loadSessionDeviceInfo(info,false);




        BaseApp.isStop = false;

    }




    private  void showEditView(){

        DeviceEnum info =  DeviceEnum.getEnum(selectType);

        if(RelativeLayout_input_bg.getVisibility() == View.VISIBLE){
            return;

        }

        Map item =new HashMap();
        //??????????????????????????????????????????

        switch (info) {
            case Temp:  item.put("temp","");break;

            case Oxy:
                item.put("oxy","");
                item.put("heart","");
                break;
            case BloodPressure:
                item.put("heightPressure","");
                item.put("lowPressure","");
                item.put("heart","");
                break;
            case BloodSugar:   item.put("gluc","");break;
            case Weight:     item.put("weight","");break;
            case Waist:   item.put("waist","");break;
            case Cholesterol:   item.put("cho",""); break;
            case Uric:   item.put("ua",""); break;

            default:
                //?????????????????????
                return;

        }

        RelativeLayout_input_bg.setVisibility(View.VISIBLE);


        showInputView(info,item);
    }



    private void showInputView(DeviceEnum type,Map item){

        lastType = type;
        lastResult = item;

        LinearLayout_2.setVisibility(View.GONE);
        LinearLayout_3.setVisibility(View.GONE);
        switch (type){

            case Temp:
                //??????

              /*
                    time = item.get("time").toString();


                    String report= item.get("report").toString();
                    String status=item.get("status").toString();

                    valueMsg=("??????:"+temp+"???,??????:"+status);
                    reportMsg = report;


                */
                String temp = item.get("temp").toString();
                TextView_type.setText("??????(???)");
                TextView_1.setText("?????????");
                EditText_1.setHint("???????????????");
                EditText_1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);//8194
                EditText_1.setMaxEms(4);
                EditText_1.setText(temp);




                break;
            case Oxy:
                //??????
                String oxy = item.get("oxy").toString();
                String heart= item.get("heart").toString();
                TextView_type.setText("??????");
                TextView_1.setText("??????(%)???");
                TextView_2.setText("?????????");

                EditText_1.setHint("????????????????????????");
                EditText_1.setText(oxy);
                EditText_1.setInputType(InputType.TYPE_CLASS_NUMBER);
                EditText_1.setMaxEms(2);

                EditText_2.setHint("???????????????");
                EditText_2.setText(heart);
                EditText_2.setInputType(InputType.TYPE_CLASS_NUMBER);
                EditText_2.setMaxEms(3);
                LinearLayout_2.setVisibility(View.VISIBLE);





               /*

                    time = item.get("time").toString();

                    String report= item.get("report").toString();


                    valueMsg=("??????:"+oxy+"%,??????:"+heart+"???/??????");
                    reportMsg = report;

 */

                break;
            case Breath:
                //?????????

                break;

            case BloodPressure:
                //??????
                String heightPressure = item.get("heightPressure").toString();
                String lowPressure= item.get("lowPressure").toString();
                String heart2= item.get("heart").toString();
                TextView_type.setText("??????(mmHg)");

                TextView_1.setText("????????????");
                TextView_2.setText("????????????");
                TextView_3.setText("?????????");


                EditText_1.setHint("??????????????????");
                EditText_1.setText(heightPressure);
                EditText_1.setInputType(InputType.TYPE_CLASS_NUMBER);
                EditText_1.setMaxEms(3);

                EditText_2.setHint("??????????????????");
                EditText_2.setText(lowPressure);
                EditText_2.setInputType(InputType.TYPE_CLASS_NUMBER);
                EditText_2.setMaxEms(3);

                EditText_3.setHint("???????????????");
                EditText_3.setText(heart2);
                EditText_3.setInputType(InputType.TYPE_CLASS_NUMBER);
                EditText_3.setMaxEms(3);

                LinearLayout_2.setVisibility(View.VISIBLE);
                LinearLayout_3.setVisibility(View.VISIBLE);
                  /*
                    time = item.get("time").toString();

                    String report= item.get("report").toString();
                    String status= item.get("status").toString();

                    valueMsg=("??????:"+heightPressure+"/"+lowPressure +"mmHg,??????:"+heart+"???/??????");
                    reportMsg = report;



                     */

                break;
            case BloodSugar:
                //??????
                String gluc = item.get("gluc").toString();

                TextView_type.setText("??????(mmol/L)");
                TextView_1.setText("?????????");

                EditText_1.setHint("???????????????");
                EditText_1.setText(gluc);
                EditText_1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
                EditText_1.setMaxEms(5);
                  /*
                    time = item.get("time").toString();



                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("??????:"+gluc+"mmol/L,"+status);
                    reportMsg = report;

                 */


                break;
            case Infusion:
                //??????



                break;
            case Weight:
                //??????
                String weight = item.get("weight").toString();
                TextView_type.setText("??????(KG)");
                TextView_1.setText("?????????");

                EditText_1.setHint("???????????????");
                EditText_1.setText(weight);
                EditText_1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
                EditText_1.setMaxEms(5);
             /*

                    time = item.get("time").toString();

                    String bmi = item.get("bmi").toString();

                    String report= item.get("report").toString();


                    valueMsg=("??????:"+weight+" KG???BMI:"+bmi);
                    reportMsg = report;*/




                break;
            case Waist:
                //??????
                String waist = item.get("waist").toString();
                TextView_type.setText("??????(CM)");
                TextView_1.setText("?????????");
                EditText_1.setHint("???????????????");
                EditText_1.setText(waist);
                EditText_1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
                EditText_1.setMaxEms(2);
                  /*
                    time = item.get("time").toString();



                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("??????:"+waist+"CM,"+status);
                    reportMsg = report;*/





                break;
            case Smart:
                //??????


                break;
            case Uric:
                //??????
                String ua = item.get("ua").toString();
                TextView_type.setText("??????(mmol/L)");
                EditText_1.setHint("???????????????");
                EditText_1.setText(ua);
                EditText_1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
                EditText_1.setMaxEms(6);
                  /*
                    time = item.get("time").toString();



                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("??????:"+ua+"mmol/L,"+status);
                    reportMsg = report;*/





                break;

            case Cholesterol:
                //????????????
                String cho = item.get("cho").toString();
                TextView_type.setText("????????????(mmol/L)");
                EditText_1.setHint("?????????????????????");
                EditText_1.setText(cho);
                EditText_1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
                EditText_1.setMaxEms(5);
                  /*
                    time = item.get("time").toString();



                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("????????????:"+cho+"mmol/L,"+status);
                    reportMsg = report;*/



                break;
        }



    }


}