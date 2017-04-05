package com.andbase.chartlibrary.formatter;


import com.andbase.chartlibrary.model.SubcolumnValue;

public interface ColumnChartValueFormatter {

    public int formatChartValue(char[] formattedValue, SubcolumnValue value);

}
