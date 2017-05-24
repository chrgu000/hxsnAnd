package com.hxsn.zzd.fragment;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
 * 实时监测页面
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class RealTimeFragment extends Fragment {
    private Context context;
    private WebView webView;
    private String urlWebView;

    public RealTimeFragment(Context context) {
        this.context = context;
    }

    public RealTimeFragment() {
    }
    public static RealTimeFragment newInstance(Context context,int mode) {
        RealTimeFragment fragment = new RealTimeFragment(context);
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

        if(TApplication.user == null || TApplication.defaultGreenHouse == null){
            return view;
        }

        Log.i("RealTimeFragment.class", "--------选择棚室"+TApplication.defaultGreenHouse.getName());

        urlWebView = Const.URL_REALTIME_WEB+ TApplication.user.getUserId()+"&dyid="+ TApplication.defaultGreenHouse.getId();
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
        LogUtil.i("RealTimeFragment", "---------消息订阅者订阅得到的消息event="+event);
        if(event.equals("home_realtime_canGoBack")){
            webView.goBack();
            if(!webView.canGoBack()){
                LogUtil.i("RealTimeFragment", "---------不能返回了");
                EventBus.getDefault().post("cannotBack");
            }else {
                EventBus.getDefault().post("realtime_canGoBack");
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
        LogUtil.i("RealTimeFragment","defaultGreenHouse.name="+TApplication.defaultGreenHouse.getName()+"urlWebView="+urlWebView);
        BaseWebViewClient webViewClient = new BaseWebViewClient("realtime_canGoBack");
        webView.setWebViewClient(webViewClient);

        webView.loadUrl(urlWebView);
    }




}
