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
 * 农事汇页面
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class Nong4Fragment extends Fragment {
    private Context context;
    private WebView webView;
    private String urlWebView;

    public Nong4Fragment(Context context) {
        this.context = context;
    }

    public Nong4Fragment() {
    }
    public static Nong4Fragment newInstance(Context context,int mode) {
        Nong4Fragment fragment = new Nong4Fragment(context);
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

        urlWebView = Const.URL_NSH_WEB+TApplication.user.getUserName();
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
        if(event.equals("home_nong4_canGoBack")){
            webView.goBack();
            if(!webView.canGoBack()){
                LogUtil.i("Nong4Fragment", "---------不能返回了");
                EventBus.getDefault().post("cannotBack");
            }else {
                EventBus.getDefault().post("nong4_canGoBack");
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    //设置webView
    private void setWebView() {
        webView.setVisibility(View.VISIBLE);

        webView.getSettings().setJavaScriptEnabled(true);
        LogUtil.i("Nong4Fragment","urlWebView="+urlWebView);

        BaseWebViewClient webViewClient = new BaseWebViewClient("nong4_canGoBack");
        webView.setWebViewClient(webViewClient);

        webView.loadUrl(urlWebView);
    }


}
