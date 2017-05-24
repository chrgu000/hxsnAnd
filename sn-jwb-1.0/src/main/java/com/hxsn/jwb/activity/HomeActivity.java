package com.hxsn.jwb.activity;

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
import android.widget.TextView;

import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.LogUtil;
import com.andbase.ssk.utils.PermissionUtils;
import com.hxsn.jwb.R;
import com.hxsn.jwb.TApplication;
import com.hxsn.jwb.fragment.Mine5Fragment;
import com.hxsn.jwb.fragment.Nshui2Fragment;
import com.hxsn.jwb.fragment.Ssk1Fragment;
import com.hxsn.jwb.utils.Const;
import com.hxsn.jwb.utils.Shared;
import com.hxsn.jwb.utils.UpdateUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 主页
 */
@SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
public class HomeActivity extends Activity implements View.OnClickListener {
    private String TAG = "HomeActivity";
    public static  ImageView imgLeft;
    //private RelativeLayout rl1,rl2,rl3,rl4,rl5;
    private ImageView rb1, rb2, rb3, rb4, rb5;
    private TextView textView1,textView2,textView3,textView4,textView5;
    private TextView txtTitle,txtRight,txtLeft;
    public static WebView webView;
    public static FrameLayout frameLayout;
    private Fragment mine5Fragment,ssk1Fragment,njhui2Fragment;// njhui2Fragment;//,wen3Fragment, nqZhan4Fragment;
    private FragmentManager fm;
    private FragmentTransaction transaction;
   // private int fragmentMode = 1;
    private ImageView imgPoint;//小红点
    private String urlWebView;
    //private static int shouldOverrideUrlLoadingCnt=0;
    private boolean isExit = false;

    @Override
    @NonNull
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        addView();

        addFragment();
        addListener();

        //setClickView(TApplication.mode);

        LogUtil.i("HomeActivity","onCreate");
        TApplication.baiduNotifyCallback = new Handler.Callback(){
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == 11){
                    LogUtil.i("HomeActivity", "---------当前页面是控制页面且收到百度推送TApplication.webUrl="+ TApplication.webUrl);
                    Bundle bundle = msg.getData();
                    String message = bundle.getString("message");
                    urlWebView = TApplication.webUrl;
                    webView.loadUrl("javascript:funFromjs('"+message+"')");
                }
                return false;
            }
        };

        //检查是否有新版本
        //动态申请CODE_WRITE_EXTERNAL_STORAGE权限
        if(Integer.parseInt(android.os.Build.VERSION.SDK) < 23){
            UpdateUtil.updateNowifiApp(this,Const.URL_UPDATE);
        }else {
            PermissionUtils.requestPermission(this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, new PermissionUtils.PermissionGrant() {
                @Override
                public void onPermissionGranted(int requestCode) {
                    UpdateUtil.updateNowifiApp(HomeActivity.this,Const.URL_UPDATE);
                }
            });
        }

        //获取用户未读警情信息数量
        requestReadWarning();
    }



    private void addFragment() {
        mine5Fragment = new Mine5Fragment(this);
        ssk1Fragment = new Ssk1Fragment(this);
        njhui2Fragment = new Nshui2Fragment(this);
        fm = getFragmentManager();
        transaction = fm.beginTransaction();
        transaction.add(R.id.framelayout, ssk1Fragment);
        transaction.commitAllowingStateLoss();
    }


    private void addView() {
        imgLeft = (ImageView)findViewById(R.id.img_left);
        imgLeft.setVisibility(View.INVISIBLE);
        rb1 = (ImageView) findViewById(R.id.in_bottom).findViewById(R.id.rb_1);
        rb2 = (ImageView) findViewById(R.id.in_bottom).findViewById(R.id.rb_2);
        rb3 = (ImageView) findViewById(R.id.in_bottom).findViewById(R.id.rb_3);
        rb4 = (ImageView) findViewById(R.id.in_bottom).findViewById(R.id.rb_4);
        rb5 = (ImageView) findViewById(R.id.in_bottom).findViewById(R.id.rb_5);

        textView1 = (TextView) findViewById(R.id.in_bottom).findViewById(R.id.txt_1);
        textView2 = (TextView) findViewById(R.id.in_bottom).findViewById(R.id.txt_2);
        textView3 = (TextView) findViewById(R.id.in_bottom).findViewById(R.id.txt_3);
        textView4 = (TextView) findViewById(R.id.in_bottom).findViewById(R.id.txt_4);
        textView5 = (TextView) findViewById(R.id.in_bottom).findViewById(R.id.txt_5);
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

    //获取用户未读警情信息数量
    @NonNull
    private void requestReadWarning() {
        LogUtil.i("HomeActivity","TApplication.user="+ TApplication.user);
        if(TApplication.user == null){
            return;
        }
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

    @Override
    public void onClick(View v) {
        LogUtil.i("HomeActivity","onClick mode="+ TApplication.mode);
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
                intent.setClass(this,SendQuestActivity.class);
                startActivity(intent);

                break;
        }
    }

    private void clearButton() {
        rb1.setBackgroundResource(R.drawable.bottom1_n);
        rb2.setBackgroundResource(R.drawable.bottom2_n);
        rb3.setBackgroundResource(R.drawable.bottom3_n);
        rb4.setBackgroundResource(R.drawable.bottom4_n);
        rb5.setBackgroundResource(R.drawable.bottom5_n);
        textView1.setTextColor(getResources().getColor(R.color.gray_dark));
        textView2.setTextColor(getResources().getColor(R.color.gray_dark));
        textView3.setTextColor(getResources().getColor(R.color.gray_dark));
        textView4.setTextColor(getResources().getColor(R.color.gray_dark));
        textView5.setTextColor(getResources().getColor(R.color.gray_dark));
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
            LogUtil.i("HomeActivity", "onResume-TApplication.isNotify="+ TApplication.isNotify);
            urlWebView = Shared.getValue("controlUrl");
            TApplication.isNotify = false;
            setWebView();
        }
        LogUtil.i("HomeActivity","onResume mode="+ TApplication.mode);
        setClickView(TApplication.mode);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onStart() {
        LogUtil.i("HomeActivity","onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void exit(){
        if (!isExit) {
            isExit = true;
            UpdateUtil.show(getApplicationContext(), "再按一次退出程序");
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

            imgLeft.setVisibility(View.GONE);
        }else {
            if(TApplication.mode == 1){
                exit();
            }else if(TApplication.mode > 5){//按下返回键，调用退出函数，并且通知ssk1Fragment页面切换到实时监控
                TApplication.mode = 1;

            }else{
                TApplication.mode = 1;
                setClickView(1);
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.i("HomeActivity","---onKeyDown-------keyCode="+keyCode+"------TApplication.mode="+ TApplication.mode+"webView.canGoBack()="+webView.canGoBack());
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


    private void setClickView(int mode){
        LogUtil.i("HomeActivity","setClickView mode="+ TApplication.mode);
        clearButton();
        if(mode != 1){
            imgLeft.setVisibility(View.VISIBLE);
        }
        switch (mode) {
            case 1:
                frameLayout.setVisibility(View.VISIBLE);
                rb1.setBackgroundResource(R.drawable.bottom1_s);
                textView1.setTextColor(getResources().getColor(R.color.green_none));
                txtTitle.setText("看鸡舍");
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout, ssk1Fragment);
                transaction.commitAllowingStateLoss();
                break;
            case 2:
                frameLayout.setVisibility(View.VISIBLE);
                rb2.setBackgroundResource(R.drawable.bottom2_s);
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout, njhui2Fragment);
                transaction.commitAllowingStateLoss();
                txtTitle.setText("农事汇");
                break;
            case 3:
                txtRight.setText("提问题");
                rb3.setBackgroundResource(R.drawable.bottom3_s);
                txtTitle.setText("问专家");

                if(TApplication.user.getUserRole() == null || TApplication.user.getUserRole().length()==0){
                    new AndHttpRequest(TApplication.context) {
                        @Override
                        public void getResponse(String response) {
                            if(AndJsonUtils.getCode(response).equals("200")){
                                AndJsonUtils.setCmsUser(response,TApplication.user);
                                refreshExpertUI();
                            }else {
                                UpdateUtil.show(HomeActivity.this,AndJsonUtils.getDescription(response));
                            }
                        }
                    }.doGet(Const.URL_GET_CMS_USER_INFO+ TApplication.user.getUserName());
                }else {
                    refreshExpertUI();
                }
                break;
            case 4:
                webView.setVisibility(View.VISIBLE);
                rb4.setBackgroundResource(R.drawable.bottom4_s);
                urlWebView = Const.URL_NQZHAN_WEB+ TApplication.user.getUserId();
                setWebView();
                txtTitle.setText("农情站");
                break;
            case 5:
                txtRight.setText("");
                frameLayout.setVisibility(View.VISIBLE);
                rb5.setBackgroundResource(R.drawable.bottom5_s);
                textView5.setTextColor(getResources().getColor(R.color.green_none));
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout, mine5Fragment);
                transaction.commitAllowingStateLoss();
                txtTitle.setText("我的");
                break;
            default:
                frameLayout.setVisibility(View.VISIBLE);
                txtRight.setText("");
                rb1.setBackgroundResource(R.drawable.bottom1_s);
                textView1.setTextColor(getResources().getColor(R.color.green_none));
                txtTitle.setText("看鸡舍");
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout, ssk1Fragment);
                transaction.commitAllowingStateLoss();
                break;
        }
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
