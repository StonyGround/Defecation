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
import com.kjy.care.R;
import com.kjy.care.adapter.DeviceAdapter;
import com.kjy.care.adapter.OnItemClickListener;
import com.kjy.care.adapter.StringAdapter;
import com.kjy.care.api.HttpUtil;
import com.kjy.care.bean.DeviceInfo;
import com.kjy.care.bean.RecordList;

import com.kjy.care.service.MessageEvent;
import com.kjy.care.util.AlertUtil;
import com.kjy.care.util.AppUtil;
import com.kjy.care.util.DateUtil;
import com.kjy.care.util.FastClickUtil;
import com.kjy.care.util.LinkedTreeMapUtil;
import com.kjy.care.util.MyDataUtil;
import com.kjy.care.util.MySqlUtil;
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

/**
 * 工作记录
 */
public class WorkActivity extends BaseActivity  implements View.OnClickListener {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        AppUtil.closeBar(this);



        initView();



    }



    RelativeLayout  RelativeLayout_app,RelativeLayout_loading;
    private TextView act_healthytest_datepicker_tv,TextView_day;




    private LinearLayout LinearLayout_datepicker;




    StringAdapter myAdapter;
    LinearLayoutManager linearLayoutManager;

    List<RecordList> listItem;


    private void initView(){
        RelativeLayout_app= findViewById(R.id.RelativeLayout_app);
        //界面旋转180度
       // RelativeLayout_app.setRotation(180);







        RelativeLayout_loading= findViewById(R.id.RelativeLayout_loading);

        LinearLayout_datepicker = findViewById(R.id.LinearLayout_datepicker);
        act_healthytest_datepicker_tv= findViewById(R.id.act_healthytest_datepicker_tv);





        act_healthytest_datepicker_tv= findViewById(R.id.act_healthytest_datepicker_tv);

        TextView_day= findViewById(R.id.TextView_day);




         listItem = new ArrayList<RecordList>();




        //  ArrayAdapter
        //创建一个simpleAdapter
        myAdapter = new StringAdapter(this);
        myAdapter.setList(listItem);

        RecyclerView listView = (RecyclerView) findViewById(R.id.ListView_work);
         linearLayoutManager= new LinearLayoutManager(this, GridLayoutManager.VERTICAL,false);
        listView.setLayoutManager(linearLayoutManager);

        listView.setAdapter(myAdapter);

        act_healthytest_datepicker_tv.setText(select_day);


        getDataInfo();

    }


    public static void luncher(){

        Intent intent =new Intent();
        intent.setClass(BaseApp.getAppContext(), WorkActivity.class);
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

            case R.id.ImageView_date:
                showDatePicker(view);
                break;
            case R.id.ImageView_right:

                String time = DateUtil.GetPreDate(DateUtil.strToDate(select_day), 1);

                getDataList(time);

                break;
            case R.id.ImageView_left:

                String time2 = DateUtil.GetPreDate(DateUtil.strToDate(select_day), -1);

                getDataList(time2);
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





    private Date showTime =new Date();
    private int maxWait=10;




    private String select_day=DateTimeUtil.getNowDate();






    /**
     * 通过日期 获取 1个月内 的 数据记录
     * @param time 2020-10-15
     */
    private void getDataList(String time){
        Date date = DateTimeUtil.getDate(time);
        String day =  DateTimeUtil.getDay(date);
        TextView_day.setText(day);
        act_healthytest_datepicker_tv.setText(time);
        select_day = time;
        getDataInfo();
    }



    /**
     * 获取历史记录
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


        listItem  =  MyDataUtil.listRecordList(mysql,select_day,patientNo);

        myAdapter.setList(listItem);
        myAdapter.notifyDataSetChanged();



    }





    /**
     * 显示日期选择
     * @param view
     */
    private void showDatePicker(View view) {


        DatePickerPopupWindow.show(this, view,select_day, new DatePickerPopupWindow.OnSelectListener() {
            @Override
            public void onSelect(int num, String dateStr) {

                //  Log.e("onSelect","DatePickerPopupWindow=>"+dateStr+"===="+num);
                getDataList(dateStr);


            }
        });



    }





    @Override
    protected void onResume() {
        super.onResume();




    }











}