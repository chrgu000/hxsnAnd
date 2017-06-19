package com.hxsn.hf;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hxsn.hf.base.BaseFragmentActivity;
import com.hxsn.hf.cs.CsFragment;
import com.hxsn.hf.discovery.DiscoveryFragment;
import com.hxsn.hf.me.MeFragment;
import com.hxsn.hf.navigation.FramClassListActivity;
import com.hxsn.hf.navigation.NavigationFragment;

public class MainActivity extends BaseFragmentActivity {
	private ImageView framListIbt;
	
	private FragmentManager fm;
	private LinearLayout navigation;
	private LinearLayout cs;                                                        
	private LinearLayout discovery;
	private LinearLayout me;
	
	
	private ImageView navigation_iv;
	private ImageView cs_iv;
	private ImageView discovery_iv;
	private ImageView me_iv;
	
	
	private  NavigationFragment navigationFragment;
	private  CsFragment csFragment;
	private  DiscoveryFragment discoveryFragment;
	private  MeFragment meFragment;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);
		init();
		initPage(0);
		initOnClick();
	}

	private void init() {
		
		navigation = (LinearLayout) findViewById(R.id.navigation);
		cs = (LinearLayout) findViewById(R.id.cs);
		discovery = (LinearLayout) findViewById(R.id.discovery);
		me = (LinearLayout) findViewById(R.id.me);
		
		navigationFragment = new NavigationFragment();
		csFragment = new CsFragment();
		discoveryFragment = new DiscoveryFragment();
		meFragment = new MeFragment();
		framListIbt =(ImageView) findViewById(R.id.fram_list_ibt);
		
		
		navigation_iv=(ImageView) findViewById(R.id.navigation_iv);
		cs_iv=(ImageView) findViewById(R.id.cs_iv);
		discovery_iv=(ImageView) findViewById(R.id.discovery_iv);
		me_iv=(ImageView) findViewById(R.id.me_iv);
	}

	private void initPage(int id) {
		if (id == 0) {
			
			navigation_iv.setImageResource(R.drawable.navigation_1); 
			cs_iv.setImageResource(R.drawable.cs_0);
			discovery_iv.setImageResource(R.drawable.discovery_0);
			me_iv.setImageResource(R.drawable.me_0);
			
			framListIbt.setVisibility(View.VISIBLE);
			initFM(0);
		}else if(id==1){
			navigation_iv.setImageResource(R.drawable.navigation_0); 
			cs_iv.setImageResource(R.drawable.cs_1);
			discovery_iv.setImageResource(R.drawable.discovery_0);
			me_iv.setImageResource(R.drawable.me_0);
			framListIbt.setVisibility(View.GONE);
			initFM(1);
		}else if(id==2){
			navigation_iv.setImageResource(R.drawable.navigation_0); 
			cs_iv.setImageResource(R.drawable.cs_0);
			discovery_iv.setImageResource(R.drawable.discovery_1);
			me_iv.setImageResource(R.drawable.me_0);
			framListIbt.setVisibility(View.GONE);
			initFM(2);
		}else if(id==3){
			navigation_iv.setImageResource(R.drawable.navigation_0); 
			cs_iv.setImageResource(R.drawable.cs_0);
			discovery_iv.setImageResource(R.drawable.discovery_0);
			me_iv.setImageResource(R.drawable.me_1);
			framListIbt.setVisibility(View.GONE);
			initFM(3);
		}

	}

	private void initFM(int id) {
		fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		if (id == 0) {
			transaction.replace(R.id.main_fragment, navigationFragment);
		}else if(id==1){
			transaction.replace(R.id.main_fragment, csFragment);
		}else if(id==2){
			transaction.replace(R.id.main_fragment, discoveryFragment);
		}else if(id==3){
			transaction.replace(R.id.main_fragment, meFragment);
		}
		transaction.commit();
	}

	private void initOnClick() {

		// -------------------����¼��߼�-----------------------------

		OnClickListener onClick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				switch (v.getId()) {
				case R.id.navigation:
					initPage(0);
					break;
				case R.id.cs:
					initPage(1);
					break;
				case R.id.discovery:
					initPage(2);
					break;
				case R.id.me:
					initPage(3);
					break;
				case R.id.fram_list_ibt:
					Intent intent =new Intent(MainActivity.this,FramClassListActivity.class);
					startActivity(intent);
					break;
				default:
					break;
				}
			}
		};
		// ----------------------�ؼ���Ӽ���---------------------

		navigation.setOnClickListener(onClick);
		discovery.setOnClickListener(onClick);
		cs.setOnClickListener(onClick);
		me.setOnClickListener(onClick);
		framListIbt.setOnClickListener(onClick);
	}
}
