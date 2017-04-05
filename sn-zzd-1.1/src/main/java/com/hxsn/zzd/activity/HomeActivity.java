package com.hxsn.zzd.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.andbase.common.RxBus;
import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.AndShared;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.fragment.Mine5Fragment;
import com.hxsn.zzd.fragment.PestFragment;
import com.hxsn.zzd.fragment.Ssk1Fragment;
import com.hxsn.zzd.utils.Const;
import com.hxsn.zzd.utils.UpdateUtil;
import com.hxsn.zzd.videogo.activity.EZCameraListActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 主页
 */
@SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
public class HomeActivity extends Activity implements View.OnClickListener {
    private String TAG = "HomeActivity";
    public static ImageView imgLeft;
    private RadioButton rb1, rb2, rb3, rb4, rb5;
    private TextView txtTitle,txtRight,txtLeft;
    public static WebView webView;
    public static FrameLayout frameLayout;
    private Fragment mine5Fragment,ssk1Fragment,pestFragment;// njhui2Fragment;//,wen3Fragment, nqZhan4Fragment;
    private FragmentManager fm;
    private FragmentTransaction transaction;
    // private int fragmentMode = 1;
    private ImageView imgPoint;//小红点
    private String urlWebView;
    //private static int shouldOverrideUrlLoadingCnt=0;
    private boolean isExit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addView();

        addFragment();
        addListener();

        LogUtil.i("HomeActivity","onCreate");
        TApplication.baiduNotifyCallback = new Handler.Callback(){
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == 11){
                    LogUtil.i("HomeActivity", "---------当前页面是控制页面且收到百度推送TApplication.webUrl="+TApplication.webUrl);
                    Bundle bundle = msg.getData();
                    String message = bundle.getString("message");
                    urlWebView = TApplication.webUrl;
                    webView.loadUrl("javascript:funFromjs('"+message+"')");
                }
                return false;
            }
        };

        //检查是否有新版本
        UpdateUtil.updateNowifiApp(this,Const.URL_UPDATE);

        //获取用户未读警情信息数量
        requestReadWarning();

        //RxJava观察者和订阅者的注册，被观察者是JavaScriptInterface的showCnt方法，观察该方法是否被js远程调用
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void addFragment() {
        mine5Fragment = new Mine5Fragment(this);
        ssk1Fragment = new Ssk1Fragment(this);
        pestFragment = new PestFragment();

        fm = getFragmentManager();
        transaction = fm.beginTransaction();
        transaction.add(R.id.framelayout, ssk1Fragment);
        transaction.commit();
    }


    private void addView() {
        imgLeft = (ImageView)findViewById(R.id.img_left);
        imgLeft.setVisibility(View.INVISIBLE);
        rb1 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_1);
        rb2 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_2);
        rb3 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_3);
        rb4 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_4);
        rb5 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_5);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtRight = (TextView)findViewById(R.id.txt_right);
        txtLeft = (TextView)findViewById(R.id.txt_expert);
        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        webView = (WebView) findViewById(R.id.webView);
        imgPoint = (ImageView)findViewById(R.id.in_bottom).findViewById(R.id.img_red_point);
        rb1.setBackgroundResource(R.drawable.bottom1_s);
    }

    private void addListener() {
        rb1.setOnClickListener(this);
        rb2.setOnClickListener(this);
        rb3.setOnClickListener(this);
        rb4.setOnClickListener(this);
        rb5.setOnClickListener(this);
        imgLeft.setOnClickListener(this);
        txtRight.setOnClickListener(this);
        txtLeft.setOnClickListener(this);
    }

    //订阅/观察者事件FirstEvent，被观察者是JavaScriptInterface的showCnt方法，如果该方法被js远程调用，则进行触发
    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onEventMainThread(String event){

        LogUtil.i("HomeActivity", "---------消息订阅者订阅得到的消息event="+event);
        if(event.equals("JavaScriptInterface")){
            requestReadWarning();
            //imgPoint.setVisibility(View.GONE);
        }else if (event.equals("shouldOverrideUrlLoading")){
            imgLeft.setVisibility(View.VISIBLE);
        }else if(event.equals("deviceStatus")){//收到手动控制的控制云推送消息了
            setClickView(1);//进入设备控制页面
        }else if(event.contains("mode")){
            if(event.equals("mode1")){//观察Ssk1Fragment发来的事件
                txtRight.setText("视频监控");
            }else if(event.equals("mode2")){
                txtRight.setText("提问题");
            }else {
                txtRight.setText("");
            }
            //EventBus.getDefault().cancelEventDelivery(event);//取消这个消息
        }
    }


    //设置webv
    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView() {
        webView.setVisibility(View.VISIBLE);
        webView.removeAllViews();
        webView.getSettings().setJavaScriptEnabled(true);
        MyWebViewClient webViewClient = new MyWebViewClient();
        webView.setWebViewClient(webViewClient);
        LogUtil.i("HomeActivity-setWebView","urlWebView="+urlWebView);
        webView.loadUrl(urlWebView);
    }

    private void clearButton() {
        rb1.setBackgroundResource(R.drawable.bottom1_n);
        rb2.setBackgroundResource(R.drawable.bottom2_n);
        rb3.setBackgroundResource(R.drawable.bottom3_n);
        rb4.setBackgroundResource(R.drawable.bottom4_n);
        rb5.setBackgroundResource(R.drawable.bottom5_n);
        txtLeft.setText("");
        txtRight.setText("");
        imgLeft.setVisibility(View.INVISIBLE);
        frameLayout.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        TApplication.sskCanGoback = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( TApplication.isNotify){//点击通知栏进入
            LogUtil.i("HomeActivity", "onResume-TApplication.isNotify="+TApplication.isNotify);
            urlWebView = AndShared.getValue("controlUrl");
            TApplication.isNotify = false;
            setWebView();
        }
        LogUtil.i("HomeActivity","onResume mode="+TApplication.mode);
        setClickView(TApplication.mode);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    /**
     * 渲染问专家页面
     */
    private void refreshExpertUI(){
        webView.setVisibility(View.VISIBLE);
        urlWebView = Const.URL_NSH_WEN + TApplication.user.getUserName();
        setWebView();
        if (TApplication.user.getUserRole().equals("3")) {
            txtLeft.setText("专家回复");
        }
    }

    //获取用户未读警情信息数量
    @NonNull
    private void requestReadWarning() {

        String url = Const.URL_WARN_READ + TApplication.user.getUserId();
        new AndHttpRequest(this) {
            @Override
            public void getResponse(String response) {

                if(!AndJsonUtils.getResult(response).equals("0")){//没有未读预警
                    imgPoint.setVisibility(View.VISIBLE);
                }else {
                    imgPoint.setVisibility(View.GONE);
                }
            }
        }.doGet(url);
    }

    private void setClickView(int mode){
        LogUtil.i("HomeActivity","setClickView mode="+TApplication.mode);
        clearButton();
        if(mode != 1){
            imgLeft.setVisibility(View.VISIBLE);
        }
        switch (mode) {
            case 1:
                frameLayout.setVisibility(View.VISIBLE);
                txtRight.setText("视频监控");
                rb1.setBackgroundResource(R.drawable.bottom1_s);
                txtTitle.setText("早知道");
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout, ssk1Fragment);
                transaction.commit();
                break;
            case 2:
                txtRight.setText("提问题");
                rb2.setBackgroundResource(R.drawable.bottom2_s);
                txtTitle.setText("问专家");
                if(TApplication.user.getUserRole() == null || TApplication.user.getUserRole().length()==0){
                    new AndHttpRequest(TApplication.context) {
                        @Override
                        public void getResponse(String response) {
                            if(AndJsonUtils.getCode(response).equals("200")){
                                TApplication.user = AndJsonUtils.setCmsUser(response,TApplication.user);

                                refreshExpertUI();
                            }else {
                                AbToastUtil.showToast(HomeActivity.this,AndJsonUtils.getDescription(response));
                            }
                        }
                    }.doGet(Const.URL_GET_CMS_USER_INFO+TApplication.user.getUserName());
                }else {
                    refreshExpertUI();
                }
                LogUtil.i("HomeActivity", "问专家-urlWebView=" + urlWebView);
                break;
            case 3:
                frameLayout.setVisibility(View.VISIBLE);
                rb3.setBackgroundResource(R.drawable.bottom3_s);
                txtRight.setText("");
                txtTitle.setText("病虫害");
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout, pestFragment);
                transaction.commit();
                break;
            case 4:
                txtRight.setText("");
                webView.setVisibility(View.VISIBLE);
                rb4.setBackgroundResource(R.drawable.bottom4_s);
                urlWebView = Const.URL_NSH_WEB+TApplication.user.getUserName();
                setWebView();
                txtTitle.setText("农事汇");
                break;
            case 5:
                txtRight.setText("");
                frameLayout.setVisibility(View.VISIBLE);
                rb5.setBackgroundResource(R.drawable.bottom5_s);
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout, mine5Fragment);
                transaction.commit();
                txtTitle.setText("我的");
                break;
            default:
                frameLayout.setVisibility(View.VISIBLE);
                txtRight.setText("");
                rb1.setBackgroundResource(R.drawable.bottom1_s);
                txtTitle.setText("早知道");
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout, ssk1Fragment);
                transaction.commit();
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.i("HomeActivity","---onKeyDown-------keyCode="+keyCode+"------TApplication.mode="+TApplication.mode+"webView.canGoBack()="+webView.canGoBack());
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if(webView.canGoBack()){
                webView.goBack();
            }else {
                handleBackEvent();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode,event);
        }
    }


    public void exit(){
        if (!isExit) {
            isExit = true;
            AbToastUtil.showToast(getApplicationContext(), "再按一次退出程序");
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            System.exit(0);
            isExit = false;
        }
    }

    //延时2s,按两次返回键退出
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    /**
     * 处理返回键和左上角的返回按钮事件
     */
    private void handleBackEvent(){

        if(TApplication.sskCanGoback){
            LogUtil.i("HomeActivity","------------------------实时监控二级页面返回可用了------------");
            TApplication.sskCanGoback = false;
            EventBus.getDefault().post("sskGoBack");//创建一个被观察者事件由Ssk1Fragment来订阅,得到消息后进行webView的回退操作
            imgLeft.setVisibility(View.GONE);
        }else {
            if(TApplication.mode == 1){
                exit();
            }else if(TApplication.mode > 5){//按下返回键，调用退出函数，并且通知ssk1Fragment页面切换到实时监控
                TApplication.mode = 1;
                RxBus.getInstance().post(TAG+"发送消息给ssk1Fragment");//向fragment发送消息
            }else{
                TApplication.mode = 1;
                setClickView(1);
            }
        }

    }

    @Override
    public void onClick(View v) {
        LogUtil.i("HomeActivity","onClick mode="+TApplication.mode);
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.rb_1:
                if (TApplication.mode != 1) {
                    TApplication.mode = 1;
                    setClickView(1);
                }
                break;
            case R.id.rb_2:
                if (TApplication.mode != 2) {
                    setClickView(2);
                    TApplication.mode = 2;
                }
                break;
            case R.id.rb_3:
                if (TApplication.mode != 3) {
                    setClickView(3);
                    TApplication.mode = 3;
                }
                break;
            case R.id.rb_4:
                if (TApplication.mode != 4) {
                    setClickView(4);
                    TApplication.mode = 4;
                }
                break;
            case R.id.rb_5:
                if (TApplication.mode != 5) {
                    setClickView(5);
                    TApplication.mode = 5;
                }
                break;
            case R.id.img_left:
                LogUtil.i("HomeActivity","点击左上角 返回按钮了");
                if(webView.canGoBack()){
                    webView.goBack();
                }else {
                    handleBackEvent();
                }
                break;
            case R.id.txt_expert:

                intent.setClass(this,ExpertActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_right:
                intent = new Intent();
                switch (TApplication.mode){
                    case 1:
                        intent.setClass(this,EZCameraListActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(this,SendQuestActivity.class);
                        startActivity(intent);
                        break;
                }
                break;
        }
    }


    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            TApplication.webUrl = url;
            LogUtil.i("HomeActivity", "shouldOverrideUrlLoading-url="+url);
            imgLeft.setVisibility(View.VISIBLE);

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.i("HomeActivity", "onPageStarted-url="+url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogUtil.i("HomeActivity", "onPageFinished-url="+url);
            TApplication.webUrl = url;
        }
    }
}
