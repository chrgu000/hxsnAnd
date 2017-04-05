package com.hxsn.jwb.utils;

import android.graphics.Color;
import android.view.View;

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
import com.hxsn.jwb.R;
import com.hxsn.jwb.TApplication;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by jiely on 2017/3/9.
 */
public class ChartUtil {

    final static int COLOR_POINT = TApplication.context.getResources().getColor(R.color.red_dart);       //圆点颜色
    final static int COLOR_TEXT = TApplication.context.getResources().getColor(R.color.black_text_n);    //文字颜色
    public static void initLineChart(String xName,List<List<PointValue>> pointValueLists, LineChartView chart){

        List<Line> lines = new ArrayList<>();
        List<AxisValue> mAxisXValues = new ArrayList<>();
        int cnt = 0;
        for(List<PointValue> pointValueList: pointValueLists){
            Line line = new Line(pointValueList).setColor(Const.COLORS[cnt]);  //折线的颜色

            line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
            line.setPointColor(COLOR_POINT);//圆点颜色
            line.setPointRadius(2);
            line.setCubic(false);//曲线是否平滑，即是曲线还是折线
            line.setFilled(false);//是否填充曲线的面积
            line.setHasLabels(true);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
            line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
            line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
            lines.add(line);

            if(cnt == 0){

                for(int i=0; i<pointValueList.size(); i++){
                    mAxisXValues.add(new AxisValue(i).setLabel(String.valueOf(i)));
                }
            }
            cnt++;

        }

        LineChartData data = new LineChartData();
        data.setLines(lines);
        data.setValueLabelsTextColor(Color.GRAY);
        data.setValueLabelTextSize(6);

        //x坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setName(xName);  //表格名称
        axisX.setTextColor(COLOR_TEXT);  //设置x轴字体颜色

        axisX.setTextSize(13);//设置字体大小
        axisX.setMaxLabelChars(7); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称

        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        //y坐标轴
        Axis axisY = new Axis();  //Y轴
       // axisY.setName(yName);//y轴标注

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
