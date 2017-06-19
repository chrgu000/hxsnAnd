package com.hxsn.hf;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.hxsn.hf.base.BaseActivity;
import com.hxsn.hf.navigation.messageservice;
import com.hxsn.hf.utils.Shared;

public class WelcomeActivity extends BaseActivity {
	private ImageView welcome_logo;
	private Animation anim;
	
	
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		init();
		initAnimation();
		
		//启动消息推送服务
		startService(new Intent(this, messageservice.class));
		
		welcome_logo.setAnimation(anim);
	}

	private void init() {
		welcome_logo = (ImageView) this.findViewById(R.id.welcome_logo);
	}

	private void initAnimation() {
		anim = new AlphaAnimation(0.2f, 1.0f);
		anim.setDuration(1500);
	
		anim.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				Intent inte = new Intent();
				if (Shared.getIsFirstRun()) {
					inte.setClass(WelcomeActivity.this, FristWelcomeActivity.class);
				} else {
					//inte.setClass(WelcomeActivity.this, MainWebActivity.class);
					inte.setClass(WelcomeActivity.this, MainActivity.class);
				}
				startActivity(inte);
				overridePendingTransition(R.anim.infrom_right, R.anim.outto_left);
				finish();
			}
		});
	}
}
