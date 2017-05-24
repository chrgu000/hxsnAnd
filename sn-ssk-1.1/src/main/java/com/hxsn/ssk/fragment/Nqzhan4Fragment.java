package com.hxsn.ssk.fragment;

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
import com.hxsn.ssk.R;
import com.hxsn.ssk.TApplication;
import com.hxsn.ssk.utils.Const;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * A simple {@link Fragment} subclass.  随时看
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
@SuppressLint("ValidFragment")
public class Nqzhan4Fragment extends Fragment {
    private Context context;
    private WebView webView;
    private String urlWebView;

    public Nqzhan4Fragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("JavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        webView = (WebView) view.findViewById(R.id.webView);

        if(TApplication.user == null){
            return view;
        }

        urlWebView =  Const.URL_NQZHAN_WEB+ TApplication.user.getUserId();
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
