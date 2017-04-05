package com.andbase.chartlibrary.formatter;


import com.andbase.chartlibrary.model.SliceValue;

public interface PieChartValueFormatter {

    public int formatChartValue(char[] formattedValue, SliceValue value);
}
