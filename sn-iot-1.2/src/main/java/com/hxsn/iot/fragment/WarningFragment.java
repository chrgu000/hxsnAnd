package com.hxsn.iot.fragment;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.andbase.ssk.utils.AndShared;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.iot.R;
import com.hxsn.iot.TApplication;
import com.hxsn.iot.activity.LoginActivity;
import com.hxsn.iot.base.JavaScriptInterface;
import com.hxsn.iot.uitls.Const;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 报警预警页面
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class WarningFragment extends Fragment {
    private Context context;
    private Button btnLook;
    private WebView webView;
    private String urlWebView;
    private int lookMode = 0;

    public WarningFragment(Context context) {
        this.context = context;
    }
    public WarningFragment() {
    }
    public static WarningFragment newInstance(Context context,int mode) {
        WarningFragment fragment = new WarningFragment(context);
        Bundle args = new Bundle();
        args.putInt("fragment_mode", mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_warning, container, false);

        webView = (WebView)view.findViewById(R.id.web_view);
        btnLook = (Button)view.findViewById(R.id.btn_look);
        btnLook.setVisibility(View.VISIBLE);
        btnLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(lookMode == 0){
                    lookMode = 1;
                    btnLook.setText("查看当前");
                    if(TApplication.user == null){
                        return;
                    }
                    urlWebView = Const.URL_WARNING_WEB+ TApplication.user.getUserId()+"&dyid=";

                }else if(lookMode == 1){
                    lookMode = 0;
                    btnLook.setText("查看所有");
                    if(TApplication.user == null || TApplication.defaultGreenHouse == null){
                        return;
                    }
                    urlWebView = Const.URL_WARNING_WEB+ TApplication.user.getUserId()+"&dyid="+ TApplication.defaultGreenHouse.getId()+"&warningid="+TApplication.warningId;
                    TApplication.warningId = "";

                }
                LogUtil.i(WarningFragment.class,"lookMode="+lookMode);
                LogUtil.i(WarningFragment.class,"urlWebView="+urlWebView);
                setWebView();
            }
        });

        if(TApplication.user == null || TextUtils.isEmpty(TApplication.user.getUserId())) {
            TApplication.user = AndShared.getUser();
            if(TextUtils.isEmpty(TApplication.user.getUserId())){//进入登录页面
                Intent intent = new Intent();
                intent.setClass(context, LoginActivity.class);
                startActivity(intent);
            }
        }
        if(TApplication.defaultGreenHouse == null){
            return view;
        }

        urlWebView = Const.URL_WARNING_WEB+ TApplication.user.getUserId()+"&dyid="+ TApplication.defaultGreenHouse.getId();
        LogUtil.i(WarningFragment.class,"urlWebView="+urlWebView);
        setWebView();

        //RxJava观察者和订阅者的注册，被观察者是JavaScriptInterface的showCnt方法，观察该方法是否被js远程调用
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        return view;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onEventMainThread(String event){
        LogUtil.i("WarningFragment", "---------消息订阅者订阅得到的消息event="+event);
        if(event.equals("home_warning_canGoBack")){
            webView.goBack();
            if(!webView.canGoBack()){
                btnLook.setVisibility(View.VISIBLE);
                LogUtil.i("WarningFragment", "---------不能返回了--发送cannotBack");
                EventBus.getDefault().post("cannotBack");
            }else {
                LogUtil.i("WarningFragment", "---------还可以返回--发送warning_canGoBack");
                EventBus.getDefault().post("warning_canGoBack");
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
        LogUtil.i("WarningFragment","defaultGreenHouse.name="+TApplication.defaultGreenHouse.getName()+"urlWebView="+urlWebView);

        JavaScriptInterface javaScriptInterface = new JavaScriptInterface();
        webView.addJavascriptInterface(javaScriptInterface,"androidZzd");
        MyWebViewClient webViewClient = new MyWebViewClient();
        webView.setWebViewClient(webViewClient);

        webView.loadUrl(urlWebView);
    }


    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            //TApplication.sskCanGoback = true;//实时监控页面到二级页面了现在可以返回了
            btnLook.setVisibility(View.GONE);
            LogUtil.i("BaseWebViewClient", "在shouldOverrideUrlLoading里发送warning_canGoBack---url="+url);
            EventBus.getDefault().post("warning_canGoBack");//创建一个被观察者事件由HomeActivity来订阅,通知主界面显示回退按钮

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.i("BaseWebViewClient", "onPageStarted-url=" + url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogUtil.i("BaseWebViewClient", "onPageFinished-url=" + url);

        }
    }

}
