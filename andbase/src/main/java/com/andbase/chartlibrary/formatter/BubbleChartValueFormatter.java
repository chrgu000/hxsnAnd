package com.andbase.chartlibrary.formatter;


import com.andbase.chartlibrary.model.BubbleValue;

public interface BubbleChartValueFormatter {

    public int formatChartValue(char[] formattedValue, BubbleValue value);
}
