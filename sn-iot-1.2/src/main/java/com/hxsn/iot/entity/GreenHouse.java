package com.hxsn.iot.entity;

import java.io.Serializable;

/**
 * 温室大棚
 * Created by jiely on 2017/1/18.
 */
public class GreenHouse implements Serializable{
    private String id;
    private String name;//大棚名称

    public GreenHouse(){
        this.id = "";
    }

    public GreenHouse(String id, String name) {
        this.id = id;
        this.name = name;
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
}
