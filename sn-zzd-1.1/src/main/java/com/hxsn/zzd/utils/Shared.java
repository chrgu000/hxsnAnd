package com.hxsn.zzd.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.andbase.gson.Gson;
import com.andbase.gson.reflect.TypeToken;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.zzd.model.Gardening;
import com.hxsn.zzd.model.GreenHouse;

import java.util.List;


/**
 *  Created by jiely on 2016/6/28.
 */
public class Shared {

    private static SharedPreferences shared;

    public static void init(Context context,String sharedName) {
        shared = context.getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        LogUtil.i("Shared","shared="+shared);
    }


    public static GreenHouse getGreenHouse() {
        String json = shared.getString("GreenHouse", "");
        Gson gson = new Gson();
        GreenHouse greenHouse = gson.fromJson(json,GreenHouse.class);
        if(greenHouse == null){
            greenHouse = new GreenHouse();
        }
        return greenHouse;
    }


    public static boolean saveGreenHouse(GreenHouse greenHouse) {
        Gson gson = new Gson();
        String json = gson.toJson(greenHouse);
        return shared.edit().putString("GreenHouse", json).commit();
    }


    public static Gardening getGardening() {
        String json = shared.getString("garden", "");
        Gson gson = new Gson();
        Gardening gardening = gson.fromJson(json,Gardening.class);
        if(gardening == null){
            gardening = new Gardening();
        }
        return gardening;
    }


    public static boolean saveGardening(Gardening gardening) {
        Gson gson = new Gson();
        String json = gson.toJson(gardening);
        return shared.edit().putString("garden", json).commit();
    }



    public static boolean saveGardingList(List list){
        Gson gson = new Gson();
        String json = gson.toJson(list);

        return shared.edit().putString(Gardening.class.getSimpleName()+"list", json).commit();
    }

    public static List getGardingList() {
        String json = shared.getString(Gardening.class.getSimpleName()+"list", "");
        Gson gson = new Gson();
        List<Gardening> gardeningList = gson.fromJson(json, new TypeToken<List<Gardening>>(){}.getType());
        return gardeningList;
    }

}
