package com.hxsn.zzd.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.AndShared;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.videogo.activity.EZCameraListActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

//import com.hxsn.zzd.videogo.activity.EZCameraListActivity;

/**
 *
 * Created by jiely on 2017/1/18.
 */

public class Tools {


    /**
     * 按照指定格式转换为时间字符串"yyyy-MM-dd HH:mm:ss"
     * @param time 毫秒
     * @param format 格式
     * @return string
     */
    public static String formatTime(long time, String format){
        Date date = new Date(time);
        SimpleDateFormat formatter= new SimpleDateFormat(format);
        return formatter.format(date);
    }

    /**
     * 获取当前时间 月日时分
     * @return
     */
    public static String getCurTime(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return month+"月"+day+"日  "+hour+":"+minute;
    }

    /**
     * 获取本机手机号码
     * @param context 上下文
     * @return 手机号
     */
    public static String getLocalPhone(Context context){
        TelephonyManager phoneMgr=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneMgr.getSimOperatorName();
        LogUtil.i("ControlFragment","显示手机号:"+phoneMgr.getLine1Number());
        LogUtil.i("ControlFragment","手机型号:"+ Build.MODEL);
        String phone = phoneMgr.getLine1Number();
        if(phone == null){
            phone = "";
        }
        return phone;
    }

    /**
     * 获取本机手机型号
     * @param context 上下文
     * @return 手机型号
     */
    public static String getPhoneType(Context context){
        TelephonyManager phoneMgr=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneMgr.getSimOperatorName();
        LogUtil.i("ControlFragment","显示手机号:"+phoneMgr.getLine1Number());
        LogUtil.i("ControlFragment","手机型号:"+ Build.MODEL);
        String type = Build.MODEL;
        return type;
    }


    /**
     * 分别添加摄像头到萤石云和zzd
     */
    public static void addDevice(final Context context, final String mSerialNoStr, String mVerifyCode) {
        final String token = AndShared.getValue("accessToken");

        String addUrl = Const.URL_ADD_CAMERA_FOR_API+token+"&deviceSerial="+mSerialNoStr+"&validateCode="+mVerifyCode;
        //向萤石云添加设备
        new AndHttpRequest(context) {

            @Override
            public void getResponse(String response) {
                if(AndJsonUtils.getCode(response).equals("200")){
                    //向物联网添加设备
                    String zzdUrl = Const.URL_ADD_CAMERA_FOR_ZZD+ TApplication.user.getUserId()+"&deviceSerial="+mSerialNoStr;
                    new AndHttpRequest(context) {

                        @Override
                        public void getResponse(String response) {
                            String code = AndJsonUtils.getCode(response);
                            if(code.equals("200")){
                                AbToastUtil.showToast(context, "添加摄像头成功");
                                Intent intent = new Intent(context,EZCameraListActivity.class);
                                context.startActivity(intent);
                            }else if(code.equals("20013")){
                                AbToastUtil.showToast(context, "该设备已被他人添加");
                            }else if(code.equals("20018")){
                                AbToastUtil.showToast(context, "该用户不拥有该设备,请在萤石云客户端添加，并在后台指定用户");
                            }else {
                                AbToastUtil.showToast(context, AndJsonUtils.getResult(response));
                            }
                        }
                    }.doPost(zzdUrl);

                }else {
                    AbToastUtil.showToast(context, JsonUtils.getMessage(response));
                }
            }
        }.doPost(addUrl);
    }





}
