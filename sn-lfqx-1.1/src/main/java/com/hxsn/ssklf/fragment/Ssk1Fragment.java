package com.hxsn.ssklf.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andbase.ssk.utils.AndHttpRequest;
import com.hxsn.ssklf.R;
import com.hxsn.ssklf.TApplication;
import com.hxsn.ssklf.activity.SelectSiteActivity;
import com.hxsn.ssklf.activity.WarningActivity;
import com.hxsn.ssklf.utils.Const;
import com.hxsn.ssklf.utils.JsonUtil;

import java.lang.reflect.Field;


/**
 *  随时看页面
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
@SuppressLint("ValidFragment")
public class Ssk1Fragment extends Fragment implements View.OnClickListener {
    private Context context;
    private RelativeLayout layout1, layout2, layout3,layout4, layout5;//随时看五个顶部菜单
    private ImageView img1, img2, img3, img4, img5;
    private TextView txt1, txt2, txt3,txt4, txt5;
    private TextView txtSiteName;
    private int menuTopMode = 1,/*顶部菜单5个 实时数据-变化曲线-报警预警-历史数据-更多*/menuMidMode=1;//


    private Fragment fragmentRealTime,fragmentWeather,fragmentCurve,fragmentHistory,fragmentMore;
    private FragmentManager fm;
    private FragmentTransaction transaction;
    private TextView txtWarning;
    //private TextView txtSiteName;

    public Ssk1Fragment() {
    }

    public Ssk1Fragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Ssk1Fragment","onCreate");
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ssk1, container, false);
        addView(view);
        addFragment(view);

        Log.i("Ssk1Fragment","onCreateView");

        addListener();

        //获取国家突发事件预警信息
        obtainWarning();

        return view;
    }

    /**
     * 获取国家突发事件预警信息
     */
    private void obtainWarning() {
        new AndHttpRequest(context) {
            @Override
            public void getResponse(String response) {
                if(JsonUtil.getStatus(response)) {
                    TApplication.warningInfoList = JsonUtil.getWarningList(response);
                    Handler.Callback callback = new WarningInfoCallback();
                    Handler handler = new Handler(callback);
                    Message message = handler.obtainMessage();

                    message.what = 11;

                    handler.sendMessage(message);

                }
            }
        }.doGet(Const.URL_WARNING_WEATHER);
    }

    class WarningInfoCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 11){
                if(TApplication.warningInfoList.size() != 0){
                    txtWarning.setVisibility(View.VISIBLE);
                    txtWarning.setText(TApplication.warningInfoList.get(0).getContent());
                }
            }
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void addFragment(View view) {
        fragmentRealTime = new RealTimeFragment(context);
        fragmentCurve = new CurveFragment(context);
        fragmentHistory = new HistoryFragment(context);
        fragmentWeather = new WeatherFragment(context);

        fm = getChildFragmentManager();
        transaction = fm.beginTransaction();
        transaction.add(R.id.framelayout_ssk, fragmentRealTime);
        transaction.commit();
    }

    @Override
    public void onResume() {
        Log.i("Ssk1Fragment","onResume");
        menuTopMode = 1;
        super.onResume();
    }

    @Override
    /**
     *  解决 IllegalStateException: No activity问题
     */
    public void onDetach() {
        super.onDetach();
        Log.i("Ssk1Fragment","onDetach");
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void addListener() {
        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        layout3.setOnClickListener(this);
        layout4.setOnClickListener(this);
        layout5.setOnClickListener(this);

        txtWarning.setOnClickListener(this);
    }

    /**
     * 初始化视图
     * @param view fragment视图
     */
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

        txtWarning = (TextView) view.findViewById(R.id.txt_warning);

        txtSiteName = (TextView) view.findViewById(R.id.txt_siteName);
        txtSiteName.setText(TApplication.curSiteInfo.getName());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.layout1://实时监控
                if (menuTopMode != 1) {
                    clearTopMenu();
                    menuTopMode = 1;
                    transaction = fm.beginTransaction();
                    transaction.replace(R.id.framelayout_ssk, fragmentRealTime);
                    transaction.commit();
                    img1.setBackgroundResource(R.drawable.detect_s);
                    txt1.setTextColor(getResources().getColor(R.color.green));
                }
                break;
            case R.id.layout2://天气预告
                if (menuTopMode != 2) {
                    clearTopMenu();
                    txtSiteName.setVisibility(View.GONE);
                    menuTopMode = 2;
                    img2.setBackgroundResource(R.drawable.qx_s);
                    txt2.setTextColor(getResources().getColor(R.color.green));
                    transaction = fm.beginTransaction();
                    transaction.replace(R.id.framelayout_ssk, fragmentWeather);
                    transaction.commit();
                }
                break;
            case R.id.layout3://变化曲线
                if (menuTopMode != 3) {
                    clearTopMenu();
                    menuTopMode = 3;
                    img3.setBackgroundResource(R.drawable.curve_s);
                    txt3.setTextColor(getResources().getColor(R.color.green));
                    transaction = fm.beginTransaction();
                    transaction.replace(R.id.framelayout_ssk, fragmentCurve);
                    transaction.commit();
                }
                break;
            case R.id.layout4://历史数据
                if (menuTopMode != 4) {
                    clearTopMenu();
                    menuTopMode = 4;
                    img4.setBackgroundResource(R.drawable.history_s);
                    txt4.setTextColor(getResources().getColor(R.color.green));
                    transaction = fm.beginTransaction();
                    transaction.replace(R.id.framelayout_ssk, fragmentHistory);
                    transaction.commit();
                }
                break;
            case R.id.layout5://更多
                    intent.setClass(getActivity(), SelectSiteActivity.class);
                    startActivity(intent);
                break;
            case R.id.txt_warning:
                intent.setClass(getActivity(), WarningActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 顶部视图复原
     */
    private void clearTopMenu() {
        img1.setBackgroundResource(R.drawable.detect_n);
        txt1.setTextColor(getResources().getColor(R.color.gray));
        img2.setBackgroundResource(R.drawable.qx_n);
        txt2.setTextColor(getResources().getColor(R.color.gray));
        img3.setBackgroundResource(R.drawable.curve_n);
        txt3.setTextColor(getResources().getColor(R.color.gray));
        img4.setBackgroundResource(R.drawable.history_n);
        txt4.setTextColor(getResources().getColor(R.color.gray));
        img5.setBackgroundResource(R.drawable.more_n);
        txt5.setTextColor(getResources().getColor(R.color.gray));
        txtSiteName.setVisibility(View.VISIBLE);
    }
}
