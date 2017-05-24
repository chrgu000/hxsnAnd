package com.hxsn.zzd.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.base.BaseTitle;
import com.hxsn.zzd.utils.Const;
import com.hxsn.zzd.utils.UpdateUtil;


/**
 * 关于我们
 */
public class AboutUsActivity extends Activity {
    private WebView webView;
    private TextView txtVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        BaseTitle.getInstance(this).setTitle("关于我们");
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(Const.URL_ABOUT_WEB);

        txtVersion = (TextView)findViewById(R.id.txt_version);

        String version = "zzd_v" + UpdateUtil.getThisAppVersion();
        switch (TApplication.versionType) {
            case Const.TEST_VERTION:
                version += "T";
                break;
            case Const.TEST_VERTION1:
                version += "T1";
                break;
            case Const.DEBUG_VERTION:
                version += "D";
                break;
            case Const.RELEASE_VERTION:
                version += "R";
                break;
        }
        txtVersion.setText(version);

    }

}
