package com.kjy.care.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kjy.care.activity.BaseApp;

import org.greenrobot.eventbus.EventBus;


public class BlueChangedReceiver extends BroadcastReceiver {
    private static String TAG = "BlueChangedReceiver>>";



    @Override
    public void onReceive(Context context, Intent intent) {


        String action = intent.getAction();

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

            if (BaseApp.mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {

                //	LogH.e("打开蓝牙", "打开蓝牙");
                EventBus.getDefault().post(MessageEvent.Blue_on);
            } else if (BaseApp.mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {

                //	LogH.e("关闭蓝牙", "关闭蓝牙");
                EventBus.getDefault().post(MessageEvent.Blue_off);
            }
        }
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {// 手机屏幕关闭
            //	LogH.e("手机屏幕关闭", "手机屏幕关闭");

        }

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {// 手机屏幕开启
            //	LogH.e("手机屏幕开启", "手机屏幕开启");

        }


        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        }
        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


        }

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            //发现蓝牙设备
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);




        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            //	LogH.e("DISCOVERY_FINISHED","==扫描完毕=");
            //蓝牙搜索完成
            // 蓝牙搜索是非常消耗系统资源开销的过程，搜索完毕后应该及时取消搜索
            //	mBluetoothAdapter.cancelDiscovery();
        }

    }


}
