package com.hxsn.hf.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.hxsn.hf.R;
import com.hxsn.hf.base.BaseActivity;

public class FramItemActivity  extends BaseActivity{
	private Button camera_btn;
	private ImageView back;
	private Button duijiang_btn;
	boolean ismessagepush = true;//������������Ϊfalse; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fram_item);
		init();
		initOnClick();
		
		kaituisong();
		
	}
	
	private void init(){
		camera_btn =(Button) findViewById(R.id.camera_btn);
		back = (ImageView) findViewById(R.id.back);
		duijiang_btn = (Button) findViewById(R.id.duijiang_btn);
	}
	
	private void initOnClick(){
		
		//-------------------����¼��߼�-----------------------------
		
		 OnClickListener onClick=new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.camera_btn:
					Intent intent = new Intent();
					intent.putExtra("channelName", "dahua_1");
					intent.putExtra("channelId", "1000000$1$0$0");
					intent.setClass(FramItemActivity.this, NewRealPlayActivity.class);
					startActivity(intent);
					break;
					
				case R.id.back:
					finish();
					break;
					
				case R.id.duijiang_btn:
					ssduijiang();
					break;

				default:
					break;
				}
			}
		};
		//----------------------�ؼ���Ӽ���---------------------
		camera_btn.setOnClickListener(onClick);
		back.setOnClickListener(onClick);
		duijiang_btn.setOnClickListener(onClick);
	}
	
	private void ssduijiang(){
		Intent intent = new Intent();
		intent.putExtra("channelId", "1000000$1$0$0");
		intent.putExtra("channelName", "dahua_1");
		//intent.putExtra("deviceId", "0A3DB68D13021011");
		intent.putExtra("deviceId", "1000000");
		intent.setClass(FramItemActivity.this, OperateSoundTalk.class);
		startActivity(intent);
	}
	
	private void kaituisong(){
		
		if(ismessagepush){ 
			startService(new Intent(this, messageservice.class));
			ismessagepush = false;
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(!ismessagepush){ 
			stopService(new Intent(this, messageservice.class));
		}
	}
	
}