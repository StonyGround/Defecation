package com.kjy.care.activity;



import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kjy.care.R;
import com.kjy.care.activity.fragment.ControlAutoFragment;
import com.kjy.care.activity.fragment.ControlUserFragment;
import com.kjy.care.bean.User;

import com.kjy.care.service.BlueChangedReceiver;
import com.kjy.care.service.MessageEvent;
import com.kjy.care.service.NetworkConnectChangedReceiver;
import com.kjy.care.util.AppUtil;
import com.kjy.care.util.ButtonChangeUtil;
import com.kjy.care.util.ComCmdUtil;
import com.kjy.care.util.CrashHandler;
import com.kjy.care.util.DateUtil;
import com.kjy.care.util.FastClickUtil;
import com.kjy.care.util.MediaPlayerUtil;
import com.kjy.care.util.MyDataUtil;
import com.kjy.care.util.SPUtil;
import com.tapadoo.alerter.Alerter;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.SerialportUtil;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import es.dmoral.toasty.Toasty;

public class MainActivity extends BaseActivity  implements View.OnClickListener {


    public static void luncher(){

        Intent intent =new Intent();
        intent.setClass(BaseApp.getAppContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getAppContext().startActivity(intent);
    }



    private String T_USER="";//??????ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppUtil.closeBar(this);

        BaseApp.User    = new User("10000001", "1990-01-25", "170", "???");

        T_USER=BaseApp.User.getId();


        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        requestPermissions();



        startService();


        initAnimation();
        initView();

        timer.schedule(task,0,1000);


        loadCount();
        SerialportUtil.init();




    }


    final  int permissionCode = 1000;


    private void  requestPermissions(){


        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> permissionsList = new ArrayList<>();
            String[] permissions = {
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.READ_PHONE_STATE
            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // ?????????????????????????????????.
                }
            }

            if (!permissionsList.isEmpty()) {
                String[] strings = new String[permissionsList.size()];
                requestPermissions(permissionsList.toArray(strings), permissionCode);
            }else{
                //?????????????????????
                CrashHandler crashHandler = CrashHandler.getInstance();
                crashHandler.init(getApplicationContext());
            }
        }




    }

       Handler mainHandler = new Handler(){

           @Override
           public void handleMessage(Message msg) {
               switch (msg.what){
                   case 1:ComCmdUtil.sendAutoCmd();break;
                   case 2:ComCmdUtil.sendWaterPressCmd(0);break;
                   case 3:ComCmdUtil.sendWaterTempCmd(0);break;
                   case 4:ComCmdUtil.sendWarmAirCmd(0);break;
                   case 5:ComCmdUtil.sendTimerDryCmd(0);break;

                   default:break;
               }
           }
       };


    //??????????????????
    private void  sendDefaultLev(){
        // ComCmdUtil.sendGetInfo(mysql);

        mainHandler.sendEmptyMessageDelayed(1,50);
        mainHandler.sendEmptyMessageDelayed(2,100);
        mainHandler.sendEmptyMessageDelayed(3,150);
        mainHandler.sendEmptyMessageDelayed(4,200);
        mainHandler.sendEmptyMessageDelayed(5,250);

         // ?????? ??????
        // ComCmdUtil.sendAutoCmd();
        // ?????? ???
        // ComCmdUtil.sendWaterPressCmd(0);

       // ?????? ???
        // ComCmdUtil.sendWaterTempCmd(0);

       //  ?????? ???
        // ComCmdUtil.sendWarmAirCmd(0);
       // ???????????? ???
        // ComCmdUtil.sendTimerDryCmd(0);

        MyDataUtil.addRecordList(mysql,"??????????????????", BaseApp.User.getId());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == permissionCode){
            requestPermissions();
        }


    }

    BlueChangedReceiver blueChangedReceiver=new BlueChangedReceiver();
    NetworkConnectChangedReceiver networkConnectChangedReceiver=new NetworkConnectChangedReceiver();

    private void startService(){
        //Android8.0??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

        IntentFilter filterWifi = new IntentFilter();
        filterWifi.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filterWifi.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filterWifi.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filterWifi.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        filterWifi.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(networkConnectChangedReceiver, filterWifi);






        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);


        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        // ???????????????????????????????????????????????????
        registerReceiver( blueChangedReceiver, intentFilter);


    }

    ImageView ImageView_water_press,   ImageView_water_temper,   ImageView_water_air,    ImageView_timer_dry, ImageView_water_clean,ImageView_water_dirty;
    ImageView ImageView_stop,ImageView_auto,ImageView_control;
    LinearLayout LinearLayout_left_auto, LinearLayout_left_user;

    ImageView ImageView_defecate,ImageView_pee,ImageView_wabsorption,ImageView_dry,ImageView_run;


    RelativeLayout RelativeLayout_control,RelativeLayout_tips,RelativeLayout_tips_none,RelativeLayout_app;

    TextView TextView_debug,TextView_tips,TextView_run,TextView_pee_num,TextView_defeate_num;

    LinearLayout LinearLayout_tip_1,LinearLayout_tip_2,LinearLayout_tip_3,LinearLayout_tip_4,LinearLayout_tip_5,LinearLayout_tip_6,LinearLayout_tip_7;

    private void initView(){
        RelativeLayout_app= findViewById(R.id.RelativeLayout_app);
        //????????????180???
      // RelativeLayout_app.setRotation(180);



        RelativeLayout_tips= findViewById(R.id.RelativeLayout_tips);
        RelativeLayout_tips_none= findViewById(R.id.RelativeLayout_tips_none);

        ImageView_water_press = findViewById(R.id.ImageView_water_press);
        ImageView_water_temper = findViewById(R.id.ImageView_water_temper);
        ImageView_water_air = findViewById(R.id.ImageView_water_air);
        ImageView_timer_dry = findViewById(R.id.ImageView_timer_dry);
        ImageView_water_clean = findViewById(R.id.ImageView_water_clean);
        ImageView_water_dirty = findViewById(R.id.ImageView_water_dirty);


        LinearLayout_left_auto= findViewById(R.id.LinearLayout_left_auto);
        LinearLayout_left_user= findViewById(R.id.LinearLayout_left_user);


        ImageView_stop= findViewById(R.id.ImageView_stop);
        ImageView_auto= findViewById(R.id.ImageView_auto);
        ImageView_control= findViewById(R.id.ImageView_control);


        ImageView_defecate= findViewById(R.id.ImageView_defecate);
        ImageView_pee= findViewById(R.id.ImageView_pee);
        ImageView_wabsorption= findViewById(R.id.ImageView_wabsorption);
        ImageView_dry= findViewById(R.id.ImageView_dry);
        ImageView_run= findViewById(R.id.ImageView_run);

        RelativeLayout_control= findViewById(R.id.RelativeLayout_control);


        TextView_pee_num= findViewById(R.id.TextView_pee_num);
        TextView_defeate_num= findViewById(R.id.TextView_defeate_num);

        TextView_run= findViewById(R.id.TextView_run);
        TextView_tips= findViewById(R.id.TextView_tips);
        TextView_debug= findViewById(R.id.TextView_debug);
        TextView_debug.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                 runOnUiThread(new Runnable(){
                     @Override
                     public void run() {
                         TextView_debug.setText("debug??????");
                     }
                 });
                return false;
            }
        });
        TextView_debug.setVisibility(View.GONE);


        LinearLayout_tip_1  = findViewById(R.id.LinearLayout_tip_1);
        LinearLayout_tip_2  = findViewById(R.id.LinearLayout_tip_2);
        LinearLayout_tip_3  = findViewById(R.id.LinearLayout_tip_3);
        LinearLayout_tip_4  = findViewById(R.id.LinearLayout_tip_4);
        LinearLayout_tip_5  = findViewById(R.id.LinearLayout_tip_5);
        LinearLayout_tip_6  = findViewById(R.id.LinearLayout_tip_6);
        LinearLayout_tip_7  = findViewById(R.id.LinearLayout_tip_7);


    }


    /**
     * ??????????????????,??????????????????
     */
    private  void  changeAuto(){


        if((ImageView_auto.getDrawable().getConstantState())== this.getResources().getDrawable(R.mipmap.auto_0).getConstantState()) {
            //??????->??????

            changeAutoUI();
            loadNowLevIco();//??????????????????
            ComCmdUtil.sendAutoCmd();
            MyDataUtil.addRecordList(mysql,"??????->?????? ????????????",T_USER);

        }else{
            //  ??????->??????
            changeHandUI();
            ComCmdUtil.sendHandCmd();

            MyDataUtil.addRecordList(mysql,"??????->?????? ????????????",T_USER);
        }

    }


    /**
     * ???????????????????????????  UserFragment ????????????
     */
    private void loadNowLevIco(){

        String WATER_PRESS_LEV=SPUtil.get(this,SPUtil.WATER_PRESS,"0");//?????? ???
        String WATER_TEMPER_LEV=SPUtil.get(this,SPUtil.WATER_TEMPER,"0");//??????  ???
        String WARM_AIR_LEV=SPUtil.get(this,SPUtil.WARM_AIR,"0");//??????
        String TIMER_DRY_LEV=SPUtil.get(this,SPUtil.TIMER_DRY,"0");//????????????
        String MASSAGE_LEV=SPUtil.get(this,SPUtil.MASSAGE,"0");//??????
        String AUTO_LEV=SPUtil.get(this,SPUtil.AUTO,"1");//??????


        ButtonChangeUtil.changeWaterPressIco(ImageView_water_press,this,Integer.parseInt(WATER_PRESS_LEV));
        ButtonChangeUtil.changeWaterTemperIco(ImageView_water_temper,this,Integer.parseInt(WATER_TEMPER_LEV));
        ButtonChangeUtil.changeWarmAirIco(ImageView_water_air,this,Integer.parseInt(WARM_AIR_LEV));
        ButtonChangeUtil.changeTimerDryIco(ImageView_timer_dry,this,Integer.parseInt(TIMER_DRY_LEV));



        if(AUTO_LEV.equals("1")){

            changeAutoUI();
        }else{
            changeHandUI();

        }




    }














    private void changeAutoUI(){
        ImageView_auto.setImageDrawable(this.getResources().getDrawable(R.mipmap.auto_1));
        LinearLayout_left_auto.setVisibility(View.VISIBLE);
        LinearLayout_left_user.setVisibility(View.GONE);
        SPUtil.set(this,SPUtil.AUTO,"1");
    }

    private void changeHandUI(){
        ImageView_auto.setImageDrawable(this.getResources().getDrawable(R.mipmap.auto_0));
        LinearLayout_left_user.setVisibility(View.VISIBLE);
        LinearLayout_left_auto.setVisibility(View.GONE);
        SPUtil.set(this,SPUtil.AUTO,"0");
    }




    @Override
    public void onClick(View view) {
        if(FastClickUtil.isFastClick()){return;}
        switch (view.getId()){

            case R.id.LinearLayout_water_press:
                //??????
                ButtonChangeUtil.changeWaterPress(ImageView_water_press,this,mysql);


                break;

            case R.id.LinearLayout_water_temper:
                //??????
                ButtonChangeUtil.changeWaterTemper(ImageView_water_temper,this,mysql);
                break;
            case R.id.LinearLayout_warm_air:
                //??????
                ButtonChangeUtil.changeWarmAir(ImageView_water_air,this,mysql);
                break;
            case R.id.LinearLayout_timer_dry:
                //????????????
                ButtonChangeUtil.changeTimerDry(ImageView_timer_dry,this,mysql);
                break;


            case R.id.LinearLayout_defecate:
                //??????
                ButtonChangeUtil.changeDefecate(ImageView_defecate,this,true);


                break;

            case R.id.LinearLayout_pee:
                //??????
                ButtonChangeUtil.changePee(ImageView_pee,this,true);
                break;
            case R.id.LinearLayout_absorption:
                //??????
                ButtonChangeUtil.changeWabsorption(ImageView_wabsorption,this,true,mysql);
                break;
            case R.id.LinearLayout_dry:
                //??????
                ButtonChangeUtil.changeDry(ImageView_dry,this,true,mysql);
                break;







            case R.id.ImageView_auto:
                //??????
                changeAuto();



       /* byte[] values=new byte[]{

                (byte) 0xff,
                04,
                (byte) 0xa1,
                00,
                (byte) 0xa5

        };

                SerialportUtil.write(values);*/


                break;

            case R.id.ImageView_stop:
                //??????
                ButtonChangeUtil.changeStop(ImageView_stop,this,true,mysql);
                break;

            case R.id.ImageView_control:
                //??????
                if((hiddenAnimation!=null && !hiddenAnimation.hasEnded())){return;}

                   showControlView();
                break;
            case R.id.RelativeLayout_control:
                if((showAnimation!=null && !showAnimation.hasEnded())){return;}

                hiddenControlView();

                break;
            case R.id.RelativeLayout_userinfo:
                UserActivity.luncher();
                break;
        }


       // AlertUtil.show("????????????");




    }
    Animation showAnimation;
    Animation hiddenAnimation;

    private void initAnimation(){








    }


    private void hiddenControlView(){



        getSupportFragmentManager().popBackStack();
        hiddenAnimation= AnimationUtils.loadAnimation(this,R.anim.slide_out);
        hiddenAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                RelativeLayout_control.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        RelativeLayout_control.setAnimation(hiddenAnimation);



    }


    private  void  showControlView(){

        RelativeLayout_control.setVisibility(View.VISIBLE);
        showAnimation= AnimationUtils.loadAnimation(this,R.anim.slide_in);
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        RelativeLayout_control.setAnimation(showAnimation);

        if((ImageView_auto.getDrawable().getConstantState())== this.getResources().getDrawable(R.mipmap.auto_0).getConstantState()) {
            //??????
            if(controlUserFragment == null) {
                controlUserFragment = new ControlUserFragment();
            }

            transaction=    getSupportFragmentManager()
                    .beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.slide_right_in,
                    R.anim.slide_right_out

            ).replace(R.id.FrameLayout_control,controlUserFragment)
                    .addToBackStack(null)
                    .commit();




        }else{
            //  ??????
            if(controlAutoFragment == null) {
                controlAutoFragment = new ControlAutoFragment();
            }

            transaction=    getSupportFragmentManager()
                    .beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.slide_right_in,
                    R.anim.slide_right_out

            ).replace(R.id.FrameLayout_control,controlAutoFragment)
                    .addToBackStack(null)
                    .commit();

        }



    }



    FragmentTransaction transaction;
    ControlAutoFragment  controlAutoFragment;

    ControlUserFragment controlUserFragment;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SerialportUtil.close();
        try {
            task.cancel();
            timer.cancel();
        }catch (Exception e){

        }


        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        unregisterReceiver(networkConnectChangedReceiver);
        unregisterReceiver(blueChangedReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent obj){

        Log.e("??????",obj.getName()+"===");
        switch (obj) {
            case Com_data:

                 TextView_debug.append("\r\n"+"?????????"+obj.getData().toString());


                getCmdData(obj.getData().toString().toUpperCase());

                break;
            case Send_data:

                TextView_debug.append("\r\n"+"?????????"+obj.getData().toString());


                break;
            case Com_open_ok:


                 TextView_debug.append("\r\n"+"??????????????????");
                 Toasty.success(this,"??????????????????").show();



                //????????? ??????????????????????????????????????????
                sendDefaultLev();



                break;
            case Com_open_error:


                TextView_debug.append("\r\n"+"??????????????????");
                Toasty.error(this,"??????????????????").show();;
                break;
            default:break;

        }



      }


    /**
     * ?????? ?????? ?????? ?????? 3
     */
    private  void countDry(){
        MyDataUtil.insertDataList(mysql,3,T_USER);
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                ImageView_run.setImageResource(R.mipmap.run_dry_3x);

            }
        });

        ButtonChangeUtil.runAnimation(ImageView_run,TextView_run,this);


        findCountDry();

    }


    /**
     * ?????????????????? ?????? 3
     */
    private  void findCountDry(){

        final int n2=  MyDataUtil.countDataList(mysql,3,T_USER, DateUtil.getStringDateShort());
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                TextView_run.setText("???????????? "+ n2 +" ???");
            }
        });



    }





    /**
     * ?????? ?????? ?????? ?????? 1
     */
    private  void countPee(){
        MyDataUtil.insertDataList(mysql,1,T_USER);
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                ImageView_run.setImageResource(R.mipmap.pee_3x);

            }
        });

        ButtonChangeUtil.runAnimation(ImageView_run,TextView_run,this);


        findCountPee();
        loadCount();
    }


    /**
     * ?????????????????? ?????? 1
     */
    private  void findCountPee(){

        final int n2=  MyDataUtil.countDataList(mysql,1,T_USER, DateUtil.getStringDateShort());
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                TextView_run.setText("???????????? "+ n2 +" ???");
            }
        });



    }

    /**
     * ?????? ?????? ?????? ?????? 2
     */
    private  void countDefecate(){
        MyDataUtil.insertDataList(mysql,2,T_USER);

        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                ImageView_run.setImageResource(R.mipmap.defecate_3x);

            }
        });
        ButtonChangeUtil.runAnimation(ImageView_run,TextView_run,this);

        findCountDefecate();
        loadCount();
    }


    /**
     * ?????????????????? ?????? 2
     */
    private  void findCountDefecate(){
        final int n=  MyDataUtil.countDataList(mysql,2,T_USER, DateUtil.getStringDateShort());
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                TextView_run.setText("???????????? "+ n+" ???");
            }
        });
    }


    private void loadCount(){

        final int n=  MyDataUtil.countDataList(mysql,2,T_USER, DateUtil.getStringDateShort());
        final int n2=  MyDataUtil.countDataList(mysql,1,T_USER, DateUtil.getStringDateShort());
        runOnUiThread(new Runnable(){
            @Override
            public void run() {

                TextView_defeate_num.setText(n+"");
                TextView_pee_num.setText(n2+"");
            }
        });



    }


    /**
     * ????????????
     * @param hex
     */
    private void getCmdData(String hex){

        if(!hex.startsWith("FF")){
            return;
        }


          if(hex.startsWith("FF04") && hex.substring(6,8).equals("01")){
              //????????????
              String cmd = hex.substring(4,6);

              switch (cmd){

                  case "B2":
                      //????????????????????????
                      MyDataUtil.addRecordList(mysql,"????????????",T_USER);
                      break;
                  case "B4":
                      //????????????????????????


                    //  countDefecate();

                      break;
                  case "B5":
                      //????????????????????????


                    //  countPee();



                      break;
                  case "B7":
                      //????????????????????????
                      countDry();
                      break;

              }



          }



        if(hex.startsWith("FF04E8")){
            //????????????
            String cmd = hex.substring(8,10);

            switch (cmd){
                case "01":
                    //????????????
                    changeHandUI();
                    break;
                case "02":
                    //????????????
                    changeAutoUI();
                    break;
                case "03":
                    //????????????
                    ButtonChangeUtil.changeDefecate(ImageView_defecate,this,false);

                    countDefecate();

                    break;
                case "04":
                    //????????????
                    ButtonChangeUtil.changePee(ImageView_pee,this,false);
                    countPee();

                    break;
                case "05":
                    //?????????
                    ButtonChangeUtil.changeDry(ImageView_dry,this,false,mysql);
                    break;
                case "06":
                    //?????????
                    ButtonChangeUtil.changeStop(ImageView_stop,this,false,mysql);
                    break;

            }



        }

          if(hex.startsWith("FF04E5")){
               //  FF04E50D  00  00 00 00 00 00 00 00 00 00 00 00 00 00 sum
               //FF04E5 0D(????????????)  00  00 00 00 00 00 00 00 00 00 00 00 00 00  sum(?????????)

              //???????????????
              if(!hex.substring(8,10).equals("00")){
                  //1.????????? ??????

                  //????????????
                //  ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"???????????????",this);

                  LinearLayout_tip_1.setVisibility(View.VISIBLE);
                  //????????? ???????????????
                  MediaPlayerUtil.play(R.raw.l1);
              }else{
                  LinearLayout_tip_1.setVisibility(View.GONE);
              }
              if(!hex.substring(10,12).equals("00")){
                  //2.????????? ??????
                  //????????????
                 // ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"???????????????",this);


                  LinearLayout_tip_2.setVisibility(View.VISIBLE);
                  //?????????????????????????????????
                  MediaPlayerUtil.play(R.raw.l2);

              }else{
                  LinearLayout_tip_2.setVisibility(View.GONE);
              }
              if(hex.substring(12,14).equals("00")){
                  //3.???????????????
                  //????????????
                 // ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"???????????????",this);
                  LinearLayout_tip_3.setVisibility(View.VISIBLE);
                  MyDataUtil.addRecordList(mysql,"?????????????????????????????????????????????????????????????????????????????????",T_USER);
                  MediaPlayerUtil.play(R.raw.l3);

              }else{

                  LinearLayout_tip_3.setVisibility(View.GONE);
              }
              if(hex.substring(14,16).equals("00")){
                  //4.??????????????????
                  //????????????
               //   ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"????????????",this);
                  LinearLayout_tip_4.setVisibility(View.VISIBLE);
                  MyDataUtil.addRecordList(mysql,"???????????????????????????????????????????????????????????????",T_USER);
                  MediaPlayerUtil.play(R.raw.l4);
              }else{
                  LinearLayout_tip_4.setVisibility(View.GONE);
              }
              if(hex.substring(16,18).equals("00")){
                  //5.?????????????????????
                  //????????????
                 // ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"?????????????????????",this);
                  LinearLayout_tip_5.setVisibility(View.VISIBLE);
                  MyDataUtil.addRecordList(mysql,"?????????????????????????????????????????????",T_USER);
                  MediaPlayerUtil.play(R.raw.l5);
              }else{

                  LinearLayout_tip_5.setVisibility(View.GONE);
              }
              if(hex.substring(18,20).equals("00")){
                  //6.?????????

                  LinearLayout_tip_7.setVisibility(View.VISIBLE);
                  MyDataUtil.addRecordList(mysql,"????????????????????????",T_USER);
                  MediaPlayerUtil.play(R.raw.l6);

              }else{

                  LinearLayout_tip_7.setVisibility(View.GONE);
              }
              if(!hex.substring(20,22).equals("00")){
                  //7.????????? ??????+1

                //  countDefecate();
              }
              if(!hex.substring(22,24).equals("00")){
                  //8.????????? ??????+1
                //  countPee();
              }


              if(hex.substring(26,28).equals("00")){
                  //10.????????????,?????????
                  ButtonChangeUtil.changeWaterDirtyIco(ImageView_water_dirty,this,2);
                  ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"??????????????????????????????????????????",this);
                  MyDataUtil.addRecordList(mysql,"??????????????????????????????????????????",T_USER);
                  MediaPlayerUtil.play(R.raw.l10);
              }else{
                  //????????? ??????????????? ??????
                  if(hex.substring(24,26).equals("00")){
                      //9.????????????,?????????????????????
                      ButtonChangeUtil.changeWaterDirtyIco(ImageView_water_dirty,this,2);
                      // ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"????????????????????????",this);
                      MyDataUtil.addRecordList(mysql,"??????????????????????????????????????????",T_USER);
                      MediaPlayerUtil.play(R.raw.l9);
                  }else{
                      ButtonChangeUtil.changeWaterDirtyIco(ImageView_water_dirty,this,0);
                  }


              }

             // Log.e("??????",hex+"======??????======"+hex.substring(28,30));



              String tempWater_01="00";//
              String tempWater_02="00";//
              String tempWater_03="00";//

              if(!hex.substring(28,30).equals("00")){

                  tempWater_01="01";

                 // Log.e("??????","======??????======");
                  //11. ????????????

              }
              if(hex.substring(30,32).equals("00")){
                  //12.????????????
                // ButtonChangeUtil.changeWaterClean(ImageView_water_clean,1,this);
              }else{
                  tempWater_02="01";

                  //12.????????????
                 // ButtonChangeUtil.changeWaterClean(ImageView_water_clean,0,this);
              }
              if(hex.substring(32,34).equals("00")){
                  //13. ?????????????????????  ?????????
              }else{

                  tempWater_03="01";
              }


              if(tempWater_03.equals("00")){
                  //??????
                  ButtonChangeUtil.changeWaterClean(ImageView_water_clean,3,this);
                  ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"??????????????????????????????????????????",this);
                  MyDataUtil.addRecordList(mysql,"??????????????????????????????????????????",T_USER);
                  MediaPlayerUtil.play(R.raw.l13_2);

              }else{
                  //??????


                  if(tempWater_02.equals("00")){
                      //?????????
                      ButtonChangeUtil.changeWaterClean(ImageView_water_clean,2,this);
                  }else{
                      if(tempWater_01.equals("00")){
                          //?????????
                          ButtonChangeUtil.changeWaterClean(ImageView_water_clean,1,this);

                          ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"????????????????????????",this);
                          MyDataUtil.addRecordList(mysql,"????????????????????????",T_USER);
                          MediaPlayerUtil.play(R.raw.l13_1);

                      }else{
                          //??????
                          ButtonChangeUtil.changeWaterClean(ImageView_water_clean,0,this);
                          ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"???????????????????????????????????????????????????",this);
                          MyDataUtil.addRecordList(mysql,"???????????????????????????????????????????????????",T_USER);
                          MediaPlayerUtil.play(R.raw.l13);
                      }

                  }

              }



              if(hex.substring(34,36).equals("00")){
                  //14.?????????
                  //????????????
                //  ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"?????????",this);
                  LinearLayout_tip_6.setVisibility(View.VISIBLE);
                  MyDataUtil.addRecordList(mysql,"??????????????????",T_USER);
                  MediaPlayerUtil.play(R.raw.l14);
              }else{
                  LinearLayout_tip_6.setVisibility(View.GONE);
              }





          }





        if(hex.startsWith("FF04D1")){
            //??????????????????????????????
            // 0X01?????????
            //0X02:  ??????
            //????????????
            String cmd = hex.substring(8,10);

            switch (cmd){
                case "01":

                    //??????
                   /* runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            ImageView_run.setImageResource(R.mipmap.defecate_3x);
                        }
                    });*/

                    countDefecate();
                    MyDataUtil.addRecordList(mysql,"??????????????????",T_USER);
                    break;
                case "02":
                    //??????
                    ButtonChangeUtil.resetRunAnimation(ImageView_run,TextView_run,this);
                    MyDataUtil.addRecordList(mysql,"????????????????????????",T_USER);
                    MediaPlayerUtil.play(R.raw.finish);
                    Alerter.create(this)
                            .setTitle("????????????")
                            .setText("????????????????????????")
                            .setDuration(10*1000)
                            .setBackgroundColor(R.color.orange)
                            .show();
                    break;


            }



        }



        if(hex.startsWith("FF04D2")){
            //??????????????????????????????
            // 0X01?????????
            //0X02:  ??????
            //????????????
            String cmd = hex.substring(8,10);

            switch (cmd){
                case "01":
                    //??????
                    //?????? ??????+1

                    /*runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            ImageView_run.setImageResource(R.mipmap.pee_3x);
                        }
                    });*/
                  //  findCountPee();
                    countPee();
                    MyDataUtil.addRecordList(mysql,"??????????????????",T_USER);
                    break;
                case "02":
                    //??????
                    ButtonChangeUtil.resetRunAnimation(ImageView_run,TextView_run,this);
                    MyDataUtil.addRecordList(mysql,"????????????????????????",T_USER);
                    MediaPlayerUtil.play(R.raw.finish);

                    Alerter.create(this)
                            .setTitle("????????????")
                            .setText("????????????????????????")
                            .setDuration(10*1000)
                            .setBackgroundColor(R.color.orange)
                            .show();
                    break;


            }



        }

        if(hex.startsWith("FF04D3")){
            //??????????????????????????????
            // 0X01?????????
            //0X02:  ??????
            //????????????
            String cmd = hex.substring(8,10);

            switch (cmd){
                case "01":
                    //??????
                   /*  runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            ImageView_run.setImageResource(R.mipmap.defecate_3x);
                        }
                    });
                   findCountDefecate();*/
                    countDefecate();





                    MyDataUtil.addRecordList(mysql,"??????????????????",T_USER);
                    break;
                case "02":
                    //??????
                    ButtonChangeUtil.resetRunAnimation(ImageView_run,TextView_run,this);
                    MyDataUtil.addRecordList(mysql,"????????????????????????",T_USER);
                    MediaPlayerUtil.play(R.raw.finish);
                    Alerter.create(this)
                            .setTitle("????????????")
                            .setText("????????????????????????")
                            .setDuration(10*1000)
                            .setBackgroundColor(R.color.orange)
                            .show();
                    break;


            }



        }



        if(hex.startsWith("FF04D4")){
            //??????????????????????????????
            // 0X01?????????
            //0X02:  ??????
            //????????????
            String cmd = hex.substring(8,10);

            switch (cmd){
                case "01":
                    //??????
                   /* runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            ImageView_run.setImageResource(R.mipmap.pee_3x);
                        }
                    });
                    findCountPee();*/

                    countPee();

                    MyDataUtil.addRecordList(mysql,"??????????????????",T_USER);
                    break;
                case "02":
                    //??????
                    ButtonChangeUtil.resetRunAnimation(ImageView_run,TextView_run,this);
                    MyDataUtil.addRecordList(mysql,"????????????????????????",T_USER);
                    MediaPlayerUtil.play(R.raw.finish);
                    Alerter.create(this)
                            .setTitle("????????????")
                            .setText("????????????????????????")
                            .setDuration(10*1000)
                            .setBackgroundColor(R.color.orange)
                            .show();
                    break;


            }



        }

        if(hex.startsWith("FF04D5")){
            //????????????????????????
            // 0X01?????????
            //0X02:  ??????
            //????????????
            String cmd = hex.substring(8,10);

            switch (cmd){
                case "01":
                    //??????
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            ImageView_run.setImageResource(R.mipmap.run_dry_3x);
                        }
                    });
                    findCountDry();
                    break;
                case "02":
                    //??????
                    ButtonChangeUtil.resetRunAnimation(ImageView_run,TextView_run,this);

                    MyDataUtil.addRecordList(mysql,"?????? ??????",T_USER);
                    MediaPlayerUtil.play(R.raw.finish);
                    Alerter.create(this)
                            .setTitle("????????????")
                            .setText("?????? ??????")
                            .setDuration(10*1000)
                            .setBackgroundColor(R.color.orange)
                            .show();
                    break;


            }



        }





        if(hex.startsWith("FF04A4")){
            //?????????????????????app????????????

            ComCmdUtil.sendSetInfo(this,mysql);

        }




        if(hex.startsWith("FF04A7")){
            //APP???????????????????????????
            String   value = hex.substring(8,26);


            String WATER_PRESS_LEV= value.substring(0,2);//?????? ???
            String WATER_TEMPER_LEV=value.substring(2,6);//??????  ???
            String WARM_AIR_LEV=value.substring(6,8);//??????
            String TIMER_DRY_LEV=value.substring(8,12);//????????????
            String MASSAGE_LEV=value.substring(12,14);//??????
            String AUTO_LEV=value.substring(14,16);//??????
            String OTHER_LEV=value.substring(16,18);//??????

            if(WATER_PRESS_LEV.equals("00")){ WATER_PRESS_LEV="0"; }else{ WATER_PRESS_LEV="1"; }
            if(WARM_AIR_LEV.equals("00")){ WARM_AIR_LEV="0"; }else{ WARM_AIR_LEV="1"; }
            if(MASSAGE_LEV.equals("00")){ MASSAGE_LEV="0"; }else{ MASSAGE_LEV="1"; }
            if(AUTO_LEV.equals("01")){ AUTO_LEV="1"; }else{ AUTO_LEV="2"; }
          //  if(OTHER_LEV.equals("00")){ OTHER_LEV="0"; }else{ OTHER_LEV="1"; }


            String WATER_TEMPER_VALUE="0";

            if(WATER_TEMPER_LEV.equals("0000")){WATER_TEMPER_VALUE="0";}
            if(WATER_TEMPER_LEV.equals("012C")){WATER_TEMPER_VALUE="1";}
            if(WATER_TEMPER_LEV.equals("014A")){WATER_TEMPER_VALUE="2";}
            if(WATER_TEMPER_LEV.equals("017C")){WATER_TEMPER_VALUE="3";}




            /////////////????????????  ????????????
            String TIMER_DRY_VALUE = "0000"; //???????????????
            if(TIMER_DRY_LEV.equals("0000")){TIMER_DRY_VALUE="0";}
            if(TIMER_DRY_LEV.equals("003C")){TIMER_DRY_VALUE="1";}
            if(TIMER_DRY_LEV.equals("0078")){TIMER_DRY_VALUE="2";}
            if(TIMER_DRY_LEV.equals("00B4")){TIMER_DRY_VALUE="3";}
            if(TIMER_DRY_LEV.equals("00F0")){TIMER_DRY_VALUE="4";}




            SPUtil.set(this,SPUtil.WATER_PRESS,WATER_PRESS_LEV);//?????? ???
            SPUtil.set(this,SPUtil.WATER_TEMPER,WATER_TEMPER_VALUE);//??????  ???
            SPUtil.set(this,SPUtil.WARM_AIR,WARM_AIR_LEV);//??????
            SPUtil.set(this,SPUtil.TIMER_DRY,TIMER_DRY_VALUE);//????????????
            SPUtil.set(this,SPUtil.MASSAGE,MASSAGE_LEV);//??????
            SPUtil.set(this,SPUtil.AUTO,AUTO_LEV);//??????


            loadNowLevIco();


        }










      }




    Timer timer=new Timer();

    long count = 0;

    TimerTask task =new TimerTask() {
        @Override
        public void run() {
            count++;


            if(count >= 24*3600){
                //????????? 0 ??????

                count = 0;
            }

            if(count==20){

                EventBus.getDefault().post(MessageEvent.Second);

            }


            if(count%60==0){

                EventBus.getDefault().post(MessageEvent.Minite);

            }

            if(count%(3*60)==0){

                EventBus.getDefault().post(MessageEvent.CheckWifi);

            }


        //

        }
    };




}