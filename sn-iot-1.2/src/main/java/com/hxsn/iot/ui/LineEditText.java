package com.hxsn.iot.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by jiely on 2016/4/27.
 */
public class LineEditText extends EditText {

    private Paint mPaint;

    /**
     * @param context
     * @param attrs
     */
    public LineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mPaint = new Paint();

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLUE);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //      画底线
        canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, mPaint);
    }
}
