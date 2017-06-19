package com.hxsn.ssklf;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.andbase.ssk.entity.NotifyInfo;
import com.andbase.ssk.utils.AndShared;
import com.hxsn.ssklf.model.SiteInfo;
import com.hxsn.ssklf.model.SiteValue;
import com.hxsn.ssklf.model.WarningInfo;
import com.hxsn.ssklf.utils.Const;
import com.hxsn.ssklf.utils.Shared;

import java.util.ArrayList;
import java.util.List;

/**
 *  Created by jiely on 2016/6/28.
 */
public class TApplication extends Application{
    public static int versionType = Const.TEST_VERTION;  //0-text 1-debug, 2-release
    public static Context context;
    public static Resources resources;
    public static List<Activity> activities;
    public static String URL_STRING,URL_NONGSH_STRING;//农事汇url,在另一个IP下
    public static int intAndroidSDK = android.os.Build.VERSION.SDK_INT;
    public static NotifyInfo notifyInfo;
    public static boolean isUpdateNongshImage= false;//判断是否需要更新图标,默认不需要更新，进入APP首先在APPBiz中判断是否更新
    public static SiteInfo curSiteInfo;
    public static SiteValue curSiteValue;
    public static List<WarningInfo> warningInfoList;//国家应急

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        resources = getResources();

        AndShared.init(getApplicationContext(),"ssk_lf_dic");

        String name = Shared.getValue("siteName");
        String uuid = Shared.getValue("siteUUid");
        if(name.length() > 0){
            curSiteInfo = new SiteInfo(name,uuid);
        }else {
            curSiteInfo = new SiteInfo("永清新苑阳光A01","2c908a905905bb1801590680f4c50039");
        }

        activities = new ArrayList<>();
        switch (versionType) {
            case Const.TEST_VERTION:
                URL_STRING = "http://msc.fweb.cn:7700/aiot2/app/qixj/";
                URL_NONGSH_STRING = "http://60.10.151.28:8980/sskcms";
                break;
            case Const.TEST_VERTION1:
                URL_STRING = "http://60.10.151.28:7990/ssk/sskapp/";
                URL_NONGSH_STRING = "http://60.10.151.28:8980/sskcms";
                break;
            case Const.DEBUG_VERTION:
                URL_STRING = "http://192.168.12.94:7990/ssk/sskapp/";
                URL_NONGSH_STRING = "http://192.168.12.94:8980/sskcms";
                break;
            case Const.RELEASE_VERTION:
                URL_STRING = "http://msc.fweb.cn:7700/aiot2/app/qixj/";
                URL_NONGSH_STRING = "http://60.10.151.28:8980/sskcms";
                break;
        }

    }
}
