package com.hxsn.ssklf.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andbase.chartlibrary.gesture.ContainerScrollType;
import com.andbase.chartlibrary.gesture.ZoomType;
import com.andbase.chartlibrary.model.Axis;
import com.andbase.chartlibrary.model.AxisValue;
import com.andbase.chartlibrary.model.Line;
import com.andbase.chartlibrary.model.LineChartData;
import com.andbase.chartlibrary.model.PointValue;
import com.andbase.chartlibrary.model.ValueShape;
import com.andbase.chartlibrary.model.Viewport;
import com.andbase.chartlibrary.view.LineChartView;
import com.andbase.ssk.utils.AndHttpRequest;
import com.hxsn.ssklf.R;
import com.hxsn.ssklf.TApplication;
import com.hxsn.ssklf.model.SiteValue;
import com.hxsn.ssklf.utils.Const;
import com.hxsn.ssklf.utils.JsonUtil;
import com.hxsn.ssklf.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * 变化曲线页面
 */
@SuppressLint("ValidFragment")
public class CurveFragment extends Fragment implements View.OnClickListener{

    private Context context;
    // 中间菜单选择-四个指标的选择  下面日期模式选择 今天-昨天-前天
    private int menuMidMode=1,dayMode=1;
    // 四个指标文字和数值
    private TextView txtTemp,txtTempVal,txtHumidity,txtHumidityVal,txtSoil,txtSoilVal,txtIllu,txtIlluVal;
    private View viewLine1,viewLine2,viewLine3,viewLine4;//mid_menu宽线条
    private RelativeLayout rlMenu1,rlMenu2,rlMenu3,rlMenu4;//实时监测和变化曲线的四个指标菜单按钮

    //今天-昨天-前天按钮
    private TextView txtToday,txtYesterday,txtThreeDay;
    private View view;
    //处理小数点后为0时不显示
    //private DecimalFormat decimalFormat = new DecimalFormat("###################.###########");

    private List<SiteValue> siteList;//今天昨天前天小时数据
    private int hour;//当前是几点
    private LineChartView chart;

    public CurveFragment() {
    }

    public CurveFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.in_curve, container, false);
        Log.i("CurveFragment","menuMidMode="+menuMidMode);
        menuMidMode=1;
        dayMode = 1;
        addView(view);
        addListener();
        hour = Tools.getHour();
        //addCurve();
        //获取今天
        obtainData(0);
        return view;
    }


    /**
     *
     * @param type 0-今天 1-昨天 2-前天
     */
    private void obtainData(final int type) {
        String url = Const.URL_PRE_THREE+TApplication.curSiteInfo.getUuid()+"&date="+type;
        new AndHttpRequest(context) {
            @Override
            public void getResponse(String response) {
                if(JsonUtil.getStatus(response)){
                    siteList = JsonUtil.getSiteList(response);
                    addCurve();
                }
            }
        }.doGet(url);
    }

    /**
     * 添加曲线数据并画曲线
     */
    private void addCurve() {
        List<PointValue> mPointValues = new ArrayList<PointValue>();
        List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
        String xName = "";
        String yName = "";
        int count = 24;
        if(dayMode == 1){
            count = hour;
        }
        for(int i=0; i<count; i++){
            mAxisXValues.add(new AxisValue(i).setLabel(String.valueOf(i)));
            switch (menuMidMode){
                case 1:
                    mPointValues.add(new PointValue(i, (float)siteList.get(i).getTemperature()));
                    xName = "空气温度";
                    yName = "℃";
                    break;
                case 2:
                    mPointValues.add(new PointValue(i, (float)siteList.get(i).getHumidity()));
                    xName = "空气湿度";
                    yName = "%";
                    break;
                case 3:
                    mPointValues.add(new PointValue(i, (float)siteList.get(i).getSoilTemp()));
                    xName = "土壤温度";
                    yName = "℃";
                    break;
                case 4:
                    mPointValues.add(new PointValue(i, (float)siteList.get(i).getIllu()));
                    xName = "光照";
                    yName = "Lx";
                    break;
            }
        }
        initLineChart(xName,yName,mPointValues,mAxisXValues);

    }

    private void addListener() {
        rlMenu1.setOnClickListener(this);
        rlMenu2.setOnClickListener(this);
        rlMenu3.setOnClickListener(this);
        rlMenu4.setOnClickListener(this);
        txtToday.setOnClickListener(this);
        txtYesterday.setOnClickListener(this);
        txtThreeDay.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void addView(View view) {
        //中间菜单
        txtTemp = (TextView)view.findViewById(R.id.in_mid_menu).findViewById(R.id.txt_temp);
        txtTempVal = (TextView)view.findViewById(R.id.in_mid_menu).findViewById(R.id.txt_temp_val);
        txtHumidity = (TextView)view.findViewById(R.id.in_mid_menu).findViewById(R.id.txt_humidity);
        txtHumidityVal = (TextView)view.findViewById(R.id.in_mid_menu).findViewById(R.id.txt_humidity_val);
        txtSoil = (TextView)view.findViewById(R.id.in_mid_menu).findViewById(R.id.txt_soil);
        txtSoilVal = (TextView)view.findViewById(R.id.in_mid_menu).findViewById(R.id.txt_soil_val);
        txtIllu = (TextView)view.findViewById(R.id.in_mid_menu).findViewById(R.id.txt_illu);
        txtIlluVal = (TextView)view.findViewById(R.id.in_mid_menu).findViewById(R.id.txt_illu_val);
        viewLine1 = view.findViewById(R.id.in_mid_menu).findViewById(R.id.view_line1);
        viewLine2 = view.findViewById(R.id.in_mid_menu).findViewById(R.id.view_line2);
        viewLine3 = view.findViewById(R.id.in_mid_menu).findViewById(R.id.view_line3);
        viewLine4 = view.findViewById(R.id.in_mid_menu).findViewById(R.id.view_line4);

        rlMenu1 = (RelativeLayout)view.findViewById(R.id.rl_menu1);
        rlMenu2 = (RelativeLayout)view.findViewById(R.id.rl_menu2);
        rlMenu3 = (RelativeLayout)view.findViewById(R.id.rl_menu3);
        rlMenu4 = (RelativeLayout)view.findViewById(R.id.rl_menu4);

        txtToday = (TextView)view.findViewById(R.id.txt_today);
        txtYesterday = (TextView)view.findViewById(R.id.txt_yesterday);
        txtThreeDay = (TextView)view.findViewById(R.id.txt_three_day);

        chart = (LineChartView) view.findViewById(R.id.chart);

        if(TApplication.curSiteValue != null){

            String str = Tools.subZeroAndDot(String.valueOf(TApplication.curSiteValue.getTemperature()));

            txtTempVal.setText(String.valueOf(str+"℃"));
            txtHumidityVal.setText(String.valueOf(TApplication.curSiteValue.getHumidity())+"%");
            str = Tools.subZeroAndDot(String.valueOf(TApplication.curSiteValue.getSoilTemp()));
            txtSoilVal.setText(str+"℃");
            txtIlluVal.setText(String.valueOf(TApplication.curSiteValue.getIllu())+"Lx");
        }

        txtTemp.setTextColor(getResources().getColor(R.color.green_none));
        txtTempVal.setTextColor(getResources().getColor(R.color.green_none));
        txtTempVal.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_green));
        viewLine1.setBackgroundColor(getResources().getColor(R.color.green_none));
    }

    /**
     * 日期图标回复原始状态
     */
    private void clearDayUI(){
        txtToday.setTextColor(getResources().getColor(R.color.black_text_n));
        txtToday.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_gray));
        txtYesterday.setTextColor(getResources().getColor(R.color.black_text_n));
        txtYesterday.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_gray));
        txtThreeDay.setTextColor(getResources().getColor(R.color.black_text_n));
        txtThreeDay.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_gray));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_menu1://空气温度
                if(menuMidMode != 1){
                    clearMidMenu();
                    menuMidMode = 1;
                    txtTemp.setTextColor(getResources().getColor(R.color.green_none));
                    txtTempVal.setTextColor(getResources().getColor(R.color.green_none));
                    txtTempVal.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_green));
                    viewLine1.setBackgroundColor(getResources().getColor(R.color.green_none));
                    addCurve();
                }
                break;
            case R.id.rl_menu2://空气湿度
                if(menuMidMode != 2){
                    clearMidMenu();
                    menuMidMode = 2;

                    txtHumidity.setTextColor(getResources().getColor(R.color.green_none));
                    txtHumidityVal.setTextColor(getResources().getColor(R.color.green_none));
                    txtHumidityVal.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_green));
                    viewLine2.setBackgroundColor(getResources().getColor(R.color.green_none));
                    addCurve();
                }
                break;
            case R.id.rl_menu3://土壤温度
                if(menuMidMode != 3){
                    clearMidMenu();
                    menuMidMode = 3;
                    txtSoil.setTextColor(getResources().getColor(R.color.green_none));
                    txtSoilVal.setTextColor(getResources().getColor(R.color.green_none));
                    txtSoilVal.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_green));
                    viewLine3.setBackgroundColor(getResources().getColor(R.color.green_none));
                    addCurve();
                }
                break;
            case R.id.rl_menu4://光照
                if(menuMidMode != 4){
                    clearMidMenu();
                    menuMidMode = 4;
                    txtIllu.setTextColor(getResources().getColor(R.color.green_none));
                    txtIlluVal.setTextColor(getResources().getColor(R.color.green_none));
                    txtIlluVal.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_green));
                    viewLine4.setBackgroundColor(getResources().getColor(R.color.green_none));
                    addCurve();
                }
                break;
            case R.id.txt_today://今天
                if(dayMode != 1){
                    dayMode = 1;
                    clearDayUI();
                    txtToday.setTextColor(getResources().getColor(R.color.white));
                    txtToday.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_sky));
                    // initSiteDatas();

                    obtainData(0);

                }
                break;
            case R.id.txt_yesterday://昨天
                if(dayMode != 2){
                    dayMode = 2;
                    clearDayUI();
                    txtYesterday.setTextColor(getResources().getColor(R.color.white));
                    txtYesterday.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_sky));
                    // initSiteDatas();

                    obtainData(1);
                }
                break;
            case R.id.txt_three_day://前天
                if(dayMode != 3){
                    dayMode = 3;
                    clearDayUI();
                    txtThreeDay.setTextColor(getResources().getColor(R.color.white));
                    txtThreeDay.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_sky));
                    //initSiteDatas();
                    //
                    obtainData(2);
                }
                break;
        }
    }

    /**
     * 中间图标回复原始状态
     */
    private void clearMidMenu(){
        txtTemp.setTextColor(getResources().getColor(R.color.black_text_n));
        txtTempVal.setTextColor(getResources().getColor(R.color.black_text_n));
        txtTempVal.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_gray));
        viewLine1.setBackgroundColor(getResources().getColor(R.color.gray_light_n));

        txtHumidity.setTextColor(getResources().getColor(R.color.black_text_n));
        txtHumidityVal.setTextColor(getResources().getColor(R.color.black_text_n));
        txtHumidityVal.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_gray));
        viewLine2.setBackgroundColor(getResources().getColor(R.color.gray_light_n));

        txtSoil.setTextColor(getResources().getColor(R.color.black_text_n));
        txtSoilVal.setTextColor(getResources().getColor(R.color.black_text_n));
        txtSoilVal.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_gray));
        viewLine3.setBackgroundColor(getResources().getColor(R.color.gray_light_n));

        txtIllu.setTextColor(getResources().getColor(R.color.black_text_n));
        txtIlluVal.setTextColor(getResources().getColor(R.color.black_text_n));
        txtIlluVal.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_gray));
        viewLine4.setBackgroundColor(getResources().getColor(R.color.gray_light_n));
    }

    final int COLOR_TEXT = TApplication.context.getResources().getColor(R.color.black_text_n);    //文字颜色
    final int COLOR_POINT = TApplication.context.getResources().getColor(R.color.sky_blue);       //圆点颜色
    final int COLOR_CURVE = TApplication.context.getResources().getColor(R.color.green_none);     //折线、曲线颜色

    private void initLineChart(String xName,String yName,List<PointValue> mPointValues,List<AxisValue> mAxisXValues){

        Line line = new Line(mPointValues).setColor(COLOR_CURVE);  //折线的颜色
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setPointColor(COLOR_POINT);//圆点颜色
        line.setCubic(true);//曲线是否平滑，即是曲线还是折线
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注

//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();

        data.setLines(lines);
        data.setValueLabelsTextColor(Color.WHITE);
        data.setValueLabelTextSize(6);


        //x坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setName(xName);  //表格名称
        axisX.setTextColor(COLOR_TEXT);  //设置x轴字体颜色

        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(7); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称

        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        //y坐标轴
        Axis axisY = new Axis();  //Y轴
        axisY.setName(yName);//y轴标注

        axisY.setTextSize(10);//设置字体大小
        axisY.setTextColor(COLOR_TEXT);  //设置x轴字体颜色
        axisY.setHasLines(true);
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边
        //设置行为属性，支持缩放、滑动以及平移

        chart.setInteractive(true);
        chart.setZoomType(ZoomType.HORIZONTAL);
        chart.setMaxZoom((float) 10);//最大方法比例
        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        chart.setLineChartData(data);
        chart.setVisibility(View.VISIBLE);

        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        Viewport v = new Viewport(chart.getMaximumViewport());
        v.left = 5;
        v.right= 17;
        chart.setCurrentViewport(v);
        /*Viewport port = initViewPort(0,50);
        chart.setCurrentViewportWithAnimation(port);
        chart.startDataAnimation();*/
    }
}
