package com.hxsn.witwork.ui;


import android.content.Context;
import android.content.SharedPreferences;

public class Shared {

	private static SharedPreferences shared;
	
	public static void init(Context context) {

		shared = context.getSharedPreferences("HXSNInfo", Context.MODE_PRIVATE);
	}
	
	public static void setVersion(String version) {
		shared.edit().putString("version", version).commit();
	}
	public static String getVersion() {

		return shared.getString("version", "");
	}
	
	public static void setIsFirstRun(boolean isFirstRun) {
		shared.edit().putBoolean("isFirstRun", isFirstRun).commit();
	}
	public static boolean getIsFirstRun() {

		return shared.getBoolean("isFirstRun", true);
	}
	
	public static void setUserID(String userID) {
		shared.edit().putString("userID", userID).commit();
	}
	public static String getUserID() {

		return shared.getString("userID", "");
	}
	
	/**
	 * 不需要加GetURL.BASEURL
	 */
	public static void setUserHead(String userHead) {
		shared.edit().putString("userHead", userHead).commit();
	}
	public static String getUserHead() {

		return shared.getString("userHead", "");
	}
	
	public static void setUserName(String username) {
		shared.edit().putString("username", username).commit();
	}
	public static String getUserName() {

		return shared.getString("username", "");
	}
	
	public static void setPassword(String password) {
		shared.edit().putString("password", password).commit();
	}
	public static String getPassword() {

		return shared.getString("password", "");
	}
	
	public static void setNickName(String name) {
		shared.edit().putString("nickname", name).commit();
	}

	public static String getNickName() {

		return shared.getString("nickname", "");
	}
	
	
	public static void setCode(String code) {
		shared.edit().putString("code", code).commit();
	}
	
	public static String getCode() {
		return shared.getString("code", "");
	}
	
	
	
	public static void setSex(String sex) {
		shared.edit().putString("sex", sex).commit();
	}
	
	public static String getSex() {

		return shared.getString("sex", "男");
	}
	
	public static void setAddress(String addresName) {
		shared.edit().putString("address", addresName).commit();
	}
	public static String getAddress() {

		return shared.getString("address", "0");
	}
	
	public static void setUserHeadUrl(String userID, String UserHeadUrl) {
		shared.edit().putString(userID, UserHeadUrl).commit();
	}
	public static String getUserHeadUrl(String userID) {

		return shared.getString(userID, "");
	}
	
	//消息提醒设置
	public static void setMessageAlert(String flag) {
		shared.edit().putString("messageAlert", flag).commit();
	}
	public static String getMessageAlert() {

		return shared.getString("messageAlert", "1");
	}
}
