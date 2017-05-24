package com.hxsn.jwb.utils;

import com.hxsn.jwb.model.ChickHome;
import com.hxsn.jwb.model.Device;
import com.hxsn.jwb.model.HourData;
import com.hxsn.jwb.model.Warning;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by jiely on 2017/1/18.
 */

public class JsonUtils {

    private static String[] params;



    /**
     * 获取鸡舍的设备列表
     * @param jsonString json
     * @return cameraInfoList
     */
    //private final static String[] DEVICELIST = {"id","name","temperature","createTime"};
    public static List<Device> getDeviceList(String jsonString){
        params = new String[]{"id","name","temperature","createTime"};
        List<Device> deviceList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject = jsonObject.getJSONObject("result");
            JSONArray jsonArray = jsonObject.optJSONArray("deviceList");
            if(jsonArray == null){
                return deviceList;
            }
            for(int i=0; i<jsonArray.length();i++){
                jsonObject = jsonArray.optJSONObject(i);

                Device device = new Device();
                device.setId(jsonObject.optString(params[0]));
                device.setName(jsonObject.optString(params[1]));

                device.setTemperate((float) jsonObject.optDouble(params[2]));
                device.setCreateTime(jsonObject.optInt(params[3]));

                deviceList.add(device);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceList;
    }




    /**
     * 获取鸡舍列表
     * @param jsonString json
     * @return cameraInfoList
     */
    //private final static String[] homeValues = {"id","name","isWarning","temperature","dayAge","createTime"};
    public static List<ChickHome> getChickHomeList(String jsonString){
        params = new String[]{"homeid","name","isWarning","temperature","dayAge","createTime"};
        List<ChickHome> chickHomeList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject = jsonObject.getJSONObject("result");
            JSONArray jsonArray = jsonObject.optJSONArray("homeList");
            if(jsonArray == null){
                return chickHomeList;
            }
            for(int i=0; i<jsonArray.length();i++){
                jsonObject = jsonArray.optJSONObject(i);

                ChickHome home = new ChickHome();
                home.setId(jsonObject.optString(params[0]));
                home.setName(jsonObject.optString(params[1]));
                home.setIsWarning(jsonObject.optString(params[2]));
                home.setTemperature((float) jsonObject.optDouble(params[3]));
                int day = jsonObject.optInt(params[4]);
                home.setDayAge("鸡龄："+day+"天");
                home.setCreateTime(jsonObject.optLong(params[5]));

                chickHomeList.add(home);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chickHomeList;
    }

    /**
     * 获取总页数
     * @param jsonString json
     * @return cameraInfoList
     */
    public static int getTotalSize(String jsonString){

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject = jsonObject.getJSONObject("result");
            int totalPage = jsonObject.optInt("totalSize");
            return totalPage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * json解析 获取设备小时数据
     * @param  jsonString json串
     * @return 列表
     */
    //private static final String[] SSKVALUELIST = {"id", "temperate", "dateType","hour"};
    public static List<HourData> getHourDataList(String jsonString){
        params = new String[]{"temperature","hour"};
        List<HourData> siteList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject = jsonObject.optJSONObject("result");
            JSONArray jsonArray = jsonObject.optJSONArray("temperatureList");
            if(jsonArray == null){
                return siteList;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.optJSONObject(i);
                HourData sskValue = new HourData();
               // sskValue.setId(jsonObject.optString(params[0]));
                sskValue.setTemperature((float) jsonObject.optDouble(params[0]));
               // sskValue.setDateType(jsonObject.optInt(params[2]));
                sskValue.setHour(jsonObject.optInt(params[1]));
                siteList.add(sskValue);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return siteList;
    }

    /**
     * json解析 获取报警预警列表
     * @param  jsonString json串
     * @return 列表
     */
    //private static final String[] SSKVALUELIST = {"id", "temperate", "dateType","hour"};
    public static List<Warning> getWarningList(String jsonString){
        params = new String[]{"id","name", "createTime", "deviceId","deviceName","info","isReaded"};
        List<Warning> siteList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject = jsonObject.optJSONObject("result");
            if(jsonObject == null){
                return siteList;
            }
            JSONArray jsonArray = jsonObject.optJSONArray("warningList");
            if(jsonArray == null){
                return siteList;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.optJSONObject(i);
                Warning sskValue = new Warning();
                sskValue.setId(jsonObject.optString(params[0]));
                sskValue.setName(jsonObject.optString(params[1]));
                sskValue.setCreateTime(jsonObject.optLong(params[2]));
                /*long timeLong = jsonObject.optLong(params[2]);
                Date date = new Date(timeLong);
                SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time=formatter.format(date);*/
                sskValue.setDeviceId(jsonObject.optString(params[3]));
                sskValue.setDeviceName(jsonObject.optString(params[4]));
                sskValue.setInfo(jsonObject.optString(params[5]));
                sskValue.setIsReaded(jsonObject.optInt(params[6]));

                siteList.add(sskValue);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return siteList;
    }


    /**
     * json解析 获取历史数据列表
     * @param  jsonString json串
     * @return 列表
     */
    //private static final String[] SSKVALUELIST = {"id", "temperate", "dateType","hour"};
    public static List<ChickHome> getHistoryList(String jsonString){
        params = new String[]{"createTime", "temperature"};
        List<ChickHome> siteList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject = jsonObject.optJSONObject("result");
            JSONArray jsonArray = jsonObject.optJSONArray("historyList");

            if(jsonArray == null){
                return siteList;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.optJSONObject(i);
                ChickHome sskValue = new ChickHome();
                sskValue.setCreateTime(jsonObject.optLong(params[0]));
                sskValue.setTemperature((float) jsonObject.optDouble(params[1]));

                siteList.add(sskValue);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return siteList;
    }

}
