package com.andbase.chartlibrary.listener;


import com.andbase.chartlibrary.model.PointValue;
import com.andbase.chartlibrary.model.SubcolumnValue;

public interface ComboLineColumnChartOnValueSelectListener extends OnValueDeselectListener {

    public void onColumnValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value);

    public void onPointValueSelected(int lineIndex, int pointIndex, PointValue value);

}
