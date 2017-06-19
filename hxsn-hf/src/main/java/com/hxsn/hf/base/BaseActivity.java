package com.hxsn.hf.base;

import android.app.Activity;
import android.widget.Toast;

public class BaseActivity extends Activity {

	protected void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	protected void showLongToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}
