package com.andbase.chartlibrary.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;

import com.andbase.chartlibrary.computator.PreviewChartComputator;
import com.andbase.chartlibrary.gesture.PreviewChartTouchHandler;
import com.andbase.chartlibrary.model.ColumnChartData;
import com.andbase.chartlibrary.renderer.PreviewColumnChartRenderer;
import com.andbase.library.BuildConfig;


/**
 * Preview chart that can be used as overview for other ColumnChart. When you change Viewport of this chart, visible
 * area of other chart will change. For that you need also to use
 * { Chart#setViewportChangeListener(lecho.lib.hellocharts.listener.ViewportChangeListener)}
 *
 * @author Leszek Wach
 */
public class PreviewColumnChartView extends ColumnChartView {
    private static final String TAG = "ColumnChartView";

    protected PreviewColumnChartRenderer previewChartRenderer;

    public PreviewColumnChartView(Context context) {
        this(context, null, 0);
    }

    public PreviewColumnChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewColumnChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        chartComputator = new PreviewChartComputator();
        previewChartRenderer = new PreviewColumnChartRenderer(context, this, this);
        touchHandler = new PreviewChartTouchHandler(context, this);
        setChartRenderer(previewChartRenderer);
        setColumnChartData(ColumnChartData.generateDummyData());
    }

    public int getPreviewColor() {
        return previewChartRenderer.getPreviewColor();
    }

    public void setPreviewColor(int color) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Changing preview area color");
        }

        previewChartRenderer.setPreviewColor(color);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        final int offset = computeHorizontalScrollOffset();
        final int range = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }
}
