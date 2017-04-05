package com.andbase.ssk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.andbase.gson.Gson;
import com.andbase.ssk.entity.User;

/**
 *
 * Created by jiely on 2017/3/27.
 */
public class AndShared {

    private static SharedPreferences shared;

    public static void init(Context context, String sharedName) {
        shared = context.getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        LogUtil.i("Shared","shared="+shared);
    }

    public  static void setValue(String key,String value) {
        shared.edit().putString(key, value).apply();
    }

    public  static String getValue(String key) {
        return shared.getString(key, "");
    }


    public static void setVersion(String version) {
        shared.edit().putString("version", version).apply();
    }
    public static String getVersion() {
        return shared.getString("version", "");
    }


    public static User getUser() {
        String json = shared.getString("user_info", "");
        Gson gson = new Gson();
        User user = gson.fromJson(json,User.class);
        if(user == null){
            user = new User();
        }
        return user;
    }


    public static boolean saveUser(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        return shared.edit().putString("user_info", json).commit();
    }

    public static boolean clearUser() {
        Gson gson = new Gson();
        String json = gson.toJson(new User());
        return shared.edit().putString("user_info", json).commit();
    }

}
