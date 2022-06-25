package com.kjy.care.activity;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.kjy.care.R;
import com.kjy.care.service.MessageEvent;
import com.kjy.care.util.AppUtil;
import com.kjy.care.util.FastClickUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HelpActivity extends BaseActivity  implements View.OnClickListener {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        AppUtil.closeBar(this);



        initView();



    }




    RelativeLayout  RelativeLayout_app;


    private void initView(){
        RelativeLayout_app= findViewById(R.id.RelativeLayout_app);
        //界面旋转180度
        //RelativeLayout_app.setRotation(180);





    }


    public static void luncher(){

        Intent intent =new Intent();
        intent.setClass(BaseApp.getAppContext(), HelpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getAppContext().startActivity(intent);
    }










    @Override
    public void onClick(View view) {
        if(FastClickUtil.isFastClick()){return;}
        String title="";
        switch (view.getId()){
            case R.id.LinearLayout_back:

                finish();
                return;

            case R.id.RelativeLayout_1: title="操作知道";break;
            case R.id.RelativeLayout_2: title="保养和维护";break;
            case R.id.RelativeLayout_3: title="使用说明";break;
            case R.id.RelativeLayout_4: title="瘫痪卧床病人如何护理？";break;
            case R.id.RelativeLayout_5: title="偏瘫老人的护理要点";break;
            case R.id.RelativeLayout_6: title="功能介绍";break;

                //
        }

        AppUtil.toWeb("http://hos.kjyun.net/android/getDeptEduInfo?id=12",title);

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