package com.hxsn.iot.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andbase.common.RxBus;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.iot.R;
import com.hxsn.iot.TApplication;
import com.hxsn.iot.uitls.Const;

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
public class ZzdFragment extends Fragment implements View.OnClickListener{
    private Context context;
    private static final String TAG ="ZzdFragment";

    private RelativeLayout layout1, layout2, layout3, layout4, layout5;
    private ImageView img1, img2, img3, img4, img5;
    private TextView txt1, txt2, txt3, txt4, txt5;
    private ImageView imgPoint;//,imgSetting;//报警预警的圆点

    private Fragment controlFragment,warningFragment,realtimeFragment,weatherFragment,houseSelectFragment;
    private FragmentManager fm;
    private FragmentTransaction transaction;
    private int zzdMode = 1;
    //private FrameLayout frameLayout;

    private Subscription subscription;//rxJava-rxBus观察者，观察返回按钮有没有被按下
    //是否进行了棚室选择，如果进行棚室选择，则页面返回原来的页面
    private boolean hasHouseClick = false;

    public ZzdFragment() {
    }

    public static ZzdFragment newInstance(Context context,int mode) {
        ZzdFragment fragment = new ZzdFragment(context);
        Bundle args = new Bundle();
        args.putInt("fragment_mode", mode);
        fragment.setArguments(args);
        return fragment;
    }

    public ZzdFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onStart() {
        LogUtil.i(ZzdFragment.class, "onStart");
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.i(ZzdFragment.class, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.i(ZzdFragment.class, "onCreateView");
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
        LogUtil.i("ZzdFragment", "---------消息订阅者订阅得到的消息event="+event);
        switch (event){
            case "JavaScriptInterface"://收到报警页面的未读信息已读完的消息
                obtainUnReadWarning();
                break;
            case "deviceStatus":////收到手动控制的控制云推送消息了
                setClickView(8);//进入设备控制页面
                break;
            case "homeActivity_switch"://收到HomeActivity的消息，切换fragment页面
                setClickView(1);
                break;
            case "house_select_to_zzd":
                LogUtil.i("ZzdFragment", "TApplication.mode="+ TApplication.mode);
                setClickView(TApplication.mode);
                break;
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
                        LogUtil.i(ZzdFragment.class, s);
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
        controlFragment = ControlFragment.newInstance(context,8);
        warningFragment = WarningFragment.newInstance(context,7);
        realtimeFragment = RealTimeFragment.newInstance(context,1);
        weatherFragment = WeatherFragment.newInstance(context,6);
       // houseSelectFragment = HouseSelectFragment.newInstance(context,9);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            fm = getChildFragmentManager();
        }else {
            fm = getFragmentManager();
        }


        if(!realtimeFragment.isAdded()){
            transaction = fm.beginTransaction();
            transaction.add(R.id.framelayout, realtimeFragment);
            transaction.commit();
        }

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

        imgPoint = (ImageView)view.findViewById(R.id.img_red_point);
        //imgSetting = (ImageView)view.findViewById(R.id.img_setting);
    }


    private void setClickView(int mode) {

        LogUtil.i("ZzdFragment","setClickView mode="+ TApplication.mode);
        clearClickView();

        if(mode != 9){
            hasHouseClick = false;
            TApplication.isHouseView = false;//在实时监测页面，要保留棚室选择的状态，以便显示返回按钮
        }
        EventBus.getDefault().post("zzd_to_home_click");//给homeActivity发送消息，通知其显示左上角返回按钮，如果mode=1则不显示返回按钮

        transaction = fm.beginTransaction();
        //EventBus.getDefault().post("cannotBack");

        switch (mode) {
            case 1://实时监控
                img1.setBackgroundResource(R.drawable.detect_s);
                txt1.setTextColor(getResources().getColor(R.color.green));
                transaction.replace(R.id.framelayout, realtimeFragment);
                transaction.commit();
                break;
            case 6://气象数据
                img2.setBackgroundResource(R.drawable.qx_s);
                txt2.setTextColor(getResources().getColor(R.color.green));
                transaction.replace(R.id.framelayout, weatherFragment);
                transaction.commit();
                break;
            case 7://报警预警
                img3.setBackgroundResource(R.drawable.worn_s);
                txt3.setTextColor(getResources().getColor(R.color.green));
                transaction.replace(R.id.framelayout, warningFragment);
                transaction.commit();

                break;
            case 8://设备控制

                img4.setBackgroundResource(R.drawable.control_s);
                txt4.setTextColor(getResources().getColor(R.color.green));
                transaction.replace(R.id.framelayout, controlFragment);
                transaction.commit();
                break;
            case 9://棚室选择

                img5.setBackgroundResource(R.drawable.more_s);
                txt5.setTextColor(getResources().getColor(R.color.green));
                houseSelectFragment = HouseSelectFragment.newInstance(context,zzdMode);
                transaction.replace(R.id.framelayout, houseSelectFragment);
                transaction.commit();
                break;
        }

    }

    @Override
    public void onResume() {
        LogUtil.i(ZzdFragment.class,"onResume");
        setClickView(TApplication.mode);//根据页面模式选择进入的页面
        super.onResume();

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


    }

    @Override
    public void onClick(View v) {
       // LogUtil.i(ZzdFragment.class,"onClick-hasHouseClick="+hasHouseClick);
        switch (v.getId()) {
            case R.id.layout1://实时监控
                if( zzdMode != 1 || hasHouseClick || TApplication.mode != 1){
                    TApplication.mode = 1;
                    zzdMode = 1;
                    setClickView(1);
                }
                break;
            case R.id.layout2://气象数据
                if( zzdMode != 6 || hasHouseClick || TApplication.mode != 6){
                    zzdMode = 6;
                    TApplication.mode = 6;
                    setClickView(6);
                }

                break;
            case R.id.layout3://报警预警
                if( zzdMode != 7 || hasHouseClick || TApplication.mode != 7){
                    TApplication.mode = 7;
                    zzdMode = 7;
                    setClickView(7);
                }
                break;
            case R.id.layout4://设备控制
                if( zzdMode != 8 || hasHouseClick || TApplication.mode != 8 ){
                    zzdMode = 8;
                    TApplication.mode = 8;
                    setClickView(8);
                }
                break;
            case R.id.layout5://棚室选择

                if(!hasHouseClick){//未进行棚室选择
                    hasHouseClick = true;
                    TApplication.isHouseView = true;//告诉主页，这个是棚室选择页面，需要显示返回按钮
                    setClickView(9);

                }
                //intent.setClass(getActivity(), MoreActivity.class);
                //startActivity(intent);
                break;

            default:
                if(TApplication.mode != 1){
                    TApplication.mode = 1;
                    setClickView(1);
                }
                break;
        }
    }

}
