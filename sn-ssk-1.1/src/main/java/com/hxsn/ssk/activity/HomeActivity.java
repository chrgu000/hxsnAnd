package com.hxsn.ssk.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.LogUtil;
import com.andbase.ssk.utils.PermissionUtils;
import com.hxsn.ssk.R;
import com.hxsn.ssk.TApplication;
import com.hxsn.ssk.fragment.Mine5Fragment;
import com.hxsn.ssk.fragment.Nqzhan4Fragment;
import com.hxsn.ssk.fragment.Nshui2Fragment;
import com.hxsn.ssk.fragment.Ssk1Fragment;
import com.hxsn.ssk.fragment.Wen3Fragment;
import com.hxsn.ssk.utils.Const;
import com.hxsn.ssk.utils.UpdateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 主页
 */
public class HomeActivity extends Activity implements View.OnClickListener {
    private ImageView imgLeft;
    private RadioButton rb1, rb2, rb3, rb4, rb5;
    private TextView txtTitle,txtRight,txtLeft;
    private Fragment mine5Fragment,  njhui2Fragment,ssk1Fragment,wen3Fragment, nqZhan4Fragment;
    private FragmentManager fm;
    private FragmentTransaction transaction;
    private int mode = 1;
    private boolean isExit = false;
    private String goBackEvent;
 //   private boolean isWebViewCanGoback = false;//是否有webView可返回

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addView();
        addFragment();
        addListener();

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

        //RxJava观察者和订阅者的注册，被观察者是JavaScriptInterface的showCnt方法，观察该方法是否被js远程调用
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    //订阅/观察者事件FirstEvent，被观察者是JavaScriptInterface的showCnt方法，如果该方法被js远程调用，则进行触发
    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onEventMainThread(String event){

        goBackEvent = event;

        LogUtil.i("HomeActivity", "---------消息订阅者订阅得到的消息event="+event);
        if (event.contains("canGoBack")){
            LogUtil.i("HomeActivity", "---------goBack");
            imgLeft.setVisibility(View.VISIBLE);
           // isWebViewCanGoback = true;
        }else if(event.contains("cannotBack")){
            if(mode == 1){
                imgLeft.setVisibility(View.INVISIBLE);
              //  isWebViewCanGoback = false;
            }
        }
    }

    private void leftGoBack() {
        LogUtil.i("HomeActivity","点击左上角 返回按钮了goBackEvent="+goBackEvent);
        if(goBackEvent.contains("canGoBack")){
            if(goBackEvent.equals("wen_canGoBack")){
                EventBus.getDefault().post("home_wen_canGoBack");
            }else if(goBackEvent.equals("nong4_canGoBack")){
                EventBus.getDefault().post("home_nong4_canGoBack");
            }else if(goBackEvent.equals("ssk1_canGoBack")){
                EventBus.getDefault().post("home_ssk1_canGoBack");
            }
        }else {
            handleBackEvent();
        }
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void addFragment() {
        ssk1Fragment = new Ssk1Fragment(this);
        njhui2Fragment = new Nshui2Fragment(this);
        wen3Fragment = new Wen3Fragment(this);
        nqZhan4Fragment = new Nqzhan4Fragment(this);
        mine5Fragment = new Mine5Fragment(this);

        fm = getFragmentManager();
        transaction = fm.beginTransaction();
        transaction.add(R.id.framelayout_home, ssk1Fragment);
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

        rb1.setBackgroundResource(R.drawable.bottom1_s);
        txtRight.setText("");
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
                leftGoBack();
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
        imgLeft.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.i("HomeActivity","---onKeyDown-------keyCode="+keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            leftGoBack();
            return false;
        } else {
            return super.onKeyDown(keyCode,event);
        }
    }

    /**
     * 处理返回键和左上角的返回按钮事件
     */
    private void handleBackEvent(){
        LogUtil.i(HomeActivity.class,"handleBackEvent---------mode="+mode);
        if(mode == 1){
            imgLeft.setVisibility(View.INVISIBLE);
            exit();
        }else{
            mode = 1;
            setClickView(1);

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



    private void setClickView(int mode) {
        clearButton();
        if(mode != 1){
            imgLeft.setVisibility(View.VISIBLE);
        }
        switch (mode) {
            case 1:
                rb1.setBackgroundResource(R.drawable.bottom1_s);
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout_home, ssk1Fragment);
                transaction.commit();
                txtTitle.setText("随时看");
                break;
            case 2:
                rb2.setBackgroundResource(R.drawable.bottom2_s);
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout_home, njhui2Fragment);
                transaction.commit();
                txtTitle.setText("农事汇");
                break;
            case 3:
                txtRight.setText("提问题");
                rb3.setBackgroundResource(R.drawable.bottom3_s);
                txtTitle.setText("问专家");
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout_home, wen3Fragment);
                transaction.commit();
                if(TApplication.user.getUserRole() == null || TApplication.user.getUserRole().length()==0){
                    new AndHttpRequest(TApplication.context) {
                        @Override
                        public void getResponse(String response) {
                            if(AndJsonUtils.getCode(response).equals("200")){
                                AndJsonUtils.setCmsUser(response,TApplication.user);
                                if (TApplication.user.getUserRole().equals("3")) {
                                    txtLeft.setText("专家回复");
                                }
                            }else {
                                AbToastUtil.showToast(HomeActivity.this,AndJsonUtils.getDescription(response));
                            }
                        }
                    }.doGet(Const.URL_GET_CMS_USER_INFO+ TApplication.user.getUserName());
                }else {
                    if (TApplication.user.getUserRole().equals("3")) {
                        txtLeft.setText("专家回复");
                    }
                }
                break;
            case 4:

                rb4.setBackgroundResource(R.drawable.bottom4_s);
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout_home, nqZhan4Fragment);
                transaction.commit();
                txtTitle.setText("农情站");
                break;
            case 5:

                rb5.setBackgroundResource(R.drawable.bottom5_s);
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout_home, mine5Fragment);
                transaction.commit();
                txtTitle.setText("我的");
                break;
        }
    }
}
