package com.hxsn.ssklf.model;

/**
 * 作者：jiely on 2017/6/14 15:12
 * 邮箱：songlj@fweb.cn
 */
public class Weather {
    private String createTime;//发布时间
    private String temperature; //温度
    private String humidity;  //湿度
    private String name;      //阴晴等
    private String windy;      //风


    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWindy() {
        return windy;
    }

    public void setWindy(String windy) {
        this.windy = windy;
    }
}
