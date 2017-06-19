package com.hxsn.hf.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class BaseService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	protected void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	protected void showLongToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}
