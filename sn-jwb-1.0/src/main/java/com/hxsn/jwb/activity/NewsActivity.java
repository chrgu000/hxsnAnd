package com.hxsn.jwb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.andbase.ssk.utils.LogUtil;
import com.hxsn.jwb.R;
import com.hxsn.jwb.utils.Const;


public class NewsActivity extends Activity {
    private WebView webView;
    private String id;
    private TextView txtTitle;
    private ImageView imgLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        LogUtil.i("NewsActivity","-------------------NewsActivity-------------------");

        addView();

        Intent intent = getIntent();
        String url;
        id = intent.getStringExtra("id");

            String name = intent.getStringExtra("name");
            txtTitle.setText(name);
            url = Const.URL_NONGSH_INFO+id;//+"&username="+TApplication.user.getUserName();

        setWebView(url);
        LogUtil.i("NewsActivity","url="+url);
    }

    private void addView() {
        webView = (WebView) findViewById(R.id.webView);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        imgLeft = (ImageView) findViewById(R.id.img_left);

        imgLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //设置webv
    private void setWebView(String url1) {
        try{
            WebSettings wSet = webView.getSettings();
            wSet.setJavaScriptEnabled(true);

            webView.removeAllViews();
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });

            webView.loadUrl(url1);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {


            if(webView.canGoBack()) {
                webView.goBack();//返回上一页面
            } else {
                super.onBackPressed();
            }

    }

}
