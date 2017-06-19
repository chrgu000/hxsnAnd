package com.hxsn.iot.entity;

import java.util.List;

/**
 * 控制设备名称,如 卷帘，温控，滴灌
 * 作者：jiely on 2017/6/8 13:58
 * 邮箱：songlj@fweb.cn
 */
public class ControlName {
    private String  id;
    private String name;
    private String king;    //控制设备类别，备用
    private List<Status> statusList;  //控制设备状态列表，每个状态对应一个控制按钮

    public Status GetInner() {
        return new Status();
    }

    public class Status{
        private String  id;
        private String name;

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

    public String getKing() {
        return king;
    }

    public void setKing(String king) {
        this.king = king;
    }

    public List<Status> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Status> statusList) {
        this.statusList = statusList;
    }
}
