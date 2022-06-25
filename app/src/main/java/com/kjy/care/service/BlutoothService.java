package com.kjy.care.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.kjy.care.R;
import com.kjy.care.activity.BaseApp;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class BlutoothService extends Service {





    NotificationManager notificationManager;

    String notificationId = "healthServer112";

    String notificationName = "healthServer";

    private void startForegroundService()
    {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //创建NotificationChannel

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(channel);

        }

        startForeground(1,getNotification());

    }

    private Notification getNotification() {

             Notification.Builder builder = new Notification.Builder(this)

                .setSmallIcon(R.drawable.ic_launcher)

                .setContentTitle("检测服务")

                .setContentText("检测服务正在运行...");

        //设置Notification的ChannelID,否则不能正常显示

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setChannelId(notificationId);

        }

        Notification notification = builder.build();

        return notification;

    }
    @Override
    public void onCreate() {
        super.onCreate();
        init();
        startForegroundService();
/*
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { // 注意notification也要适配Android 8 哦
            startForeground(ID, new Notification());// 通知栏标识符 前台进程对象唯一ID
        }*/
      /*  val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var mChannel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = NotificationChannel(
                    CHANNEL_ID_STRING, getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(mChannel);
            val notification =
                    Notification.Builder(applicationContext, CHANNEL_ID_STRING)
                            .setContentText("正在后天定位")
                            .build()
            startForeground(1, notification)
        }*/

    }





    private void init(){

        //延迟10秒开启服务
        if(BaseApp.disposableTimer==null) {
            BaseApp.disposableTimer = Flowable.timer(10, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .onBackpressureLatest()
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            //设置时间

                            BaseApp.getBLE();


                        }
                    });
        }

    }





    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BaseApp.disposableTimer != null) {
            BaseApp.disposableTimer.dispose();

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}