package com.hxsn.ssklf.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hxsn.ssklf.model.SiteInfo;

import java.util.ArrayList;
import java.util.List;

import static com.hxsn.ssklf.TApplication.context;

/**
 *  Created by jiely on 2016/6/28.
 */
public class Shared {

    private static SharedPreferences shared;
    static {
        shared = context.getSharedPreferences("ssk_lf_dic", Context.MODE_PRIVATE);
    }


    public  static void setValue(String key,String value) {
        shared.edit().putString(key, value).apply();
    }

    public  static String getValue(String key) {
        return shared.getString(key, "");
    }



    /**
     * 把园区大棚站点列表持久化
     * @param siteList 站点列表信息
     */
    public static void setSiteList(List<SiteInfo> siteList){
        int size = siteList.size();
        shared.edit().putInt("siteListSize",size).apply();
        for(int i=0; i<size; i++){
            shared.edit().putString(i+"sitename",siteList.get(i).getName()).apply();
            shared.edit().putString(i+"siteid",siteList.get(i).getUuid()).apply();
        }
    }

    /**
     * 从shared中读取站点列表
     * @return  siteList 站点列表信息
     */
    public static List<SiteInfo> getSiteList(){
        int size = shared.getInt("siteListSize",0);
        List<SiteInfo> siteList = new ArrayList<>();
        for(int i=0; i<size; i++){
            SiteInfo siteInfo = new SiteInfo();
            siteInfo.setName(shared.getString(i+"sitename",""));
            siteInfo.setUuid(shared.getString(i+"siteid",""));
            siteList.add(siteInfo);
        }
        return siteList;
    }

    public static void setInt(String key, int count){
        shared.edit().putInt(key, count).apply();
    }
    public static int getInt(String key, int i){
        return shared.getInt(key, 0);
    }
}
