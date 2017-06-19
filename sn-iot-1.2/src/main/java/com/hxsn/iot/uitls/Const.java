package com.hxsn.iot.uitls;

import android.os.Environment;

import com.hxsn.iot.TApplication;

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
    public static final String SQLITE_DBNAME = "iot";            //数据库名称


    //常量
    public static String CODE_CHANNEL_ID = "channelId";//设备通道ID，百度推送分配给设备的id号；

    // 得到sdcard的root
    public static final File SDCARD_ROOT = Environment.getExternalStorageDirectory();// .getAbsolutePath();
    public static final String PATH_IMAGES = SDCARD_ROOT + "/iot/";
    public static final int MSG_SUCCESS = 11;
    public static final int MSG_FAILURE = 12;
    //图片地址
    public static final String URL_PICTURE_PATH = TApplication.URL_ZZD_STRING + "getPictureFile?fileEntryId=";
    public static String PATH_APK = SDCARD_ROOT + "/iot/apkDownload/";
    public static String PATH_NONGSH_IMAGE_PATH = SDCARD_ROOT + "/iot/nongsh/";
    public static String PATH_WEN_IMAGE = SDCARD_ROOT+"/iot/wen/";

    /*-----------------------------------物联网后台相关接口-------------------------------------*/
    //登录模块
    public static String URL_LOGIN = TApplication.URL_ZZD_STRING + "/app/login.json?logname=";//jiwl&logpwd=123456
    public static String URL_EDIT_PSW = TApplication.URL_ZZD_STRING + "/app/resetpwd.json?uid=";//修改密码
    public static String URL_MODIFY_MINE = TApplication.URL_ZZD_STRING + "updateUserinfo.json";//修改个人信息?uid=
    //升级模块
    public static String URL_UPDATE = TApplication.URL_ZZD_STRING + "/app/zzd/appversion.json";

    //获取用户未读警情信息数量
    public static String URL_WARN_READ = TApplication.URL_ZZD_STRING + "/app/zzd/getUnreadMessageNums.json?uid=";
    //获取用户所选大棚未读警情信息数量
    public static String URL_WARN_HOUSE_UNREAD = TApplication.URL_ZZD_STRING + "/app/zzd/getUnreadMessageNums.json?uid=";

    //获取控制设备名称列表
    public static String URL_CONTROL_NAME_LIST = TApplication.URL_ZZD_STRING + "/app/aiot2/ctrllist.json?uid=";

    //获取用户当前设备状态
    public static String URL_DEVICE_STATUS = TApplication.URL_ZZD_STRING + "/app/aiot2/getControllerStatus.json?uid=";
    //控制用户当前设备
    public static String URL_CONTROL_DEVICE = TApplication.URL_ZZD_STRING + "/app/zzd2/doControl.json";
    //登记分机号码
    public static String URL_REGISTER_PHONE = TApplication.URL_ZZD_STRING + "/app/zzd2/doRegisterPhone.json";

    //获取园区及棚室列表
    public static String URL_GET_HOUSE_LIST = TApplication.URL_ZZD_STRING + "/app/zzd2/getYqDyData.json?uid=";

    //获取所有园区列表 售后人员可以获得所有园区以便添加设备
    public static String URL_GET_ALL_GARDEN = TApplication.URL_ZZD_STRING + "/app/zzd2/garden/getList.json?uid=";

    //获取PLC类型列表 售后人员添加设备
    public static String URL_GET_PLC_LIST = TApplication.URL_ZZD_STRING + "/app/zzd2/plc/typeList.json";

    //添加棚温宝设备
    public static String URL_ADD_ZZD = TApplication.URL_ZZD_STRING + "/app/zzd2/device/create.json";

    /*-----------------------------------物联网后台相关页面-------------------------------------*/
    //早知道页面
    public static String URL_ZZD_WEB = TApplication.URL_ZZD_STRING + "/app/zzd/index.do?uid=";

    //控制页面
    public static String URL_CONTROL_WEB = TApplication.URL_ZZD_STRING+"/app/zzd/kongzhi.do?uid=";
    //告警页面
    public static String URL_WARN_WEB = TApplication.URL_ZZD_STRING + "/app/zzd/bjyjxq.do?uid=";

    //早知道实时监测页面
    public static String URL_REALTIME_WEB = TApplication.URL_ZZD_STRING + "/app/aiot2/index.do?uid=";

    //早知道气象数据页面
    public static String URL_WEATHER_WEB = TApplication.URL_ZZD_STRING + "/app/zzd2/qixiang.do?uid=";
    //早知道预警报警页面
    public static String URL_WARNING_WEB = TApplication.URL_ZZD_STRING + "/app/zzd2/baojing.do?uid=";

    //PLC手工设置
    public static String URL_SYSTEM_SETTING = TApplication.URL_ZZD_STRING+"/app/zzd/swzParams.do?uid=";

    //PLC模板设置
    public static String URL_TEMPLATE_SETTING = TApplication.URL_ZZD_STRING+"/app/zzd2/setPlcParam.do?uid=";
    /*-----------------------------------cms相关接口-------------------------------------*/
    //提问
    public static String URL_ASK_QUEST = TApplication.URL_CMS_STRING + "/app/addWen.json";
    //获取问题列表
    public static String URL_GET_QUEST_LIST = TApplication.URL_CMS_STRING + "/app/wzjhfList.json?username=";
    //获取回复列表
    public static String URL_GET_ANSWER_LIST = TApplication.URL_CMS_STRING + "/app/wzjhfLook.json?wzjid=";

    //提交回复内容
    public static String URL_SUBMIT_ANSWER = TApplication.URL_CMS_STRING + "/app/hfReply.json";

    //获取CMS用户信息
    public static final String URL_GET_CMS_USER_INFO = TApplication.URL_CMS_STRING + "/app/userinfo.json?username=";

    /*-----------------------------------cms相关页面-------------------------------------*/
    //农情站页面
    public static String URL_NQZHAN_WEB = TApplication.URL_CMS_STRING + "/app/nsh.do?chid=40288c9a55ec67500155ec79ebcd0008&username=";
    //农事汇webView
    public static String URL_NSH_WEB = TApplication.URL_CMS_STRING + "/app/nsh.do?chid=40288c9a55ec488c0155ec4d86720000&username=";
    //问专家webView
    public static String URL_NSH_WEN = TApplication.URL_CMS_STRING + "/app/wzj.do?username=";
    //关于
    public static String URL_ABOUT_WEB = TApplication.URL_CMS_STRING + "/app/about.do";

    //推送新闻资讯类页面
    public static String URL_NEWS_WEB = TApplication.URL_CMS_STRING + "/app/xwzxLook.do?newsid=";

    //获取萤石云账号token
    public static String URL_GET_ACCESS_TOKEN  = "https://open.ys7.com/api/lapp/token/get?appKey=896e18cccd614e9ab54daf169e9e7348&appSecret=1a33e376845a239a3ad306ee6140da67";
    //获取设备列表
    public static String  URL_GET_DEVICE_LIST = "https://open.ys7.com/api/lapp/device/list?accessToken=";
    //获取设备列表
    public static String  URL_GET_CAMERA_LIST_FOR_HXSN = TApplication.URL_ZZD_STRING + "/app/zzd2/cameralist.json?uid=";
    //获取摄像头列表
    public static String  URL_GET_CAMERA_LIST = "https://open.ys7.com/api/lapp/camera/list?accessToken=";
    //添加摄像头
    public static String  URL_ADD_CAMERA_FOR_API = "https://open.ys7.com/api/lapp/device/add?accessToken=";

    public static String  URL_ADD_CAMERA_FOR_ZZD = TApplication.URL_ZZD_STRING + "/app/zzd2/cameraadd.json?uid=";


}
