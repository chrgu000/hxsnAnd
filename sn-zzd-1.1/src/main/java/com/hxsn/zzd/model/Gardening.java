package com.hxsn.zzd.model;

import java.util.List;

/**
 * 用户所管辖的园区
 * Created by jiely on 2017/1/18.
 */

public class Gardening {
    private String id;
    private String name;//大棚所在的园区名称
    private List<GreenHouse> greenHouseList;//单位所拥有的大棚列表

    public Gardening(){
        this.id = "";
    }

    public Gardening(String name, List<GreenHouse> greenHouseList) {
        this.id = "";
        this.name = name;
        this.greenHouseList = greenHouseList;
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

    public List<GreenHouse> getGreenHouseList() {
        return greenHouseList;
    }

    public void setGreenHouseList(List<GreenHouse> greenHouseList) {
        this.greenHouseList = greenHouseList;
    }
}
