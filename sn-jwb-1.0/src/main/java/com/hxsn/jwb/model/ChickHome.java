package com.hxsn.jwb.model;

import java.io.Serializable;

/**
 *  Created by jiely on 2016/12/13.
 *  鸡舍
 */
public class ChickHome implements Serializable{
    private String id;
    private String name;        //鸡舍名称
    private String isWarning;   //是否有警报
    private float temperature;  //温度
    private String dayAge;      //鸡龄
    private long createTime;    //时间


    public ChickHome() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsWarning() {
        return isWarning;
    }

    public void setIsWarning(String isWarning) {
        this.isWarning = isWarning;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public String getDayAge() {
        return dayAge;
    }

    public void setDayAge(String dayAge) {
        this.dayAge = dayAge;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
