package com.kjy.care.util;

public class FastClickUtil {

    static long lastClickTime;

    public static synchronized boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 300) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
