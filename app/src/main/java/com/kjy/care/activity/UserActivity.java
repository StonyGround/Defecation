package com.kjy.care.activity;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.kjy.care.R;
import com.kjy.care.service.BlueChangedReceiver;
import com.kjy.care.service.MessageEvent;
import com.kjy.care.util.AppUtil;
import com.kjy.care.util.ButtonChangeUtil;
import com.kjy.care.util.ComCmdUtil;
import com.kjy.care.util.FastClickUtil;
import com.kjy.care.util.MyDataUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class UserActivity extends BaseActivity  implements View.OnClickListener {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        AppUtil.closeBar(this);



        initView();



    }




    RelativeLayout  RelativeLayout_app;

    ImageView ImageView_stop;
    private void initView(){
        RelativeLayout_app= findViewById(R.id.RelativeLayout_app);
        //界面旋转180度
      //  RelativeLayout_app.setRotation(180);

        ImageView_stop=findViewById(R.id.ImageView_stop);





    }


    public static void luncher(){

        Intent intent =new Intent();
        intent.setClass(BaseApp.getAppContext(),UserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getAppContext().startActivity(intent);
    }










    @Override
    public void onClick(View view) {
        if(FastClickUtil.isFastClick()){return;}
        switch (view.getId()){
            case R.id.ImageView_stop:

                ButtonChangeUtil.changeStop(ImageView_stop,this,true,mysql);
                break;
            case R.id.RelativeLayout_back:

                finish();
                break;
            case R.id.LinearLayout_userinfo:
            case R.id.ImageView_img:
                UserInfoActivity.luncher();
                break;
            case R.id.RelativeLayout_set:
                SetActivity.luncher();
                break;

            case R.id.LinearLayout_help:
                HelpActivity.luncher();
                break;

            case R.id.LinearLayout_device:
                DeviceActivity.luncher();
                break;
            case R.id.LinearLayout_add:
                //工作记录

                WorkActivity.luncher();

                break;

            case R.id.LinearLayout_health:
                HealthActivity.luncher();
                break;

            case R.id.LinearLayout_about:
                AppUtil.toWeb("http://hos.kjyun.net/android/getDeptEduInfo?id=13","关于");
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