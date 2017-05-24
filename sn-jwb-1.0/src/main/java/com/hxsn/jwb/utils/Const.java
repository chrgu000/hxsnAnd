package com.hxsn.jwb.utils;

import android.os.Environment;

import com.hxsn.jwb.TApplication;

import java.io.File;

/**
 * 常量
 */
public class Const {
    //version
    public static final int TEST_VERTION = 1;
    public static final int TEST_VERTION1 = 4;
    public static final int DEBUG_VERTION = 2;
    public static final int RELEASE_VERTION = 3;
    //public static final int RELEASET_VERTION = 6;
    public static final int SQLITE_VERTION = 2;              //数据库版本
    public static final String SQLITE_DBNAME = "zzd";            //数据库名称
    public final static int[] COLORS = {0xFFf7911b,0xFF17cbdb,0xFF800080,0xFFb4c169,0xFF00D400,0xFFFF4081};

    //常量
    public static String CODE_CHANNEL_ID = "channelId";//设备通道ID，百度推送分配给设备的id号；

    // 得到sdcard的root
    public static final File SDCARD_ROOT = Environment.getExternalStorageDirectory();// .getAbsolutePath();

    public static String PATH_WEN_IMAGE = SDCARD_ROOT+"/jwb/wen/";

    /*-----------------------------------物联网后台相关接口-------------------------------------*/
    //登录模块
    public static String URL_LOGIN = TApplication.URL_SSK_STRING + "/login.json";//jiwl&logpwd=123456
    public static String URL_EDIT_PSW = TApplication.URL_SSK_STRING + "/resetpwd.json";//修改密码
    public static String URL_MODIFY_MINE = TApplication.URL_SSK_STRING + "/updateUserinfo.json";//修改个人信息?uid=

    //升级模块
    public static String URL_UPDATE = "http://60.10.151.28:7990/ssk/sskapp/app/jwbappversion";

    //获取用户未读警情信息数量
    public static String URL_WARN_READ = TApplication.URL_JWB_STRING + "/getUnreadMessageNums.json?uid=";
    //获取用户所选大棚未读警情信息数量
    public static String URL_WARN_HOUSE_UNREAD = TApplication.URL_JWB_STRING + "/getUnreadMessageNums.json?uid=";

    //获取鸡舍列表
    public static String URL_GET_HOME_LIST = TApplication.URL_JWB_STRING + "/getHomeList.json";
    //获取最近三天鸡舍数据列表
    public static String URL_AVERAGE_DATA_LIST = TApplication.URL_JWB_STRING + "/getAverageDataList.json";
    //通过鸡舍ID获取设备列表
    public static String URL_HOME_DEIVICE_LIST = TApplication.URL_JWB_STRING + "/getDeviceHourList.json";
    //获取最近三天鸡舍内某个随时看设备数据列表
    public static String URL_DEVICE_HOUR_LIST = TApplication.URL_JWB_STRING + "/getDeviceHourList.json";
    //获取报警预警列表
    public static String URL_GET_WARNING_LIST = TApplication.URL_JWB_STRING + "/getWarningList.json";
    //获取鸡舍历史数据列表
    public static String URL_GET_HOME_HISTORY_LIST = TApplication.URL_JWB_STRING + "/getHomeHistoryList.json";
    //上传已读报警预警
    public static String URL_WARNING_READ = TApplication.URL_JWB_STRING + "/hasReaded.json";

    //上传已读报警预警
    public static String URL_SETTING_WARNNING_PARAMS = TApplication.URL_JWB_STRING + "/setWarning.json";


    /*-----------------------------------cms相关接口-------------------------------------*/
    //提问
    public static String URL_ASK_QUEST = TApplication.URL_CMS_STRING + "/app/addWen.json";
    //获取问题列表
    public static String URL_GET_QUEST_LIST = TApplication.URL_CMS_STRING + "/app/wzjhfList.json";
    //获取回复列表
    public static String URL_GET_ANSWER_LIST = TApplication.URL_CMS_STRING + "/app/wzjhfLook.json";

    public static String URL_NONGSH_INFO = TApplication.URL_CMS_STRING+"/app/xwzx.do?chid=";

    //提交回复内容
    public static String URL_SUBMIT_ANSWER = TApplication.URL_CMS_STRING + "/app/hfReply.json";

    //获取CMS用户信息
    public static final String URL_GET_CMS_USER_INFO = TApplication.URL_CMS_STRING + "/app/userinfo.json?username=";

    /*-----------------------------------cms相关页面-------------------------------------*/
    //农情站页面
    public static String URL_NQZHAN_WEB = TApplication.URL_CMS_STRING + "/app/nsh.do?chid=40288c9a55ec67500155ec79ebcd0008&username=";
    //农事汇webView
    public static String URL_NONGSH_LIST = TApplication.URL_CMS_STRING+"/app/channelList.json";//农事汇图标列表地址
    //问专家webView
    public static String URL_NSH_WEN = TApplication.URL_CMS_STRING + "/app/wzj.do?username=";
    //关于
    public static String URL_ABOUT_WEB = TApplication.URL_CMS_STRING + "/app/about.do";

    //推送新闻资讯类页面
    public static String URL_NEWS_WEB = TApplication.URL_CMS_STRING + "/app/xwzxLook.do?newsid=";

    //气象数据页面
    public static String URL_WEATHER_WEB = "http://msc.fweb.cn:7990/ssk/app/jwb/tianqi.do?jsuuid=";


}
