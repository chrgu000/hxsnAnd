package com.hxsn.jwb.activity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.andbase.library.app.base.AbBaseActivity;
import com.hxsn.jwb.R;
import com.hxsn.jwb.base.BaseTitle;
import com.hxsn.jwb.utils.Const;


public class AboutUsActivity extends AbBaseActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        BaseTitle.getInstance(this).setTitle("关于我们");

        webView = (WebView) findViewById(R.id.webView);

        WebSettings wSet = webView.getSettings();
        wSet.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(Const.URL_ABOUT_WEB);

    }
}
