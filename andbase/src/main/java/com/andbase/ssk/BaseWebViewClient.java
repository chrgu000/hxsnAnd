package com.andbase.ssk;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.andbase.ssk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

/**
 *
 * Created by jiely on 2017/4/13.
 */
public class BaseWebViewClient extends WebViewClient {
    private String event;//发送的eventBus消息

    public BaseWebViewClient(String event){
        this.event = event;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        //TApplication.sskCanGoback = true;//实时监控页面到二级页面了现在可以返回了
        EventBus.getDefault().post(event);//创建一个被观察者事件由HomeActivity来订阅,通知主界面显示回退按钮
        LogUtil.i("BaseWebViewClient", "shouldOverrideUrlLoading-url="+url);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        LogUtil.i("BaseWebViewClient", "onPageStarted-url="+url);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        LogUtil.i("BaseWebViewClient", "onPageFinished-url="+url);

    }
}
