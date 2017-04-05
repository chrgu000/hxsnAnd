package com.hxsn.jwb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.hxsn.jwb.R;
import com.hxsn.jwb.base.BaseTitle;


public class SystemSettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);

        BaseTitle.getInstance(this).setTitle("系统设置");
        final WebView webView = (WebView)findViewById(R.id.web_view);


    }

}
