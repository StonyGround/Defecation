package com.kjy.care.activity;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.kjy.care.R;
import com.kjy.care.service.MessageEvent;
import com.kjy.care.util.AppUtil;
import com.kjy.care.util.FastClickUtil;
import com.kjy.care.util.MyDataUtil;
import com.kjy.care.util.MySqlUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import es.dmoral.toasty.Toasty;

public class SetActivity extends BaseActivity  implements View.OnClickListener {



    public MySqlUtil mysql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        AppUtil.closeBar(this);
        mysql = new MySqlUtil(this);


        initView();



    }




    RelativeLayout  RelativeLayout_app;


    private void initView(){
        RelativeLayout_app= findViewById(R.id.RelativeLayout_app);
        //界面旋转180度
       // RelativeLayout_app.setRotation(180);




    }


    public static void luncher(){

        Intent intent =new Intent();
        intent.setClass(BaseApp.getAppContext(), SetActivity.class);
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
            case R.id.RelativeLayout_set:

                AppUtil.toSystemSet(this,"com.android.settings.Settings");

                break;
            case R.id.RelativeLayout_clean:
                //计数清零

                MyDataUtil.updateClean(mysql,BaseApp.User.getId());

                Toasty.success(this,"计数已清零").show();
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


























}