package com.kjy.care.service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;


import com.kjy.care.util.SPUtil;
import com.kjy.care.util.WifiUtils;

import org.greenrobot.eventbus.EventBus;


public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private static String TAG = "NetworkConnectChangedReceiver>>";
    private WifiManager mWifiManager;
    private static final int WIFICIPHER_WPA = 2;


    @Override
    public void onReceive(Context context, Intent intent) {
        //如果关闭了自动连接WiFi的功能，就返回
        mWifiManager = WifiUtils.getWifiManager(context);

        //这里测试 每次网络变化后，收到几次广播，分别是什么广播
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的打开与关闭，与wifi的连接无关
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    if (WifiUtils.isAutoConnect(context)) {
                        WifiUtils.setWifiEnabled(mWifiManager, true);
                    }
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    break;
            }
        }

        // 这个监听wifi的连接状态,即是否连上了一个有效的无线路由，当上边广播的状态是WifiManager.WIFI_STATE_DISABLING 和 WIFI_STATE_DISABLED的时候，不会接到这个广播。
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            String wifiName = SPUtil.get(context,WifiUtils.WIFI_NAME);
            String wifiPwd = SPUtil.get(context,WifiUtils.WIFI_PWD);

            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State state = networkInfo.getState();
                WifiUtils.isConnectingWifi=state==NetworkInfo.State.CONNECTING;
                boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态

                Log.i(TAG, "isConnected = " + isConnected);
                if (isConnected) {
                 //   UIUtils.showToast("网络已连接");
                    //保存已连接的wifi，并保存账号和密码
//                    getAndSaveWifi();

                    //如果有打开自动连接WiFi的功能
                   /* if (WifiUtils.isWifiConnect(context)) {
                        //获取连接成功的wifi名称
                        WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
                        String ssid = connectionInfo.getSSID();

                        if (!ssid.contains(wifiName)) {//不包含 "YDHL"
                            int networkId = connectionInfo.getNetworkId();
                            mWifiManager.removeNetwork(networkId);
                            WifiUtils.connect2Wifi(context,mWifiManager,wifiName,wifiPwd,WIFICIPHER_WPA);
                            return;
                        }
                    }*/
                    EventBus.getDefault().post(MessageEvent.WifiConnect);
                  //  EventBus.getDefault().post(EventBusTags.ON_NET_OK);
//                    //wifi连接成功后 延迟6秒刷新数据
//                    UIUtils.getHandle().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                        }
//                    }, 1000 * 6);
                }
            }else{
                EventBus.getDefault().post(MessageEvent.WifiDisConnect);
            }
        }
    }

    private void getAndSaveWifi() {

    }
}
