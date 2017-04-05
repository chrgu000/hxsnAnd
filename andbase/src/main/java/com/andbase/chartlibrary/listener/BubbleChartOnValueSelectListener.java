package com.andbase.chartlibrary.listener;


import com.andbase.chartlibrary.model.BubbleValue;

public interface BubbleChartOnValueSelectListener extends OnValueDeselectListener {

    public void onValueSelected(int bubbleIndex, BubbleValue value);

}
