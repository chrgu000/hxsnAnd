package com.hxsn.hf.discovery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hxsn.hf.R;
import com.hxsn.hf.base.BaseActivity;

public class CookBook_listActivity extends BaseActivity{
	private ImageView back;
	private RelativeLayout discovery_cookbooklist2_RL;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cookbook_list);
		init();
		initOnClick();
		
	}
	
	private void init(){
		back =(ImageView) findViewById(R.id.back);
		discovery_cookbooklist2_RL=(RelativeLayout) findViewById(R.id.discovery_cookbooklist2_RL);
	}
	
	private void initOnClick(){

		//-------------------点击事件逻辑-----------------------------
		
		 OnClickListener onClick=new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				Intent intent;
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.back:
					finish();
					break;
				case R.id.discovery_cookbooklist2_RL:
					intent =new Intent(CookBook_listActivity.this,FoodintroduceListCookbookActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
			}
		};
		//----------------------控件添加监听---------------------
		back.setOnClickListener(onClick);
		discovery_cookbooklist2_RL.setOnClickListener(onClick);
		
	}
	
	
}