package com.hxsn.zzd.fragment;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.andbase.ssk.BaseWebViewClient;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.utils.Const;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 问专家页面
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class WenFragment extends Fragment {
    private Context context;
    private WebView webView;
    private String urlWebView;

    public WenFragment(Context context) {
        this.context = context;
    }

    public WenFragment() {
    }
    public static WenFragment newInstance(Context context,int mode) {
        WenFragment fragment = new WenFragment(context);
        Bundle args = new Bundle();
        args.putInt("fragment_mode", mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        webView = (WebView)view.findViewById(R.id.web_view);

        if(TApplication.user == null){
            return view;
        }

        urlWebView = Const.URL_NSH_WEN + TApplication.user.getUserName();
        setWebView();

        //RxJava观察者和订阅者的注册，被观察者是JavaScriptInterface的showCnt方法，观察该方法是否被js远程调用
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        return view;
    }

    //订阅/观察者事件FirstEvent，被观察者是JavaScriptInterface的showCnt方法，如果该方法被js远程调用，则进行触发
    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onEventMainThread(String event){
        LogUtil.i("WenFragment", "---------消息订阅者订阅得到的消息event="+event);
        if(event.equals("home_wen_canGoBack")){
            webView.goBack();
            if(!webView.canGoBack()){
                LogUtil.i("WenFragment", "---------不能返回了");
                EventBus.getDefault().post("cannotBack");
            }else {
                EventBus.getDefault().post("wen_canGoBack");
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();
       // EventBus.getDefault().unregister(this);
    }

    //设置webView
    private void setWebView() {

        webView.getSettings().setJavaScriptEnabled(true);
        LogUtil.i("WenFragment","urlWebView="+urlWebView);

        BaseWebViewClient webViewClient = new BaseWebViewClient("wen_canGoBack");
        webView.setWebViewClient(webViewClient);

        webView.loadUrl(urlWebView);
    }


}
