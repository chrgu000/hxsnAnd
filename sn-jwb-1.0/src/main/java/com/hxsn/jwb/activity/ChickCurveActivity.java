package com.hxsn.jwb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andbase.chartlibrary.model.PointValue;
import com.andbase.chartlibrary.view.LineChartView;
import com.andbase.common.AndroidUtil;
import com.andbase.library.http.model.AbRequestParams;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.jwb.R;
import com.hxsn.jwb.TApplication;
import com.hxsn.jwb.base.BaseTitle;
import com.hxsn.jwb.model.ChickHome;
import com.hxsn.jwb.model.Device;
import com.hxsn.jwb.model.HourData;
import com.hxsn.jwb.utils.ChartUtil;
import com.hxsn.jwb.utils.Const;
import com.hxsn.jwb.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 鸡舍内多个传感器曲线图
 */
public class ChickCurveActivity extends Activity implements View.OnClickListener{
    private final String TAG = "ChickCurveActivity";
    //private List<List<Float>> dataLists;   //多天数据集合
    private List<HourData> hourDatas;      //今天昨天前天小时数据
    private List<Device> deviceList;

    private TextView txt1,txt2,txt3;
    private LineChartView chart;
    private LinearLayout lineText;
    List<List<PointValue>> pointValueListes = new ArrayList<>();
    //private AbHttpUtil httpUtil = null;

    private String homeId;

    private int obtainCnt=0;
    private int mode = 1;

    private Handler handler;
    private Handler.Callback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chick_curve);

        BaseTitle.getInstance(this).setTitle("鸡舍变化曲线");

        addView();
        addListener();
        // LineData mLineData = ChartUtil.makeLineData2List(datas);
        //ChartUtil.setChartStyle(chart, mLineData, Color.WHITE);
        addData();
        obtainDevices();
    }

    class MyHandlerCallback implements Handler.Callback{

        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 11){
                int width = AndroidUtil.getWidthPix(ChickCurveActivity.this)/pointValueListes.size();
                for(int i=0;i<pointValueListes.size();i++){
                    TextView textView = new TextView(ChickCurveActivity.this);
                    textView.setBackgroundColor(Const.COLORS[i]);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    params.setMargins(10, 0, 10, 0);
                    textView.setLayoutParams(params);
                    textView.setGravity(Gravity.CENTER);
                    textView.setHeight(50);

                    textView.setWidth(width-20);
                    textView.setTextColor(getResources().getColor(R.color.white));
                    textView.setTextSize(10);

                    if(homeId.equals("homeId")){
                        textView.setText(TApplication.homeList.get(i).getName());
                    }else {
                        textView.setText(deviceList.get(i).getName());
                    }

                    lineText.addView(textView);
                }

                ChartUtil.initLineChart("鸡舍平均温度",pointValueListes,chart);

            }
            return false;
        }
    }

    private void addListener() {
        txt1.setOnClickListener(this);
        txt2.setOnClickListener(this);
        txt3.setOnClickListener(this);
    }

    private void addView() {
        chart = (LineChartView) findViewById(R.id.chart);
        // 制作24个数据点（沿x坐标轴）
       // chart.setDescription("鸡舍内各传感器温度变化曲线");// 数据描述

        txt1 = (TextView)findViewById(R.id.txt_today);
        txt2 = (TextView)findViewById(R.id.txt_yesterday);
        txt3 = (TextView)findViewById(R.id.txt_pre_day);
        lineText = (LinearLayout)findViewById(R.id.line_text);
    }

    private void addData() {
        //dataLists = new ArrayList<>();
        pointValueListes = new ArrayList<>();
        homeId = getIntent().getStringExtra("homeId");
        deviceList = new ArrayList<>();
    }

    private void obtainDevices() {

        if(homeId.equals("homeId")){
            obtainData(0);
        }else {
            //String url = "http://192.168.12.97:8080/jwb/getDeviceList.json?uid=1&homeId="+homeId;
            String url = Const.URL_HOME_DEIVICE_LIST;
            AbRequestParams params = new AbRequestParams();
            params.put("uid",TApplication.user.getUserId());
            params.put("homeId",homeId);
            new AndHttpRequest(this) {
                @Override
                public void getResponse(String response) {
                    if(AndJsonUtils.getCode(response).equals("200")){
                        deviceList = JsonUtils.getDeviceList(response);
                        obtainData(0);
                    }
                }
            }.doPost(url,params);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        obtainCnt = 0;
    }

    private void obtainData(int dateType) {
        if(homeId.equals("homeId")){
            for(ChickHome chickHome: TApplication.homeList){
                //String url = "http://192.168.12.97:8080/jwb/getAverageDataList.json";
                String url = Const.URL_AVERAGE_DATA_LIST;
                AbRequestParams params = new AbRequestParams();
                params.put("uid",TApplication.user.getUserId());
                params.put("homeId",chickHome.getId());
                params.put("dateType",dateType);
                obtainDataTemp(url,params);
            }
        }else {
            for(Device device: deviceList){
                String url = "http://192.168.12.97:8080/jwb/getDeviceHourList.json";
                //String url = Const.URL_DEVICE_HOUR_LIST;
                AbRequestParams params = new AbRequestParams();
                params.put("uid",TApplication.user.getUserId());
                params.put("deviceId",device.getId());
                params.put("dateType",dateType);
                if(obtainCnt > 6){//最多显示6条曲线
                    break;
                }
                obtainDataTemp(url,params);
            }
        }
    }

    private void obtainDataTemp(String url,AbRequestParams params){
        new AndHttpRequest(this) {
            @Override
            public void getResponse(String response) {

                if (AndJsonUtils.isSuccess(response)) {
                    hourDatas = JsonUtils.getHourDataList(response);
                    //List<Float> datas = new ArrayList<>();
                    List<PointValue> pointValues = new ArrayList<PointValue>();
                    for(int i=0; i<hourDatas.size();i++){
                        pointValues.add(new PointValue(i, (float)hourDatas.get(i).getTemperature()));
                    }

                    pointValueListes.add(pointValues);
                    obtainCnt++;
                    int tempSize = 0;
                    if(homeId.equals("homeId")){
                        tempSize = TApplication.homeList.size();
                    }else {
                        tempSize = deviceList.size();
                    }
                    if(obtainCnt == tempSize){
                        LogUtil.i(TAG,"---------------所有设备数据都拿到了---------------");
                        callback = new MyHandlerCallback();
                        handler = new Handler(callback);
                        Message message = handler.obtainMessage();
                        message.what = 11;
                        handler.sendMessage(message);
                        obtainCnt = 0;
                    }
                }
            }
        }.doPost(url,params);
    }

    private void clearTxtUi(){
        txt1.setBackgroundColor(getResources().getColor(R.color.sky_blue));
        txt2.setBackgroundColor(getResources().getColor(R.color.sky_blue));
        txt3.setBackgroundColor(getResources().getColor(R.color.sky_blue));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.txt_today:
                if(mode != 1){
                    clearTxtUi();
                    txt1.setBackgroundColor(getResources().getColor(R.color.orange));
                    mode = 1;
                    obtainCnt = 0;
                    pointValueListes.clear();
                    obtainData(0);
                }
                break;
            case R.id.txt_yesterday:
                if(mode != 2){
                    clearTxtUi();
                    txt2.setBackgroundColor(getResources().getColor(R.color.orange));
                    mode = 2;
                    obtainCnt = 0;
                    pointValueListes.clear();
                    obtainData(1);
                }
                break;
            case R.id.txt_pre_day:
                if(mode != 3){
                    clearTxtUi();
                    txt3.setBackgroundColor(getResources().getColor(R.color.orange));
                    mode = 3;
                    obtainCnt = 0;
                    pointValueListes.clear();
                    obtainData(2);
                }
                break;
        }
    }
}
