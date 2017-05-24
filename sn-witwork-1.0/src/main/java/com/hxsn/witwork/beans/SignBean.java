package com.hxsn.witwork.beans;

import java.util.ArrayList;

public class SignBean {
	
	private String type;
	private String text;
	private String dkcode;
	private ArrayList<SignListBean> signList;
	
	public ArrayList<SignListBean> getSignList() {
		return signList;
	}
	public void setSignList(ArrayList<SignListBean> signList) {
		this.signList = signList;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getDkcode() {
		return dkcode;
	}
	public void setDkcode(String dkcode) {
		this.dkcode = dkcode;
	}	
}
