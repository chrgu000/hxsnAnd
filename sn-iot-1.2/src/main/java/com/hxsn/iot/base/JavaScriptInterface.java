package com.hxsn.iot.base;

import android.webkit.JavascriptInterface;

import com.andbase.ssk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;


/**
 * js和android的交互，js可以调用android的本地方法，这个类下的方法都可以被js调用，因此方法显示灰色，并不表示这个方法真的没被调用
 * Created by jiely on 2017/2/22.
 */
public class JavaScriptInterface {

    public JavaScriptInterface() {

    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast() {
        LogUtil.i("TAG", "调用成功==================》》》》》");
    }

    @JavascriptInterface
    public void showCnt(int count) {
        if(count == 0){
            //创建一个被观察者事件由zzdFragment和HomeActivity来订阅
            EventBus.getDefault().post("JavaScriptInterface");
        }

        LogUtil.i("TAG", "调用成功==================》》》》》count="+count);
    }
}
