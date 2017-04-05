package com.hxsn.zzd.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.andbase.ssk.utils.LogUtil;
import com.hxsn.zzd.R;
import com.hxsn.zzd.base.BaseTitle;
import com.hxsn.zzd.utils.Const;



public class AboutUsActivity extends Activity {
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
        webView.addJavascriptInterface(new JsInterface(),"androidZzd");
        //DebugUtil.i("AboutUsActivity","webViewUrl = "+Const.URL_ABOUT_WEB);
        webView.loadUrl(Const.URL_ABOUT_WEB);

        //webView.loadUrl("http://192.168.12.97:8989/html/hello.html");
        //webView.loadUrl("http://192.168.12.97:8080/dyTable.html");

    }

    class JsInterface{
        /** Instantiate the interface and set the context */
        public JsInterface() {
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showCnt(int count) {
            LogUtil.i("TAG", "调用成功==================》》》》》count="+count);
        }

    }



}
