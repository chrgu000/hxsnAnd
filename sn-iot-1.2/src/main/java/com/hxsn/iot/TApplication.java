package com.hxsn.iot;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.andbase.ssk.entity.NotifyInfo;
import com.andbase.ssk.entity.QuestionInfo;
import com.andbase.ssk.entity.User;
import com.andbase.ssk.utils.AndShared;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.iot.entity.GreenHouse;
import com.hxsn.iot.uitls.Const;
import com.hxsn.iot.uitls.Shared;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZCameraInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * 全局变量定义类
 *  Created by Administrator on 16-4-7.
 */
public class TApplication extends Application {

    public static TApplication instance;
    public static int versionType = Const.RELEASE_VERTION;  //0-text 1-debug, 2-release
    public static String URL_ZZD_STRING,URL_CMS_STRING;//农事汇url,在另一个IP下

    public static Context context;
    public static Resources resources;
    public static User user;

    public static List<Activity> activities;
    public static NotifyInfo notifyInfo;
    public static boolean isNotify = false;                     //判断有百度透传消息

    public static String webUrl="";                                //当前web的地址
    public static int mode = 1;
    public static GreenHouse defaultGreenHouse;             //默认大棚
    public static QuestionInfo questionInfo;                //点击的某个问题
    public static List<EZCameraInfo> cameraInfoList;//视频摄像头列表
    public static EZCameraInfo cameraInfo;//摄像头信息

    public static boolean isHouseView = false;//是否进入了棚室选择页面，解决进入棚室选择页面时没有返回按钮的问题
    public static String warningId = ""; //报警预警ID，当从通知栏过来的时候，需要此ID


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        user = new User();
        context = getApplicationContext();
        resources = getResources();

        Shared.init(getApplicationContext(),"iot_dic");
        AndShared.init(getApplicationContext(),"iot_dic");
        defaultGreenHouse = Shared.getGreenHouse();
        activities = new ArrayList<>();
        //initSDK();

        initImageLoader(context);

        switch (versionType) {
            case Const.TEST_VERTION:
                LogUtil.openLog();
                URL_ZZD_STRING = "http://msc.fweb.cn:7700/aiot2";
                URL_CMS_STRING = "http://115.28.140.121:8280/zzdcms";
                break;
            case Const.TEST_VERTION1:
                URL_ZZD_STRING = "http://115.28.140.121:8480/aiot2";
                URL_CMS_STRING = "http://115.28.140.121:8280/zzdcms";
                break;
            case Const.DEBUG_VERTION:
                URL_ZZD_STRING = "http://192.168.12.33:8080/sn-aiot-2.0";
                URL_CMS_STRING = "http://192.168.12.121:8280/zzdcms";
                break;
            //case Const.RELEASET_VERTION:
            case Const.RELEASE_VERTION:
                LogUtil.closeLog();
                URL_ZZD_STRING = "http://msc.fweb.cn:7700/aiot2";
                URL_CMS_STRING = "http://115.28.140.121:8280/zzdcms";
                break;
        }
    }


    public static EZOpenSDK getOpenSDK() {
        return EZOpenSDK.getInstance();
    }


    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

}
