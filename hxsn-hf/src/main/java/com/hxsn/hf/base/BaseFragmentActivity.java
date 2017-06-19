package com.hxsn.hf.base;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class BaseFragmentActivity extends FragmentActivity {
	
	protected void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
}
