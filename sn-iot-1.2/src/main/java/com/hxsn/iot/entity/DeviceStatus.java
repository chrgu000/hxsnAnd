package com.hxsn.iot.entity;

/**
 * 作者：jiely on 2017/6/8 16:47
 * 邮箱：songlj@fweb.cn
 */
public class DeviceStatus {
    private String  id;
    private String status;
    private Long createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
