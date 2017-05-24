package com.andbase.ssk.entity;


import com.andbase.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * "username":"jiwl",	//用户名
 * "realname":"",	//姓名
 * "nickname":"",	//昵称
 * "email":"",	//Email地址
 * "phone":"",	//手机号码
 * "address":""	//地址
 */
public class User implements Serializable{
    @SerializedName("userid")
    private String userId;
    @SerializedName("username")
    private String userName;
    private String password;
    @SerializedName("realname")
    private String realName;
    private String nickName;//昵称
    @SerializedName("address")
    private String address;
    private String email;
    private String phone;
    @SerializedName("userwentype")
    private String userExpertId;//CMS 用户专家组别，是个ID号
    @SerializedName("userrole")
    private String userRole;    //CMS用户角色
    private int hasAddDevice;  //是否有添加设备的权限 0无 1有

    public User(){
        this.userId = "";
        this.userName = "";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserExpertId() {
        return userExpertId;
    }

    public void setUserExpertId(String userExpertId) {
        this.userExpertId = userExpertId;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public int getHasAddDevice() {
        return hasAddDevice;
    }

    public void setHasAddDevice(int hasAddDevice) {
        this.hasAddDevice = hasAddDevice;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", realName='" + realName + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", userExpertId='" + userExpertId + '\'' +
                ", userRole='" + userRole + '\'' +
                '}';
    }
}
