package com.hxsn.ssk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.andbase.ssk.entity.NotifyInfo;
import com.andbase.ssk.entity.QuestionInfo;
import com.andbase.ssk.entity.User;
import com.andbase.ssk.utils.AndShared;
import com.hxsn.ssk.utils.Const;

import java.util.ArrayList;
import java.util.List;



/**
 *  Created by jiely on 2016/6/28.
 */
public class TApplication extends Application{
    public static int versionType = Const.RELEASE_VERTION;  //0-text 1-debug, 2-release
    public static Context context;
    public static Resources resources;
    public static List<Activity> activities;
    public static String URL_STRING,URL_CMS_STRING;//cms url,在另一个IP下
    //public static String URL_UPDATE;//升级的地址有外网和内网
    public static int intAndroidSDK = android.os.Build.VERSION.SDK_INT;
    public static User user;
    public static NotifyInfo notifyInfo;

    public static String newsUrl = "";//农事汇或咨询的url
    public static QuestionInfo questionInfo;                //点击的某个问题

    @Override
    public void onCreate() {
        super.onCreate();

        user = new User();//防止空指针异常
        context = getApplicationContext();
        resources = getResources();

        AndShared.init(getApplicationContext(),"jwb");

        activities = new ArrayList<>();
        switch (versionType) {
            case Const.TEST_VERTION:
                URL_STRING = "http://192.168.12.94:7990/ssk/sskapp";
                URL_CMS_STRING = "http://192.168.12.26:80/sskcms";
                break;
            case Const.TEST_VERTION1:
                URL_STRING = "http://60.10.151.28:7990/ssk/sskapp";
                URL_CMS_STRING = "http://60.10.151.28:8980/sskcms";
                break;
            case Const.DEBUG_VERTION:
                URL_STRING = "http://192.168.12.94:7990/ssk/sskapp";
                URL_CMS_STRING = "http://192.168.12.94:8980/sskcms";
                break;

            case Const.RELEASE_VERTION:
                URL_STRING = "http://60.10.151.28:7990/ssk/sskapp";
                URL_CMS_STRING = "http://60.10.151.28:8980/sskcms";
                break;
        }
    }

}
