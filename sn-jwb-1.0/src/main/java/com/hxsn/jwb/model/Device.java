package com.hxsn.jwb.model;

/**
 *
 * Created by jiely on 2017/3/13.
 */
public class Device {
    private String id;
    private String name;
    private float temperate;

    private long createTime;    //时间

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

    public float getTemperate() {
        return temperate;
    }

    public void setTemperate(float temperate) {
        this.temperate = temperate;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


}
