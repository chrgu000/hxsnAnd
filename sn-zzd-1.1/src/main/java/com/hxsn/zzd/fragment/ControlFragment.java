package com.hxsn.zzd.fragment;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andbase.library.http.model.AbRequestParams;
import com.andbase.library.util.AbDateUtil;
import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.LogUtil;
import com.andbase.ssk.utils.PermissionUtils;
import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.utils.Const;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

/**
 * 设备控制页面
 */
@SuppressLint("ValidFragment")
public class ControlFragment extends Fragment implements View.OnClickListener{
    private Context context;
    private TextView txtName,txtStatus,txtCreateTime;
    private ImageView imgStart,imgStop,ivRefresh;
    private String telPhone,phoneType;//本机号码，手机型号
    //private boolean hasControl = true;//设备是否可控
    private ChildThread childThread = null;//子线程，延时任务
    int[] permissionIndex = new int[]{PermissionUtils.CODE_READ_PHONE_STATE,PermissionUtils.CODE_BODY_SENSORS};
    //private Button btnRegister;//分机号码登记
    private long controlTime;//发出控制命令的时间，当控制命令发出并收到状态改变通知，这时获取设备状态的时间就是这个时间

    public ControlFragment(Context context) {
        this.context = context;

    }

    public ControlFragment() {
    }


    public static ControlFragment newInstance(Context context,int mode) {
        ControlFragment fragment = new ControlFragment(context);
        Bundle args = new Bundle();
        args.putInt("fragment_mode", mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_control, container, false);
        addView(view);
        addListener();

        //获取设备状态
        obtainDeviceStatus();

        //注册EventBus用来接收来自百度推送的设备状态信息
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        return view;
    }


    //订阅/观察者事件onEventMainThread，收到EventBus的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onEventMainThread(String event){
        LogUtil.i("RealTimeFragment", "---------消息订阅者订阅得到的消息event="+event);
        if(event.contains("deviceStatus")){//这个消息在BaiduPushMessageReceiver中接收，然后由eventBuf.post过来
            String status =  event.substring("deviceStatus".length());//对event字符串进行截取，获取deviceStatus之后的字符串
            txtStatus.setText("设备最近状态："+status);
            String date = AbDateUtil.getStringByFormat(controlTime,AbDateUtil.dateFormatYMDHMS);
            txtCreateTime.setText(date);
        }
    }

    private void addListener() {
        imgStart.setOnClickListener(this);
        imgStop.setOnClickListener(this);
        ivRefresh.setOnClickListener(this);
    }

    private void addView(View view) {
        txtName = (TextView)view.findViewById(R.id.txt_name);
        txtStatus = (TextView)view.findViewById(R.id.txt_status);
        imgStart = (ImageView)view.findViewById(R.id.img_start);
        imgStop = (ImageView)view.findViewById(R.id.img_stop);
        txtName.setText(TApplication.defaultGreenHouse.getName());
        ivRefresh = (ImageView)view.findViewById(R.id.iv_refresh);
        txtCreateTime = (TextView)view.findViewById(R.id.txt_create_time);
    }

    //获取设备状态
    private void obtainDeviceStatus() {
         
        if(TApplication.defaultGreenHouse == null ||  TApplication.user == null){
            return;
        }
        String url = Const.URL_DEVICE_STATUS + TApplication.user.getUserId()+"&dyid="+ TApplication.defaultGreenHouse.getId();
        new AndHttpRequest(context) {
            @Override
            public void getResponse(String response) {
                String code = AndJsonUtils.getCode(response);
                String result = AndJsonUtils.getResult(response);
                if(code.equals("200")){

                    String text = "设备最近状态：";
                    String status = AndJsonUtils.getStatus(result);
                    long curTime = AndJsonUtils.getCreateTime(result);

                    if(status.equals("KZW")){
                        txtStatus.setText("设备最近状态：自动模式已启动");
                        String textTime = AbDateUtil.getStringByFormat(curTime,AbDateUtil.dateFormatYMDHMS);
                        txtCreateTime.setText(textTime);
                        closeChildThread();
                    }else if (status.equals("TZW")){
                        txtStatus.setText("设备最近状态：自动模式已停止");
                        String textTime = AbDateUtil.getStringByFormat(curTime,AbDateUtil.dateFormatYMDHMS);
                        txtCreateTime.setText(textTime);
                        closeChildThread();
                    }else if (status.equals("Doing")){
                        txtStatus.setText("设备最近状态：正在获取自动模式，请稍候...");
                        openChildThread();
                    }else {
                        txtStatus.setText(text+status);
                        String textTime = AbDateUtil.getStringByFormat(new Date().getTime(),AbDateUtil.dateFormatYMDHMS);
                        txtCreateTime.setText(textTime);
                    }

                }else {
                    AbToastUtil.showToast(context,result);
                }


            }
        }.doGet(url);
    }

    @Override
    public void onClick(final View v) {

        switch(v.getId()){
            case R.id.img_start:
            case R.id.img_stop:
                sendControlDemand(v);
                break;
            case R.id.iv_refresh://刷新设备状态

                obtainDeviceStatus();
                txtStatus.setText("当前状态：正在获取自动模式，请稍候...");
                break;
        }
    }


    /**
     * 发送控制命令
     * @param view v
     */
    private void sendControlDemand(View view){
        AbRequestParams map = new AbRequestParams();
        map.put("uid", TApplication.user.getUserId());
        map.put("dyid", TApplication.defaultGreenHouse.getId());
        String cmd = "";
        if(view.getId() == R.id.img_start){
            cmd += "KZW";
        }else{
            cmd += "TZW";
        }
        map.put("cmd",cmd);
        map.put("telPhone",TApplication.user.getPhone());
        map.put("phoneModel",phoneType);

        new AndHttpRequest(context) {
            @Override
            public void getResponse(String response) {
                String result = AndJsonUtils.getResult(response);
                String code = AndJsonUtils.getCode(response);
                if(code.equals("200")){
                    //obtainDeviceStatus();
                    txtStatus.setText("正在获取设备状态，请稍候...");
                    controlTime = new Date().getTime();

                }else {
                    AbToastUtil.showToast(context,result);
                }
            }
        }.doPost(Const.URL_CONTROL_DEVICE,map);
    }


    String text = "当前状态：正在获取自动模式，请稍候";
    private int sentCount = 0;
    private String getDot(int count){
        String dot = "";
        if(count == 0){
            return dot;
        }
        for(int i=1; i<count; i++){
            dot = dot+".";
        }
        return dot;
    }


    /**
     * 主线程收到子线程的消息，说明状态信息还未读取到，继续网络请求
     */
    Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LogUtil.i(ControlFragment.class, "接收子线程的消息" + "几个点呢="+getDot(sentCount%4));
            txtStatus.setText(text+getDot(sentCount%4));
            obtainDeviceStatus();
            if(sentCount == 30 && childThread != null){//1分钟后还未读取到状态
                LogUtil.i(ControlFragment.class, "***************关闭子线程**************");
                closeChildThread();
                txtStatus.setText("读取状态失败");
            }
        }
    };


    @Override
    public void onStop() {
        super.onStop();
        closeChildThread();
    }

    private void closeChildThread(){
        if(childThread != null){
            if(childThread.childHandler != null){
                childThread.childHandler.getLooper().quit();
            }

            childThread.interrupt();
            childThread = null;
        }
    }


    private void openChildThread(){
        if(childThread == null){//为获取到状态,开启延时状态
            childThread = new ChildThread();
            childThread.start();
        }else {//发消息给子线程，我还没收到
            sendMsgToChild();
        }
    }


    /**
     * 发送消息给子线程
     */
    private void sendMsgToChild(){
        // 发送消息给子线程
        Message msg = new Message();
        msg.obj = mMainHandler.getLooper().getThread().getName() + "发送消息给子线程";
        if(childThread.isAlive()){
            childThread.childHandler.sendMessage(msg);
        }

    }

    /**
     * 延时子线程
     */
    class ChildThread extends Thread {
        private Handler childHandler = null;

        public void run() {
            this.setName("ChildThread");
            LogUtil.i(ControlFragment.class, "ChildThread-------开启子线程---ChildThread" +"sentCount="+sentCount);
            sentCount++;

            try {
                sleep(2000);
                Message toMain = new Message();
                toMain.obj = "ChildThread发消息给主线程";
                mMainHandler.sendMessage(toMain);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 初始化消息循环队列，需要在Handler创建之前
            Looper.prepare();
            childHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    sentCount++;
                    LogUtil.i(ControlFragment.class, "childHandler接收主线程的消息" +  msg.obj +"sentCount="+sentCount);
                    try {
                        // 在子线程中可以做一些耗时的工作
                        sleep(2000);
                        Message toMain = new Message();
                        toMain.obj = "ChildThread发消息给主线程";
                        mMainHandler.sendMessage(toMain);
                        LogUtil.i(ControlFragment.class, "发送消息给主线程" +toMain.obj);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            // 启动子线程消息循环队列
            Looper.loop();

        }
    }

}
