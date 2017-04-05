package com.hxsn.ssk.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
        Log.i("ControlFragment","显示手机号:"+phoneMgr.getLine1Number());
        Log.i("ControlFragment","手机型号:"+ Build.MODEL);
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
        Log.i("ControlFragment","显示手机号:"+phoneMgr.getLine1Number());
        Log.i("ControlFragment","手机型号:"+ Build.MODEL);
        String type = Build.MODEL;
        return type;
    }


}
