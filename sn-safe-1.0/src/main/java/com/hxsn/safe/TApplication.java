package com.hxsn.safe;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * 作者：jiely on 2017/6/19 17:22
 * 邮箱：songlj@fweb.cn
 */
public class TApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
