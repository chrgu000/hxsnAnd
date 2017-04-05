package com.hxsn.jwb.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页
 * Created by jiely on 2017/3/10.
 */
public class Pager<T> {
    // 当前页
    private int currentPage;
    // 总页数
    private int totalPage;
    // 总记录数
    private int totalRecord;
    // 每页条数
    private int pageSize;
    // 是否有下一页
    private boolean hasNext;

    private List<T> entitys = new ArrayList();// 记录当前页中的数据条目

    public Pager(int currentPage, int pageSize, List<T> entitys) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.entitys = entitys;
        int total = entitys.size()/pageSize;
        this.totalPage = total;
        if(entitys.size() % pageSize != 0){
            this.totalPage++;
        }
        this.totalRecord = entitys.size();
        if(this.currentPage == this.totalPage){
            hasNext = false;
        }else {
            hasNext = true;
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List getEntitys() {
        List<T> tempList = new ArrayList<>();
        for(int i=0; i<pageSize; i++){
            if(i == totalRecord){
                break;
            }
            tempList.add(entitys.get(currentPage*pageSize+i));
        }
        return tempList;
    }

    public void setEntitys(List entitys) {
        this.entitys = entitys;
    }
}
