package com.hxsn.witwork.utils;

import android.os.Environment;

import com.hxsn.witwork.TApplication;

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


    public static final String BARCODE_BITMAP = "barcode_bitmap";


    //常量
    public static String CODE_CHANNEL_ID = "channelId";//设备通道ID，百度推送分配给设备的id号；

    // 得到sdcard的root
    public static final File SDCARD_ROOT = Environment.getExternalStorageDirectory();// .getAbsolutePath();
    public static final String PATH_IMAGES = SDCARD_ROOT + "/zzd/";
    public static final int MSG_SUCCESS = 11;
    public static final int MSG_FAILURE = 12;
    //图片地址
    public static final String URL_PICTURE_PATH = TApplication.URL_STRING + "getPictureFile?fileEntryId=";
    public static String PATH_APK = SDCARD_ROOT + "/zzd/apkDownload/";
    public static String PATH_NONGSH_IMAGE_PATH = SDCARD_ROOT + "/zzd/nongsh/";
    public static String PATH_WEN_IMAGE = SDCARD_ROOT+"/zzd/wen/";

    /*-----------------------------------物联网后台相关接口-------------------------------------*/
    //登录模块
    public static String URL_LOGIN = TApplication.URL_STRING + "/app/login.json?app=3&";//jiwl&logpwd=123456

    //升级模块
    public static String URL_UPDATE = TApplication.URL_STRING + "/app/zzd/appversion.json";

    public static String GETSERVERS = TApplication.URL_STRING+"/app/getservices.json?app=3&uid=";
    /*****************我的模块****************************/
    //意见反馈
    public static String YJFK=TApplication.URL_STRING+"/app/feedback.do?app=3&uid=";
    //获取个人信息
    public static String GETMYINFO=TApplication.URL_STRING+"/app/getpersonal.json?app=3&uid=";
    //更新头像
    public static String UPDATEHEAD=TApplication.URL_STRING+"/app/setpersonal.json";
    //更新昵称
    public static String UPDATENAME=TApplication.URL_STRING+"/app/setpersonal.json?app=3&uid=";
    //更新性别
    public static String UPDATESEX=TApplication.URL_STRING+"/app/setpersonal.json?app=3&uid=";
    //更新地址
    public static String UPDATEADDRESS=TApplication.URL_STRING+"/app/setpersonal.json?app=3&uid=";
    // 关于农庄
    public static String ABOUTFARM = TApplication.URL_STRING + "/app/nongzhuang.do?app=3&uid=";
    public static String WELCOME = TApplication.URL_STRING + "/app/welcome.do?app=3&uid=";
    /*****************追溯模块**********************************/
    public static String  ZHUISU=TApplication.URL_STRING+"/app/ zhuisu.do?app=3&uid=";

    /*****************游园模块**********************************/
    public static String  YOUYUAN=TApplication.URL_STRING+"/app/dikuai.do?app=3&uid=";
    public static String  DKFQ=TApplication.URL_STRING+"/app/dikuaifq.do?app=3&uid=";



    /*****************农事模块**********************************/
    public static String  NONGSHI=TApplication.URL_STRING+"/app/zixun.do?app=3&uid=";
    public static String  DIKUAI=TApplication.URL_STRING+"/app/getdikuai.json?app=3&uid=";
    //提交参数app=3&uid=userid&dkid=G2A04
    public static String  ProductLL=TApplication.URL_STRING+"/app/lvli.do?app=3&uid=";


    /*****************采摘模块**********************************/
    public static String  CAIZHAI=TApplication.URL_STRING+"/app/caizhai.do?app=3&uid=";


    /*****************作业模块**********************************/

    //获取我的作业
    public static String  DK_NAME=TApplication.URL_STRING+"/app/getDkInfo.json";

    //提交作业登记数据接口
    //dkcodes hjname neirong filedata
    public static String  SUBMITWORK=TApplication.URL_STRING+"/app/addzuoye.json?";
    //获取我的作业
    public static String  GETMYWORK=TApplication.URL_STRING+"/app/myzuoye.do?app=3&uid=";
    //获取收获取接口 code
    //{"code":200,"result":{"name":"芹菜",//生成的商品名称 "lvnums":"5",//履历信息个数"units":"Kg"}}
    public static String SHSM=TApplication.URL_STRING+"/app/getHarvestInfo.json?app=3&uid=";
    /**提交收货接口
     code	地块编码，扫描的二维码内容
     name	商品名称
     lvnums	履历信息个数
     caidan	更新菜单主料：0不更新、1更新
     songcai	更新送菜清单：0不更新、1更新
     qrcode	打印二维码：0不打印、1打印（手持二维码打印机用）
     nums	收获数量**/
    public static String SUBMITSH=TApplication.URL_STRING+"/app/addHarvestInfo.json?";
    //采摘登记URL
    public static String CAIZHAIDJ=TApplication.URL_STRING+"/app/caizhaidj.do?app=3&uid=";
    //作业信息详情页面的URL
    //jobuuid
    //wst.javaFun(‘作业信息ID’,2);
    public static String ZYDETAIL=TApplication.URL_STRING+"/app/myzuoyexq.do?app=3&uid=";
    /*****************视频服务器地址**********************************/
    //旧视频地址
    //public static String  VideoURL="http://60.10.151.28";
    //新视频地址--外网
    //  public static String  VideoURL="http://60.10.69.100";
    public static String VideoIP="60.10.69.100";
    //内网视频地址
    //public static String  VideoIP="192.168.15.2";
    public static String  VideoName="appsystem";
    public static String  VideoPwd="a12345678";



}
