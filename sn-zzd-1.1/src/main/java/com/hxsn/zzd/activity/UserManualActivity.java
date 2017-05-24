package com.hxsn.zzd.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.hxsn.zzd.R;
import com.hxsn.zzd.base.BaseTitle;

/**
 * 用户手册
 */
public class UserManualActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manual);

        BaseTitle.getInstance(this).setTitle("早知道用户手册");

        WebView webView = (WebView)findViewById(R.id.web_view);
        assert webView != null;
        webView.loadUrl("file:///android_asset/www/help.html");
    }
}
