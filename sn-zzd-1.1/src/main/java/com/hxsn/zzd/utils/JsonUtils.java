package com.hxsn.zzd.utils;

import com.hxsn.zzd.model.Gardening;
import com.hxsn.zzd.model.GreenHouse;
import com.videogo.openapi.bean.EZCameraInfo;

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
     * json解析 获取用户所有部门及大棚列表，每个用户可以有多个部门，每个部门有多个大棚
     * @param  jsonString json串
     * @return 气象站列表
     */
    public static List<Gardening> getGardeningList(String jsonString){
        List<Gardening> departmentList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.optJSONArray("result");
            if(jsonArray == null){
                return departmentList;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                Gardening department= new Gardening();
                jsonObject = jsonArray.getJSONObject(i);
                department.setId(jsonObject.optString("yquuid"));
                department.setName(jsonObject.optString("yqname"));
                JSONArray jsonArray2 = jsonObject.optJSONArray("dys");
                List<GreenHouse> greenHouseList = new ArrayList<>();
                if(jsonArray2 == null){
                    return departmentList;
                }
                for (int j = 0; j < jsonArray2.length(); j++){
                    GreenHouse greenHouse = new GreenHouse();
                    jsonObject = jsonArray2.getJSONObject(j);
                    greenHouse.setId(jsonObject.optString("dyuuid"));
                    greenHouse.setName(jsonObject.optString("dyname"));
                    greenHouseList.add(greenHouse);
                }
                department.setGreenHouseList(greenHouseList);
                departmentList.add(department);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return departmentList;
    }

    /**
     * 萤石云添加设备时未成功的消息
     * @param jsonString
     * @return
     */
    public static String getMessage(String jsonString){
        String desc;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            desc = jsonObject.optString("msg");
            return desc;
        } catch (JSONException e) {

            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取accessToken
     * @param jsonString json
     * @return token
     */
    public static String getAccessToken(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject = jsonObject.optJSONObject("data");
            return jsonObject.optString("accessToken");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取accessToken
     * @param jsonString json
     * @return page
     */
    public static String getTokenFromZzd(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject.optString("accessToken");
        } catch (Exception e) {
           e.printStackTrace();
        }
        return "";
    }


    /**
     * 获取设备列表
     * @param jsonString json
     * @return cameraInfoList
     */
    public static List<EZCameraInfo> getCameraListForZzd(String jsonString){
        List<EZCameraInfo> cameraInfoList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.optJSONArray("result");
            if(jsonArray == null){
                return cameraInfoList;
            }
            for(int i=0; i<jsonArray.length();i++){
                jsonObject = jsonArray.optJSONObject(i);

                EZCameraInfo cameraInfo = new EZCameraInfo();
                cameraInfo.setDeviceSerial(jsonObject.optString("deviceSerial"));
                cameraInfo.setCameraName(jsonObject.optString("deviceName"));
                cameraInfo.setCameraNo(1);
                cameraInfo.setIsShared(0);
                cameraInfo.setVideoLevel(0);
                cameraInfo.setCameraCover("");
                cameraInfoList.add(cameraInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cameraInfoList;
    }

}
