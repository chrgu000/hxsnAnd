package com.hxsn.zzd.utils;

import com.andbase.library.model.AbEntity;
import com.andbase.ssk.entity.User;
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
     *
     * @param jsonString
     * @return
     */
    public static List<AbEntity> getEntityList(String jsonString){
        List<AbEntity> entityList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.optJSONArray("result");
            if(jsonArray == null){
                return entityList;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                AbEntity department= new AbEntity();
                jsonObject = jsonArray.getJSONObject(i);
                department.setId(jsonObject.optString("id"));
                department.setName(jsonObject.optString("name"));
                entityList.add(department);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entityList;
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


    /**
     * desc:user解析
     * auther:jiely
     * create at 2015/10/10 19:52
     */
    public static User getUser(String jsonString) {
        params = new String[]{"userid", "username", "realname", "nickname","email", "phone", "address"};
        User user = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            int hasAddDevice = jsonObject.optInt("hasAddDevice");

            jsonObject = jsonObject.optJSONObject("result");
            String userId = jsonObject.optString(params[0]);
            user = new User();
            user.setUserId(userId);
            String userName = jsonObject.optString(params[1]);
            user.setUserName(userName);
            String realName = jsonObject.optString(params[2]);
            user.setRealName(realName);
            String nickName = jsonObject.optString(params[3]);
            user.setNickName(nickName);
            String email = jsonObject.optString(params[4]);
            user.setEmail(email);
            String phone = jsonObject.optString(params[5]);
            user.setPhone(phone);
            String address = jsonObject.optString(params[6]);
            user.setAddress(address);

            user.setHasAddDevice(hasAddDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

}
