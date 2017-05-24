package com.hxsn.witwork.beans;

public class OffLineMessage {

	private String userPhone;
	private int msgNum = 0;
	
	public String getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	public int getMsgNum() {
		return msgNum;
	}
	public void setMsgNum() {
		msgNum ++;
	}
	
}
