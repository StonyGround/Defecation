package com.kjy.care.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


import com.kjy.care.activity.BaseApp;

import org.greenrobot.eventbus.EventBus;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

/*
 *  @项目名：  EckCTT_guangming_bf
 *  @包名：    com.ekz.ctt.eckctt.utils
 *  @文件名:   IPUtils
 *  @创建者:   Administrator
 *  @创建时间:  2018/9/17 15:41
 *  @描述：    TODO
 */
public class WifiUtils {

    public static String GUANGMING_IP1 = "10.1.26";
    public static String GUANGMING_IP2 = "10.1.27";
    public static String GUANGMING_ROOM_WIFI_KEY = "aWiFi";

    public static String WIFI_NAME="WIFI_NAME";
    public static String WIFI_PWD="WIFI_PWD";

    public static String WIFI_AUTO="WIFI_AUTO"; // true   false

    public static boolean isConnectingWifi =false;

    public static final int WIFICIPHER_NOPASS = 0;
    public static final int WIFICIPHER_WEP = 1;
    public static final int WIFICIPHER_WPA = 2;

    public static boolean isAutoConnect(Context context){
        String wifi_AUTO = SPUtil.get(context,WIFI_AUTO,"false");
        if (wifi_AUTO.equals("true")) {

            return true;
        }

        return false;
    }
    /**
     * 一定要连接
     * @param context
     * @return
     */
    public static int getWifiLev(Context context){

        if(isWifiConnect()) {
            WifiManager manage = WifiUtils.getWifiManager(context);
            if(manage == null){ return 0; }
            WifiInfo info = manage.getConnectionInfo();

            return WifiUtils.getWifiManager(context).calculateSignalLevel(info.getRssi(), 5);
        }else{
            return 0;
        }



    }

    public static boolean isAvailable(Context context) {
        //如果关闭了自动连接WiFi的功能，就返回


        String wifiName = SPUtil.get(context,WIFI_NAME);
        String wifiPwd = SPUtil.get(context,WIFI_PWD);



        WifiManager mWifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        if (!isWifiConnect(context)) { //wifi不可用
            if (isAutoConnect(context)) {
                //打开或重启wifi
                setWifiEnabled(mWifiManager, true);

                //重连
                connect2Wifi(context, mWifiManager, wifiName, wifiPwd, WIFICIPHER_WPA);
                return false;
            } else {
                return false;
            }
        }


        //校验wifi名字
        //如果有打开自动连接WiFi的功能
        if (isAutoConnect(context)) {
            //获取连接成功的wifi名称
            WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
            String ssid = connectionInfo.getSSID();

            if (!ssid.contains(wifiName)) {//不包含 "YDHL"
                int networkId = connectionInfo.getNetworkId();
                mWifiManager.removeNetwork(networkId);
                connect2Wifi(context,mWifiManager,wifiName,wifiPwd,WIFICIPHER_WPA);
                return false;
            }
        }


        try {
            //校验IP
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        String ipStr = inetAddress.getHostAddress().toString();

                        if (ipStr.contains(GUANGMING_IP1) || ipStr.contains(GUANGMING_IP2)) {//不可用的ip 26、27
                            //移除wifi配置信息
                            WifiConfiguration wifiConfiguration = isExist(context, wifiName);
                            if (wifiConfiguration != null) {
                                mWifiManager.removeNetwork(wifiConfiguration.networkId);
                            }

                            //扫描附近可连接的wifi列表
                            List<ScanResult> scanResults = mWifiManager.getScanResults();

                            //●从头到尾 免密连接wifi(不成功则提示“无免密wifi)
                            EventBus.getDefault().post( EventBusTags.ON_NET_NOTAVAIL);
                            if (scanResults != null) {
                                for (int i = 0; i < scanResults.size(); i++) {
                                    String ssid = scanResults.get(i).SSID;

                                    if (ssid.contains(GUANGMING_ROOM_WIFI_KEY)) {
                                        connect2Wifi(context, mWifiManager, ssid, "", WIFICIPHER_NOPASS);
                                        break;
                                    }
                                }
                            }
                            return false;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    // 开启/关闭 WIFI.
    public static boolean setWifiEnabled(WifiManager manager, boolean enabled) {
        return manager != null && manager.setWifiEnabled(enabled);
    }

    /**
     * 连接WiFi
     * @param context
     * @param mWifiManager
     * @param wifiName
     * @param wifiPwd
     * @param type
     */
    public static void connect2Wifi(Context context, WifiManager mWifiManager, String wifiName, String wifiPwd, int type) {
        //如果正在连接就返回
        if (isConnectingWifi) return;
        //正在为您连接网络
//        UIUtils.showCustomToast("正在连接网络...");
        try {
            WifiConfiguration mWifiConfig = createWifiConfig(context,
                    wifiName,
                    wifiPwd,
                    type);
            int netId = mWifiManager.addNetwork(mWifiConfig);

            boolean enable = mWifiManager.enableNetwork(netId,true);

            //可选操作，让Wifi重新连接最近使用过的接入点
            boolean reconnect = mWifiManager.reconnect();
        }catch (Exception e){

        }
    }

    public static WifiConfiguration createWifiConfig(Context context,
                                                     String ssid,
                                                     String password,
                                                     int type) throws Exception{
        WifiManager mWifiManager = getWifiManager(context);
        //初始化WifiConfiguration
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        //指定对应的SSID
        config.SSID = "\"" + ssid + "\"";

        //如果之前有类似的配置
        WifiConfiguration tempConfig = isExist(context, ssid);
        if (tempConfig != null) {
            //则清除旧有配置
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        //不需要密码的场景
        if (type == WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //以WEP加密的场景
        } else if (type == WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            //以WPA加密的场景，自己测试时，发现热点以WPA2建立时，同样可以用这种配置连接
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * 获取WiFi管家
     * @param context
     * @return
     */
    public static WifiManager getWifiManager(Context context) {
        return context == null
                ? null
                : (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * @param context
     * @param ssid
     * @return
     */
    public static WifiConfiguration isExist(Context context, String ssid) {
        WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();

        if (configs != null && configs.size() > 0) {
            for (WifiConfiguration config : configs) {
                if (config.SSID.equals("\"" + ssid + "\"")) {
                    return config;
                }
            }
        }
        return null;
    }
    public static boolean isWifiConnect() {
        ConnectivityManager connManager = (ConnectivityManager) BaseApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifiInfo.isConnected();
    }

    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(
                CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI );
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE );
        NetworkInfo mEthernet = connManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET );
        if (mWifi == null && mMobile == null && mEthernet == null)
            return false;
        else {
            if (mWifi == null && mMobile == null){
                return mEthernet.isConnectedOrConnecting();
            }else if (mWifi == null && mEthernet == null){
                return mMobile.isConnectedOrConnecting();
            }else if (mMobile == null && mEthernet == null){
                return  mWifi.isConnectedOrConnecting();
            }else if (mWifi == null){
                return mMobile.isConnectedOrConnecting() || mEthernet.isConnectedOrConnecting();
            }else if (mMobile == null){
                return mWifi.isConnectedOrConnecting() || mEthernet.isConnectedOrConnecting();
            }else if (mEthernet == null){
                return mWifi.isConnectedOrConnecting() || mMobile.isConnectedOrConnecting();
            }else if (mWifi != null && mMobile != null && mEthernet != null){
                return mWifi.isConnectedOrConnecting() || mMobile.isConnectedOrConnecting()|| mEthernet.isConnectedOrConnecting();
            }else {
                return false;
            }
        }
    }
}
