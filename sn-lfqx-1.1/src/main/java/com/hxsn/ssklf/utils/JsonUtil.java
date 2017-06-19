package com.hxsn.ssklf.utils;

import android.util.Log;


import com.hxsn.ssklf.model.SiteInfo;
import com.hxsn.ssklf.model.SiteValue;
import com.hxsn.ssklf.model.Threshold;
import com.hxsn.ssklf.model.WarningInfo;
import com.hxsn.ssklf.model.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *  Created by jiely on 2016/10/10.
 */
public class JsonUtil {

    /**
     * param  jsonString
     * return String    返回类型
     * author：jiely
     * Date：2015-2-5
     * Title: getToken
     * Description: 从后台获取Token
     */
    public static boolean getStatus(String jsonString) {
        try {
            Log.i("JsonUtil","jsonString="+jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.toString() == null || jsonObject.toString().length() == 0) {
                return false;
            }
            return jsonObject.optBoolean("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * param  jsonString
     * return String    返回类型
     * author：jiely
     * Date：2015-2-5
     * Title: getToken
     * Description: 获取当前天气
     */

    private final static String[] weathers={"观测时间","气温","相对湿度","天气","风向风力"};
    public static Weather getWeather(String jsonString){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            Weather weather = new Weather();
            String str = jsonObject.optString(weathers[0]);
            weather.setCreateTime(str);
            str = jsonObject.optString(weathers[1]);
            weather.setTemperature(str);
            str = jsonObject.optString(weathers[2]);
            weather.setHumidity(str);
            str = jsonObject.optString(weathers[3]);
            weather.setName(str);
            str = jsonObject.optString(weathers[4]);
            weather.setWindy(str);
            return weather;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private final static String[] weather7Days={"星期","天气","天气2","气温","气温2","风向风力"};
    public static List<Weather> get7Weathers(String jsonString){
        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            List<Weather> weatherList = new ArrayList<>();

            for(int i=0; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Weather weather = new Weather();
                String str = jsonObject.optString(weather7Days[0]);
                weather.setCreateTime(str);
                str = jsonObject.optString(weather7Days[1]);
                String str2 = jsonObject.optString(weather7Days[2]);
                if(str.equals(str2)){
                    weather.setName(str);
                }else {
                    weather.setName(str+"转"+str2);
                }

                str = jsonObject.optString(weather7Days[3]);
                str2 = jsonObject.optString(weather7Days[4]);
                weather.setTemperature(str+"-"+str2+"℃");

                str = jsonObject.optString(weather7Days[5]);
                weather.setWindy(str);
                weatherList.add(weather);
            }

            return weatherList;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final String[] thresholdArray = {"upTemp","downTemp","upHumidity","downHumidity","upTemp15cm","downTemp15cm","upSunlight","downSunlight"};
    /**
     * json解析 获取参数阈值
     * @param  jsonString json串
     * @return 阈值
     */
    public static Threshold getThreshold(String jsonString){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject = jsonObject.optJSONObject("threshold");
            Threshold threshold = new Threshold();
            int hold = jsonObject.optInt(thresholdArray[0]);
            threshold.setUpTemp(hold);
            hold = jsonObject.optInt(thresholdArray[1]);
            threshold.setDownTemp(hold);
            hold = jsonObject.optInt(thresholdArray[2]);
            threshold.setUpHumidity(hold);
            hold = jsonObject.optInt(thresholdArray[3]);
            threshold.setDownHumidity(hold);
            hold = jsonObject.optInt(thresholdArray[4]);
            threshold.setUpTemp15cm(hold);
            hold = jsonObject.optInt(thresholdArray[5]);
            threshold.setDownTemp15cm(hold);
            hold = jsonObject.optInt(thresholdArray[6]);
            threshold.setUpSunlight(hold);
            hold = jsonObject.optInt(thresholdArray[7]);
            threshold.setDownSunlight(hold);
            return threshold;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * json解析 获取气象站数据
     * @param  jsonString json串
     * @return 气象站数据
     */
    public static SiteValue getSiteValue(String jsonString){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject = jsonObject.optJSONObject("siteVal");
            SiteValue siteValue = paseSiteValue(jsonObject);
            return siteValue;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * json解析 获取气象站数据列表
     * @param  jsonString json串
     * @return 列表
     */
    public static List<SiteValue> getSiteList(String jsonString){
        List<SiteValue> siteList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.optJSONArray("siteValList");

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.optJSONObject(i);
                SiteValue siteValue = paseSiteValue(jsonObject);
                siteList.add(siteValue);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return siteList;
    }

    private static SiteValue paseSiteValue(JSONObject jsonObject){
        SiteValue siteValue = new SiteValue();
        siteValue.setTimeInfo(jsonObject.optString("timeInfo"));
        Double airTemp = jsonObject.optDouble("airTemp");
        siteValue.setTemperature(airTemp);
        Double humidity = jsonObject.optDouble("humidity");
        siteValue.setHumidity(humidity);
        airTemp = jsonObject.optDouble("temp15cm");
        siteValue.setSoilTemp(airTemp);
        humidity = jsonObject.optDouble("sunlight");
        siteValue.setIllu(humidity);
        return siteValue;
    }

    /**
     * json解析 获取气象站列表
     * @param  jsonString json串
     * @return 气象站列表
     */
    public static List<SiteInfo> getSiteInfoList(String jsonString){
        List<SiteInfo> siteInfoList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.optJSONArray("siteList");

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.optJSONObject(i);
                SiteInfo siteValue = new SiteInfo();
                String str = jsonObject.optString("name");
                siteValue.setName(str);
                str = jsonObject.optString("uuid");
                siteValue.setUuid(str);
                siteInfoList.add(siteValue);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return siteInfoList;
    }

    private final static String[] warningParams = {"title","content","createTime"};

    /**
     * 获取廊坊市气象预警列表信息，来源于
     * @param jsonString
     * @return
     */
    public static List<WarningInfo> getWarningList(String jsonString){
        List<WarningInfo> warningInfoList = new ArrayList<>();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.optJSONArray("warning_list");

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.optJSONObject(i);
                WarningInfo warningInfo = new WarningInfo();
                String str = jsonObject.optString(warningParams[0]);
                warningInfo.setTitle(str);
                str = jsonObject.optString(warningParams[1]);
                warningInfo.setContent(str);
                long createTime = jsonObject.optLong(warningParams[2]);
                warningInfo.setCreateTime(createTime);
                warningInfoList.add(warningInfo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return warningInfoList;
    }

}
