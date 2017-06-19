package com.hxsn.hf.navigation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.hxsn.hf.JavaScriptObject;
import com.hxsn.hf.R;

public class XiaoxiActivity extends Activity {
	
	private WebView main_web_xiaoxi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.xiaoxi_activity);
		
		inint();
	}

	private void inint() {
		main_web_xiaoxi = (WebView) findViewById(R.id.main_web_xiaoxi);
		
		WebSettings wSet = main_web_xiaoxi.getSettings();
		wSet.setJavaScriptEnabled(true);
		
		main_web_xiaoxi.addJavascriptInterface(new JavaScriptObject(this), "wst");
		String url = "file:///android_asset/xq.html";
		main_web_xiaoxi.loadUrl(url);

		main_web_xiaoxi.setWebViewClient(new WebViewClient() {
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
