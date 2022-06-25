package com.kjy.care.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.kjy.care.activity.BaseApp;
import com.kjy.care.api.HttpUtil;
import com.kjy.care.util.downfile.DownLoadObserver;
import com.kjy.care.util.downfile.DownloadInfo;
import com.kjy.care.util.downfile.DownloadManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kjy.care.service.MessageEvent.VersionUpdate;

public class AppVersionUtil {



    /**
     * 加载服务器版本信息
     */

    public static  void getAppVersion(Context context) {

        try {

            Map<String,String> params = new HashMap<String,String>();

            params.put("t_app_name", HttpUtil.getAppName);

            LogH.e("============getAppVersion============="+HttpUtil.getAppName);
            HttpUtil.getinstance().post(HttpUtil.getAppVersion,params, new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {

                    LogH.e("getAppVersion",response.toString());
                    try {


                        JSONObject map = response.body();
                        LogH.e("getAppVersion",map.toString());
                        if(map==null){return;}

                        if (LinkedTreeMapUtil.CodeOk(map)) {

                                  JSONObject temp =map.getJSONObject("body");

                               String  AppURL = JSONUtil.getString(temp, "t_app_url");
                                String VersionValue = JSONUtil.getString(temp, "t_app_version");
                                String VersionNumber = JSONUtil.getString(temp, "t_app_version_name");

                                // LogH.e("getAppVersion","VersionValue:"+VersionValue+",AppURL:"+AppURL);

                                int version = getVersion();
                                if (Integer.valueOf(VersionValue) > version) {
                                    //服务器版本大于当前

                                    if (AppURL == null || AppURL.length() == 0) {
                                        return;
                                    }
                                    //   showLoadingDialog("当前版本:V"+info.versionName);
                                    //  txtProgress.setText("最新版本:V"+VersionNumber+",是否升级?");

                                    downLoadFile(AppURL,VersionNumber+"_jkjc.apk");



                            }


                        }

                    } catch (Exception e) {
                        LogH.e("============getAppVersion============="+e.getMessage());
                       // e.printStackTrace();
                    //    LogH.e("getAppVersion",e.getMessage());
                        AlertUtil.show("数据异常:100");
                    }

                }


                @Override
                public void onFailure(Call<JSONObject> call, Throwable throwable) {

                  //  throwable.printStackTrace();
                //    LogH.e("getAppVersion",throwable.toString());
                    AlertUtil.show("网络异常:101");
                }
            });

        }catch (Exception e){

            e.printStackTrace();

            LogH.e("getAppVersion",e.getMessage());
        }

    }

    static boolean downloading =false;
    //下载文件
    private static void downLoadFile(String url,String fileName){
        if(AppVersionUtil.downloading){
            return;
        }
        AppVersionUtil.downloading = true;
        LogH.e("下载","downLoadFile=>"+fileName);
        DownloadManager.getInstance().download(url,fileName, new DownLoadObserver() {
            @Override
            public void onNext(DownloadInfo value) {
                super.onNext(value);
                updateProgress(value.getProgress(), value.getTotal());
            }

            @Override
            public void onComplete() {
                AppVersionUtil.downloading = false;
                if(downloadInfo != null){
                 //   AlertUtil.show(MainActivity.this,"下载完成");
                 //   hDialogBuilder.dismiss();


                    VersionUpdate.setData("安装中...");
                    EventBus.getDefault().post(VersionUpdate);

                    String url= downloadInfo.getLocation();
                    LogH.e("下载完成","下载完成======"+url);
                    // //Build.VERSION_CODES.P
                    if (Build.VERSION.SDK_INT >= 27) {
                        AppUtil.executeInstallCommand(url);
                    }else {
                        AppUtil.installSilent(url);

                    }

                    //AppUtil.installApk(url);//提示安装

                    VersionUpdate.setData("安装完成");
                    EventBus.getDefault().post(VersionUpdate);

                }
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AppVersionUtil.downloading = false;
                LogH.e("下载完成","下载失败===onError==="+fileName);
                VersionUpdate.setData("下载失败");
                EventBus.getDefault().post(VersionUpdate);
            }
        });
    }




    /**
     * 更新下载进度
     *
     * @param progress
     * @param total
     */
    private  static void updateProgress(long progress, long total) {
        //txtProgress.setText(String.format("正在下载：(%s/%s)",
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(0);
        String value = numberFormat.format((float) progress / (float) total * 100);
        String msg  =String.format("下载:%s",value+"%");
        LogH.e("正在下载",HttpUtil.getAppName+msg);
        VersionUpdate.setData(msg);
        EventBus.getDefault().post(VersionUpdate);
    }

    public static int getVersion(){

        //包管理操作管理类
        PackageManager pm = BaseApp.getAppContext().getPackageManager();

        try {
            PackageInfo packinfo = pm.getPackageInfo(BaseApp.getPackgeName(), 0);


            //Build.VERSION_CODES.P
           /* if (Build.VERSION.SDK_INT >= 27) {
            return (int)packinfo.getLongVersionCode();
            }else{ }*/

                return packinfo.versionCode;


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();



        }
        return 0;


    }






    public static String getVersionName(){

        //包管理操作管理类
        PackageManager pm = BaseApp.getAppContext().getPackageManager();

        try {
            PackageInfo packinfo = pm.getPackageInfo(BaseApp.getPackgeName(), 0);

            return packinfo.versionName;


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();



        }
        return "";


    }
}
