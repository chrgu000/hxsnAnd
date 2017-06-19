package com.hxsn.ssklf;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andbase.library.model.AbEntity;
import com.andbase.library.util.AbDialogUtil;
import com.andbase.library.view.wheel.AbEntityWheelAdapter;
import com.andbase.library.view.wheel.AbWheelView;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.LogUtil;
import com.andbase.ssk.utils.PermissionUtils;
import com.hxsn.ssklf.activity.HomeActivity;
import com.hxsn.ssklf.model.Weather;
import com.hxsn.ssklf.utils.JsonUtil;
import com.hxsn.ssklf.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * 主Activity  loading页，5秒自动进入主页，点击按钮进入主页
 */
public class MainActivity extends Activity implements View.OnClickListener{
    private LayoutInflater mInflater;
    private View vSelectHome = null;//轮子选择
    private List<AbEntity> cityList;
    private String city;
    private String cityCode="54515";
    private RelativeLayout rlDefault,rlMain;//默认背景布局
    private TextView txtSelect,txtIn1,txtIn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        //动态申请CODE_ACCESS_FINE_LOCATION权限
        if(Integer.parseInt(android.os.Build.VERSION.SDK) < 23){
            city = Tools.getCityName(this);
            LogUtil.i(MainActivity.class,"city="+city);
            if(!TextUtils.isEmpty(city)){
                cityCode = getCityCode();
            }

        }else {
            PermissionUtils.requestPermission(this, PermissionUtils.CODE_ACCESS_FINE_LOCATION, new PermissionUtils.PermissionGrant() {
                @Override
                public void onPermissionGranted(int requestCode) {
                    city = Tools.getCityName(MainActivity.this);
                    LogUtil.i(MainActivity.class,"city="+city);
                    if(!TextUtils.isEmpty(city)){
                        cityCode = getCityCode();
                    }
                }
            });
        }

        addView();

        addListener();

        obtainRealTimeWeather();

        obtain7DaysWeather();

    }

    private void obtainRealTimeWeather() {
        String url = "http://wx.hbweather.com.cn/langfang/getNow.php?area="+cityCode;
        new AndHttpRequest(this) {
            @Override
            public void getResponse(String response) {
                Weather weather = JsonUtil.getWeather(response);
                if(weather != null){
                    rlDefault.setVisibility(View.GONE);
                    setMainBackground();
                    TextView txtTime = (TextView)findViewById(R.id.txt_time);
                    TextView txtName = (TextView)findViewById(R.id.txt_name);
                    TextView txtTemp = (TextView)findViewById(R.id.txt_temp);
                    TextView txtHumidity = (TextView)findViewById(R.id.txt_humidity);
                    String time = weather.getCreateTime();
                    String month = time.substring(4,5);
                    if(time.substring(4,4).equals("0")){
                        month = time.substring(5,5);
                    }
                    String day = time.substring(6,7);
                    if(time.substring(6,6).equals("0")){
                        day = time.substring(7,7);
                    }
                    txtTime.setText(time.substring(0,3)+"年"+month+"月"+day+"日 "+time.substring(8,9)+"点");
                    txtName.setText(weather.getName());
                    txtTemp.setText(weather.getTemperature()+"℃");
                    txtHumidity.setText(weather.getHumidity());
                }
            }
        }.doGet(url);
    }

    private void obtain7DaysWeather() {
        String url = "http://wx.hbweather.com.cn/langfang/getSeven.php?area="+cityCode;
        new AndHttpRequest(this) {
            @Override
            public void getResponse(String response) {
                List<Weather> weatherList = JsonUtil.get7Weathers(response);

                if(weatherList != null && weatherList.size()==7){
                    rlDefault.setVisibility(View.GONE);
                    TextView txtTime1 = (TextView)findViewById(R.id.txt_week1);
                    TextView txtTime2 = (TextView)findViewById(R.id.txt_week2);
                    TextView txtTime3 = (TextView)findViewById(R.id.txt_week3);
                    TextView txtTime4 = (TextView)findViewById(R.id.txt_week4);
                    TextView txtTime5 = (TextView)findViewById(R.id.txt_week5);
                    TextView txtTime6 = (TextView)findViewById(R.id.txt_week6);
                    TextView txtTime7 = (TextView)findViewById(R.id.txt_week7);
                    TextView txtName1 = (TextView)findViewById(R.id.txt_weather1);
                    TextView txtName2 = (TextView)findViewById(R.id.txt_weather2);
                    TextView txtName3 = (TextView)findViewById(R.id.txt_weather3);
                    TextView txtName4 = (TextView)findViewById(R.id.txt_weather4);
                    TextView txtName5 = (TextView)findViewById(R.id.txt_weather5);
                    TextView txtName6 = (TextView)findViewById(R.id.txt_weather6);
                    TextView txtName7 = (TextView)findViewById(R.id.txt_weather7);
                    TextView txtTemp1 = (TextView)findViewById(R.id.txt_temp1);
                    TextView txtTemp7 = (TextView)findViewById(R.id.txt_temp7);
                    TextView txtTemp2 = (TextView)findViewById(R.id.txt_temp2);
                    TextView txtTemp3 = (TextView)findViewById(R.id.txt_temp3);
                    TextView txtTemp4 = (TextView)findViewById(R.id.txt_temp4);
                    TextView txtTemp5 = (TextView)findViewById(R.id.txt_temp5);
                    TextView txtTemp6 = (TextView)findViewById(R.id.txt_temp6);
                    TextView txtWindy1 = (TextView)findViewById(R.id.txt_windy1);
                    TextView txtWindy2 = (TextView)findViewById(R.id.txt_windy2);
                    TextView txtWindy3 = (TextView)findViewById(R.id.txt_windy3);
                    TextView txtWindy4 = (TextView)findViewById(R.id.txt_windy4);
                    TextView txtWindy5= (TextView)findViewById(R.id.txt_windy5);
                    TextView txtWindy6 = (TextView)findViewById(R.id.txt_windy6);
                    TextView txtWindy7 = (TextView)findViewById(R.id.txt_windy7);
                    txtTime1.setText(weatherList.get(0).getCreateTime());
                    txtTime2.setText(weatherList.get(1).getCreateTime());
                    txtTime3.setText(weatherList.get(2).getCreateTime());
                    txtTime4.setText(weatherList.get(3).getCreateTime());
                    txtTime5.setText(weatherList.get(4).getCreateTime());
                    txtTime6.setText(weatherList.get(5).getCreateTime());
                    txtTime7.setText(weatherList.get(6).getCreateTime());
                    txtName1.setText(weatherList.get(0).getName());
                    txtName2.setText(weatherList.get(1).getName());
                    txtName3.setText(weatherList.get(2).getName());
                    txtName4.setText(weatherList.get(3).getName());
                    txtName5.setText(weatherList.get(4).getName());
                    txtName6.setText(weatherList.get(5).getName());
                    txtName7.setText(weatherList.get(6).getName());
                    txtTemp1.setText(weatherList.get(0).getTemperature());
                    txtTemp2.setText(weatherList.get(1).getTemperature());
                    txtTemp3.setText(weatherList.get(2).getTemperature());
                    txtTemp4.setText(weatherList.get(3).getTemperature());
                    txtTemp5.setText(weatherList.get(4).getTemperature());
                    txtTemp6.setText(weatherList.get(5).getTemperature());
                    txtTemp7.setText(weatherList.get(6).getTemperature());
                    txtWindy1.setText(weatherList.get(0).getWindy());
                    txtWindy2.setText(weatherList.get(1).getWindy());
                    txtWindy3.setText(weatherList.get(2).getWindy());
                    txtWindy4.setText(weatherList.get(3).getWindy());
                    txtWindy5.setText(weatherList.get(4).getWindy());
                    txtWindy6.setText(weatherList.get(5).getWindy());
                    txtWindy7.setText(weatherList.get(6).getWindy());

                }
            }
        }.doGet(url);
    }

    private void addListener() {
        txtIn1.setOnClickListener(this);
        txtIn2.setOnClickListener(this);
        txtSelect.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setMainBackground(){
        switch (cityCode){
            case "54515":
                rlMain.setBackground(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(),R.drawable.langf));
                break;
            case "54512":
                rlMain.setBackground(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(),R.drawable.guan));
                break;
            case "54519":
                rlMain.setBackground(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(),R.drawable.yongq));
                break;
            case "54521":
                rlMain.setBackground(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(),R.drawable.xiangh));
                break;
            case "54613":
                rlMain.setBackground(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(),R.drawable.dacheng));
                break;
            case "54612":
                rlMain.setBackground(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(),R.drawable.wenan));
                break;
            case "54510":
                rlMain.setBackground(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(),R.drawable.dachang));
                break;
            case "54518":
                rlMain.setBackground(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(),R.drawable.bazhou));
                break;
            case "54520":
                rlMain.setBackground(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(),R.drawable.sanhe));
                break;
        }
    }

    private void addView() {
        mInflater = LayoutInflater.from(this);
        txtSelect = (TextView)findViewById(R.id.txt_select);
        txtIn1 = (TextView)findViewById(R.id.txt_in1);
        txtIn2 = (TextView)findViewById(R.id.txt_in2);
        rlDefault = (RelativeLayout)findViewById(R.id.rl_default);
        rlMain = (RelativeLayout)findViewById(R.id.rl_main);
    }

    private void initData() {
        if(cityList == null){
            cityList = new ArrayList<>();
            AbEntity ab = new AbEntity("54515","廊坊");
            cityList.add(ab);
            ab = new AbEntity("54512","固安");
            cityList.add(ab);
            ab = new AbEntity("54519","永清");
            cityList.add(ab);
            ab = new AbEntity("54521","香河");
            cityList.add(ab);
            ab = new AbEntity("54513","大城");
            cityList.add(ab);
            ab = new AbEntity("54512","文安");
            cityList.add(ab);
            ab = new AbEntity("54510","大厂");
            cityList.add(ab);
            ab = new AbEntity("54518","霸州");
            cityList.add(ab);
            ab = new AbEntity("54520","三河");
            cityList.add(ab);
        }

    }


    /**
     * 用于权限管理
     * 如果全部授权的话，则直接通过进入
     * 如果权限拒绝，缺失权限时，则使用dialog提示
     *
     * @param requestCode  请求代码
     * @param permissions  权限参数
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.i(MainActivity.class,"***********onRequestPermissionsResult************");

        PermissionUtils.requestPermissionsResult(this, PermissionUtils.CODE_ACCESS_FINE_LOCATION, permissions, grantResults, new PermissionUtils.PermissionGrant() {
            @Override
            public void onPermissionGranted(int requestCode) {
                city = Tools.getCityName(MainActivity.this);
                LogUtil.i(MainActivity.class,"onRequestPermissionsResult---city="+city);
                if(!TextUtils.isEmpty(city)){
                    cityCode = getCityCode();
                }
            }
        });
    }

    private String getCityCode(){
        for(AbEntity a:cityList){
            if(city.equals(a.getName())){
                return a.getId();
            }
        }
        return "54515";
    }

    //选择园区的轮子
    public void initWheelData1(){
        vSelectHome = mInflater.inflate(R.layout.choose_one, null);

        final AbWheelView mWheelView1 = (AbWheelView)vSelectHome.findViewById(R.id.zzd_wheelView);
        mWheelView1.setAdapter(new AbEntityWheelAdapter(cityList));
        // 可循环滚动
        mWheelView1.setCyclic(false);
        // 添加文字
        //   mWheelView1.setLabel(getResources().getString(com.andbase.R.string.data1_unit));
        // 初始化时显示的数据
        mWheelView1.setCurrentItem(2);//设置默认的那个item
        mWheelView1.setValueTextSize(35);
        mWheelView1.setLabelTextSize(35);
        mWheelView1.setLabelTextColor(0x80000000);
        // mWheelView1.setCenterSelectDrawable(this.getResources().getDrawable(com.andbase.R.drawable.wheel_select));

        Button okBtn = (Button)vSelectHome.findViewById(R.id.okBtn);
        Button cancelBtn = (Button)vSelectHome.findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(MainActivity.this);
                int index = mWheelView1.getCurrentItem();

                cityCode = cityList.get(index).getId();
                obtain7DaysWeather();
                obtainRealTimeWeather();
            }

        });

        cancelBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(MainActivity.this);
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_in1:
            case R.id.txt_in2:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_select:
                initWheelData1();
                AbDialogUtil.showDialog(vSelectHome, Gravity.TOP);
                break;
        }
    }
}
