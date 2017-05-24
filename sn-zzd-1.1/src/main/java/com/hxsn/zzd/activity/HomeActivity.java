package com.hxsn.zzd.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.andbase.common.RxBus;
import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.LogUtil;
import com.andbase.ssk.utils.PermissionUtils;
import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.fragment.Mine5Fragment;
import com.hxsn.zzd.fragment.Nong4Fragment;
import com.hxsn.zzd.fragment.PestFragment;
import com.hxsn.zzd.fragment.WenFragment;
import com.hxsn.zzd.fragment.ZzdFragment;
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
    //public static WebView webView;
    //public static FrameLayout frameLayout;
    private Fragment mine5Fragment,zzd1Fragment,pestFragment,wenFragment,nong4Fragment;// njhui2Fragment;//,wen3Fragment, nqZhan4Fragment;
    private FragmentManager fm;
    private FragmentTransaction transaction;
    private ImageView imgPoint;//小红点
    //private static int shouldOverrideUrlLoadingCnt=0;
    private String goBackEvent="";//返回事件
    private boolean isExit = false;//是否退出

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addView();


        addFragment();
        addListener();

        //根据模式选择进入的页面
        setClickView(TApplication.mode);
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

        //RxJava观察者和订阅者的注册，被观察者是JavaScriptInterface的showCnt方法，观察该方法是否被js远程调用
        //为了保证该页面能保持注册，移植onStart中
        /*if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }*/
    }

    /**
     * 添加Fragment
     */
    private void addFragment() {
        mine5Fragment = Mine5Fragment.newInstance(this,5);
        zzd1Fragment = ZzdFragment.newInstance(this,1);
        pestFragment = PestFragment.newInstance(this,3);
        wenFragment = WenFragment.newInstance(this,2);
        nong4Fragment = Nong4Fragment.newInstance(this,4);

        fm = getFragmentManager();
        if(!zzd1Fragment.isAdded()){
            transaction = fm.beginTransaction();
            transaction.add(R.id.framelayout_home, zzd1Fragment);
            transaction.commit();
        }

    }

    /**
     * 添加view控件
     */
    private void addView() {
        imgLeft = (ImageView)findViewById(R.id.img_left);
        rb1 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_1);
        rb2 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_2);
        rb3 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_3);
        rb4 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_4);
        rb5 = (RadioButton) findViewById(R.id.in_bottom).findViewById(R.id.rb_5);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtRight = (TextView)findViewById(R.id.txt_right);
        txtLeft = (TextView)findViewById(R.id.txt_expert);
        imgPoint = (ImageView)findViewById(R.id.in_bottom).findViewById(R.id.img_red_point);
        rb1.setBackgroundResource(R.drawable.bottom1_s);
        txtLeft.setText("");
        initTitleUi(false);

    }

    /**
     * 添加按钮监听
     */
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

        //goBackEvent = event;

        LogUtil.i("HomeActivity", "---------消息订阅者订阅得到的消息event="+event);

        switch (event){
            case "JavaScriptInterface":
                requestReadWarning();
                break;

            case "deviceStatus"://收到手动控制的控制云推送消息了
                setClickView(1);//进入设备控制页面
                break;
            case "zzd_to_home_click"://观察zzdFragment发来的点击事件
                initTitleUi(true);
                break;
        }

        if(event.contains("Back")){//可显示返回按钮
            goBackEvent = event;
            imgLeft.setVisibility(View.VISIBLE);
        }
        LogUtil.i("HomeActivity","onEventMainThread--goBackEvent="+goBackEvent);
    }

    //左上角返回键的处理,
    private void leftGoBack() {
        LogUtil.i("HomeActivity","点击左上角 返回按钮了goBackEvent="+goBackEvent);
        if(goBackEvent.contains("canGoBack")){
            switch (goBackEvent){
                case "warning_canGoBack":
                    EventBus.getDefault().post("home_warning_canGoBack");
                    break;
                case "realtime_canGoBack":
                    EventBus.getDefault().post("home_realtime_canGoBack");
                    break;
                case "wen_canGoBack":
                    EventBus.getDefault().post("home_wen_canGoBack");
                    break;
                case "nong4_canGoBack":
                    EventBus.getDefault().post("home_nong4_canGoBack");
                    break;
            }
        }else {
            handleBackEvent();
            if(goBackEvent.equals("cannotBack")){//不能返回了，就可以到首页面了
                TApplication.mode = 1;
            }
           // goBackEvent = "";
        }
    }

    /**
     *  初始化标题栏的UI
     * @param isFromClick 是否是从点击按钮过来的 true 是
     */
    private void initTitleUi(boolean isFromClick){
        LogUtil.i(HomeActivity.class,"initTitleUi--TApplication.isHouseView="+TApplication.isHouseView);
        imgLeft.setVisibility(View.VISIBLE);
        if(TApplication.mode == 1 || TApplication.mode > 5){
            txtRight.setText("视频监控");
            if(TApplication.mode == 1){
                if (isFromClick){
                    if(!TApplication.isHouseView){//在棚室选择页面时，mode的值也为1，这时应该显示返回键
                        imgLeft.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }else {
            txtRight.setText("");
        }
    }

    //初始化ＵＩ
    private void initUI() {
        rb1.setBackgroundResource(R.drawable.bottom1_n);
        rb2.setBackgroundResource(R.drawable.bottom2_n);
        rb3.setBackgroundResource(R.drawable.bottom3_n);
        rb4.setBackgroundResource(R.drawable.bottom4_n);
        rb5.setBackgroundResource(R.drawable.bottom5_n);
        txtLeft.setText("");
        initTitleUi(false);
    }

    @Override
    protected void onStart() {
        setClickView(TApplication.mode);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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

    //设置点击事件
    private void setClickView(int mode){
        LogUtil.i("HomeActivity","setClickView-mode="+TApplication.mode);
        initUI();

        switch (mode) {
            case 1://早知道
                txtRight.setText("视频监控");
                rb1.setBackgroundResource(R.drawable.bottom1_s);
                txtTitle.setText("早知道");
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout_home, zzd1Fragment);
                transaction.commit();
                break;
            case 2://问专家
                txtRight.setText("提问题");
                rb2.setBackgroundResource(R.drawable.bottom2_s);
                txtTitle.setText("问专家");

                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout_home, wenFragment);
                transaction.commit();

                //获取用户身份，是否是专家
                if(TApplication.user.getUserRole() == null || TApplication.user.getUserRole().length()==0){
                    new AndHttpRequest(TApplication.context) {
                        @Override
                        public void getResponse(String response) {
                            if(AndJsonUtils.getCode(response).equals("200")){
                                TApplication.user = AndJsonUtils.setCmsUser(response,TApplication.user);
                                if (TApplication.user.getUserRole().equals("3")) {
                                    txtLeft.setText("专家回复");
                                }

                            }else {
                                //AbToastUtil.showToast(HomeActivity.this,AndJsonUtils.getDescription(response));
                            }
                        }
                    }.doGet(Const.URL_GET_CMS_USER_INFO+TApplication.user.getUserName());
                }else {
                    if (TApplication.user.getUserRole().equals("3")) {
                        txtLeft.setText("专家回复");
                    }
                }
                break;
            case 3://病虫害
                rb3.setBackgroundResource(R.drawable.bottom3_s);
                txtRight.setText("");
                txtTitle.setText("病虫害");
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout_home, pestFragment);
                transaction.commit();
                break;
            case 4://农事汇
                txtRight.setText("");
                rb4.setBackgroundResource(R.drawable.bottom4_s);
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout_home, nong4Fragment);
                transaction.commit();
                txtTitle.setText("农事汇");
                break;
            case 5://我的
                txtRight.setText("");
                rb5.setBackgroundResource(R.drawable.bottom5_s);
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout_home, mine5Fragment);
                transaction.commit();
                txtTitle.setText("我的");
                break;
            default:
                txtRight.setText("视频监控");
                rb1.setBackgroundResource(R.drawable.bottom1_s);
                txtTitle.setText("早知道");
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout_home, zzd1Fragment);
                transaction.commit();
                break;
        }
    }

    @Override
    /**
     * 监控按键
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.i("HomeActivity","---onKeyDown-------keyCode="+keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            leftGoBack();
            return false;
        } else {
            return super.onKeyDown(keyCode,event);
        }
    }


    //按两次退出ＡＰＰ
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

        LogUtil.i(HomeActivity.class,"handleBackEvent********TApplication.mode="+TApplication.mode+",TApplication.isHouseView="+TApplication.isHouseView);

        if(TApplication.mode == 1){
            imgLeft.setVisibility(View.INVISIBLE);

            if(!TApplication.isHouseView){//mode=1且不在棚室选择页面才能按两次退出
                exit();
            }else {
                RxBus.getInstance().post("homeActivity_switch");//向zzdFragment发送消息
            }
            TApplication.isHouseView = false;

        }else if(TApplication.mode > 5){//按下返回键，调用退出函数，并且通知zzdFragment页面切换到实时监控
            TApplication.mode = 1;
            imgLeft.setVisibility(View.VISIBLE);
            RxBus.getInstance().post("homeActivity_switch");//向zzdFragment发送消息
        }else{
            TApplication.mode = 1;
            setClickView(1);
        }


    }

    @Override
    public void onClick(View v) {
        LogUtil.i("HomeActivity","onClick mode="+TApplication.mode);
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.rb_1://早知道
                if (TApplication.mode != 1) {
                    TApplication.mode = 1;
                    setClickView(1);
                }
                break;
            case R.id.rb_2://问专家
                if (TApplication.mode != 2) {
                    TApplication.mode = 2;
                    setClickView(2);
                }
                break;
            case R.id.rb_3://病虫害
                if (TApplication.mode != 3) {
                    TApplication.mode = 3;
                    setClickView(3);
                }
                break;
            case R.id.rb_4://农事汇
                if (TApplication.mode != 4) {
                    TApplication.mode = 4;
                    setClickView(4);
                }
                break;
            case R.id.rb_5://我的
                if (TApplication.mode != 5) {
                    TApplication.mode = 5;
                    setClickView(5);
                }
                break;
            case R.id.img_left://返回按钮
                leftGoBack();
                break;
            case R.id.txt_expert://专家回复
                intent.setClass(this,ExpertActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_right://视频监控或提问题
                intent = new Intent();
                switch (TApplication.mode){
                    case 2:
                        intent.setClass(this,SendQuestActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        intent.setClass(this,EZCameraListActivity.class);
                        startActivity(intent);
                        break;
                }
                break;
        }
    }

}
