package com.hxsn.safe;

import java.io.Serializable;

/**
 * 作者：jiely on 2017/6/19 16:27
 * 邮箱：songlj@fweb.cn
 */
public class Fire implements Serializable {
    private String fireTime;
    private String name;
    private String info;

    public String getFireTime() {
        return fireTime;
    }

    public void setFireTime(String fireTime) {
        this.fireTime = fireTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Fire(String fireTime, String name, String info) {
        this.fireTime = fireTime;
        this.name = name;
        this.info = info;
    }
}
