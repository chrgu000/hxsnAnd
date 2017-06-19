package com.hxsn.hf;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hxsn.hf.base.BaseActivity;

@SuppressLint("SetJavaScriptEnabled")
public class MainWebActivity extends BaseActivity {

	private WebView main_web_wv;

	@SuppressLint("JavascriptInterface")
	@Override
	protected void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_web);

		main_web_wv = (WebView) findViewById(R.id.main_web_wv);

		WebSettings wSet = main_web_wv.getSettings();
		wSet.setJavaScriptEnabled(true);
		
		main_web_wv.addJavascriptInterface(new JavaScriptObject(this), "wst");
		String url = "file:///android_asset/index.html";
		main_web_wv.loadUrl(url);

		main_web_wv.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
		});
	}
}
