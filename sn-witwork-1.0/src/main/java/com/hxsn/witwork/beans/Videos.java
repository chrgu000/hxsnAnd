package com.hxsn.witwork.beans;

public class Videos {

	 String id;
	 String position;
	 String isdef;
	 String eqtype;
	 String address;
	 String name;
	 
	public Videos() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Videos(String id, String position, String isdef, String eqtype,
			String address, String name) {
		super();
		this.id = id;
		this.position = position;
		this.isdef = isdef;
		this.eqtype = eqtype;
		this.address = address;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getIsdef() {
		return isdef;
	}
	public void setIsdef(String isdef) {
		this.isdef = isdef;
	}
	public String getEqtype() {
		return eqtype;
	}
	public void setEqtype(String eqtype) {
		this.eqtype = eqtype;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	 
	 
	
}
