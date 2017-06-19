package com.hxsn.hf.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hxsn.hf.R;
import com.hxsn.hf.base.BaseActivity;

public class FramListActivity  extends BaseActivity{
	private RelativeLayout framlist_btn;
	private ImageView back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_framlist);
		init();
		initOnClick();
		
	}
	
	private void init(){
		framlist_btn=(RelativeLayout) findViewById(R.id.framlist_btn);
		back = (ImageView) findViewById(R.id.back);
	}
	
	private void initOnClick(){

		 OnClickListener onClick=new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.framlist_btn:
					Intent intent =new Intent(FramListActivity.this,FramItemActivity.class);
					startActivity(intent);
					break;
					
				case R.id.back:
					finish();
					break;

				default:
					break;
				}
			}
		};

		framlist_btn.setOnClickListener(onClick);
		back.setOnClickListener(onClick);
	}
	
	
}