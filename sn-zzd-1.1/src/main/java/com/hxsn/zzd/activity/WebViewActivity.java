package com.hxsn.zzd.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.p2p.WifiP2pConfig;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.andbase.ssk.utils.LogUtil;
import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private int shouldOverrideUrlLoadingCnt=0;
    private ImageView imgLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        addView();

        setWebView();

    }

    private void addView() {
        webView = (WebView)findViewById(R.id.web_view);
        WifiP2pConfig config = new WifiP2pConfig();
        ImageView imgLeft = (ImageView)findViewById(R.id.img_left);
        imgLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView txtMiddle = (TextView)findViewById(R.id.txt_middle);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        txtMiddle.setText(title);
    }


    //设置webView
    private void setWebView() {

        WebSettings wSet = webView.getSettings();
        wSet.setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        MyWebViewClient webViewClient = new MyWebViewClient();
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(TApplication.webUrl);

        LogUtil.i("WebViewActivity","urlWebView="+TApplication.webUrl);
    }


    @Override
    /**
     * webView页面返回处理
     */
    public void onBackPressed() {
        if(shouldOverrideUrlLoadingCnt == 0 ){
            super.onBackPressed();
        }else{
            if(shouldOverrideUrlLoadingCnt <= 0){
            }else {
                if(webView.canGoBack()) {
                    shouldOverrideUrlLoadingCnt--;
                    if(shouldOverrideUrlLoadingCnt<0){
                        if(shouldOverrideUrlLoadingCnt<-1){
                            super.onBackPressed();
                        }
                    }else if(shouldOverrideUrlLoadingCnt >= 0){
                        webView.goBack();//返回上一页面
                    }

                } else {
                    if(shouldOverrideUrlLoadingCnt<0){
                        super.onBackPressed();
                    }
                }
            }
        }

        Log.i("Home-onBackPressed","shouldOverrideUrlLoadingCnt="+shouldOverrideUrlLoadingCnt);
    }

    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            //TApplication.webUrl = url;

            Log.i("HomeActivity", "shouldOverrideUrlLoading-url="+url);
            shouldOverrideUrlLoadingCnt++;
            Log.i("Home-Override", "shouldOverrideUrlLoadingCnt=" + shouldOverrideUrlLoadingCnt);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.i("HomeActivity", "onPageStarted-url="+url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i("HomeActivity", "onPageFinished-url="+url);
            // TApplication.webUrl = url;
        }
    }
}
