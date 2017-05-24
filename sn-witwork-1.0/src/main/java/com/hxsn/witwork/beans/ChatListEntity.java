package com.hxsn.witwork.beans;

public class ChatListEntity {

	private String name;
	private String date;
	private String txt;
	private String rold;
	private String ivUrl;
	private String id;
	private String phone;
	private int remind;
	
	
	public ChatListEntity(String name, String date, String txt, String rold,
			String ivUrl, String id) {
		this.name = name;
		this.date = date;
		this.txt = txt;
		this.rold = rold;
		this.ivUrl = ivUrl;
		this.id = id;
	}
	
	
	public int getRemind() {
		return remind;
	}


	public void setRemind(int remind) {
		this.remind = remind;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getRold() {
		return rold;
	}
	public void setRold(String rold) {
		this.rold = rold;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIvUrl() {
		return ivUrl;
	}
	public void setIvUrl(String ivUrl) {
		this.ivUrl = ivUrl;
	}
	public ChatListEntity() {
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTxt() {
		return txt;
	}
	public void setTxt(String txt) {
		this.txt = txt;
	}
	@Override
	public String toString() {
		return "ChatListEntity [name=" + name + ", date=" + date + ", txt="
				+ txt + ", rold=" + rold + ", ivUrl=" + ivUrl + ", id=" + id
				+ "]";
	}
	
}
