package com.hxsn.jwb.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.jwb.R;
import com.hxsn.jwb.TApplication;
import com.hxsn.jwb.activity.HomeActivity;
import com.hxsn.jwb.activity.SelectHomeActivity;
import com.hxsn.jwb.utils.Const;
import com.hxsn.jwb.utils.Shared;
import com.hxsn.jwb.utils.Tools;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;


/**
 * 首页面
 * create an instance of this fragment.
 */
@SuppressLint("ValidFragment")
public class Ssk1Fragment extends Fragment implements View.OnClickListener {
    private Context context;

    private static final String TAG ="Ssk1Fragment";
    private RelativeLayout layout1, layout2, layout3, layout4, layout5;
    private ImageView img1, img2, img3, img4, img5;
    private TextView txt1, txt2, txt3, txt4, txt5;
   // private TextView txtFirst, txtPre, txtMiddle, txtNext, txtEnd;
    private ImageView imgPoint;//报警预警的圆点
    private WebView webView;//,webViewHome;
    private String urlWebView;//webView地址

    private Fragment fragmentRealTime,warningFragment,historyFragment;
    private FragmentManager fm;
    private FragmentTransaction transaction;
    private FrameLayout frameLayout;


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

        //获取用户所选大棚未读报警预警数量
        obtainUnReadWarning();

        return view;
    }

    private void obtainUnReadWarning() {
        if(Shared.getChickHome() != null &&  TApplication.user != null){
            String homeid = Shared.getChickHome().getId();
            String url = Const.URL_WARN_HOUSE_UNREAD + TApplication.user.getUserId()+"&homeid="+homeid;//+TApplication.defaultGreenHouse.getId();
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
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void addFragment() {
        fragmentRealTime = new RealTimeFragment(context);
        warningFragment = new WarningFragment(context);
        historyFragment = new HistoryFragment(context);
        frameLayout.setVisibility(View.VISIBLE);

        fm = getChildFragmentManager();
        transaction = fm.beginTransaction();
        android.support.v4.app.Fragment fragment;
        switch (TApplication.mode){
            case 1:
                transaction.add(R.id.framelayout, fragmentRealTime);
                break;
            case 7:
                transaction.add(R.id.framelayout, warningFragment);
                break;
            case 8:
                transaction.add(R.id.framelayout, historyFragment);
                break;
            default:
                transaction.add(R.id.framelayout, fragmentRealTime);
                break;
        }
        //transaction.add(R.id.framelayout, fragmentRealTime);
        transaction.commitAllowingStateLoss();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

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

        imgPoint = (ImageView)view.findViewById(R.id.img_red_point);

        frameLayout = (FrameLayout)view.findViewById(R.id.framelayout);

    }



    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.layout1:
                clearClickView();
                if(TApplication.mode != 1){
                    TApplication.mode = 1;
                    EventBus.getDefault().post("mode1");//创建一个被观察者事件由HomeActivity来订阅,如果不是model则视频监控按钮不可见
                    setClickView(1);
                }
                break;
            case R.id.layout2:
                clearClickView();
                if(TApplication.mode != 6){
                    TApplication.mode = 6;
                    EventBus.getDefault().post("mode2");
                    setClickView(2);
                }

                break;
            case R.id.layout3:
                clearClickView();
                if(TApplication.mode != 7){
                    TApplication.mode = 7;
                    EventBus.getDefault().post("mode3");
                    setClickView(3);
                }
                break;
            case R.id.layout4:
                clearClickView();
                if(TApplication.mode != 8){
                    TApplication.mode = 8;
                    EventBus.getDefault().post("mode4");
                    setClickView(4);
                }
                break;
            case R.id.layout5:
                clearClickView();

                intent.setClass(getActivity(), SelectHomeActivity.class);
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

    private void setClickView(int mode) {
        LogUtil.i("SskFragment","setClickView mode="+ TApplication.mode);
        clearClickView();
        switch (mode) {
            case 1://实时监控
                frameLayout.setVisibility(View.VISIBLE);
                img1.setBackgroundResource(R.drawable.detect_s);
                txt1.setTextColor(getResources().getColor(R.color.green));
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout, fragmentRealTime);
                transaction.commitAllowingStateLoss();
                break;
            case 2://气象数据
                img2.setBackgroundResource(R.drawable.qx_s);
                txt2.setTextColor(getResources().getColor(R.color.green));
                urlWebView = Const.URL_WEATHER_WEB+ Tools.getHomeId();
                LogUtil.i("Ssk1Fragment","**********************webViewUrl="+urlWebView+"***********************LogUtil.i");

                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(urlWebView);
                break;
            case 3://报警预警
                img3.setBackgroundResource(R.drawable.worn_s);
                txt3.setTextColor(getResources().getColor(R.color.green));

                frameLayout.setVisibility(View.VISIBLE);

                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout, warningFragment);
                transaction.commitAllowingStateLoss();

                break;
            case 4:
                frameLayout.setVisibility(View.VISIBLE);
                img4.setBackgroundResource(R.drawable.history_s);
                txt4.setTextColor(getResources().getColor(R.color.green));
                transaction = fm.beginTransaction();
                transaction.replace(R.id.framelayout, historyFragment);
                transaction.commitAllowingStateLoss();
                break;
            case R.id.layout5://更多
                Intent intent = new Intent();
                intent.setClass(getActivity(), SelectHomeActivity.class);
                startActivity(intent);
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
        img4.setBackgroundResource(R.drawable.history_n);
        txt4.setTextColor(getResources().getColor(R.color.gray));
        img5.setBackgroundResource(R.drawable.more_n);
        txt5.setTextColor(getResources().getColor(R.color.gray));
        if(TApplication.mode != 1){
            HomeActivity.imgLeft.setVisibility(View.VISIBLE);
        }else {
            HomeActivity.imgLeft.setVisibility(View.INVISIBLE);
        }

        webView.setVisibility(View.GONE);
        frameLayout.setVisibility(View.GONE);
        TApplication.sskCanGoback = false;
    }

    private void initView(){
        webView.setVisibility(View.VISIBLE);

        clearClickView();
        if(TApplication.mode > 5){
            setClickView(TApplication.mode-4);
        }else{
            setClickView(1);
        }
    }

    class MyAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public MyAdapter() {
            super();
            inflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getCount() {
            return TApplication.homeList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_home, parent, false);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
                viewHolder.txtDayAge = (TextView) convertView.findViewById(R.id.txt_day_age);
                viewHolder.txtTemperate = (TextView) convertView.findViewById(R.id.txt_temperate);
                viewHolder.imgRing = (ImageView)convertView.findViewById(R.id.img_ring);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.txtName.setText(TApplication.homeList.get(position).getName());
            viewHolder.txtDayAge.setText(TApplication.homeList.get(position).getDayAge());
            viewHolder.txtTemperate.setText(String.valueOf(TApplication.homeList.get(position).getTemperature())+"℃");
            if(TApplication.homeList.get(position).getIsWarning().equals("1")){
                viewHolder.imgRing.setVisibility(View.VISIBLE);
            }else {
                viewHolder.imgRing.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    //     ViewHolder 模式, 效率提高 50%
    static class ViewHolder {
        TextView txtName,txtDayAge,txtTemperate;
        ImageView imgRing;
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
