package com.andbase.chartlibrary.formatter;


import com.andbase.chartlibrary.model.PointValue;

public interface LineChartValueFormatter {

    public int formatChartValue(char[] formattedValue, PointValue value);
}
