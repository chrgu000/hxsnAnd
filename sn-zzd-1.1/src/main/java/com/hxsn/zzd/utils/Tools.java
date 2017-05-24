package com.hxsn.zzd.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.AndShared;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.videogo.activity.EZCameraListActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    public static String formatTime(long time, String format) {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    /**
     * 获取当前时间 月日时分
     * @return
     */
    public static String getCurTime() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return month + "月" + day + "日  " + hour + ":" + minute;
    }

    /**
     * 获取本机手机号码
     * @param context 上下文
     * @return 手机号
     */
    public static String getLocalPhone(Context context) {
        TelephonyManager phoneMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneMgr.getSimOperatorName();
        LogUtil.i("ControlFragment", "显示手机号:" + phoneMgr.getLine1Number());
        LogUtil.i("ControlFragment", "手机型号:" + Build.MODEL);
        String phone = phoneMgr.getLine1Number();
        if (phone == null) {
            phone = "";
        }
        return phone;
    }

    /**
     * 获取本机手机型号
     * @param context 上下文
     * @return 手机型号
     */
    public static String getPhoneType(Context context) {
        TelephonyManager phoneMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneMgr.getSimOperatorName();
        LogUtil.i("ControlFragment", "显示手机号:" + phoneMgr.getLine1Number());
        LogUtil.i("ControlFragment", "手机型号:" + Build.MODEL);
        String type = Build.MODEL;
        return type;
    }


    /**
     * 分别添加摄像头到萤石云和zzd
     */
    public static void addDevice(final Context context, final String mSerialNoStr, String mVerifyCode) {
        final String token = AndShared.getValue("accessToken");

        String addUrl = Const.URL_ADD_CAMERA_FOR_API + token + "&deviceSerial=" + mSerialNoStr + "&validateCode=" + mVerifyCode;
        //向萤石云添加设备
        new AndHttpRequest(context) {

            @Override
            public void getResponse(String response) {
                if (AndJsonUtils.getCode(response).equals("200")) {
                    //向物联网添加设备
                    String zzdUrl = Const.URL_ADD_CAMERA_FOR_ZZD + TApplication.user.getUserId() + "&deviceSerial=" + mSerialNoStr;
                    new AndHttpRequest(context) {

                        @Override
                        public void getResponse(String response) {
                            String code = AndJsonUtils.getCode(response);
                            if (code.equals("200")) {
                                AbToastUtil.showToast(context, "添加摄像头成功");
                                Intent intent = new Intent(context, EZCameraListActivity.class);
                                context.startActivity(intent);
                            } else if (code.equals("20013")) {
                                AbToastUtil.showToast(context, "该设备已被他人添加");
                            } else if (code.equals("20018")) {
                                AbToastUtil.showToast(context, "该用户不拥有该设备,请在萤石云客户端添加，并在后台指定用户");
                            } else {
                                AbToastUtil.showToast(context, AndJsonUtils.getResult(response));
                            }
                        }
                    }.doPost(zzdUrl);

                } else {
                    AbToastUtil.showToast(context, JsonUtils.getMessage(response));
                }
            }
        }.doPost(addUrl);
    }


    /**
     * 通过GPS得到位置详细信息
     *
     * @param context
     *            一個Activity
     * @return 城市名
     */
    public static String getCityName(Context context) {
        LocationManager locationManager;
        String contextString = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) context.getSystemService(contextString);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        // 取得效果最好的criteria
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider == null) {
            return null;
        }
        // 得到坐标相关的信息
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return null;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            return null;
        }

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            // 更具地理环境来确定编码
            Geocoder gc = new Geocoder(context, Locale.getDefault());
            try {
                // 取得地址相关的一些信息\经度、纬度
                List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {

                    Address address = addresses.get(0);
                    return address.getAddressLine(0);
                    //sb.append(address.getLocality()).append("\n");
                   // cityName = sb.toString();
                }
            } catch (IOException e) {
            }
        }
        return "";
    }


}
