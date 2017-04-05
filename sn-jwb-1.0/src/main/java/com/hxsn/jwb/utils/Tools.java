package com.hxsn.jwb.utils;

import android.text.TextUtils;

import com.hxsn.jwb.model.ChickHome;

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

    public static String getHomeId(){
        ChickHome home = Shared.getChickHome();
        String homeId = "";
        if(home != null){
            if(TextUtils.isEmpty(homeId)){
                homeId = "2c908ade5af4f077015af4f564a20001";
            }
        }else {
            homeId = "2c908ade5af4f077015af4f564a20001";
        }
        return homeId;
    }


}
