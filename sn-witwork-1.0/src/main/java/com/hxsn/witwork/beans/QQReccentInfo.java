package com.hxsn.witwork.beans;

/**
 * Copyright 2015 
 *
 *All right reserved
 *
 *Created on 2015-6-19  ����11:16:50
 *
 * @author  ����
 */
public class QQReccentInfo {
	
	private String DBName;
	private String userPhone;
	private String name;
	private String content;
	private String time;
	private int count;
	private String userPic;

	public String getUserPic() {
		return userPic;
	}

	public void setUserPic(String userPic) {
		this.userPic = userPic;
	}

	public String getDBName() {
		return DBName;
	}

	public void setDBName(String dBName) {
		DBName = dBName;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
