package com.andbase.chartlibrary.listener;


import com.andbase.chartlibrary.model.SliceValue;

public interface PieChartOnValueSelectListener extends OnValueDeselectListener {

    public void onValueSelected(int arcIndex, SliceValue value);

}
