package com.kjy.care.activity;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android_serialport_api.SerialportUtil;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import com.alibaba.fastjson.JSONObject;
import com.kjy.care.BuildConfig;
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
import com.tuya.smartai.iot_sdk.DPEvent;
import com.tuya.smartai.iot_sdk.IoTSDKManager;
import com.tuya.smartai.iot_sdk.UpgradeEventCallback;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import es.dmoral.toasty.Toasty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BaseActivity implements View.OnClickListener {

  public static final String TAG = "MainActivity";


  public static void luncher() {

    Intent intent = new Intent();
    intent.setClass(BaseApp.getAppContext(), MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    BaseApp.getAppContext().startActivity(intent);
  }


  private String T_USER = "";//用户ID

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    AppUtil.closeBar(this);

    BaseApp.User = new User("10000001", "1990-01-25", "170", "男");

    T_USER = BaseApp.User.getId();

    if (!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this);
    }

    requestPermissions();

    initSDK();

    startService();

    initAnimation();
    initView();

    timer.schedule(task, 0, 1000);

    loadCount();
    SerialportUtil.init();


  }


  final int permissionCode = 1000;


  private void requestPermissions() {

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
          // 进入到这里代表没有权限.
        }
      }

      if (!permissionsList.isEmpty()) {
        String[] strings = new String[permissionsList.size()];
        requestPermissions(permissionsList.toArray(strings), permissionCode);
      } else {
        //实例化异常捕获
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
      }
    }


  }

  IoTSDKManager ioTSDKManager;
  String mPid;
  String mUid;
  String mAk;
  ImageView qrCode;

  private void initSDK() {
    mPid = BuildConfig.PID;
    mUid = BuildConfig.UUID;
    mAk = BuildConfig.AUTHOR_KEY;

    com.tuya.smartai.iot_sdk.Log.init(this, "/sdcard/tuya_log/iot_demo/", 3);

    ioTSDKManager = new IoTSDKManager(this) {
      @Override
      protected boolean isOffline() {
        //实现自定义网络监测
        com.tuya.smartai.iot_sdk.Log.d(TAG, "isOffline: " + super.isOffline());
        return super.isOffline();
      }
    };

    //注意：这里的pid等配置读取自local.properties文件，不能直接使用。请填写你自己的配置！
    ioTSDKManager.initSDK("/sdcard/tuya_iot/", mPid
        , mUid, mAk, BuildConfig.VERSION_NAME, new IoTSDKManager.IoTCallback() {

          @Override
          public void onDpEvent(DPEvent event) {
            if (event != null) {
              Log.d(TAG, "收到 dp: " + event);

            }
          }

          @Override
          public void onReset() {

            getSharedPreferences("event_cache", MODE_PRIVATE).edit().clear().commit();

          }

          @Override
          public void onShorturl(String urlJson) {
            Log.d(TAG, "shorturl: " + urlJson);

            String url = (String) JSONObject.parseObject(urlJson).get("shortUrl");

            runOnUiThread(() -> {
              qrCode.setVisibility(View.VISIBLE);
              qrCode.setImageBitmap(CodeUtils.createImage(url, 400, 400, null));
            });
          }

          @Override
          public void onActive() {

            runOnUiThread(() -> {
              qrCode.setVisibility(View.GONE);
            });
          }

          @Override
          public void onFirstActive() {
            Log.d(TAG, "onFirstActive");
          }

          @Override
          public void onMQTTStatusChanged(int status) {
            Log.d(TAG, "onMQTTStatusChanged: " + status);

            switch (status) {
              case IoTSDKManager.STATUS_OFFLINE:
                // 设备网络离线
                break;
              case IoTSDKManager.STATUS_MQTT_OFFLINE:
                // 网络在线MQTT离线
                break;
              case IoTSDKManager.STATUS_MQTT_ONLINE:
                // 网络在线MQTT在线

                SharedPreferences sp = getSharedPreferences("event_cache", MODE_PRIVATE);

                DPEvent[] events = ioTSDKManager.getEvents();

                if (events != null) {
                  for (DPEvent event : events) {
                    if (event != null) {
                      Log.d(TAG, event.toString());
                    }
                  }
                }
                break;
            }
          }

//                    @Override
//                    public void onMqttMsg(int protocol, org.json.JSONObject msgObj) {
//
//                    }
        });

    ioTSDKManager.setUpgradeCallback(new UpgradeEventCallback() {
      @Override
      public void onUpgradeInfo(String s) {
        com.tuya.smartai.iot_sdk.Log.w(TAG, "onUpgradeInfo: " + s);

        Log.d(TAG, "收到升级信息: " + s);

//                runOnUiThread(() -> upgradeDialog.show());

        ioTSDKManager.startUpgradeDownload();
      }

      @Override
      public void onUpgradeDownloadStart() {
        com.tuya.smartai.iot_sdk.Log.w(TAG, "onUpgradeDownloadStart");

        Log.d(TAG, "开始升级下载");
      }

      @Override
      public void onUpgradeDownloadUpdate(int i) {
        com.tuya.smartai.iot_sdk.Log.w(TAG, "onUpgradeDownloadUpdate: " + i);
      }

      @Override
      public void upgradeFileDownloadFinished(int result, String file) {
        com.tuya.smartai.iot_sdk.Log.w(TAG, "upgradeFileDownloadFinished: " + result);

      }
    });
  }

  Handler mainHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case 1:
          ComCmdUtil.sendAutoCmd();
          break;
        case 2:
          ComCmdUtil.sendWaterPressCmd(0);
          break;
        case 3:
          ComCmdUtil.sendWaterTempCmd(0);
          break;
        case 4:
          ComCmdUtil.sendWarmAirCmd(0);
          break;
        case 5:
          ComCmdUtil.sendTimerDryCmd(0);
          break;

        default:
          break;
      }
    }
  };


  //加载默认等级
  private void sendDefaultLev() {
    // ComCmdUtil.sendGetInfo(mysql);

    mainHandler.sendEmptyMessageDelayed(1, 50);
    mainHandler.sendEmptyMessageDelayed(2, 100);
    mainHandler.sendEmptyMessageDelayed(3, 150);
    mainHandler.sendEmptyMessageDelayed(4, 200);
    mainHandler.sendEmptyMessageDelayed(5, 250);

    // 默认 自动
    // ComCmdUtil.sendAutoCmd();
    // 水压 弱
    // ComCmdUtil.sendWaterPressCmd(0);

    // 水温 关
    // ComCmdUtil.sendWaterTempCmd(0);

    //  暖风 关
    // ComCmdUtil.sendWarmAirCmd(0);
    // 定时干燥 关
    // ComCmdUtil.sendTimerDryCmd(0);

    MyDataUtil.addRecordList(mysql, "下发默认等级", BaseApp.User.getId());

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == permissionCode) {
      Log.d(TAG, "onActivityResult: ");
      requestPermissions();
    }


  }

  BlueChangedReceiver blueChangedReceiver = new BlueChangedReceiver();
  NetworkConnectChangedReceiver networkConnectChangedReceiver = new NetworkConnectChangedReceiver();

  private void startService() {
    //Android8.0之后很多静态广播不能用了，想要接收应用安装、覆盖安装和删除的广播，需要动态注册广播。

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

    // 注册广播接收器，接收并处理搜索结果
    registerReceiver(blueChangedReceiver, intentFilter);


  }

  ImageView ImageView_water_press, ImageView_water_temper, ImageView_water_air, ImageView_timer_dry, ImageView_water_clean, ImageView_water_dirty;
  ImageView ImageView_stop, ImageView_auto, ImageView_control;
  LinearLayout LinearLayout_left_auto, LinearLayout_left_user;

  ImageView ImageView_defecate, ImageView_pee, ImageView_wabsorption, ImageView_dry, ImageView_run;


  RelativeLayout RelativeLayout_control, RelativeLayout_tips, RelativeLayout_tips_none, RelativeLayout_app;

  TextView TextView_debug, TextView_tips, TextView_run, TextView_pee_num, TextView_defeate_num;

  LinearLayout LinearLayout_tip_1, LinearLayout_tip_2, LinearLayout_tip_3, LinearLayout_tip_4, LinearLayout_tip_5, LinearLayout_tip_6, LinearLayout_tip_7;

  private void initView() {
    RelativeLayout_app = findViewById(R.id.RelativeLayout_app);
    //界面旋转180度
    // RelativeLayout_app.setRotation(180);
    qrCode = findViewById(R.id.qrcode);

    RelativeLayout_tips = findViewById(R.id.RelativeLayout_tips);
    RelativeLayout_tips_none = findViewById(R.id.RelativeLayout_tips_none);

    ImageView_water_press = findViewById(R.id.ImageView_water_press);
    ImageView_water_temper = findViewById(R.id.ImageView_water_temper);
    ImageView_water_air = findViewById(R.id.ImageView_water_air);
    ImageView_timer_dry = findViewById(R.id.ImageView_timer_dry);
    ImageView_water_clean = findViewById(R.id.ImageView_water_clean);
    ImageView_water_dirty = findViewById(R.id.ImageView_water_dirty);

    LinearLayout_left_auto = findViewById(R.id.LinearLayout_left_auto);
    LinearLayout_left_user = findViewById(R.id.LinearLayout_left_user);

    ImageView_stop = findViewById(R.id.ImageView_stop);
    ImageView_auto = findViewById(R.id.ImageView_auto);
    ImageView_control = findViewById(R.id.ImageView_control);

    ImageView_defecate = findViewById(R.id.ImageView_defecate);
    ImageView_pee = findViewById(R.id.ImageView_pee);
    ImageView_wabsorption = findViewById(R.id.ImageView_wabsorption);
    ImageView_dry = findViewById(R.id.ImageView_dry);
    ImageView_run = findViewById(R.id.ImageView_run);

    RelativeLayout_control = findViewById(R.id.RelativeLayout_control);

    TextView_pee_num = findViewById(R.id.TextView_pee_num);
    TextView_defeate_num = findViewById(R.id.TextView_defeate_num);

    TextView_run = findViewById(R.id.TextView_run);
    TextView_tips = findViewById(R.id.TextView_tips);
    TextView_debug = findViewById(R.id.TextView_debug);
    TextView_debug.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View view) {

        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            TextView_debug.setText("debug模式");
          }
        });
        return false;
      }
    });
    TextView_debug.setVisibility(View.GONE);

    LinearLayout_tip_1 = findViewById(R.id.LinearLayout_tip_1);
    LinearLayout_tip_2 = findViewById(R.id.LinearLayout_tip_2);
    LinearLayout_tip_3 = findViewById(R.id.LinearLayout_tip_3);
    LinearLayout_tip_4 = findViewById(R.id.LinearLayout_tip_4);
    LinearLayout_tip_5 = findViewById(R.id.LinearLayout_tip_5);
    LinearLayout_tip_6 = findViewById(R.id.LinearLayout_tip_6);
    LinearLayout_tip_7 = findViewById(R.id.LinearLayout_tip_7);


  }


  /**
   * 切换自动手动,获取当前状态
   */
  private void changeAuto() {

    if ((ImageView_auto.getDrawable().getConstantState()) == this.getResources()
        .getDrawable(R.mipmap.auto_0).getConstantState()) {
      //手动->自动

      changeAutoUI();
      loadNowLevIco();//加载默认图标
      ComCmdUtil.sendAutoCmd();
      MyDataUtil.addRecordList(mysql, "手动->自动 模式切换", T_USER);

    } else {
      //  自动->手动
      changeHandUI();
      ComCmdUtil.sendHandCmd();

      MyDataUtil.addRecordList(mysql, "自动->手动 模式切换", T_USER);
    }

  }


  /**
   * 获取当前状态，如果  UserFragment 有操作过
   */
  private void loadNowLevIco() {

    String WATER_PRESS_LEV = SPUtil.get(this, SPUtil.WATER_PRESS, "0");//水压 低
    String WATER_TEMPER_LEV = SPUtil.get(this, SPUtil.WATER_TEMPER, "0");//水温  关
    String WARM_AIR_LEV = SPUtil.get(this, SPUtil.WARM_AIR, "0");//暖风
    String TIMER_DRY_LEV = SPUtil.get(this, SPUtil.TIMER_DRY, "0");//定时干燥
    String MASSAGE_LEV = SPUtil.get(this, SPUtil.MASSAGE, "0");//按摩
    String AUTO_LEV = SPUtil.get(this, SPUtil.AUTO, "1");//自动

    ButtonChangeUtil.changeWaterPressIco(ImageView_water_press, this,
        Integer.parseInt(WATER_PRESS_LEV));
    ButtonChangeUtil.changeWaterTemperIco(ImageView_water_temper, this,
        Integer.parseInt(WATER_TEMPER_LEV));
    ButtonChangeUtil.changeWarmAirIco(ImageView_water_air, this, Integer.parseInt(WARM_AIR_LEV));
    ButtonChangeUtil.changeTimerDryIco(ImageView_timer_dry, this, Integer.parseInt(TIMER_DRY_LEV));

    if (AUTO_LEV.equals("1")) {

      changeAutoUI();
    } else {
      changeHandUI();

    }


  }


  private void changeAutoUI() {
    ImageView_auto.setImageDrawable(this.getResources().getDrawable(R.mipmap.auto_1));
    LinearLayout_left_auto.setVisibility(View.VISIBLE);
    LinearLayout_left_user.setVisibility(View.GONE);
    SPUtil.set(this, SPUtil.AUTO, "1");
  }

  private void changeHandUI() {
    ImageView_auto.setImageDrawable(this.getResources().getDrawable(R.mipmap.auto_0));
    LinearLayout_left_user.setVisibility(View.VISIBLE);
    LinearLayout_left_auto.setVisibility(View.GONE);
    SPUtil.set(this, SPUtil.AUTO, "0");
  }


  @Override
  public void onClick(View view) {
    if (FastClickUtil.isFastClick()) {
      return;
    }
    switch (view.getId()) {

      case R.id.LinearLayout_water_press:
        //水压
        ButtonChangeUtil.changeWaterPress(ImageView_water_press, this, mysql);

        break;

      case R.id.LinearLayout_water_temper:
        //水温
        ButtonChangeUtil.changeWaterTemper(ImageView_water_temper, this, mysql);
        break;
      case R.id.LinearLayout_warm_air:
        //暖风
        ButtonChangeUtil.changeWarmAir(ImageView_water_air, this, mysql);
        break;
      case R.id.LinearLayout_timer_dry:
        //定时干燥
        ButtonChangeUtil.changeTimerDry(ImageView_timer_dry, this, mysql);
        break;

      case R.id.LinearLayout_defecate:
        //大便
        ButtonChangeUtil.changeDefecate(ImageView_defecate, this, true);

        break;

      case R.id.LinearLayout_pee:
        //小便
        ButtonChangeUtil.changePee(ImageView_pee, this, true);
        break;
      case R.id.LinearLayout_absorption:
        //强吸
        ButtonChangeUtil.changeWabsorption(ImageView_wabsorption, this, true, mysql);
        break;
      case R.id.LinearLayout_dry:
        //干燥
        ButtonChangeUtil.changeDry(ImageView_dry, this, true, mysql);
        break;

      case R.id.ImageView_auto:
        //自动
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
        //停止
        ButtonChangeUtil.changeStop(ImageView_stop, this, true, mysql);
        break;

      case R.id.ImageView_control:
        //控制
        if ((hiddenAnimation != null && !hiddenAnimation.hasEnded())) {
          return;
        }

        showControlView();
        break;
      case R.id.RelativeLayout_control:
        if ((showAnimation != null && !showAnimation.hasEnded())) {
          return;
        }

        hiddenControlView();

        break;
      case R.id.RelativeLayout_userinfo:
        UserActivity.luncher();
        break;
    }

    // AlertUtil.show("按键提示");

  }

  Animation showAnimation;
  Animation hiddenAnimation;

  private void initAnimation() {

  }


  private void hiddenControlView() {

    getSupportFragmentManager().popBackStack();
    hiddenAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out);
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


  private void showControlView() {

    RelativeLayout_control.setVisibility(View.VISIBLE);
    showAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in);
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

    if ((ImageView_auto.getDrawable().getConstantState()) == this.getResources()
        .getDrawable(R.mipmap.auto_0).getConstantState()) {
      //手动
      if (controlUserFragment == null) {
        controlUserFragment = new ControlUserFragment();
      }

      transaction = getSupportFragmentManager()
          .beginTransaction();
      transaction.setCustomAnimations(
              R.anim.slide_right_in,
              R.anim.slide_right_out

          ).replace(R.id.FrameLayout_control, controlUserFragment)
          .addToBackStack(null)
          .commit();


    } else {
      //  自动
      if (controlAutoFragment == null) {
        controlAutoFragment = new ControlAutoFragment();
      }

      transaction = getSupportFragmentManager()
          .beginTransaction();
      transaction.setCustomAnimations(
              R.anim.slide_right_in,
              R.anim.slide_right_out

          ).replace(R.id.FrameLayout_control, controlAutoFragment)
          .addToBackStack(null)
          .commit();

    }


  }


  FragmentTransaction transaction;
  ControlAutoFragment controlAutoFragment;

  ControlUserFragment controlUserFragment;

  @Override
  protected void onDestroy() {
    super.onDestroy();
    SerialportUtil.close();
    try {
      task.cancel();
      timer.cancel();
    } catch (Exception e) {

    }

    if (!EventBus.getDefault().isRegistered(this)) {
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
  public void onEvent(MessageEvent obj) {

    Log.e("订阅", obj.getName() + "===");
    switch (obj) {
      case Com_data:

        TextView_debug.append("\r\n" + "接受：" + obj.getData().toString());

        getCmdData(obj.getData().toString().toUpperCase());

        break;
      case Send_data:

        TextView_debug.append("\r\n" + "发送：" + obj.getData().toString());

        break;
      case Com_open_ok:

        TextView_debug.append("\r\n" + "串口打开成功");
        Toasty.success(this, "串口打开成功").show();

        //不读取 下位机状态，直接发送默认状态
        sendDefaultLev();

        break;
      case Com_open_error:

        TextView_debug.append("\r\n" + "串口打开失败");
        Toasty.error(this, "串口打开失败").show();
        ;
        break;
      default:
        break;

    }


  }


  /**
   * 干燥 叠加 显示 类型 3
   */
  private void countDry() {
    MyDataUtil.insertDataList(mysql, 3, T_USER);
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ImageView_run.setImageResource(R.mipmap.run_dry_3x);

      }
    });

    ButtonChangeUtil.runAnimation(ImageView_run, TextView_run, this);

    findCountDry();

  }


  /**
   * 查询干燥次数 类型 3
   */
  private void findCountDry() {

    final int n2 = MyDataUtil.countDataList(mysql, 3, T_USER, DateUtil.getStringDateShort());
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        TextView_run.setText("干燥状态 " + n2 + " 次");
      }
    });


  }


  /**
   * 小便 叠加 显示 类型 1
   */
  private void countPee() {
    MyDataUtil.insertDataList(mysql, 1, T_USER);
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ImageView_run.setImageResource(R.mipmap.pee_3x);

      }
    });

    ButtonChangeUtil.runAnimation(ImageView_run, TextView_run, this);

    findCountPee();
    loadCount();
  }


  /**
   * 查询小便次数 类型 1
   */
  private void findCountPee() {

    final int n2 = MyDataUtil.countDataList(mysql, 1, T_USER, DateUtil.getStringDateShort());
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        TextView_run.setText("小便状态 " + n2 + " 次");
      }
    });


  }

  /**
   * 大便 叠加 显示 类型 2
   */
  private void countDefecate() {
    MyDataUtil.insertDataList(mysql, 2, T_USER);

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ImageView_run.setImageResource(R.mipmap.defecate_3x);

      }
    });
    ButtonChangeUtil.runAnimation(ImageView_run, TextView_run, this);

    findCountDefecate();
    loadCount();
  }


  /**
   * 查询大便次数 类型 2
   */
  private void findCountDefecate() {
    final int n = MyDataUtil.countDataList(mysql, 2, T_USER, DateUtil.getStringDateShort());
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        TextView_run.setText("大便状态 " + n + " 次");
      }
    });
  }


  private void loadCount() {

    final int n = MyDataUtil.countDataList(mysql, 2, T_USER, DateUtil.getStringDateShort());
    final int n2 = MyDataUtil.countDataList(mysql, 1, T_USER, DateUtil.getStringDateShort());
    runOnUiThread(new Runnable() {
      @Override
      public void run() {

        TextView_defeate_num.setText(n + "");
        TextView_pee_num.setText(n2 + "");
      }
    });


  }


  /**
   * 解析命令
   *
   * @param hex
   */
  private void getCmdData(String hex) {

    if (!hex.startsWith("FF")) {
      return;
    }

    if (hex.startsWith("FF04") && hex.substring(6, 8).equals("01")) {
      //按键下发
      String cmd = hex.substring(4, 6);

      switch (cmd) {

        case "B2":
          //软件强吸按下反馈
          MyDataUtil.addRecordList(mysql, "按下水压", T_USER);
          break;
        case "B4":
          //软件大便按下反馈

          //  countDefecate();

          break;
        case "B5":
          //软件小便按下反馈

          //  countPee();

          break;
        case "B7":
          //软件干燥按下反馈
          countDry();
          break;

      }


    }

    if (hex.startsWith("FF04E8")) {
      //按键下发
      String cmd = hex.substring(8, 10);

      switch (cmd) {
        case "01":
          //手动模式
          changeHandUI();
          break;
        case "02":
          //自动模式
          changeAutoUI();
          break;
        case "03":
          //大便按下
          ButtonChangeUtil.changeDefecate(ImageView_defecate, this, false);

          countDefecate();

          break;
        case "04":
          //小便按下
          ButtonChangeUtil.changePee(ImageView_pee, this, false);
          countPee();

          break;
        case "05":
          //干燥键
          ButtonChangeUtil.changeDry(ImageView_dry, this, false, mysql);
          break;
        case "06":
          //停止键
          ButtonChangeUtil.changeStop(ImageView_stop, this, false, mysql);
          break;

      }


    }

    if (hex.startsWith("FF04E5")) {
      //  FF04E50D  00  00 00 00 00 00 00 00 00 00 00 00 00 00 sum
      //FF04E5 0D(数据长度)  00  00 00 00 00 00 00 00 00 00 00 00 00 00  sum(校验和)

      //检测位上传
      if (!hex.substring(8, 10).equals("00")) {
        //1.污水罐 未装

        //模拟提示
        //  ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"污水罐未装",this);

        LinearLayout_tip_1.setVisibility(View.VISIBLE);
        //警报： 污水罐未装
        MediaPlayerUtil.play(R.raw.l1);
      } else {
        LinearLayout_tip_1.setVisibility(View.GONE);
      }
      if (!hex.substring(10, 12).equals("00")) {
        //2.坐便器 未装
        //模拟提示
        // ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"坐便器未装",this);

        LinearLayout_tip_2.setVisibility(View.VISIBLE);
        //警报：坐便器未正确安装
        MediaPlayerUtil.play(R.raw.l2);

      } else {
        LinearLayout_tip_2.setVisibility(View.GONE);
      }
      if (hex.substring(12, 14).equals("00")) {
        //3.负压罐有水
        //模拟提示
        // ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"负压管有水",this);
        LinearLayout_tip_3.setVisibility(View.VISIBLE);
        MyDataUtil.addRecordList(mysql, "警报：负压罐有水，请及时打开底部旋钮，将负压罐内水放完", T_USER);
        MediaPlayerUtil.play(R.raw.l3);

      } else {

        LinearLayout_tip_3.setVisibility(View.GONE);
      }
      if (hex.substring(14, 16).equals("00")) {
        //4.漏水检测漏水
        //模拟提示
        //   ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"检测漏水",this);
        LinearLayout_tip_4.setVisibility(View.VISIBLE);
        MyDataUtil.addRecordList(mysql, "警报：发现机器漏水，请立即断电，并联系售后", T_USER);
        MediaPlayerUtil.play(R.raw.l4);
      } else {
        LinearLayout_tip_4.setVisibility(View.GONE);
      }
      if (hex.substring(16, 18).equals("00")) {
        //5.坐便器液位水满
        //模拟提示
        // ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"坐便器液位水满",this);
        LinearLayout_tip_5.setVisibility(View.VISIBLE);
        MyDataUtil.addRecordList(mysql, "警报：坐便器水溢出，请联系售后", T_USER);
        MediaPlayerUtil.play(R.raw.l5);
      } else {

        LinearLayout_tip_5.setVisibility(View.GONE);
      }
      if (hex.substring(18, 20).equals("00")) {
        //6.无人体

        LinearLayout_tip_7.setVisibility(View.VISIBLE);
        MyDataUtil.addRecordList(mysql, "警报：未正确穿戴", T_USER);
        MediaPlayerUtil.play(R.raw.l6);

      } else {

        LinearLayout_tip_7.setVisibility(View.GONE);
      }
      if (!hex.substring(20, 22).equals("00")) {
        //7.有大便 次数+1

        //  countDefecate();
      }
      if (!hex.substring(22, 24).equals("00")) {
        //8.有小便 次数+1
        //  countPee();
      }

      if (hex.substring(26, 28).equals("00")) {
        //10.污水罐高,溢出了
        ButtonChangeUtil.changeWaterDirtyIco(ImageView_water_dirty, this, 2);
        ButtonChangeUtil.changeShowTips(RelativeLayout_tips, RelativeLayout_tips_none,
            TextView_tips, "警报：污水罐异常，并联系售后", this);
        MyDataUtil.addRecordList(mysql, "警报：污水罐异常，并联系售后", T_USER);
        MediaPlayerUtil.play(R.raw.l10);
      } else {
        //没溢出 ，判断是否 满了
        if (hex.substring(24, 26).equals("00")) {
          //9.污水罐高,满了，需要倒水
          ButtonChangeUtil.changeWaterDirtyIco(ImageView_water_dirty, this, 2);
          // ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"警报：污水罐满了",this);
          MyDataUtil.addRecordList(mysql, "警报：污水罐满了，请倒掉污水", T_USER);
          MediaPlayerUtil.play(R.raw.l9);
        } else {
          ButtonChangeUtil.changeWaterDirtyIco(ImageView_water_dirty, this, 0);
        }


      }

      // Log.e("净水",hex+"======净水======"+hex.substring(28,30));

      String tempWater_01 = "00";//
      String tempWater_02 = "00";//
      String tempWater_03 = "00";//

      if (!hex.substring(28, 30).equals("00")) {

        tempWater_01 = "01";

        // Log.e("净水","======净水======");
        //11. 净水桶低

      }
      if (hex.substring(30, 32).equals("00")) {
        //12.净水桶中
        // ButtonChangeUtil.changeWaterClean(ImageView_water_clean,1,this);
      } else {
        tempWater_02 = "01";

        //12.净水桶中
        // ButtonChangeUtil.changeWaterClean(ImageView_water_clean,0,this);
      }
      if (hex.substring(32, 34).equals("00")) {
        //13. 换水屏横向位置  无配置
      } else {

        tempWater_03 = "01";
      }

      if (tempWater_03.equals("00")) {
        //满桶
        ButtonChangeUtil.changeWaterClean(ImageView_water_clean, 3, this);
        ButtonChangeUtil.changeShowTips(RelativeLayout_tips, RelativeLayout_tips_none,
            TextView_tips, "警报：清水桶已满，请停止加水", this);
        MyDataUtil.addRecordList(mysql, "警报：清水桶已满，请停止加水", T_USER);
        MediaPlayerUtil.play(R.raw.l13_2);

      } else {
        //空桶

        if (tempWater_02.equals("00")) {
          //中水位
          ButtonChangeUtil.changeWaterClean(ImageView_water_clean, 2, this);
        } else {
          if (tempWater_01.equals("00")) {
            //低水位
            ButtonChangeUtil.changeWaterClean(ImageView_water_clean, 1, this);

            ButtonChangeUtil.changeShowTips(RelativeLayout_tips, RelativeLayout_tips_none,
                TextView_tips, "警报：请先加满水", this);
            MyDataUtil.addRecordList(mysql, "警报：请先加满水", T_USER);
            MediaPlayerUtil.play(R.raw.l13_1);

          } else {
            //空桶
            ButtonChangeUtil.changeWaterClean(ImageView_water_clean, 0, this);
            ButtonChangeUtil.changeShowTips(RelativeLayout_tips, RelativeLayout_tips_none,
                TextView_tips, "警报：清水空了，请先加水再开机操作", this);
            MyDataUtil.addRecordList(mysql, "警报：清水空了，请先加水再开机操作", T_USER);
            MediaPlayerUtil.play(R.raw.l13);
          }

        }

      }

      if (hex.substring(34, 36).equals("00")) {
        //14.被褥湿
        //模拟提示
        //  ButtonChangeUtil.changeShowTips(RelativeLayout_tips,RelativeLayout_tips_none,TextView_tips,"被褥湿",this);
        LinearLayout_tip_6.setVisibility(View.VISIBLE);
        MyDataUtil.addRecordList(mysql, "警报：被褥湿", T_USER);
        MediaPlayerUtil.play(R.raw.l14);
      } else {
        LinearLayout_tip_6.setVisibility(View.GONE);
      }


    }

    if (hex.startsWith("FF04D1")) {
      //上传自动大便动作状态
      // 0X01：开始
      //0X02:  结束
      //动作状态
      String cmd = hex.substring(8, 10);

      switch (cmd) {
        case "01":

          //开始
                   /* runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            ImageView_run.setImageResource(R.mipmap.defecate_3x);
                        }
                    });*/

          countDefecate();
          MyDataUtil.addRecordList(mysql, "自动大便冲洗", T_USER);
          break;
        case "02":
          //结束
          ButtonChangeUtil.resetRunAnimation(ImageView_run, TextView_run, this);
          MyDataUtil.addRecordList(mysql, "自动大便冲洗完成", T_USER);
          MediaPlayerUtil.play(R.raw.finish);
          Alerter.create(this)
              .setTitle("设备提醒")
              .setText("自动大便冲洗完成")
              .setDuration(10 * 1000)
              .setBackgroundColor(R.color.orange)
              .show();
          break;


      }


    }

    if (hex.startsWith("FF04D2")) {
      //上传自动小便动作状态
      // 0X01：开始
      //0X02:  结束
      //动作状态
      String cmd = hex.substring(8, 10);

      switch (cmd) {
        case "01":
          //开始
          //小便 次数+1

                    /*runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            ImageView_run.setImageResource(R.mipmap.pee_3x);
                        }
                    });*/
          //  findCountPee();
          countPee();
          MyDataUtil.addRecordList(mysql, "自动冲洗小便", T_USER);
          break;
        case "02":
          //结束
          ButtonChangeUtil.resetRunAnimation(ImageView_run, TextView_run, this);
          MyDataUtil.addRecordList(mysql, "自动小便冲洗完成", T_USER);
          MediaPlayerUtil.play(R.raw.finish);

          Alerter.create(this)
              .setTitle("设备提醒")
              .setText("自动小便冲洗完成")
              .setDuration(10 * 1000)
              .setBackgroundColor(R.color.orange)
              .show();
          break;


      }


    }

    if (hex.startsWith("FF04D3")) {
      //上传手动大便动作状态
      // 0X01：开始
      //0X02:  结束
      //动作状态
      String cmd = hex.substring(8, 10);

      switch (cmd) {
        case "01":
          //开始
                   /*  runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            ImageView_run.setImageResource(R.mipmap.defecate_3x);
                        }
                    });
                   findCountDefecate();*/
          countDefecate();

          MyDataUtil.addRecordList(mysql, "手动冲洗大便", T_USER);
          break;
        case "02":
          //结束
          ButtonChangeUtil.resetRunAnimation(ImageView_run, TextView_run, this);
          MyDataUtil.addRecordList(mysql, "手动大便冲洗完成", T_USER);
          MediaPlayerUtil.play(R.raw.finish);
          Alerter.create(this)
              .setTitle("设备提醒")
              .setText("手动大便冲洗完成")
              .setDuration(10 * 1000)
              .setBackgroundColor(R.color.orange)
              .show();
          break;


      }


    }

    if (hex.startsWith("FF04D4")) {
      //上传手动小便动作状态
      // 0X01：开始
      //0X02:  结束
      //动作状态
      String cmd = hex.substring(8, 10);

      switch (cmd) {
        case "01":
          //开始
                   /* runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            ImageView_run.setImageResource(R.mipmap.pee_3x);
                        }
                    });
                    findCountPee();*/

          countPee();

          MyDataUtil.addRecordList(mysql, "手动冲洗小便", T_USER);
          break;
        case "02":
          //结束
          ButtonChangeUtil.resetRunAnimation(ImageView_run, TextView_run, this);
          MyDataUtil.addRecordList(mysql, "手动小便冲洗完成", T_USER);
          MediaPlayerUtil.play(R.raw.finish);
          Alerter.create(this)
              .setTitle("设备提醒")
              .setText("手动小便冲洗完成")
              .setDuration(10 * 1000)
              .setBackgroundColor(R.color.orange)
              .show();
          break;


      }


    }

    if (hex.startsWith("FF04D5")) {
      //上传干燥动作状态
      // 0X01：开始
      //0X02:  结束
      //动作状态
      String cmd = hex.substring(8, 10);

      switch (cmd) {
        case "01":
          //开始
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              ImageView_run.setImageResource(R.mipmap.run_dry_3x);
            }
          });
          findCountDry();
          break;
        case "02":
          //结束
          ButtonChangeUtil.resetRunAnimation(ImageView_run, TextView_run, this);

          MyDataUtil.addRecordList(mysql, "干燥 完成", T_USER);
          MediaPlayerUtil.play(R.raw.finish);
          Alerter.create(this)
              .setTitle("设备提醒")
              .setText("干燥 完成")
              .setDuration(10 * 1000)
              .setBackgroundColor(R.color.orange)
              .show();
          break;


      }


    }

    if (hex.startsWith("FF04A4")) {
      //下位机请求当前app当前状态

      ComCmdUtil.sendSetInfo(this, mysql);

    }

    if (hex.startsWith("FF04A7")) {
      //APP请求下位机当前状态
      String value = hex.substring(8, 26);

      String WATER_PRESS_LEV = value.substring(0, 2);//水压 低
      String WATER_TEMPER_LEV = value.substring(2, 6);//水温  关
      String WARM_AIR_LEV = value.substring(6, 8);//暖风
      String TIMER_DRY_LEV = value.substring(8, 12);//定时干燥
      String MASSAGE_LEV = value.substring(12, 14);//按摩
      String AUTO_LEV = value.substring(14, 16);//自动
      String OTHER_LEV = value.substring(16, 18);//其他

      if (WATER_PRESS_LEV.equals("00")) {
        WATER_PRESS_LEV = "0";
      } else {
        WATER_PRESS_LEV = "1";
      }
      if (WARM_AIR_LEV.equals("00")) {
        WARM_AIR_LEV = "0";
      } else {
        WARM_AIR_LEV = "1";
      }
      if (MASSAGE_LEV.equals("00")) {
        MASSAGE_LEV = "0";
      } else {
        MASSAGE_LEV = "1";
      }
      if (AUTO_LEV.equals("01")) {
        AUTO_LEV = "1";
      } else {
        AUTO_LEV = "2";
      }
      //  if(OTHER_LEV.equals("00")){ OTHER_LEV="0"; }else{ OTHER_LEV="1"; }

      String WATER_TEMPER_VALUE = "0";

      if (WATER_TEMPER_LEV.equals("0000")) {
        WATER_TEMPER_VALUE = "0";
      }
      if (WATER_TEMPER_LEV.equals("012C")) {
        WATER_TEMPER_VALUE = "1";
      }
      if (WATER_TEMPER_LEV.equals("014A")) {
        WATER_TEMPER_VALUE = "2";
      }
      if (WATER_TEMPER_LEV.equals("017C")) {
        WATER_TEMPER_VALUE = "3";
      }

      /////////////需要处理  定时干燥
      String TIMER_DRY_VALUE = "0000"; //无自定干燥
      if (TIMER_DRY_LEV.equals("0000")) {
        TIMER_DRY_VALUE = "0";
      }
      if (TIMER_DRY_LEV.equals("003C")) {
        TIMER_DRY_VALUE = "1";
      }
      if (TIMER_DRY_LEV.equals("0078")) {
        TIMER_DRY_VALUE = "2";
      }
      if (TIMER_DRY_LEV.equals("00B4")) {
        TIMER_DRY_VALUE = "3";
      }
      if (TIMER_DRY_LEV.equals("00F0")) {
        TIMER_DRY_VALUE = "4";
      }

      SPUtil.set(this, SPUtil.WATER_PRESS, WATER_PRESS_LEV);//水压 低
      SPUtil.set(this, SPUtil.WATER_TEMPER, WATER_TEMPER_VALUE);//水温  关
      SPUtil.set(this, SPUtil.WARM_AIR, WARM_AIR_LEV);//暖风
      SPUtil.set(this, SPUtil.TIMER_DRY, TIMER_DRY_VALUE);//定时干燥
      SPUtil.set(this, SPUtil.MASSAGE, MASSAGE_LEV);//按摩
      SPUtil.set(this, SPUtil.AUTO, AUTO_LEV);//自动

      loadNowLevIco();


    }


  }


  Timer timer = new Timer();

  long count = 0;

  TimerTask task = new TimerTask() {
    @Override
    public void run() {
      count++;

      if (count >= 24 * 3600) {
        //一天清 0 一次

        count = 0;
      }

      if (count == 20) {

        EventBus.getDefault().post(MessageEvent.Second);

      }

      if (count % 60 == 0) {

        EventBus.getDefault().post(MessageEvent.Minite);

      }

      if (count % (3 * 60) == 0) {

        EventBus.getDefault().post(MessageEvent.CheckWifi);

      }

      //

    }
  };


}