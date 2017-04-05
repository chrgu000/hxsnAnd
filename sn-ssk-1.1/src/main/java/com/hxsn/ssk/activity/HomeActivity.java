package com.hxsn.ssk.activity;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.hxsn.ssk.R;
import com.hxsn.ssk.TApplication;
import com.hxsn.ssk.fragment.Mine5Fragment;
import com.hxsn.ssk.fragment.Nshui2Fragment;
import com.hxsn.ssk.utils.Const;
import com.hxsn.ssk.utils.UpdateUtil;

/**
 * 主页
 */
public class HomeActivity extends Activity implements View.OnClickListener {
    private ImageView imgLeft;
    private RadioButton rb1, rb2, rb3, rb4, rb5;
    private TextView txtTitle,txtRight,txtLeft;
    private WebView webView;
    private FrameLayout frameLayout;
    private Fragment mine5Fragment,  njhui2Fragment;//ssk1Fragment,wen3Fragment, nqZhan4Fragment;
    private FragmentManager fm;
    private FragmentTransaction transaction;
    private int mode = 1;
    private String urlWebView;
    private boolean isExit = false;
    private int urlCount = 0;//进入webView二级页面的深度,早知道首页的webView和其他webView不是一个webView，所以不需要这个参数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.i("HomeActivity", "onCreate");
        //TApplication.activities.add(this);

        addView();
        urlWebView = Const.URL_SSK_WEB+ TApplication.user.getUserId();
        setWebView();
        addFragment();
        addListener();

        //检查是否有新版本
        //检查是否有新版本
        UpdateUtil.updateNowifiApp(this,Const.URL_UPDATE);

    }


    private void addFragment() {
        //ssk1Fragment = new Ssk1Fragment(this);
        njhui2Fragment = new Nshui2Fragment(this);
        //wen3Fragment = new Wen3Fragment(this);
        //nqZhan4Fragment = new Nqzhan4Fragment(this);
        mine5Fragment = new Mine5Fragment(this);

        fm = getFragmentManager();
        transaction = fm.beginTransaction();
        transaction.add(R.id.framelayout, njhui2Fragment);
        transaction.commit();
    }

    private void addView() {
        imgLeft = (ImageView)findViewById(R.id.img_left);
        txtLeft = (TextView)findViewById(R.id.txt_expert);
        imgLeft.setVisibility(View.INVISIBLE);
        rb1 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_1);
        rb2 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_2);
        rb3 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_3);
        rb4 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_4);
        rb5 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_5);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtRight = (TextView)findViewById(R.id.txt_right);
        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        webView = (WebView) findViewById(R.id.webView);
        rb1.setBackgroundResource(R.drawable.bottom1_s);
        txtRight.setText("");

        urlWebView = Const.URL_SSK_WEB+ TApplication.user.getUserId();
        setWebView();
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




    //设置webv
    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView() {
        webView.setVisibility(View.VISIBLE);
        webView.removeAllViews();
        webView.getSettings().setJavaScriptEnabled(true);
        MyWebViewClient webViewClient = new MyWebViewClient();
        webView.setWebViewClient(webViewClient);
        Log.i("HomeActivity-setWebView","urlWebView="+urlWebView);
        webView.loadUrl(urlWebView);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.rb_1:
                if (mode != 1) {
                    mode = 1;
                    setClickView(1);
                }
                break;
            case R.id.rb_2:
                if (mode != 2) {
                    mode = 2;
                    setClickView(2);
                }
                break;
            case R.id.rb_3:
                if (mode != 3) {
                    setClickView(3);
                    mode = 3;
                }
                break;
            case R.id.rb_4:
                if (mode != 4) {
                    mode = 4;
                    setClickView(4);
                }
                break;
            case R.id.rb_5:
                if (mode != 5) {
                    setClickView(5);
                    mode = 5;
                }
                break;
            case R.id.img_left:
                handleBack();
                break;
            case R.id.txt_expert:

                intent.setClass(this,ExpertActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_right:
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
        txtLeft.setText("");
        txtRight.setText("");
        imgLeft.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        urlCount = 0;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void exit(){
        if (!isExit) {
            //isExit = true;
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
            isExit = true;
        }
    };

    /**
     * 处理返回键和左上角的返回按钮事件
     */
    private void handleBackEvent(){
        if(mode == 1){
            exit();
        }else{
            Log.i("HomeActivity","---返回到首页"+mode);
            mode = 1;
            setClickView(1);
        }
    }

    private void handleBack(){
        if(webView.canGoBack() && urlCount>0){
            urlCount--;
            webView.goBack();
        }else {
            handleBackEvent();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("HomeActivity","---onKeyDown-------keyCode="+keyCode+"------mode="+mode);
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Log.i("HomeActivity","--------------------------------webView.canGoBack()="+webView.canGoBack()+"urlCount="+urlCount);
            handleBack();
            return true;
        } else {
            return false;
        }
    }


    private void setClickView(int mode) {
        clearButton();
        if(mode != 1){
            imgLeft.setVisibility(View.VISIBLE);
        }
        switch (mode) {
            case 1:
                imgLeft.setVisibility(View.INVISIBLE);
                rb1.setBackgroundResource(R.drawable.bottom1_s);
                urlWebView = Const.URL_SSK_WEB+ TApplication.user.getUserId();
                setWebView();
                txtTitle.setText("随时看");
                break;
            case 2:
                frameLayout.setVisibility(View.VISIBLE);
                rb2.setBackgroundResource(R.drawable.bottom2_s);
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout, njhui2Fragment);
                transaction.commit();
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
                                AbToastUtil.showToast(HomeActivity.this,AndJsonUtils.getDescription(response));
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
                frameLayout.setVisibility(View.VISIBLE);
                rb5.setBackgroundResource(R.drawable.bottom5_s);
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout, mine5Fragment);
                transaction.commit();
                txtTitle.setText("我的");
                break;
        }
    }

    /**
     * 渲染问专家页面
     */
    private void refreshExpertUI(){
        webView.setVisibility(View.VISIBLE);
        urlWebView = Const.URL_WNN_LIST + TApplication.user.getUserName();//"http://192.168.12.121:8280/zzdcms/app/wzj.do?userid="+ TApplication.user.getId();//Const.URL_WNN_LIST+ TApplication.user.getId();
        setWebView();
        if (TApplication.user.getUserRole().equals("3")) {
            txtLeft.setText("专家回复");
        }
    }

    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            Log.i("HomeActivity", "shouldOverrideUrlLoading-url="+url);
            imgLeft.setVisibility(View.VISIBLE);
            urlWebView = url;
            urlCount++;
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.i("HomeActivity", "onPageStarted-url="+url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i("HomeActivity", "onPageFinished-url="+url);

        }
    }
}
