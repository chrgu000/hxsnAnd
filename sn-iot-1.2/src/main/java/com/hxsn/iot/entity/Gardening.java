package com.hxsn.iot.entity;

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

    public Gardening(String id,String name){
        this.id = id;
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gardening gardening = (Gardening) o;

        if (!id.equals(gardening.id)) return false;
        return name.equals(gardening.name);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
