package com.andbase.common;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;

/**
 * Created by jiely on 2017/3/23.
 */
public class AndroidUtil {

    /**
     * 获取手机屏幕宽度
     *
     * @param activity
     * @return
     */
    public static int getWidthPix(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取手机屏幕高度
     *
     * @param activity
     * @return
     */
    public static int getHeightPix(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 打电话
     *
     * @param activity
     * @param phoneNumber
     */
    public static void callPhone(Activity activity, String phoneNumber) {
        Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber));
        activity.startActivity(phoneIntent);
    }

    /**
     * 获取android系统版本
     *
     * @return
     */
    public static int getAndroidOSVersion() {
        int osVersion;
        try {
            osVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            osVersion = 0;
        }

        return osVersion;
    }
}
