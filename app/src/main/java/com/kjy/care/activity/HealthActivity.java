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
        //界面旋转180度
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

                Button_yes.setText("确认");
            }
        });







        //,1体温 2脉搏、血氧 3呼吸 4空着 5血压 6体重 7血糖 8 腰围 9尿酸 10 胆固醇
        List<DeviceInfo> listItem = new ArrayList<DeviceInfo>();
        listItem.add(new DeviceInfo("体温","体温", DeviceEnum.Temp.getType()));
        listItem.add(new DeviceInfo("血氧","血氧",DeviceEnum.Oxy.getType()));
        listItem.add(new DeviceInfo("睡眠带","睡眠带",DeviceEnum.Breath.getType()));
        listItem.add(new DeviceInfo("血压","血压",DeviceEnum.BloodPressure.getType()));
        listItem.add(new DeviceInfo("血糖","血糖",DeviceEnum.BloodSugar.getType()));

        listItem.add(new DeviceInfo("体重","体重",DeviceEnum.Weight.getType()));
        listItem.add(new DeviceInfo("腰围","腰围",DeviceEnum.Waist.getType()));
        listItem.add(new DeviceInfo("手环","手环",DeviceEnum.Smart.getType()));
        listItem.add(new DeviceInfo("尿酸","尿酸",DeviceEnum.Uric.getType()));
        listItem.add(new DeviceInfo("总胆固醇","总胆固醇",DeviceEnum.Cholesterol.getType()));




       // listItem.add(new DeviceInfo("输液","输液",DeviceEnum.Infusion.getType()));





        //  ArrayAdapter
        //创建一个simpleAdapter
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
                    //////////////////连续点击同一个菜单处理///////////////////////////
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
                msg.arg1 = i;//设备类型
                //  mainHandler.sendMessage(msg);


            }

            @Override
            public void DisConnect(int i,String name, String mac) {
                //  LogH.e("DisConnect","DisConnect=>"+name+"=="+mac);

                Message msg=new Message();
                msg.what = DEVICE_DISCONNECT;
                msg.arg1 = i;//设备类型
                mainHandler.sendMessage(msg);
            }

            @Override
            public void ReciveData(int i,byte[] data0, String data1, String uuid, String mac,String[] value) {
                //LogH.e("ReciveData","MAIN-----ReciveData=>"+data1);

                Message msg=new Message();
                msg.what = DEVICE_REC_DATA;
                msg.arg1 = i;//设备类型
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


    final int VIEW_DATAINFO=1;//更新不同设备记录
    final int VIEW_DATE_SELECT=3;//选择时间后


    public Handler mainHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case VIEW_DATAINFO:

                    JSONArray temp =  (JSONArray)(msg.obj);

                    updateDataInfoView(temp,msg.arg1);



                    break;



                case VIEW_DATE_SELECT:
                    //选择时间后更新数据
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




        ///////////////选中状态下更新数据显示/////////////////////
        if(selectType == type){

            TextView_Connect_Value.setText("连接状态：已连接");
            TextView_Value.setText("检查结果：");
            TextView_Report.setText("指导建议：");
        }

        /////////////单独爱奥乐体温计处理////////////////////
        if(type== DeviceEnum.Temp_BIOLAND.getType()) {
            if (selectType == DeviceEnum.Temp.getType() ) {

                TextView_Connect_Value.setText("连接状态：已连接");
                TextView_Value.setText("检查结果：");
                TextView_Report.setText("指导建议：");
            }
        }


        /////////////单独爱奥乐血压计处理////////////////////
        if(type== DeviceEnum.BloodPressure_BIOLAND.getType()) {
            if (selectType == DeviceEnum.BloodPressure.getType() ) {

                TextView_Connect_Value.setText("连接状态：已连接");
                TextView_Value.setText("检查结果：");
                TextView_Report.setText("指导建议：");
            }
        }



        ////////////读取当前////缓存////////////////////

        if(type== DeviceEnum.ThreeToOne.getType()) {
            if (selectType == DeviceEnum.BloodSugar.getType()     || selectType == DeviceEnum.Cholesterol.getType() || selectType == DeviceEnum.Uric.getType()) {

                TextView_Connect_Value.setText("连接状态：已连接");
                TextView_Value.setText("检查结果：");
                TextView_Report.setText("指导建议：");
            }
        }





    }

    private  void onDeviceDisConnect(int type){


        if(selectType == type){
            TextView_Connect_Value.setText("连接状态：未连接");

        }

        /////////////单独爱奥乐体温计处理////////////////////
        if(type== DeviceEnum.Temp_BIOLAND.getType()) {
            if (selectType == DeviceEnum.Temp.getType() ) {

                TextView_Connect_Value.setText("连接状态：未连接");
            }
        }


        /////////////单独爱奥乐血压计处理////////////////////
        if(type== DeviceEnum.BloodPressure_BIOLAND.getType()) {
            if (selectType == DeviceEnum.BloodPressure.getType() ) {

                TextView_Connect_Value.setText("连接状态：未连接");
            }
        }


        /////////////设备未连接//////////////////

        if(type == DeviceEnum.ThreeToOne.getType()) {
            if (selectType == DeviceEnum.BloodSugar.getType() || selectType == DeviceEnum.Uric.getType() || selectType == DeviceEnum.Cholesterol.getType()) {
                TextView_Connect_Value.setText("连接状态：未连接");

            }
        }





    }


    private  void onDeviceRecData(int type,String[] data){
        DeviceEnum info = DeviceEnum.getEnum(type);

        //////////////输液监控数据例外/////////////
        if(info ==DeviceEnum.Infusion){

            if(data.length>0 && data[0].equals("0")){
                //////////////data[0].equals("0")//////输液中不切换左边菜单////////////////////

                //LogH.e("onDeviceRecData","输液监控中=>电池等级"+data[1]);
                if(selectType == info.getType()){


                    loadSessionDeviceInfo(info,false);
                }



                return;

            }

        }
        //////////////手环监控数据例外/////////////
        if(info ==DeviceEnum.Smart){
            //  LogH.e("onDeviceRecData","手环监控中=>"+data[2]+"==="+data[3]);
            if(data.length==4 && data[2].equals("0")){
                //////////////data[2].equals("0")//////SOS关闭下不切换////////////////////

                // LogH.e("onDeviceRecData","手环监控中=>"+data[3]);
                if(selectType == info.getType()){


                    loadSessionDeviceInfo(info,false);
                }



                return;

            }

        }
        if(BaseApp.singleMolel && !BaseApp.isStop) {
            //但设备模式下和界面是显示状态

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






        //切换数据显示,接受数据，播放语音
        switchDevice(type,true);

        //切换左边菜选中
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
                Button_yes.setText("确认");
                //RelativeLayout_input_bg.setVisibility(View.GONE);
                ///////倒计时自动提交///////

                cleanTimer();
                subForm();

                return;
            }

            Button_yes.setText("确认("+time+")");  //手动提交







        }
    };

    /**
     * 清除定时
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

    //,1体温 2脉搏、血氧 3呼吸 4空着 5血压 6体重 7血糖 8 腰围 9尿酸 10 胆固醇 20输液,21手环
    private int selectType = DeviceEnum.Temp.getType() ;

    /**
     * 加载缓存的设备绑定信息及 当日 最后一次数据值、参考、建议
     * @param info
     * @param playMusic
     */
    private void loadSessionDeviceInfo(DeviceEnum info,boolean playMusic){
        //,1体温 2脉搏、血氧 3呼吸 4空着 5血压 6体重 7血糖 8 腰围 9尿酸 10 胆固醇， 20 输液
        String value = SPUtil.get(BaseApp.getAppContext(),"data_"+info.getType());
        String connect ="未连接";

        String mac  =  SPUtil.get(this,"mac_"+info.getType());

        if(StringUtil.isEmpty(mac)){
            connect="未绑定";
        }

        String macThreeToOne  =  SPUtil.get(this,"mac_"+DeviceEnum.ThreeToOne.getType());
        if(StringUtil.isEmpty(macThreeToOne)){
            if(info == DeviceEnum.BloodSugar){
                String macBloodSugar  =  SPUtil.get(this,"mac_"+DeviceEnum.BloodSugar.getType());
                if(!StringUtil.isEmpty(macBloodSugar)){
                    connect="未连接";
                }
            }

        }else{

            if(info == DeviceEnum.Cholesterol || info == DeviceEnum.Uric){
                connect="未连接";
            }


        }


        ///////////////选中如果是体温////////////////单独处理多个体温计/////////////////////////////
        String macTemp_BIOLAND =  SPUtil.get(this,"mac_"+DeviceEnum.Temp_BIOLAND.getType());
        if(info == DeviceEnum.Temp){

            if(StringUtil.isEmpty(macTemp_BIOLAND)){
                String macBloodPressure  =  SPUtil.get(this,"mac_"+DeviceEnum.Temp.getType());
                if(!StringUtil.isEmpty(macBloodPressure)){
                    connect="未连接";
                }
            }else{

                connect = "未连接";

            }
        }




        ///////////////选中如果是血压////////////////单独处理多个血压计/////////////////////////////
        String macBloodPressure_BIOLAND =  SPUtil.get(this,"mac_"+DeviceEnum.BloodPressure_BIOLAND.getType());
        if(info == DeviceEnum.BloodPressure){

            if(StringUtil.isEmpty(macBloodPressure_BIOLAND)){
                String macBloodPressure  =  SPUtil.get(this,"mac_"+DeviceEnum.BloodPressure.getType());
                if(!StringUtil.isEmpty(macBloodPressure)){
                    connect="未连接";
                }
            }else{

                connect = "未连接";

            }
        }





        BLEDevice dev =BaseApp.getBLE().getDeviceByType(info.getType());
        if(dev!=null && dev.isConnect()){ connect ="已连接"; }


        if(!StringUtil.isEmpty(macThreeToOne)){
            if(info == DeviceEnum.BloodSugar   || info == DeviceEnum.Cholesterol   || info == DeviceEnum.Uric){
                dev =BaseApp.getBLE().getDeviceByType(DeviceEnum.ThreeToOne.getType());
                if(dev!=null && dev.isConnect()){ connect ="已连接"; }
            }

        }


        if(!StringUtil.isEmpty(macBloodPressure_BIOLAND)){
            if(info == DeviceEnum.BloodPressure){
                dev =BaseApp.getBLE().getDeviceByType(DeviceEnum.BloodPressure_BIOLAND.getType());
                if(dev!=null && dev.isConnect()){ connect ="已连接"; }
            }

        }

        if(!StringUtil.isEmpty(macTemp_BIOLAND)){
            if(info == DeviceEnum.Temp){
                dev =BaseApp.getBLE().getDeviceByType(DeviceEnum.Temp_BIOLAND.getType());
                if(dev!=null && dev.isConnect()){ connect ="已连接"; }
            }

        }





        TextView_Connect_Value.setText("连接状态："+connect);

        TextView_Value.setText("检查结果：");
        TextView_Report.setText("指导建议：");

        int blue_light = this.getResources().getColor(R.color.blue_light);
        int black_666 = this.getResources().getColor(R.color.black_666);
        int black_999 = this.getResources().getColor(R.color.black_999);
        int black_aaa= this.getResources().getColor(R.color.black_aaa);

        String valueMsg ="";
        String reportMsg ="";
        String time ="";



        switch (info){

            case Temp:
                //体温
                TextView_Value_Ref.setText("参考范围："+ DeviceDataConfig.Temp_Ref);
                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String temp = item.get("temp").toString();

                    String report= item.get("report").toString();
                    String status=item.get("status").toString();

                    valueMsg=("体温:"+temp+"℃,状态:"+status);
                    reportMsg = report;


                }


                break;
            case Oxy:
                //血氧
                TextView_Value_Ref.setText("参考范围："+DeviceDataConfig.Oxy_Ref);

                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String oxy = item.get("oxy").toString();
                    String heart= item.get("heart").toString();
                    String report= item.get("report").toString();


                    valueMsg=("血氧:"+oxy+"%,心率:"+heart+"次/分钟");
                    reportMsg = report;


                }

                break;
            case Breath:
                //睡眠带
                TextView_Value_Ref.setText("参考范围："+DeviceDataConfig.Breath_Ref);

                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String breath = item.get("breath").toString();
                    String heart= item.get("heart").toString();
                    int move= Integer.parseInt(item.get("move").toString());
                    int onbed= Integer.parseInt(item.get("onbed").toString());

                    String report= item.get("report").toString();
                    String bed ="离床";
                    if(onbed==1){

                        bed= "在床";
                    }
                    valueMsg=("呼吸:"+breath+"次/分钟,心率："+heart+"次/分钟,"+bed);
                    reportMsg = report;



                }
                // ChartDataUtil.initChartView(this.lineChartView_chart, 200.0f, 0.0f, 0.0f, 720.0f);
                break;

            case BloodPressure:
                //血压
                TextView_Value_Ref.setText("参考范围："+DeviceDataConfig.BloodPressure_Ref);

                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String heightPressure = item.get("heightPressure").toString();
                    String lowPressure= item.get("lowPressure").toString();
                    String heart= item.get("heart").toString();
                    String report= item.get("report").toString();
                    String status= item.get("status").toString();

                    valueMsg=("血压:"+heightPressure+"/"+lowPressure +"mmHg,心率:"+heart+"次/分钟");
                    reportMsg = report;

                }



                break;
            case BloodSugar:
                //血糖
                TextView_Value_Ref.setText("参考范围："+DeviceDataConfig.BloodSugar_Ref);
                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String gluc = item.get("gluc").toString();


                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("血糖:"+gluc+"mmol/L,"+status);
                    reportMsg = report;


                }


                break;
            case Infusion:
                //输液

                TextView_Value_Ref.setText("参考范围：");

                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String infusion = item.get("infusion").toString();
                    int bar = Integer.parseInt(item.get("bar").toString());
                    String report= item.get("report").toString();
                    String status= item.get("status").toString();
                    String barLess="";
                    if(bar<=16){
                        barLess=",电池电量不足,请及时充电";
                    }


                    valueMsg=(status+",电池:"+(bar)+"%");
                    reportMsg = report+barLess;


                }

                break;
            case Weight:
                //体重
                TextView_Value_Ref.setText("参考范围："+DeviceDataConfig.Weight_Ref);

                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String weight = item.get("weight").toString();
                    String bmi = item.get("bmi").toString();

                    String report= item.get("report").toString();


                    valueMsg=("体重:"+weight+" KG，BMI:"+bmi);
                    reportMsg = report;


                }

                break;
            case Waist:
                //腰围

                TextView_Value_Ref.setText("参考范围："+DeviceDataConfig.Waist_Ref);

                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String waist = item.get("waist").toString();


                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("腰围:"+waist+"CM,"+status);
                    reportMsg = report;


                }


                break;
            case Smart:
                //手环

                TextView_Value_Ref.setText("参考范围：");

                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String heart = item.get("heart").toString();
                    String temp = item.get("temp").toString();
                    String sos = item.get("sos").toString();
                    String bar = item.get("bar").toString();


                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("心率:"+heart+"次/分钟,体温:"+temp+"℃,SOS:"+(sos.equals("0")?"关":"开")+" ,电量:"+bar+"%");
                    reportMsg = report;


                }


                break;
            case Uric:
                //尿酸
                TextView_Value_Ref.setText("参考范围："+DeviceDataConfig.Uric_Ref);
                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String ua = item.get("ua").toString();


                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("尿酸:"+ua+"mmol/L,"+status);
                    reportMsg = report;


                }


                break;

            case Cholesterol:
                //总胆固醇
                TextView_Value_Ref.setText("参考范围："+DeviceDataConfig.Cholesterol_Ref);
                if(value.length()>0){
                    Map item = new Gson().fromJson(value,Map.class);
                    time = item.get("time").toString();
                    String cho = item.get("cho").toString();


                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("总胆固醇:"+cho+"mmol/L,"+status);
                    reportMsg = report;


                }


                break;
        }

        //playMusic  实时数据
        if(playMusic&&!StringUtil.isEmpty(reportMsg)){
          //  TextView_Report.postDelayed(new VoiceRunnable(valueMsg),1000);

            if(singleMolel && !BaseApp.isStop) {
                //但设备模式下和界面是显示状态
                Map item = new Gson().fromJson(value, Map.class);
                showInputView(info, item);
            }

        }



        if(time.length()>0){

            long i = new Date().getTime() - DateTimeUtil.getStringToDate(time).getTime();

            if(i<1000*60){
                //一分钟内
                TextView_Value.setText("检查结果："+valueMsg);
                TextView_Value.setTextColor(blue_light);
                TextView_Report.setText("指导建议："+reportMsg);

            }else{

                //如果是当日
                if(time.substring(0,10).equals(DateTimeUtil.getNowDate()) ) {

                    TextView_Value.setText("检查结果："+valueMsg + "(" + time + ")");
                    TextView_Value.setTextColor(black_aaa);
                    TextView_Report.setText("指导建议："+reportMsg);
                }
            }
        }

    }





    /**
     * 通过日期 获取 1个月内 的 数据记录
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


    //,1体温 2脉搏、血氧 3呼吸 4空着 5血压 6体重 7血糖 8 腰围 9尿酸 10 胆固醇
    /**
     * 获取检测记录信息
     */
    private void getDataInfo(){
        if(BaseApp.User == null){
            AlertUtil.show("查询失败,用户为空");
            return;
        }
        if(StringUtil.isEmpty(BaseApp.User.getId()) ){
            AlertUtil.show("查询失败,用户为空");
            return;
        }

        String patientNo=BaseApp.User.getId();







        int type=selectType;

        if(selectType == 20 ){
            Message msg=new Message();
            msg.what = VIEW_DATAINFO;
            msg.arg1 = type;//设备类型
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

                            AlertUtil.show("数据异常:-1");
                            cleanErrorDataView(type);


                            return;
                        }
                        //    LogH.e("数据",map.toString());
                        if (LinkedTreeMapUtil.CodeOk(map)) {
                            JSONArray temp =map.getJSONArray("body");


                            Message msg=new Message();
                            msg.what = VIEW_DATAINFO;
                            msg.arg1 = type;//设备类型
                            msg.obj =temp;
                            mainHandler.sendMessage(msg);
                        }else{
                            AlertUtil.show("数据异常:101");
                            cleanErrorDataView(type);
                        }

                    } catch (Exception e) {
                        //   e.printStackTrace();
                        //   LogH.e("getHealthCheck",e.getMessage());
                        AlertUtil.show("数据异常:100"+HttpUtil.getHealthCheck);

                        cleanErrorDataView(type);

                    }

                }


                @Override
                public void onFailure(Call<JSONObject> call, Throwable throwable) {
                    RelativeLayout_loading.setVisibility(View.GONE);
                    if(type != selectType){return;}
                    throwable.printStackTrace();
                    //  LogH.e("getHealthCheck",throwable.toString()+HttpUtil.getHealthCheck);
                    AlertUtil.show("网络异常:-100");
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
     * 请求数据失败处理
     * @param type
     */
    private void cleanErrorDataView(int type){


        JSONArray temp =new JSONArray();

        Message msg=new Message();
        msg.what = VIEW_DATAINFO;
        msg.arg1 = type;//设备类型
        msg.obj =temp;
        mainHandler.sendMessage(msg);

    }


    /**
     * 显示日期选择
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
     * 更新数据记录
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

        ///   mPointValues.clear();   可以处理重绘 遗留问题


        //  String now = DateUtil.getStringDate();

        // String day=obj.getInHospitalTime().length()>10?DateUtil.getTwoDay(now,obj.getInHospitalTime())+"天":"";
        // String inHosTime=obj.getInHospitalTime().length()>10?obj.getInHospitalTime().substring(0,10).replaceAll("-","/"):obj.getInHospitalTime();
        //,1体温 2脉搏、血氧 3呼吸 4空着 5血压 6体重 7血糖 8 腰围 9尿酸 10 胆固醇
        DeviceEnum info = DeviceEnum.getEnum(type);

        switch (info){

            case Temp:
                //1体温
                updateTempView(data);
                break;
            case Oxy:
                //血氧
                updateOxyView(data);
                break;
            case Breath:
                //睡眠带
                //   upateBreathView(data);
                break;
            case BloodPressure:
                //血压
                updateXyView(data);
                break;
            case Weight:
                //体重
                updateWeightView(data);
                break;
            case BloodSugar:
                //血糖
                updateGlucView(data);
                break;
            case Waist:
                //腰围
                updateWaistView(data);
                break;
            case Infusion:
                //输液
                updateInfusionView(data);
                break;
            case Smart:
                //手環
                updateSamrtView(data);
                break;
            case Cholesterol:
                //血糖
                updateCholesterolView(data);
                break;
            case Uric:
                //血糖
                updateUricView(data);
                break;

        }
    }

    /**
     * 更新体温历史记录
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
     * 更新血氧历史记录
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
     * 更新血压历史记录
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
     * 更新血糖历史记录
     * @param data
     */
    private  void updateGlucView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                double gluc = obj.getDouble("gluc");//空腹3.9~6.1  餐后3.9~7.8
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
     * 更新总胆固醇历史记录
     * @param data
     */
    private  void updateCholesterolView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                double value = obj.getDouble("cho");//胆固醇:3.12-5.17
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
     * 更新尿酸历史记录
     * @param data
     */
    private  void updateUricView(JSONArray data){

        String sex ="男";
        if(BaseApp.User != null && !StringUtil.isEmpty(BaseApp.User.getId())){
            sex = BaseApp.User.getSex();
        }
        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                double value = obj.getDouble("ua");//0.149-0.416(男) 0.089-0.357(女)
                String createTime = obj.getString("createTime");

                String timeStart = createTime.substring(0, 10).replaceAll("-","/");
                String timeEnd = createTime.substring(11, 19);
                createTime = timeStart+"\n"+timeEnd;
                int color =green_dark;

                if(sex.equals("男")){
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
     * 更新体重历史记录
     * @param data
     */
    private  void updateWeightView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                double weight = obj.getDouble("weight");//  范围是18.5≤BMI＜24。   24~28 超重 28 以上肥胖    人体重65公斤，身高1米72，则其体重指数=65kg/（1.72M×1.72M）=21.97
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
     * 更新腰围历史记录
     * @param data
     */
    private  void updateWaistView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                double waist = obj.getDouble("waist");//  身高*0.34    cm
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
     * 更新呼吸历史记录
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
     * 更新血压历史记录
     * @param data
     */
    private  void updateSamrtView(JSONArray data){


        for(int i=0;i<data.length();i++) {
            try {
                JSONObject obj = data.getJSONObject(i);
                double smart_value = obj.getDouble("smart_value");// 体温值
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
                  //  LogH.e("updateSamrtView", "updateSamrtView->异常" + data.getJSONObject(i).toString());
                }catch (Exception e2){

                }
            }
        }

        initLineChart(2,1);

    }


    /**
     * 更新输液历史记录
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
     * 如果 lineCount=2  说明有两条线
     * @param lineCount
     *  @param digitsNumber 小数几位
     */
    private void initLineChart(int lineCount,int digitsNumber) {

        int min=0;
        int max=200;

        int maxShowSize=10;//显示多少个点



        List<Line> lines = new ArrayList<Line>();



        //Color.parseColor("#FFCD41")



        Line line = new Line(mPointValues).setColor(green_dark).setStrokeWidth(3);  //折线的颜色（橙色）
        line.setPointRadius(5);
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上Y 的值
        // line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        // line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        line.setColorValues(mPointColors);
        line.setFormatter(new SimpleLineChartValueFormatter().setDecimalDigitsNumber(digitsNumber));




        lines.add(line);

        //
        Line line2 = new Line(mPointValues2).setColor(blue_sky).setStrokeWidth(3);  //折线的颜色（橙色）
        line2.setPointRadius(5);
        line2.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line2.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line2.setFilled(false);//是否填充曲线的面积
        line2.setHasLabels(true);//曲线的数据坐标是否加上Y 的值
        // line2.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        // line2.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line2.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        line2.setColorValues(mPointColors2);
        line2.setFormatter(new SimpleLineChartValueFormatter().setDecimalDigitsNumber(digitsNumber));
        if(lineCount==2) {
            lines.add(line2);
        }





        LineChartData data = new LineChartData();
        data.setValueLabelBackgroundEnabled(false);//采用线条颜色画嗲标签的值的背景
        data.setValueLabelsTextColor(Color.LTGRAY);// 原点标签文字颜色



        data.setLines(lines);


        //X 坐标轴
        Axis axisX = new Axis(); //X轴
        // axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.LTGRAY);  //设置字体颜色
        //axisX.setName("date");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(maxShowSize); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数 mAxisXValues.size()
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称

        axisX.setAutoGenerated(false);


        axisX.setHasLines(true); //x 轴分割线
        // axisX.setInside(true);

        data.setAxisXBottom(axisX); //x 轴在底部







        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis().setHasLines(true);  //Y轴
        axisY.setName("");
        axisY.setTextSize(12);
        //   axisY.setFormatter(new SimpleAxisValueFormatter().setDecimalDigitsNumber(digitsNumber));



        ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();

        for (int j = 0; j < 13; j++) {

            //  axisValuesY.add(new AxisValue(j).setValue(j*20));// 添加Y轴显示的刻度值

        }
        //axisY.setValues(axisValuesY);

        data.setAxisYLeft(axisY);

        data.setValueLabelTextSize(14);//设置 值的文字大小





        lineChartView_chart.setLineChartData(data);



        lineChartView_chart.setVisibility(View.VISIBLE);
        Viewport v2=new Viewport(lineChartView_chart.getMaximumViewport());
        //v2.top=max;
        //  v2.bottom=min;

        ////////////////////////处理数据不满maxShowSize//不分散显示/////////////////////////////
        if(mAxisXValues.size()<maxShowSize){
            v2.left=0;
            v2.right=maxShowSize;
            lineChartView_chart.setMaximumViewport(v2);
        }


        Viewport v = new Viewport(lineChartView_chart.getMaximumViewport());
        if(mPointValues.size()>maxShowSize) {
            v.left = v.right - maxShowSize;  //永远最后先显示
            //v.left=v.left;
            v.right = v.right;//v.right 全部展示

        }else{
            ////////////////////////处理数据不满maxShowSize//不分散显示/////////////////////////////
            v.right = maxShowSize;//v.right 全部展示
        }
        lineChartView_chart.setCurrentViewport(v);



        lineChartView_chart.setScrollEnabled(true);
        lineChartView_chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        lineChartView_chart.setZoomEnabled(false);

        // lineChartView_chart.setZoomType(ZoomType.HORIZONTAL);






    }







    /**
     * 切换显示，查询今日最后一次数据，如果是1分钟内，黑色显示，不是则 灰色显示
     * @param type
     * @param playMusic
     */
    private void switchDevice(int type,boolean playMusic){



        DeviceEnum info = DeviceEnum.getEnum(type);

        if(!playMusic && singleMolel){
            // 手动切换
            BaseApp.getBLE().setSelectItem(info);

            BaseApp.getBLE().disConnect(type,false);
        }

        selectType = info.getType();
        //,1体温 2脉搏、血氧 3呼吸 4空着 5血压 6体重 7血糖 8 腰围 9尿酸 10 胆固醇， 20 输液
        switch (info){

            case Temp:
                //1体温

                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);
                Line_name.setText("体温");
                break;
            case Oxy:
                //血氧

                Line2_color.setVisibility(View.VISIBLE);
                Line2_name.setVisibility(View.VISIBLE);

                Line_name.setText("血氧");
                Line2_name.setText("心率");
                break;
            case Breath:
                //睡眠带

                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);

                Line_name.setText("呼吸");
                break;

            case BloodPressure:
                //血压

                Line2_color.setVisibility(View.VISIBLE);
                Line2_name.setVisibility(View.VISIBLE);

                Line_name.setText("收缩压");
                Line2_name.setText("舒张压");
                break;
            case BloodSugar:
                //血糖

                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);

                Line_name.setText("血糖");
                break;
            case Infusion:
                //输液
                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);

                Line_name.setText("输液");
                break;
            case Weight:
                //体重
                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);

                Line_name.setText("体重");
                break;

            case Waist:
                //腰围
                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);

                Line_name.setText("腰围");
                break;
            case Smart:
                //手环
                Line2_color.setVisibility(View.VISIBLE);
                Line2_name.setVisibility(View.VISIBLE);

                Line_name.setText("体温");
                Line2_name.setText("心率");
                break;
            case Uric:
                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);

                Line_name.setText("尿酸");
                break;
            case Cholesterol:

                Line2_color.setVisibility(View.GONE);
                Line2_name.setVisibility(View.GONE);

                Line_name.setText("总胆固醇");
                break;
        }

        loadSessionDeviceInfo(info,playMusic);

        ///////////////////////处理输液刷新的问题/////////////////////////////

        select_day=DateTimeUtil.getNowDate();  //重置 当日时间


        this.getDataList(select_day);




    }

    private DeviceEnum lastType = null;
    private Map lastResult = null;
    /**
     * 倒计时或手动提交检测数据
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

                AlertUtil.show("请输入正常体温,输入范围0~44");
                return;
            }
        } else if(lastType == DeviceEnum.Oxy){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>0 && Float.parseFloat(value_1)<=100){
                lastResult.put("oxy",value_1);
            }else{

                AlertUtil.show("请输入正常血氧,输入范围0~100");
                return;
            }

            if(!StringUtil.isEmpty(value_2) && Float.parseFloat(value_2)>=0 && Float.parseFloat(value_2)<=280){
                lastResult.put("heart",value_2);
            }else{

                AlertUtil.show("请输入正常心率,输入范围0~280");
                return;
            }
        }else  if(lastType == DeviceEnum.BloodPressure){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>0 && Float.parseFloat(value_1)<=300){
                lastResult.put("heightPressure",value_1);
            }else{

                AlertUtil.show("请输入正常收缩压,输入范围1~300");
                return;
            }

            if(!StringUtil.isEmpty(value_2) && Float.parseFloat(value_2)>0 && Float.parseFloat(value_2)<=300){
                lastResult.put("lowPressure",value_2);
            }else{

                AlertUtil.show("请输入正常舒张压,输入范围1~300");
                return;
            }

            if(!StringUtil.isEmpty(value_3) && Float.parseFloat(value_3)>=0 && Float.parseFloat(value_3)<=280){
                lastResult.put("heart",value_3);
            }else{

                AlertUtil.show("请输入正常心率,输入范围0~280");
                return;
            }
        }else if(lastType == DeviceEnum.BloodSugar){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>0 && Float.parseFloat(value_1)<=34){
                lastResult.put("gluc",value_1);
            }else{

                AlertUtil.show("请输入正常血糖,输入范围0~34");
                return;
            }


        }else if(lastType == DeviceEnum.Weight){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>0 && Float.parseFloat(value_1)<=250){
                lastResult.put("weight",value_1);
            }else{

                AlertUtil.show("请输入正常体重,输入范围0~250");
                return;
            }


        }else if(lastType == DeviceEnum.Waist){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>0 && Float.parseFloat(value_1)<=200){
                lastResult.put("waist",value_1);
            }else{

                AlertUtil.show("请输入正常腰围,输入范围0~200");
                return;
            }


        }else if(lastType == DeviceEnum.Uric){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>=0 && Float.parseFloat(value_1)<=1.2){
                lastResult.put("ua",value_1);
            }else{

                AlertUtil.show("请输入正常尿酸,输入范围0~1.2");
                return;
            }


        }else  if(lastType == DeviceEnum.Cholesterol){
            if(!StringUtil.isEmpty(value_1) && Float.parseFloat(value_1)>=0 && Float.parseFloat(value_1)<=11){
                lastResult.put("cho",value_1);
            }else{

                AlertUtil.show("请输入正常总胆固醇,输入范围0~11");
                return;
            }


        }

        switch (lastType){

            case Temp:
                //体温
            case Oxy:
                //血氧
            case BloodPressure:
                //血压
            case BloodSugar:
                //血糖 0~34mmg/l
            case Weight:
                //体重0~250
            case Waist:
                //腰围 0~250
            case Uric:
                //尿酸0~1.2
            case Cholesterol:
                //总胆固醇0~11
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
        //但设备模式下和界面是显示状态

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
                //其他设备不填写
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
                //体温

              /*
                    time = item.get("time").toString();


                    String report= item.get("report").toString();
                    String status=item.get("status").toString();

                    valueMsg=("体温:"+temp+"℃,状态:"+status);
                    reportMsg = report;


                */
                String temp = item.get("temp").toString();
                TextView_type.setText("体温(℃)");
                TextView_1.setText("体温：");
                EditText_1.setHint("请输入体温");
                EditText_1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);//8194
                EditText_1.setMaxEms(4);
                EditText_1.setText(temp);




                break;
            case Oxy:
                //血氧
                String oxy = item.get("oxy").toString();
                String heart= item.get("heart").toString();
                TextView_type.setText("血氧");
                TextView_1.setText("血氧(%)：");
                TextView_2.setText("心率：");

                EditText_1.setHint("请输入血氧饱和度");
                EditText_1.setText(oxy);
                EditText_1.setInputType(InputType.TYPE_CLASS_NUMBER);
                EditText_1.setMaxEms(2);

                EditText_2.setHint("请输入心率");
                EditText_2.setText(heart);
                EditText_2.setInputType(InputType.TYPE_CLASS_NUMBER);
                EditText_2.setMaxEms(3);
                LinearLayout_2.setVisibility(View.VISIBLE);





               /*

                    time = item.get("time").toString();

                    String report= item.get("report").toString();


                    valueMsg=("血氧:"+oxy+"%,心率:"+heart+"次/分钟");
                    reportMsg = report;

 */

                break;
            case Breath:
                //睡眠带

                break;

            case BloodPressure:
                //血压
                String heightPressure = item.get("heightPressure").toString();
                String lowPressure= item.get("lowPressure").toString();
                String heart2= item.get("heart").toString();
                TextView_type.setText("血压(mmHg)");

                TextView_1.setText("收缩压：");
                TextView_2.setText("舒张压：");
                TextView_3.setText("心率：");


                EditText_1.setHint("请输入收缩压");
                EditText_1.setText(heightPressure);
                EditText_1.setInputType(InputType.TYPE_CLASS_NUMBER);
                EditText_1.setMaxEms(3);

                EditText_2.setHint("请输入舒张压");
                EditText_2.setText(lowPressure);
                EditText_2.setInputType(InputType.TYPE_CLASS_NUMBER);
                EditText_2.setMaxEms(3);

                EditText_3.setHint("请输入心率");
                EditText_3.setText(heart2);
                EditText_3.setInputType(InputType.TYPE_CLASS_NUMBER);
                EditText_3.setMaxEms(3);

                LinearLayout_2.setVisibility(View.VISIBLE);
                LinearLayout_3.setVisibility(View.VISIBLE);
                  /*
                    time = item.get("time").toString();

                    String report= item.get("report").toString();
                    String status= item.get("status").toString();

                    valueMsg=("血压:"+heightPressure+"/"+lowPressure +"mmHg,心率:"+heart+"次/分钟");
                    reportMsg = report;



                     */

                break;
            case BloodSugar:
                //血糖
                String gluc = item.get("gluc").toString();

                TextView_type.setText("血糖(mmol/L)");
                TextView_1.setText("血糖：");

                EditText_1.setHint("请输入血糖");
                EditText_1.setText(gluc);
                EditText_1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
                EditText_1.setMaxEms(5);
                  /*
                    time = item.get("time").toString();



                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("血糖:"+gluc+"mmol/L,"+status);
                    reportMsg = report;

                 */


                break;
            case Infusion:
                //输液



                break;
            case Weight:
                //体重
                String weight = item.get("weight").toString();
                TextView_type.setText("体重(KG)");
                TextView_1.setText("体重：");

                EditText_1.setHint("请输入体重");
                EditText_1.setText(weight);
                EditText_1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
                EditText_1.setMaxEms(5);
             /*

                    time = item.get("time").toString();

                    String bmi = item.get("bmi").toString();

                    String report= item.get("report").toString();


                    valueMsg=("体重:"+weight+" KG，BMI:"+bmi);
                    reportMsg = report;*/




                break;
            case Waist:
                //腰围
                String waist = item.get("waist").toString();
                TextView_type.setText("腰围(CM)");
                TextView_1.setText("腰围：");
                EditText_1.setHint("请输入腰围");
                EditText_1.setText(waist);
                EditText_1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
                EditText_1.setMaxEms(2);
                  /*
                    time = item.get("time").toString();



                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("腰围:"+waist+"CM,"+status);
                    reportMsg = report;*/





                break;
            case Smart:
                //手环


                break;
            case Uric:
                //尿酸
                String ua = item.get("ua").toString();
                TextView_type.setText("尿酸(mmol/L)");
                EditText_1.setHint("请输入尿酸");
                EditText_1.setText(ua);
                EditText_1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
                EditText_1.setMaxEms(6);
                  /*
                    time = item.get("time").toString();



                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("尿酸:"+ua+"mmol/L,"+status);
                    reportMsg = report;*/





                break;

            case Cholesterol:
                //总胆固醇
                String cho = item.get("cho").toString();
                TextView_type.setText("总胆固醇(mmol/L)");
                EditText_1.setHint("请输入总胆固醇");
                EditText_1.setText(cho);
                EditText_1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
                EditText_1.setMaxEms(5);
                  /*
                    time = item.get("time").toString();



                    String report= item.get("report").toString();
                    String status= item.get("status").toString();


                    valueMsg=("总胆固醇:"+cho+"mmol/L,"+status);
                    reportMsg = report;*/



                break;
        }



    }


}