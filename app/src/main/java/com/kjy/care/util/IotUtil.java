package com.kjy.care.util;

import android.content.Context;
import com.kjy.care.BuildConfig;
import com.tuya.smartai.iot_sdk.DPEvent;
import com.tuya.smartai.iot_sdk.IoTSDKManager;
import com.tuya.smartai.iot_sdk.IoTSDKManager.IoTCallback;

public class IotUtil {

  public static IoTSDKManager ioTSDKManager;

  public static void init(Context context, IoTCallback ioTCallback) {
    String mPid = BuildConfig.PID;
    String mUid = BuildConfig.UUID;
    String mAk = BuildConfig.AUTHKEY;

    com.tuya.smartai.iot_sdk.Log.init(context, "/sdcard/care/iot_demo/", 3);

    ioTSDKManager = new IoTSDKManager(context) {
      @Override
      protected boolean isOffline() {
        //实现自定义网络监测
        return super.isOffline();
      }
    };

    //注意：这里的pid等配置读取自local.properties文件，不能直接使用。请填写你自己的配置！
    ioTSDKManager.initSDK("/sdcard/care/", mPid
        , mUid, mAk, BuildConfig.VERSION_NAME, ioTCallback);
  }

  public static void sendDP(int dpid, byte type, int value) {
    ioTSDKManager.sendDP(new DPEvent(dpid, type, value, 0));
  }

  public static void destroy() {
    ioTSDKManager.destroy();
  }

  public static void reset() {
    ioTSDKManager.reset();
  }

}
