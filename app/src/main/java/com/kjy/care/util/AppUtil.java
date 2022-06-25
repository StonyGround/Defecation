package com.kjy.care.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;



import com.kjy.care.activity.BaseApp;
import com.kjy.care.activity.WebActivity;
import com.kjy.care.api.HttpUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import static android.content.Context.ACTIVITY_SERVICE;

public class AppUtil {
    public static String otherPackgeName="io.dcloud.HBuilder";//uni.kjy.ctk

    //判断当前应用是否是debug状态
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查包是否存在
     *
     * @param packname
     * @return
     */
    public static boolean checkPackInfo(String packname) {
       /* PackageInfo packageInfo = null;
        try {
            packageInfo = BaseApp.getAppContext().getPackageManager().getPackageInfo(packname, PackageManager.GET_ACTIVITIES);
            LogH.e("====", packageInfo.applicationInfo.processName + packageInfo.versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
         return packageInfo != null;
        */

       return isApkInstalled(packname);


    }


    public static Context getPackageContext(Context context, String packageName) {
        Context pkgContext = null;
        if (context.getPackageName().equals(packageName)) {
            pkgContext = context;
        } else {
            // 创建第三方应用的上下文环境
            try {
                pkgContext = context.createPackageContext(packageName,
                        Context.CONTEXT_IGNORE_SECURITY
                                | Context.CONTEXT_INCLUDE_CODE);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pkgContext;
    }


    public static void openUrl(Context context, String url) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        intent.putExtra("", "");//这里Intent当然也可传递参数,但是一般情况下都会放到上面的URL中进行传递
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startApp(Context context, String appPackageName) {
        Context pg_context = getPackageContext(context, appPackageName);
        try {
            String mainAct = "";

            //根据包名寻找
            PackageManager pkgMag = pg_context.getPackageManager();   //

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|



            @SuppressLint("WrongConstant")
            List<ResolveInfo> list = pkgMag.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
            for (int i = 0; i < list.size(); i++) {
                ResolveInfo info = list.get(i);

            //    LogH.e("程序包名",info.activityInfo.packageName+"==="+info.activityInfo.name);

                if (info.activityInfo.packageName.equals(appPackageName)) {


                    mainAct = info.activityInfo.name;
                    break;
                }
            }
            if (TextUtils.isEmpty(mainAct)) {

                Toast.makeText(pg_context, "没有找到界面", Toast.LENGTH_LONG).show();
                return;
            }
            intent.setComponent(new ComponentName(appPackageName, mainAct));
          //  intent.putExtra("cmd", "restart");
            BaseApp.getAppContext().startActivity(intent);
            LogH.e("打开程序",appPackageName+"===="+mainAct);

            //   ComponentName componentName = new ComponentName(appPackageName, appPackageName+".MainActivity");//这里是 包名  以及 页面类的全称
            //  Intent intent  = new Intent(); // = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
            //  intent.setComponent(componentName);


        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(context, "打开应用失败", Toast.LENGTH_LONG).show();

        }

    }


    public static boolean isApkInstalled(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = BaseApp.getAppContext().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            LogH.e("isApkInstalled","isApkInstalled=====");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();


            LogH.e("isApkInstalled","isApkInstalled===printStackTrace=");
            return false;
        }
    }


    public static void openMainApp(Context context) {


        String packgeName = otherPackgeName;//uni.kjy.ctk
        if (AppUtil.checkPackInfo(packgeName)) {
            AppUtil.startApp(context, packgeName);


        } else {
            Toast.makeText(context, "没有安装应用", Toast.LENGTH_LONG).show();
        }

    }


    public static Activity getGlobleActivity(Context context) {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager m = (UsageStatsManager) BaseApp.getAppContext().getSystemService(Context.USAGE_STATS_SERVICE);
                if (m != null) {
                    long now = System.currentTimeMillis();
                    //获取60秒之内的应用数据
                    List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 600 * 1000, now);
                    LogH.e("======", "Running app number in last 60 seconds : " + stats.size());
                    String topActivity = "";
                    //取得最近运行的一个app，即当前运行的app
                    if ((stats != null) && (!stats.isEmpty())) {
                        //   int j = 0;
                        for (int i = 0; i < stats.size(); i++) {
                            // if (stats.get(i).getLastTimeUsed())) {
                            // j = iz
                            //  }
                            topActivity = stats.get(i).getPackageName();
                            LogH.e("======", "top running app is : " + topActivity);
                        }

                    }
                    //  return stats.size();
                }
            }


            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> mTasks = mActivityManager.getRunningTasks(3);//5.0

            if (mTasks.size() > 0) {
                for (ActivityManager.RunningTaskInfo item : mTasks) {
                    ComponentName topActivity = item.topActivity;
                    if (topActivity != null && !TextUtils.isEmpty(topActivity.getPackageName()) &&
                            !TextUtils.isEmpty(topActivity.getClassName())) {
                        LogH.e("======", "topActivity.getPackageName() -> " + topActivity.getPackageName());
                        LogH.e("======", "topActivity.getClassName() -> " + topActivity.getClassName());

                    }
                }

            }


            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            String packageName = context.getPackageName();
            List<ActivityManager.RunningTaskInfo> appTask = am.getRunningTasks(1);

            if (appTask != null) {
                if (appTask.size() > 0) {
                    if (appTask.get(0).topActivity.toString().contains(packageName)) {
                        LogH.e("Chunna.zheng====", appTask.get(0).topActivity.toString());
                    }
                }

            }



        /*   ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            LogH.e("Chunna.zheng", "pkg:"+cn.getPackageName());//包名
            LogH.e("Chunna.zheng", "cls:"+cn.getClassName());//包名加类名*/


            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);


            LogH.e("activities", activities.size() + "======");//包名加类名

            for (Object activityRecord : activities.values()) {


                Class activityRecordClass = activityRecord.getClass();


                Field pausedField = activityRecordClass.getDeclaredField("paused");


                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");


                    LogH.e("activities==", activityField.getName());//包名加类名

                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);


                    LogH.e("Chunna.zheng", "pkg:" + activity.getPackageName());//包名
                    LogH.e("Chunna.zheng", "cls:" + activity.getLocalClassName());//包名加类名


                    return activity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void clearMemory(Context context) {
        ActivityManager am = (ActivityManager) BaseApp.getAppContext().getSystemService(ACTIVITY_SERVICE);


        am.killBackgroundProcesses("com.kjy.ctk");

        List<ActivityManager.RunningAppProcessInfo> infoList = am.getRunningAppProcesses();

        int count = 0;
        if (infoList != null) {
            for (int i = 0; i < infoList.size(); ++i) {
                ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);
                LogH.e(context.getClass().getSimpleName(), "process name : ----------" + appProcessInfo.processName);
                //importance 该进程的重要程度  分为几个级别，数值越低就越重要。
                LogH.e(context.getClass().getSimpleName(), "importance : -----------" + appProcessInfo.importance);

                //只要不是com.tq.recorder这个包名的全部进程给禁止了
                if (!appProcessInfo.processName.equals("com.kjy.home")) {
                    String[] pkgList = appProcessInfo.pkgList;
                    for (int j = 0; j < pkgList.length; ++j) {//pkgList 得到该进程下运行的包名
                        LogH.e(context.getClass().getSimpleName(), "It will be killed, package name : ----------" + pkgList[j]);
                        //am.killBackgroundProcesses(pkgList[j]);
                        count++;
                        LogH.e(context.getClass().getSimpleName(), "count : ----------" + count + "");
                    }
                }
            }
        }

        //获取运行内存大小
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        long availMem = mi.availMem;
        long ava = availMem / 1024 / 1024;
        //开始清理后台运行内存,并显示到文本上
        LogH.e(context.getClass().getSimpleName(), "可用内存 " + (ava >= 1024 ? (String.format("%.2f", (float) ava / 1024) + "G") : (ava + "M")));//mCleanMemory TextView框显示当前运行内存
        //LogH.e(context.getClass().getSimpleName(), "清理完毕,当前可用运行内存:" + mCleanMemory.getText().subSequence(4, mCleanMemory.getText().length()), Toast.LENGTH_SHORT).show();
    }


    public static boolean isBackground(Context context) {

        ActivityManager activityManager = (ActivityManager) BaseApp.getAppContext().getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        LogH.e("appProcess", appProcesses.size() + "=========");

        if (appProcesses.size() > 0) {

            ActivityManager.RunningAppProcessInfo appProcess = appProcesses.get(0); // 判断第一个是不是自己的应用就可以了

            if (appProcess.processName.equals(context.getPackageName())) {

                LogH.e("前台", appProcess.processName);

                return false;

            } else {

                LogH.e("后台", appProcess.processName);

                return true;

            }


        }

        return false;

    }


/*
    KEYCODE_CALL 	拨号键 	5
    KEYCODE_ENDCALL 	挂机键 	6
    KEYCODE_HOME 	按键Home 	3
    KEYCODE_MENU 	菜单键 	82
    KEYCODE_BACK 	返回键 	4
    KEYCODE_SEARCH 	搜索键 	84
    KEYCODE_CAMERA 	拍照键 	27
    KEYCODE_FOCUS 	拍照对焦键 	80
    KEYCODE_POWER 	电源键 	26
    KEYCODE_NOTIFICATION 	通知键 	83
    KEYCODE_MUTE 	话筒静音键 	91
    KEYCODE_VOLUME_MUTE 	扬声器静音键 	164
    KEYCODE_VOLUME_UP 	音量增加键 	24
    KEYCODE_VOLUME_DOWN 	音量减小键 	25
    KEYCODE_ENTER 	回车键 	66
    KEYCODE_ESCAPE 	ESC键 	111
    KEYCODE_DPAD_CENTER 	导航键 确定键 	23
    KEYCODE_DPAD_UP 	导航键 向上 	19
    KEYCODE_DPAD_DOWN 	导航键 向下 	20
    KEYCODE_DPAD_LEFT 	导航键 向左 	21
    KEYCODE_DPAD_RIGHT 	导航键 向右 	22
    KEYCODE_MOVE_HOME 	光标移动到开始键 	122
    KEYCODE_MOVE_END 	光标移动到末尾键 	123
    KEYCODE_PAGE_UP 	向上翻页键 	92
    KEYCODE_PAGE_DOWN 	向下翻页键 	93
    KEYCODE_DEL 	退格键 	67
    KEYCODE_FORWARD_DEL 	删除键 	112
    KEYCODE_INSERT 	插入键 	124
    KEYCODE_TAB 	Tab键 	61
    KEYCODE_NUM_LOCK 	小键盘锁 	143
    KEYCODE_CAPS_LOCK 	大写锁定键 	115
    KEYCODE_BREAK 	Break/Pause键 	121
    KEYCODE_SCROLL_LOCK 	滚动锁定键 	116
    KEYCODE_ZOOM_IN 	放大键 	168
    KEYCODE_ZOOM_OUT 	缩小键 	169
    */

    public static long keyTime = new Date().getTime();

    public static void keyDown(final int key) {
        long now = new Date().getTime();
        if (now - keyTime < 500) {

            return;
        }

        keyTime = now;


        //需要系统签名
        new Thread() {
            @Override
            public void run() {
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);

             //   LogH.e("keyDown", "=====允许执行======" + "==" + key);
            }
        }.start();


        try {
            // Runtime runtime = Runtime.getRuntime();
            // runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 执行shell 命令， 命令中不必再带 adb shell
     *
     * @param cmd
     * @return Sting  命令执行在控制台输出的结果
     */
    private static String execByRuntime(String cmd) {
        Process process = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        try {
         //   LogH.e("execByRuntime", "===============" + cmd);
            String[] cmds = new String[]{"su", "-c", cmd};
            Runtime runtime = Runtime.getRuntime();
            process =    runtime.exec(cmds);


            // process = Runtime.getRuntime().exec(cmds);
            inputStreamReader = new InputStreamReader(process.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            int read;
            char[] buffer = new char[4096];
            StringBuilder output = new StringBuilder();
            while ((read = bufferedReader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
         //   LogH.e("execByRuntime", "===============" + output.toString() + " === ");
            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != inputStreamReader) {
                try {
                    inputStreamReader.close();
                } catch (Throwable t) {
                    //
                }
            }
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (Throwable t) {
                    //
                }
            }
            if (null != process) {
                try {
                    process.destroy();
                } catch (Throwable t) {
                    //
                }
            }
        }
    }


    /**
     * 显示虚拟导航栏菜单按钮.
     * 虚拟导航栏菜单按钮在4.0以后默认不显示，可以利用反射强行设置，调用位置须在setContentView之后
     * 具体可以参考5.0以及6.0中的PhoneWindow类源码
     *
     * @param window {@link Window}
     */
    public static void showNavigationMenuKey(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            showNavigationLollipopMR1(window);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            showNavigationIceCreamSandwich(window);
        }
    }

    /**
     * 显示虚拟导航栏菜单按钮.
     * Android 4.0 - Android 5.0
     * API 14 - 21
     *
     * @param window {@link Window}
     */
    private static void showNavigationIceCreamSandwich(Window window) {
        try {
            int flags = WindowManager.LayoutParams.class.getField("FLAG_NEEDS_MENU_KEY").getInt(null);
            window.addFlags(flags);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示虚拟导航栏菜单按钮.
     * Android 5.1.1 - Android 8.0
     * API 22 - 25
     *
     * @param window {@link Window}
     */
    private static void showNavigationLollipopMR1(Window window) {
        try {
            Method setNeedsMenuKey = Window.class.getDeclaredMethod("setNeedsMenuKey", int.class);
            setNeedsMenuKey.setAccessible(true);
            int value = WindowManager.LayoutParams.class.getField("NEEDS_MENU_SET_TRUE").getInt(null);
            setNeedsMenuKey.invoke(window, value);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 隐藏虚拟按键，并且全屏
     */
    public static void hideBottomUIMenu(Activity activity) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    private static final int SYSTEM_UI_FLAG_HIDE_NAVIGATION =
            // 1 | 2 | 4 | 0x200 | 0x400 | 0x00000100;
            0x00000100
                    | 0x00000200
                    | 0x00000400
                    | 0x00000002 // hide nav bar
                    | 0x00000004 // hide status bar
                    | 0x00001000;

    public static void hidSysNavigation(Activity activity) {
        if (Build.VERSION.SDK_INT > 10) {
            try {
                View v = activity.getWindow().getDecorView();
                Method m = View.class.getMethod("setSystemUiVisibility", int.class);
                m.invoke(v, new Object[]{SYSTEM_UI_FLAG_HIDE_NAVIGATION});
                //v.setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * Android 5.0以前版本 getRunningTasks()
     *
     * @param context
     * @return
     */
    public static String getTopAppPackageName(Context context) {
        String packageName = "";
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                ActivityManager mActivityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
                packageName = rti.get(0).topActivity.getPackageName();
            }
        } catch (Exception ignored) {
        }
        return packageName;
    }


    /**
     * 在Android 5.0中，google新增了getRunningAppProcesses()方法，用来获取所有当前运行的应用，具体代码实现如下
     *
     * @param context
     * @return
     */
    public static String getTop5AppPackageName(Context context) {

        String packageName = "";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        try {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();

                LogH.e("packageName===", "packageName->" + processes.size());

                if (processes.size() == 0) {
                    return packageName;
                }
                for (ActivityManager.RunningAppProcessInfo process : processes) {

                    if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return process.processName;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        LogH.e("packageName===", "packageName->" + packageName);
        return packageName;
    }




    /**
     * Android 6.0以上配合权限 USAGE_STATS_SERVICE
     * @param context
     * @return
     */
    public static String getTop6AppPackageName(Context context) {



        String packageName = "";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            final long end = System.currentTimeMillis();
            final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            if (null == usageStatsManager) {
                return packageName;
            }
            final UsageEvents events = usageStatsManager.queryEvents((end - 60 * 1000), end);
            if (null == events) {
                return packageName;
            }
            UsageEvents.Event usageEvent = new UsageEvents.Event();
            UsageEvents.Event lastMoveToFGEvent = null;
            while (events.hasNextEvent()) {
                events.getNextEvent(usageEvent);
                if (usageEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    lastMoveToFGEvent = usageEvent;
                }
            }
            if (lastMoveToFGEvent != null) {
                packageName = lastMoveToFGEvent.getPackageName();
            }
        }

        LogH.e("packageName===", "packageName->" + packageName);

        return packageName;
    }


    //5.0以上，没有usage state权限
    public static final int AID_APP = 10000;
    public static final int AID_USER = 100000;

    /**
     * 5.0以上，没有usage state权限
     *
     * @return
     */
    public static String getForegroundApp() {
        File[] files = new File("/proc").listFiles();
        int lowestOomScore = Integer.MAX_VALUE;
        String foregroundProcess = null;
        for (File file : files) {
            if (!file.isDirectory()) {
                continue;
            }
            int pid;

            try {
                pid = Integer.parseInt(file.getName());
            } catch (NumberFormatException e) {
                continue;
            }

            try {
                String cgroup = read(String.format("/proc/%d/cgroup", pid));
                String[] lines = cgroup.split("\n");
                String cpuSubsystem;
                String cpuaccctSubsystem;

                if (lines.length == 2) {// 有的手机里cgroup包含2行或者3行，我们取cpu和cpuacct两行数据
                    cpuSubsystem = lines[0];
                    cpuaccctSubsystem = lines[1];
                } else if (lines.length == 3) {
                    cpuSubsystem = lines[0];
                    cpuaccctSubsystem = lines[2];
                } else {
                    continue;
                }

                if (!cpuaccctSubsystem.endsWith(Integer.toString(pid))) {
                    // not an application process
                    continue;
                }
                if (cpuSubsystem.endsWith("bg_non_interactive")) {
                    // background policy
                    continue;
                }

                String cmdline = read(String.format("/proc/%d/cmdline", pid));

                LogH.e("===", "cmdline->" + cmdline);
                //屏蔽掉你自己的其他后台进程
                if (cmdline.contains("com.kjy.home")) {
                    continue;
                }
                if (cmdline.contains("com.android.systemui")) {
                    continue;
                }
                int uid = Integer.parseInt(cpuaccctSubsystem.split(":")[2]
                        .split("/")[1].replace("uid_", ""));
                if (uid >= 1000 && uid <= 1038) {
                    // system process

                    LogH.e("==foregroundProcess=", "foregroundProcess->" + cmdline);

                    continue;
                }
                int appId = uid - AID_APP;
                int userId = 0;
                // loop until we get the correct user id.
                // 100000 is the offset for each user.

                while (appId > AID_USER) {
                    appId -= AID_USER;
                    userId++;
                }

                if (appId < 0) {
                    continue;
                }
                // u{user_id}_a{app_id} is used on API 17+ for multiple user
                // account support.
                // String uidName = String.format("u%d_a%d", userId, appId);
                File oomScoreAdj = new File(String.format(
                        "/proc/%d/oom_score_adj", pid));
                if (oomScoreAdj.canRead()) {
                    int oomAdj = Integer.parseInt(read(oomScoreAdj
                            .getAbsolutePath()));
                    if (oomAdj != 0) {
                        continue;
                    }
                }
                int oomscore = Integer.parseInt(read(String.format(
                        "/proc/%d/oom_score", pid)));
                if (oomscore < lowestOomScore) {
                    lowestOomScore = oomscore;
                    foregroundProcess = cmdline;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return foregroundProcess;

    }

    private static String read(String path) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        output.append(reader.readLine());

        for (String line = reader.readLine(); line != null; line = reader
                .readLine()) {
            output.append('\n').append(line);
        }
        reader.close();
        return output.toString().trim();// 不调用trim()，包名后会带有乱码
    }


    public static String getInstallApps(Context context) {
        String packge = "";


        PackageManager localPackageManager = BaseApp.getAppContext().getPackageManager();
        List localList = localPackageManager.getInstalledPackages(0);
        for (int i = 0; i < localList.size(); i++) {
            PackageInfo localPackageInfo1 = (PackageInfo) localList.get(i);
            String str1 = localPackageInfo1.packageName.split(":")[0];
            if (((ApplicationInfo.FLAG_SYSTEM & localPackageInfo1.applicationInfo.flags) == 0) && ((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP & localPackageInfo1.applicationInfo.flags) == 0) && ((ApplicationInfo.FLAG_STOPPED & localPackageInfo1.applicationInfo.flags) == 0)) {
                LogH.e("运行中====",str1);
            }
        }

     //   packge = execByRuntime("dumpsys activity");

        //  adb shell dumpsys activity | grep “mFocusedActivity”
        return packge;


    }

    public static synchronized boolean getRootAhth() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "
                    + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取当前应用程序的包名
     *
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    /**
     * 获取程序 图标
     *
     * @param context
     * @param packname 应用包名
     * @return
     */
    public Drawable getAppIcon(Context context, String packname) {
        try {
            //包管理操作管理类
            PackageManager pm = context.getPackageManager();
            //获取到应用信息
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            return info.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取程序的版本号
     *
     * @param context
     * @param packname
     * @return
     */
    public String getAppVersion(Context context, String packname) {
        //包管理操作管理类
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(packname, 0);
            return packinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return packname;
    }


    /**
     * 获取程序的名字
     *
     * @param context
     * @param packname
     * @return
     */
    public static String getAppName(Context context, String packname) {
        //包管理操作管理类
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            return info.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return packname;
    }

    /*
     * 获取程序的权限
     */
    public String[] getAllPermissions(Context context, String packname) {
        try {
            //包管理操作管理类
            PackageManager pm = context.getPackageManager();
            PackageInfo packinfo = pm.getPackageInfo(packname, PackageManager.GET_PERMISSIONS);
            //获取到所有的权限
            return packinfo.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return null;
    }


    /**
     * 获取程序的签名
     *
     * @param context
     * @param packname
     * @return
     */
    public static String getAppSignature(Context context, String packname) {
        try {
            //包管理操作管理类
            PackageManager pm = context.getPackageManager();
            PackageInfo packinfo = pm.getPackageInfo(packname, PackageManager.GET_SIGNATURES);
            //获取当前应用签名
            return packinfo.signatures[0].toCharsString();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return packname;
    }

    /**
     * 获取当前展示 的Activity名称
     *
     * @return
     */
    private static String getCurrentActivityName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;
    }


/**
 * 安卓7.0静默安装命令
 *  String[] args = { "pm", "install","-i ","xxxxxx", "-r",filePath };
 *
 *     -i 后面的参数是应用报名
 *    还有一种场景，就是你的应用帮别的应用实现静默升级，这里的包名还是用你的应用包名
 */

public static void install(String apk_path,String flag)  {

  /*  String command ="pm install -i "+flag +" -r  " +apk_path;

    Process process =null;

    DataOutputStream os =null;

    try {

        process = Runtime.getRuntime().exec("su");

        os =new DataOutputStream(process.getOutputStream());

        os.writeBytes(command +"\n");

        os.writeBytes("exit\n");

        os.flush();

    }catch (Exception e) {

        e.printStackTrace();

    }
*/


  // 会提示安装

   /* Intent intent = new Intent();//Intent.ACTION_VIEW
    intent.setAction("android.intent.action.VIEW");
    intent.addCategory("android.intent.category.DEFAULT");
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.setDataAndType(Uri.fromFile(new File(apk_path)),"application/vnd.android.package-archive");
    BaseApp.getAppContext().startActivity(intent);*/

}



    /**
     * 静默卸载 系统签名
     */
    public static void unstallApk(Context context) {
        if (getRootAhth()) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent sender = PendingIntent.getActivity(context, 0, intent, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                PackageInstaller mPackageInstaller = context.getPackageManager().getPackageInstaller();
                mPackageInstaller.uninstall("com.mcy.adbcommanddemo", sender.getIntentSender());// 卸载APK
            }
        } else {
            Toast.makeText(context,"权限不够",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 提示卸载
     * @param context
     * @param packageName
     */
    private void uninstallApp(Context context, String packageName) {
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        context.startActivity(uninstallIntent);
        //setIntentAndFinish(true, true);
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String topPackage(){
        ActivityManager activityManager = (ActivityManager)BaseApp.getAppContext().getSystemService(ACTIVITY_SERVICE);
        assert activityManager != null;
        String topPackage = activityManager.getRecentTasks(1,activityManager.RECENT_IGNORE_UNAVAILABLE).get(0).topActivity.getPackageName();
       // LogH.e("TAG====","focusedPackage isSettingTop" + topPackage);
        return topPackage;
    }


    public static void excuteSuCMD(String currentTempFilePath) {
        Process process = null;
        OutputStream out = null;
        InputStream in = null;

        try {
            // 请求root
            process = Runtime.getRuntime().exec("su");
            out = process.getOutputStream();
            // 调用安装
            out.write(("pm install -r " + currentTempFilePath + "\n").getBytes());
            in = process.getInputStream();
            int len = 0;
            byte[] bs = new byte[256];
            while (-1 != (len = in.read(bs))) {
                String state = new String(bs,len);
                if (state.equals("Success\n")) {
                    //安装成功后的操作

                    LogH.e("安装完成","安装完成->");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 静默安装
     */
    public static boolean clientInstall(String apkPath){
        PrintWriter PrintWriter = null;
        Process process = null;
        try {

          //  Runtime.getRuntime().exec("pm install -r " + apkPath);

            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("chmod 777 "+apkPath);
            PrintWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
            PrintWriter.println("pm install -r "+apkPath);
//          PrintWriter.println("exit");
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
        //    int value = 0;
            LogH.e("安装完成","安装完成->"+value);
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }

    /**
     * 静默卸载
     */
    public static boolean clientUninstall(String packageName){
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            PrintWriter.println("pm uninstall "+packageName);
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }
    private static boolean returnResult(int value){
        // 代表成功
        if (value == 0) {
            return true;
        } else if (value == 1) { // 失败
            return false;
        } else { // 未知情况
            return false;
        }
    }
    /**
     * 将文件复制到system/app 目录
     * @param apkPath 特别注意格式：该路径不能是：/storage/emulated/0/app/QDemoTest4.apk 需要是：/sdcard/app/QDemoTest4.apk
     * @return
     */
    public static boolean copy2SystemApp(String apkPath){
        PrintWriter PrintWriter = null;
        Process process = null;
        String appName = "chetou.apk",cmd;

        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            cmd = "mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system";
            LogH.e("copy2SystemApp", cmd);
            PrintWriter.println(cmd);

            cmd = "cat "+apkPath+" > /system/app/"+appName;
            LogH.e("copy2SystemApp", cmd);
            PrintWriter.println(cmd);

            cmd = "chmod 777 /system/app/"+appName +" -R";
            LogH.e("copy2SystemApp", cmd);
            PrintWriter.println(cmd);

            cmd = "mount -o remount,ro -t yaffs2 /dev/block/mtdblock3 /system";
            LogH.e("copy2SystemApp", cmd);
            PrintWriter.println(cmd);
            PrintWriter.println("reboot");  //重启
            PrintWriter.println("exit");
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }





    public static int installSilent(String filePath) {
        File file = new File(filePath);
        if (filePath == null || filePath.length() == 0 || file == null || file.length() <= 0 || !file.exists() || !file.isFile()) {
            return 1;
        }

       // String[] args = { "pm", "install", "-r", filePath };
        String[] args =    { "pm", "install","-i",BaseApp.getPackgeName(), "-r",filePath };
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        int result;
        try {
            process = processBuilder.start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }

        // TODO should add memory is not enough here
        if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
            result = 0;
        } else {
            result = 2;
        }
        LogH.e("安装", HttpUtil.getAppName+"successMsg:" + successMsg + ", ErrorMsg:" + errorMsg);
        return result;
    }





    public static void silentInstall(Context context, String path) {
        try {
            Runtime.getRuntime().exec("pm install -r " + path);
//            Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
         //   UIUtils.showCustomToast("正在升级安装...");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "安装失败", Toast.LENGTH_SHORT).show();
            LogH.e("安装失败: "+ e.getMessage());
        }
    }

    public static void generalInstall(Context context, String path) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }












    private static final String TAG = "test-test";

    private static final int TIME_OUT = 60 * 1000;

    private static String[] SH_PATH = {
            "/system/bin/sh",
            "/system/xbin/sh",
            "/system/sbin/sh"
    };

    public static boolean executeInstallCommand(String filePath) {
        File apkFile=new File(filePath);

        //android p 安装命令
        String command =" cat " + apkFile.getAbsolutePath() + " | pm install -S "+ apkFile.length();

      //  String command = "pm install -i "+BaseApp.getPackgeName()+" --user 0 " + filePath;
        Process process = null;
        DataOutputStream os = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        try {
            process = runWithEnv(getSuPath(), null);
            if (process == null) {
                return false;
            }

            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("echo \"rc:\" $?\n");
            os.writeBytes("exit\n");
            os.flush();

          //  execLinuxCommand();

            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }

            Log.w(TAG, "errorMsg, " + errorMsg);

            // Handle a requested timeout, or just use waitFor() otherwise.
            if (TIME_OUT > 0) {
                long finish = System.currentTimeMillis() + TIME_OUT;
                while (true) {
                    Thread.sleep(300);
                    if (!isProcessAlive(process)) {
                        break;
                    }

                    if (System.currentTimeMillis() > finish) {
                        Log.w(TAG, "Process doesn't seem to stop on it's own, assuming it's hanging");
                        // Note: 'finally' will call destroy(), but you might still see zombies.
                        return true;
                    }
                }
            } else {
                process.waitFor();
            }

            // In order to consider this a success, we require to things: a) a proper exit value, and ...
            if (process.exitValue() != 0) {
                return false;
            }

            return true;

        } catch (FileNotFoundException e) {
            Log.w(TAG, "Failed to run command, " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.w(TAG, "Failed to run command, " + e.getMessage());
            return false;
        } catch (InterruptedException e) {
            Log.w(TAG, "Failed to run command, " + e.getMessage());
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                try {
                    // Yes, this really is the way to check if the process is still running.
                    process.exitValue();
                } catch (IllegalThreadStateException e) {
                    process.destroy();
                }
            }
        }
    }

    private static Process runWithEnv(String command, String[] customEnv) throws IOException {
        List<String> envList = new ArrayList<String>();
        Map<String, String> environment = System.getenv();
        if (environment != null) {
            for (Map.Entry<String, String> entry : environment.entrySet()) {
                envList.add(entry.getKey() + "=" + entry.getValue());
            }
        }

        if (customEnv != null) {
            for (String value : customEnv) {
                envList.add(value);
            }
        }

        String[] arrayEnv = null;
        if (envList.size() > 0) {
            arrayEnv = new String[envList.size()];
            for (int i = 0; i < envList.size(); i++) {
                arrayEnv[i] = envList.get(i);
            }
        }

        Process process = Runtime.getRuntime().exec(command, arrayEnv);
        return process;
    }

    /**
     * Check whether a process is still alive. We use this as a naive way to implement timeouts.
     */
    private static boolean isProcessAlive(Process p) {
        try {
            p.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    /** Get the SU file path if it exist */
    private static String getSuPath() {
        for (String p : SH_PATH) {
            File sh = new File(p);
            if (sh.exists()) {
                return p;
            }
        }
        return "su";
    }



/*

    private void execLinuxCommand(){
        String cmd= "sleep 120; am start -n 包名/包名.第一个Activity的名称";
        //Runtime对象
        Runtime runtime = Runtime.getRuntime();
        try {
            Process localProcess = runtime.exec("su");
            OutputStream localOutputStream = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
            localDataOutputStream.writeBytes(cmd);
            localDataOutputStream.flush();
            LogH.e(TAG,"设备准备重启");
        } catch (IOException e) {
            LogH.e(TAG,"strLine:"+e.getMessage());
            e.printStackTrace();

        }
    }
*/




    /**
     * 重启设备，需要系统权限，以及系统签名
     */
    public static void rebootSystem() {
        PowerManager pm = (PowerManager)BaseApp.getAppContext().getSystemService(Context.POWER_SERVICE);
        pm.reboot(null);
    }






    public void hideHomeBar() {
        int SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED = 0x00002000;
        BaseApp.getActivity().getWindow().getDecorView()
                .setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }






    public static void installApk(String file)
    {
     //   FileUtil.requestWritePermission(this);
        File apkfile = new File(file);
        if (!apkfile.exists()) return;

       // LogH.e("file-size","======"+file.length());
        // 通过Intent安装APK文件
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri = null;
        //判断版本是否是 7.0 及 7.0 以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(BaseApp.getAppContext(), "com.kjy.health.provider", apkfile);//BuildConfig.APPLICATION_ID +
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            apkUri = Uri.fromFile(apkfile);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        BaseApp.getAppContext().startActivity(intent);
    }




    public static void KEEP_SCREEN_ON(){
        try {
            BaseApp.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
           /* int flags
            各种锁的类型对CPU 、屏幕、键盘的影响：
            PARTIAL_WAKE_LOCK :保持CPU 运转，屏幕和键盘灯有可能是关闭的。
            SCREEN_DIM_WAKE_LOCK ：保持CPU 运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯
            SCREEN_BRIGHT_WAKE_LOCK ：保持CPU 运转，允许保持屏幕高亮显示，允许关闭键盘灯
            FULL_WAKE_LOCK ：保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度*/
        /*    PowerManager powerManager = null;
            PowerManager.WakeLock wakeLock = null;
            powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");*/
        }catch (Exception e){

        }
    }


    /**
     * 改变App当前Window亮度
     *
     * @param brightness
     */

    public static void setBrightness(int brightness) {
        try {
       /*     int max=Settings.System.getInt(BaseApp.getAppContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);

            Settings.System.putInt(BaseApp.getAppContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,brightness);*/

            Window window = BaseApp.getActivity().getWindow();

            WindowManager.LayoutParams lp = window.getAttributes();

            if (brightness == -1) {

                lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;

            } else {

                //需要注意的是，返回的亮度是介于0~255之间的int类型值（也是为什么我们将seekBar的MaxValue设置为255的原因）

                lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / (float)getBrightness();

            }

            window.setAttributes(lp);
/*            Uri uri = android.provider.Settings.System
                    .getUriFor("screen_brightness");

            android.provider.Settings.System.putInt(BaseApp.getAppContext().getContentResolver(), "screen_brightness",
                    brightness);
            // resolver.registerContentObserver(uri, true, myContentObserver);
            BaseApp.getAppContext().getContentResolver().notifyChange(uri, null);*/

           LogH.e("亮度设置："+lp.screenBrightness);
        }catch (Exception e){
            LogH.e(e.getMessage());
        }

    }
    /**
     * 获得系统亮度
     *
     * @return
     */

    public static int getBrightness() {

        int systemBrightness = 0;

        try {

            //获取系统当前的屏幕的亮度

            systemBrightness = Settings.System.getInt(BaseApp.getAppContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);

        } catch (Settings.SettingNotFoundException e) {

            e.printStackTrace();

        }

        return systemBrightness;

    }

    /**
     * 停止自动亮度调节
     *
     * @param activity
     */
    public static void stopAutoBrightness(Activity activity) {

        Settings.System.putInt(activity.getContentResolver(),

                Settings.System.SCREEN_BRIGHTNESS_MODE,

                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }


    /**
     * 开启亮度自动调节
     *
     * @param activity
     */
    public static void startAutoBrightness(Activity activity) {

        Settings.System.putInt(activity.getContentResolver(),

                Settings.System.SCREEN_BRIGHTNESS_MODE,

                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }




    private static PowerManager.WakeLock wakeLock;
    public static void acquireWakelock() {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) BaseApp.getAppContext().getSystemService(Context.POWER_SERVICE);

            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, BaseApp.getAppContext()
                    .getClass().getCanonicalName());
            wakeLock.acquire();

        }
    }



    public static void releaseWakelock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }



    /**
     * 强制隐藏键盘
     * @param v
     */
    public  static  void hideSystemKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager) (BaseApp.getAppContext())
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        fullScreenImmersive(BaseApp.getActivity().getWindow().getDecorView());
        BaseApp.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }



    private  static void fullScreenImmersive(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uiOptions);
        }
    }


    /**
     * 不显示下面的任务栏 4.4系统有效 其他好像没啥用
     * 康明
     * 2014-9-29
     * @param context
     */
    public static void closeBar(Context context) {

        try {
            // 需要root 权限
            Build.VERSION_CODES vc = new Build.VERSION_CODES();
            Build.VERSION vr = new Build.VERSION();
            String ProcID = "79"; //低于4.0

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                ProcID = "42"; // ICS AND NEWER  高于4.0
            }

            // 需要root 权限
            Process proc = Runtime.getRuntime().exec(
                    new String[] {
                            "su",
                            "-c",
                            "service call activity " + ProcID
                                    + " s16 com.android.systemui" }); // WAS 79
            proc.waitFor();
            Log.e(TAG,"执行成功");
        } catch (Exception ex) {
            Log.e(TAG,ex.getMessage());
            // Toast.makeText(context, ex.getMessage(),
            // Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 显示下面的任务栏
     * 康明
     * 2014-9-29
     *
     */

    public static void showBar() {
        try {
            /*
             * Process proc = Runtime.getRuntime().exec( new String[] { "am",
             * "startservice", "-n", "com.android.systemui/.SystemUIService" });
             */
            Process proc = Runtime
                    .getRuntime()
                    .exec("am startservice --user 0 -n com.android.systemui/.SystemUIService");
            //    am startservice --user 0 -n com.android.systemui/.SystemUIService
            proc = Runtime
                    .getRuntime()
                    .exec(new String[] { "am",
                            "startservice", "-n", "com.android.systemui/.SystemUIService" });

            proc.waitFor();
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }



    /**
     * 跳转系统设置
     * @param context
     * @param url
     */
    public static  void toSystemSet(Context context, String url){

        Intent intent =new Intent();
        intent.setClassName("com.android.settings", url);
        context.startActivity(intent);


    }

    /**
     * 跳转网页
     * @param url
     * @param title
     */
    public static void toWeb(String url, String title){

        Intent intent =new Intent();
        intent.setClass(BaseApp.getAppContext(), WebActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("title",title);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getAppContext().startActivity(intent);
        Log.e("打开","===>"+title+"===>"+url);


    }
}