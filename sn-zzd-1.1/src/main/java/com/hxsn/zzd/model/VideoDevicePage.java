package com.hxsn.zzd.model;

/**
 * 设备列表 分页信息
 * Created by jiely on 2017/1/12.
 */

public class VideoDevicePage {
    private int total;  //总设备个数
    private int page;   //设备起始页
    private int size;   //当前页的设备个数

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
