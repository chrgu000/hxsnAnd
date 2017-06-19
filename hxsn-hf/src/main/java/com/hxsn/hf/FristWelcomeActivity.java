package com.hxsn.hf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.hxsn.hf.adapter.FirstAdapter;
import com.hxsn.hf.base.BaseActivity;
import com.hxsn.hf.login.LoginActivity;
import com.hxsn.hf.utils.Shared;

import java.util.ArrayList;

@SuppressLint("InflateParams")
public class FristWelcomeActivity extends BaseActivity implements OnPageChangeListener{
	
	private ImageView dian[];
	private ImageView page_4_img;
	private ViewPager viewPager;
	private FirstAdapter viewPagerAdapter;
	
	private ArrayList<View> viewList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_welcome);
		
		init();
		setchangePager(0);
		initListener();
	}
	
	private void init(){
		viewList = new ArrayList<View>();
		viewList.add(getLayoutInflater().inflate(R.layout.page_1, null));
		viewList.add(getLayoutInflater().inflate(R.layout.page_2, null));
		viewList.add(getLayoutInflater().inflate(R.layout.page_3, null));
		viewList.add(getLayoutInflater().inflate(R.layout.page_4, null));
		
		dian = new ImageView[4];
		dian[0] = (ImageView) findViewById(R.id.dian_0);
		dian[1] = (ImageView) findViewById(R.id.dian_1);
		dian[2] = (ImageView) findViewById(R.id.dian_2);
		dian[3] = (ImageView) findViewById(R.id.dian_3);
				
		viewPager = (ViewPager) findViewById(R.id.frist_welcome_vp);
		page_4_img = (ImageView) viewList.get(3).findViewById(R.id.page_4_img);
		
		viewPagerAdapter = new FirstAdapter(viewList);
		viewPager.setAdapter(viewPagerAdapter);
	}
	
	@SuppressWarnings("deprecation")
	private void initListener() {
		viewPager.setOnPageChangeListener(this);
		
		page_4_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Shared.setIsFirstRun(false);
				//Intent intent = new Intent(FristWelcomeActivity.this, MainWebActivity.class);
				Intent intent = new Intent(FristWelcomeActivity.this, LoginActivity.class);
				startActivity(intent);
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
	
	private void setchangePager(int id){
		for (int i = 0; i < dian.length; i++) {
			if (i == id) {
				dian[i].setImageResource(R.drawable.dian1);
			} else {
				dian[i].setImageResource(R.drawable.dian0);
			}
		}
	}

}
