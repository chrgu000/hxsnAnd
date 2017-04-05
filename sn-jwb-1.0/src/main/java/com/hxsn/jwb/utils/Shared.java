package com.hxsn.jwb.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.andbase.gson.Gson;
import com.andbase.gson.reflect.TypeToken;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.jwb.model.ChickHome;
import com.hxsn.jwb.model.Warning;

import java.util.List;


/**
 *  Created by jiely on 2016/6/28.
 */
public class Shared  {

    private static SharedPreferences shared;

    public static void init(Context context,String sharedName) {
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


    public static ChickHome getChickHome() {
        String json = shared.getString("chickHome_info", "");
        Gson gson = new Gson();
        ChickHome home = gson.fromJson(json,ChickHome.class);
        return home;
    }


    public static boolean saveChickHome(ChickHome chickHome) {

        Gson gson = new Gson();
        String json = gson.toJson(chickHome);

        return shared.edit().putString("chickHome_info", json).commit();
    }


    public static boolean saveWarningList(List list,Class c){
        Gson gson = new Gson();
        String json = gson.toJson(list);

        return shared.edit().putString(c.getSimpleName()+"list", json).commit();
    }

    public static List getWarningList(Class c) {
        String json = shared.getString(c.getSimpleName()+"list", "");
        Gson gson = new Gson();
        List<Warning> warningList = gson.fromJson(json, new TypeToken<List<Warning>>(){}.getType());
        return warningList;
    }

    /*public static boolean saveEntity(Serializable s, Class c){
        Gson gson = new Gson();
        String json = gson.toJson(s);

        return shared.edit().putString(c.getSimpleName(), json).commit();
    }

    public static Serializable getEntity(Class c) {
        String json = shared.getString(c.getSimpleName(), "");
        Gson gson = new Gson();
        Serializable s = (Serializable) gson.fromJson(json, c);
        return s;
    }*/

}
