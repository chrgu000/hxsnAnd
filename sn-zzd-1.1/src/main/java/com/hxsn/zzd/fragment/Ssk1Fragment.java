package com.hxsn.zzd.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andbase.common.RxBus;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.activity.HomeActivity;
import com.hxsn.zzd.activity.MoreActivity;
import com.hxsn.zzd.base.JavaScriptInterface;
import com.hxsn.zzd.utils.Const;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;

import rx.Subscription;
import rx.functions.Action1;


/**
 * 早知道首页面
 * create an instance of this fragment.
 */
@SuppressLint("ValidFragment")
public class Ssk1Fragment extends Fragment implements View.OnClickListener{
    private Context context;
    private Activity activity;
    private static final String TAG ="Ssk1Fragment";

    private RelativeLayout layout1, layout2, layout3, layout4, layout5;
    private ImageView img1, img2, img3, img4, img5;
    private TextView txt1, txt2, txt3, txt4, txt5;
    private ImageView imgPoint;//报警预警的圆点
    private WebView webView;
    private String urlWebView;//webView地址
    private Fragment controlFragment;
    private FragmentManager fm;
    private FragmentTransaction transaction;
    private FrameLayout frameLayout;
    private LinearLayout lineTitle;
    private Subscription subscription;//rxJava-rxBus观察者，观察返回按钮有没有被按下

    public Ssk1Fragment() {
    }

    public Ssk1Fragment(Context context) {
        this.context = context;
    }

    @Override
    public void onStart() {
        LogUtil.i("SskFragment", "onStart");
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.i("SskFragment", "onCreate");
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.i("SskFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_ssk1, container, false);
        addView(view);
        addFragment();
        addListener();

        //处理activity的返回键消息
        handleBackMessage();

        //获取用户所选大棚未读报警预警数量
        obtainUnReadWarning();
        //RxJava观察者和订阅者的注册，被观察者是JavaScriptInterface的showCnt方法，观察该方法是否被js远程调用
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        return view;
    }

    //订阅/观察者事件FirstEvent，被观察者是JavaScriptInterface的showCnt方法，如果该方法被js远程调用，则进行触发
    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onEventMainThread(String event){
        LogUtil.i("Ssk1Fragment", "---------消息订阅者订阅得到的消息event="+event);
        if(event.equals("JavaScriptInterface")){
            imgPoint.setVisibility(View.GONE);
        }else if(event.equals("sskGoBack")){
            webView.goBack();
        }else if(event.equals("deviceStatus")){//收到手动控制的控制云推送消息了
            setClickView(4);//进入设备控制页面
        }
    }

    /**
     * 接收HomeActivity用rxJava发回来的的返回键消息
     * 使用rxJava来处理activity和fragment的消息传递
     */
    private void handleBackMessage() {
        subscription =  RxBus.getInstance().toObserverable(String.class)
                .subscribe(new Action1<String>() {

                    @Override
                    public void call(String s) {
                        LogUtil.i("SskFragment", s);
                        TApplication.mode = 1;
                        setClickView(1);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }


    private void addFragment() {
        controlFragment = new ControlFragment();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            fm = getChildFragmentManager();
        }else {
            fm = getFragmentManager();
        }
        transaction = fm.beginTransaction();
        if(transaction.isEmpty()){
            transaction.add(R.id.framelayout, controlFragment);
        }else {
            transaction.replace(R.id.framelayout, controlFragment);
        }

        transaction.commit();
    }

    private void obtainUnReadWarning() {
        if(TApplication.user == null || TApplication.defaultGreenHouse == null){
            return;
        }
        String url = Const.URL_WARN_HOUSE_UNREAD + TApplication.user.getUserId()+"&dyid="+ TApplication.defaultGreenHouse.getId();
        new AndHttpRequest(context) {
            @Override
            public void getResponse(String response) {

                String result = AndJsonUtils.getResult(response);
                LogUtil.i(TAG,"------------未读预警信息个数----result="+result);
                if(!result.equals("0")){//没有未读预警
                    imgPoint.setVisibility(View.VISIBLE);
                }else {
                    imgPoint.setVisibility(View.GONE);
                }
            }
        }.doGet(url);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(subscription != null){
            subscription.unsubscribe();
        }

        EventBus.getDefault().unregister(this);

        //！！！勿删   解决Activity has been destroyed的问题
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addListener() {
        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        layout3.setOnClickListener(this);
        layout4.setOnClickListener(this);
        layout5.setOnClickListener(this);
    }

    private void addView(View view) {
        layout1 = (RelativeLayout) view.findViewById(R.id.layout1);
        img1 = (ImageView) view.findViewById(R.id.img1);
        txt1 = (TextView) view.findViewById(R.id.txt1);
        layout2 = (RelativeLayout) view.findViewById(R.id.layout2);
        img2 = (ImageView) view.findViewById(R.id.img2);
        txt2 = (TextView) view.findViewById(R.id.txt2);
        layout3 = (RelativeLayout) view.findViewById(R.id.layout3);
        img3 = (ImageView) view.findViewById(R.id.img3);
        txt3 = (TextView) view.findViewById(R.id.txt3);
        layout4 = (RelativeLayout) view.findViewById(R.id.layout4);
        img4 = (ImageView) view.findViewById(R.id.img4);
        txt4 = (TextView) view.findViewById(R.id.txt4);
        layout5 = (RelativeLayout) view.findViewById(R.id.layout5);
        img5 = (ImageView) view.findViewById(R.id.img5);
        txt5 = (TextView) view.findViewById(R.id.txt5);

        webView = (WebView)view.findViewById(R.id.webView);
        frameLayout = (FrameLayout)view.findViewById(R.id.framelayout);
        lineTitle = (LinearLayout)view.findViewById(R.id.line_title);

        imgPoint = (ImageView)view.findViewById(R.id.img_red_point);
    }

    //设置webView
    private void setWebView() {
        webView.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.GONE);
        webView.removeAllViews();

        webView.getSettings().setJavaScriptEnabled(true);
        LogUtil.i("Ssk1Fragment-setWebView","urlWebView="+urlWebView);

        JavaScriptInterface javaScriptInterface = new JavaScriptInterface();
        webView.addJavascriptInterface(javaScriptInterface,"androidZzd");
        MyWebViewClient webViewClient = new MyWebViewClient();
        webView.setWebViewClient(webViewClient);

        webView.loadUrl(urlWebView);
    }

    private void setClickView(int mode) {
        LogUtil.i("SskFragment","setClickView mode="+ TApplication.mode);
        clearClickView();
        switch (mode) {
            case 1://实时监控
                img1.setBackgroundResource(R.drawable.detect_s);
                txt1.setTextColor(getResources().getColor(R.color.green));
                if(TApplication.user == null || TApplication.defaultGreenHouse == null){
                    break;
                }
                urlWebView = Const.URL_REALTIME_WEB+ TApplication.user.getUserId()+"&dyid="+ TApplication.defaultGreenHouse.getId();
                setWebView();
                break;
            case 2://气象数据

                img2.setBackgroundResource(R.drawable.qx_s);
                txt2.setTextColor(getResources().getColor(R.color.green));
                if(TApplication.user == null || TApplication.defaultGreenHouse == null){
                    break;
                }
                urlWebView = Const.URL_WEATHER_WEB+ TApplication.user.getUserId()+"&dyid="+ TApplication.defaultGreenHouse.getId();
                setWebView();
                break;
            case 3://报警预警
                img3.setBackgroundResource(R.drawable.worn_s);
                txt3.setTextColor(getResources().getColor(R.color.green));
                //urlWebView = "http://192.168.12.97:8080/dyTable.html";
                if(TApplication.user == null || TApplication.defaultGreenHouse == null){
                    break;
                }
                urlWebView = Const.URL_WARNING_WEB+ TApplication.user.getUserId()+"&dyid="+ TApplication.defaultGreenHouse.getId();
                setWebView();
                break;
            case 4://设备控制
                lineTitle.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                img4.setBackgroundResource(R.drawable.control_s);
                txt4.setTextColor(getResources().getColor(R.color.green));
                /*if(TApplication.user == null || TApplication.defaultGreenHouse == null){
                    break;
                }
                urlWebView = Const.URL_CONTROL_WEB+ TApplication.user.getUserId()+"&dyid="+ TApplication.defaultGreenHouse.getId();
                setWebView();*/
                break;
        }
    }

    @Override
    public void onResume() {
        LogUtil.i("SskFragment","onResume");
        super.onResume();
        initView();
    }

    private void clearClickView() {
        img1.setBackgroundResource(R.drawable.detect_n);
        txt1.setTextColor(getResources().getColor(R.color.gray));
        img2.setBackgroundResource(R.drawable.qx_n);
        txt2.setTextColor(getResources().getColor(R.color.gray));
        img3.setBackgroundResource(R.drawable.worn_n);
        txt3.setTextColor(getResources().getColor(R.color.gray));
        img4.setBackgroundResource(R.drawable.control_n);
        txt4.setTextColor(getResources().getColor(R.color.gray));
        img5.setBackgroundResource(R.drawable.more_n);
        txt5.setTextColor(getResources().getColor(R.color.gray));
        lineTitle.setVisibility(View.VISIBLE);
        if(TApplication.mode != 1){
            HomeActivity.imgLeft.setVisibility(View.VISIBLE);
        }else {
            HomeActivity.imgLeft.setVisibility(View.INVISIBLE);
        }

        TApplication.sskCanGoback = false;
    }

    private void initView(){
        webView.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.GONE);
        clearClickView();
        if(TApplication.mode > 5){
            setClickView(TApplication.mode-4);
        }else{
            setClickView(1);
        }
    }

    @Override
    public void onClick(View v) {
        clearClickView();
        switch (v.getId()) {
            case R.id.layout1:
                if(TApplication.mode != 1){
                    TApplication.mode = 1;
                    EventBus.getDefault().post("mode1");//创建一个被观察者事件由HomeActivity来订阅,如果不是model则视频监控按钮不可见
                    setClickView(1);
                }
                break;
            case R.id.layout2:
                if(TApplication.mode != 6){
                    TApplication.mode = 6;
                    EventBus.getDefault().post("mode2");
                    setClickView(2);
                }

                break;
            case R.id.layout3:
                if(TApplication.mode != 7){
                    TApplication.mode = 7;
                    EventBus.getDefault().post("mode3");
                    setClickView(3);

                }
                break;
            case R.id.layout4:
                if(TApplication.mode != 8){
                    TApplication.mode = 8;
                    EventBus.getDefault().post("mode4");
                    setClickView(4);
                }
                break;
            case R.id.layout5:
                Intent intent = new Intent();
                intent.setClass(getActivity(), MoreActivity.class);
                startActivity(intent);
                break;
            default:
                if(TApplication.mode != 1){
                    TApplication.mode = 1;
                    setClickView(1);
                }
                break;
        }
    }


    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            TApplication.sskCanGoback = true;//实时监控页面到二级页面了现在可以返回了
            EventBus.getDefault().post("shouldOverrideUrlLoading");//创建一个被观察者事件由HomeActivity来订阅,通知主界面显示回退按钮
            LogUtil.i("Ssk1Fragment", "shouldOverrideUrlLoading-url="+url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.i("Ssk1Fragment", "onPageStarted-url="+url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogUtil.i("Ssk1Fragment", "onPageFinished-url="+url);
            TApplication.webUrl = url;
        }
    }

}
