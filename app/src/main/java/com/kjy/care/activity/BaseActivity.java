package com.kjy.care.activity;

import android.os.Build;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;


import com.kjy.care.util.AppUtil;
import com.kjy.care.util.MySqlUtil;

import androidx.fragment.app.FragmentActivity;


public class BaseActivity extends FragmentActivity {

    public MySqlUtil mysql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

          mysql = new MySqlUtil(this);






    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mysql.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtil.hideBottomUIMenu(this);
        AppUtil.hidSysNavigation(this);

       // SystemProperties.set("persist.sys.statusbar","2");
       // Settings.System.putInt(getContentResolver(), "status_bar_disabled", 1);
      //  Settings.System.putInt(getContentResolver(), "persist.sys.statusbar", 1);

     /*   隐藏了有些bug，还是不隐藏好。
        完全隐藏：adb shell wm overscan 0,0,0,-210
        恢复默认：adb shell wm overscan 0,0,0,0*/

        //设置2就显示，设置1就隐藏
        //RootCmd.execRootCmd("wm overscan 0,0,0,0");//有效 bug多

       // hideBottomUIMenu();
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    //手势退出 需要
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

      /*  BaseApp.LastTouchTime = new Date().getTime();

        boolean processTouchEvent = SwipeHelper.instance().processTouchEvent(ev);
        if (processTouchEvent) {
            return true;
        }*/
        return super.dispatchTouchEvent(ev);
    }

}
