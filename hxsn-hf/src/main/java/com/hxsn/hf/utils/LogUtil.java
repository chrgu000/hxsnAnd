package com.hxsn.hf.utils;

import android.util.Log;

import com.hxsn.hf.MyApplication;

public class LogUtil {

	public static void showLog(String TAG, String message) {
		if (MyApplication.isShowLog) {
			Log.i("hf", TAG + "------" + message);
		}
	}
}
