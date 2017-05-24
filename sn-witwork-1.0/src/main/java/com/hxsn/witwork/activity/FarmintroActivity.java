package com.hxsn.witwork.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.hxsn.witwork.MainActivity;
import com.hxsn.witwork.R;
import com.hxsn.witwork.adapter.FirstAdapter;
import com.hxsn.witwork.ui.LoadingDialog;
import com.hxsn.witwork.ui.Shared;
import com.hxsn.witwork.utils.Const;
import com.hxsn.witwork.utils.ImgBtnEffact;

import java.util.ArrayList;


/**
 *
 */
@SuppressLint("ShowToast")
public class FarmintroActivity extends BaseActivity implements
		OnPageChangeListener {

	private ViewPager viewPager;
	private FirstAdapter viewPagerAdapter;
	private WebView wodejjWV;
	private ArrayList<View> viewList;
	private ImageButton wdjjbackBtn;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_farmintro);
		init();
		setchangePager(0);
		initListener();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void init() {
		viewList = new ArrayList<View>();
		viewList.add(getLayoutInflater().inflate(R.layout.page_1, null));
		viewList.add(getLayoutInflater().inflate(R.layout.page_2, null));
		viewList.add(getLayoutInflater().inflate(R.layout.page_4, null));
		viewList.add(getLayoutInflater().inflate(R.layout.page_5, null));
		viewPager = (ViewPager) findViewById(R.id.farm_welcome_vp);

		wdjjbackBtn = (ImageButton) viewList.get(3).findViewById(R.id.wdjjbackBtn);
		wodejjWV = (WebView) viewList.get(3).findViewById(R.id.wodejjWV);

		WebSettings wSet = wodejjWV.getSettings();
		wSet.setJavaScriptEnabled(true);
		String url = Const.WELCOME+ Shared.getUserID();
		wodejjWV.loadUrl(url);
		wodejjWV.setWebViewClient(new MyWebViewClient());

		viewPagerAdapter = new FirstAdapter(viewList);
		viewPager.setAdapter(viewPagerAdapter);
	}

	@SuppressWarnings("deprecation")
	private void initListener() {
		viewPager.setOnPageChangeListener(this);

		wdjjbackBtn.setOnTouchListener(ImgBtnEffact.btnTL);
		wdjjbackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent inte = new Intent(FarmintroActivity.this,
						MainActivity.class);
				inte.putExtra("tag", 3);
				startActivity(inte);
				finish();
			}
		});
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		setchangePager(arg0);
	}

	private void setchangePager(int id) {

	}

	private class MyWebViewClient extends WebViewClient {
		public MyWebViewClient() {

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {// 网页页面开始加载的时候
			LoadingDialog.showLoading(FarmintroActivity.this);
			wodejjWV.setEnabled(false);// 当加载网页的时候将网页进行隐藏
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {// 网页加载结束的时候
			LoadingDialog.dissmissLoading();
			wodejjWV.setEnabled(true);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) { // 网页加载时的连接的网址
			view.loadUrl(url);
			return true;
		}
	}

}
