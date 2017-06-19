package com.hxsn.hf.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class Shared {

	private static SharedPreferences shared;
	
	public static void init(Context context) {
		// TODO Auto-generated constructor stub
		shared = context.getSharedPreferences("HFInfo", Context.MODE_PRIVATE);
	}
	
	public static void setVersion(String version) {
		shared.edit().putString("version", version).commit();
	}
	public static String getVersion() {
		// TODO Auto-generated method stub
		return shared.getString("version", "");
	}
	
	public static void setIsFirstRun(boolean isFirstRun) {
		shared.edit().putBoolean("isFirstRun", isFirstRun).commit();
	}
	public static boolean getIsFirstRun() {
		// TODO Auto-generated method stub
		return shared.getBoolean("isFirstRun", true);
	}
}
