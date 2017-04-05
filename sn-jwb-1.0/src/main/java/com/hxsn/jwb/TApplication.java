package com.hxsn.jwb;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;

import com.andbase.library.util.AbLogUtil;
import com.andbase.ssk.entity.NotifyInfo;
import com.andbase.ssk.entity.QuestionInfo;
import com.andbase.ssk.entity.User;
import com.andbase.ssk.utils.AndShared;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.jwb.model.ChickHome;
import com.hxsn.jwb.utils.Const;
import com.hxsn.jwb.utils.Shared;

import java.util.ArrayList;
import java.util.List;


/**
 *  Created by Administrator on 16-4-7.
 */
public class TApplication extends Application {
    public static TApplication instance;
    public static int versionType = Const.RELEASE_VERTION;  //0-text 1-debug, 2-release
    public static String URL_JWB_STRING,URL_SSK_STRING,URL_CMS_STRING;//农事汇url,在另一个IP下

    public static Context context;
    public static Resources resources;
    public static int intAndroidSDK = android.os.Build.VERSION.SDK_INT;
    public static User user;

    public static boolean isNotify = false;                     //判断有百度透传消息
    public static String newsUrl = "";                          //农事汇或咨询的url
    public static String webUrl="";                                //当前web的地址
    public static Handler.Callback baiduNotifyCallback;         //百度推送的回调消息

    public static int mode = 1;
    public static List<Activity> activityList;
    public static NotifyInfo notifyInfo;
   // public static GreenHouse defaultGreenHouse;             //默认大棚
    public static QuestionInfo questionInfo;                //点击的某个问题
    public static boolean sskCanGoback = false;
    public static List<ChickHome> homeList;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AbLogUtil.setVerbose(false,false,true);
        LogUtil.closeLog();

        activityList = new ArrayList<>();

        context = getApplicationContext();
        resources = getResources();

        Shared.init(getApplicationContext(),"jwb");
        AndShared.init(getApplicationContext(),"jwb");

        switch (versionType) {
            case Const.TEST_VERTION:
                URL_JWB_STRING = "http://115.28.140.121:8480/aiot2";
                URL_CMS_STRING = "http://192.168.12.26/zzdcms";
                break;
            case Const.TEST_VERTION1:
                URL_JWB_STRING = "http://115.28.140.121:8480/aiot2";
                URL_CMS_STRING = "http://115.28.140.121:8280/zzdcms";
                break;
            case Const.DEBUG_VERTION:
                URL_JWB_STRING = "http://192.168.12.121:8480/aiot2";
                URL_CMS_STRING = "http://192.168.12.121:8280/zzdcms";
                break;
            //case Const.RELEASET_VERTION:
            case Const.RELEASE_VERTION:
                URL_JWB_STRING = "http://60.10.151.28:7990/ssk/app/jwb";
                URL_SSK_STRING = "http://60.10.151.28:7990/ssk/sskapp";
                URL_CMS_STRING = "http://115.28.140.121:8280/zzdcms";
                break;
        }
    }

    public static void exit(){
        mode = 1;
        for(Activity activity: activityList){
            activity.finish();
        }
        activityList.removeAll(activityList);
        activityList.clear();

    }



}
