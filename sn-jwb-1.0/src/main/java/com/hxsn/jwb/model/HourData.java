package com.hxsn.jwb.model;

/**
 * 鸡舍小时数据
 * Created by jiely on 2017/3/10.
 */
public class HourData {
    private String id;
    private String name;
    private float temperature;       //温度
    private int hour;        //时间
    private int dateType;

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

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getDateType() {
        return dateType;
    }

    public void setDateType(int dateType) {
        this.dateType = dateType;
    }
}
