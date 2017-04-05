package com.andbase.chartlibrary.listener;


import com.andbase.chartlibrary.model.PointValue;

public interface LineChartOnValueSelectListener extends OnValueDeselectListener {

    public void onValueSelected(int lineIndex, int pointIndex, PointValue value);

}
