package com.hxsn.witwork.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hxsn.witwork.MainActivity;
import com.hxsn.witwork.R;
import com.hxsn.witwork.ui.LoadingDialog;
import com.hxsn.witwork.ui.Shared;
import com.hxsn.witwork.utils.Const;
import com.hxsn.witwork.utils.ImgBtnEffact;

public class WorkDetalActivity extends AppCompatActivity {
    String jobuuid="";
    ImageButton dtBackBtn;
    WebView wd_wv;
    LinearLayout tab1Layout, tab2Layout, tab3Layout, tab4Layout;
    Intent tabIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_detal);
        Intent inte=getIntent();
        if(inte!=null)
            jobuuid=inte.getStringExtra("jobuuid");
        initView();
    }

    private void initView() {
        tab1Layout = (LinearLayout) findViewById(R.id.tab1);
        tab2Layout = (LinearLayout) findViewById(R.id.tab2);
        tab3Layout = (LinearLayout) findViewById(R.id.tab3);
        tab4Layout = (LinearLayout) findViewById(R.id.tab4);

        wd_wv = (WebView) findViewById(R.id.wdxq_wv);

        tabIntent = new Intent(WorkDetalActivity.this, MainActivity.class);

        TextView dsfhTV=(TextView)findViewById(R.id.dsfhTV);
        dsfhTV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });


        WebSettings wSet = wd_wv.getSettings();
        wSet.setJavaScriptEnabled(true);
//		webView.addJavascriptInterface(new JavaScriptObject(this), "wst");
//		String url = "file:///android_asset/lsll.html";
        wd_wv.loadUrl(Const.ZYDETAIL+ Shared.getUserID()+"&jobuuid="+jobuuid);
        wd_wv.setWebViewClient(new MyWebViewClient());

        dtBackBtn = (ImageButton) findViewById(R.id.djBackBtn);
        dtBackBtn.setOnTouchListener(ImgBtnEffact.btnTL);
        dtBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(wd_wv.canGoBack())
                {
                    wd_wv.goBack();//返回上一页面
                }
                else
                {
                    finish();
                }
            }
        });

    }

    private class MyWebViewClient extends WebViewClient {
        public MyWebViewClient(){

        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {// 网页页面开始加载的时候
            LoadingDialog.showLoading(WorkDetalActivity.this);
            wd_wv.setEnabled(false);// 当加载网页的时候将网页进行隐藏
            super.onPageStarted(view, url, favicon);
        }

        public void onPageFinished(WebView view, String url) {// 网页加载结束的时候
            LoadingDialog.dissmissLoading();
            wd_wv.setEnabled(true);
        }


    }


    class NewIVListener implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tab1:
                    tabIntent.putExtra("tag", 0);
                    startActivity(tabIntent);
                    finish();
                    break;
                case R.id.tab2:
                    tabIntent.putExtra("tag", 1);
                    startActivity(tabIntent);
                    finish();
                    break;
                case R.id.tab3:
                    tabIntent.putExtra("tag", 2);
                    startActivity(tabIntent);
                    finish();
                    break;
                case R.id.tab4:
                    tabIntent.putExtra("tag", 3);
                    startActivity(tabIntent);
                    finish();
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(wd_wv.canGoBack())
            {
                wd_wv.goBack();//返回上一页面
                return true;
            }
            else
            {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
