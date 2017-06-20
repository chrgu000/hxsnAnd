package com.hxsn.safe;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;

/**
 * 作者：jiely on 2017/6/19 16:25
 * 邮箱：songlj@fweb.cn
 */
public class MarkerFire extends LinearLayout implements Serializable {
    private Context mContext = null;
    private View mView = null;
    private TextView fire_time;

    public MarkerFire(Context context, Fire fire) {
        super(context);
        mContext = context;
        initView();
        setfireInfo(fire);
    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.item_wen, null);
        fire_time = (TextView) mView.findViewById(R.id.txt_title);
        this.addView(mView);

    }

    /**
     * 设置poi详情信息
     *
     * @param fire
     */
    private void setfireInfo(Fire fire) {

        fire_time.setText(fire.getFireTime());

    }

    public MarkerFire(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
