package com.kjy.care.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.kjy.care.BuildConfig;
import com.tuya.smartai.iot_sdk.DPEvent;
import com.tuya.smartai.iot_sdk.IoTSDKManager;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;

public class TestActivity extends FragmentActivity implements EasyPermissions.PermissionCallbacks {

  public static final String TAG = "TestActivity";
  private String[] requiredPermissions = {
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.READ_EXTERNAL_STORAGE};
  private final int PERMISSION_CODE = 123;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ZXingLibrary.initDisplayOpinion(this);
    if (!EasyPermissions.hasPermissions(TestActivity.this, requiredPermissions)) {
      EasyPermissions.requestPermissions(TestActivity.this, "需要授予权限以使用设备", PERMISSION_CODE,
          requiredPermissions);
    } else {
      initSDK();
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

    com.tuya.smartai.iot_sdk.Log.init(this, "/sdcard/test/iot_demo/", 3);

    ioTSDKManager = new IoTSDKManager(this) {
      @Override
      protected boolean isOffline() {
        //实现自定义网络监测
        com.tuya.smartai.iot_sdk.Log.d(TAG, "isOffline: " + super.isOffline());
        return super.isOffline();
      }
    };
    Log.d(TAG, "initSDK: " + mPid + "--" + mUid + "--" + mAk);
    //注意：这里的pid等配置读取自local.properties文件，不能直接使用。请填写你自己的配置！
    ioTSDKManager.initSDK("/sdcard/test/", mPid
        , mUid, mAk, BuildConfig.VERSION_NAME, new IoTSDKManager.IoTCallback() {

          @Override
          public void onDpEvent(DPEvent event) {
            if (event != null) {

            }
          }

          @Override
          public void onReset() {

            getSharedPreferences("event_cache", MODE_PRIVATE).edit().clear().commit();

          }

          @Override
          public void onShorturl(String urlJson) {
            Log.d(TAG, "onShorturl: " + urlJson);
          }

          @Override
          public void onActive() {

          }

          @Override
          public void onFirstActive() {
          }

          @Override
          public void onMQTTStatusChanged(int status) {

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
                    }
                  }
                }
                break;
            }
          }
        });
  }

  @Override
  public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    initSDK();
  }

  @Override
  public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

  }
}
