package com.hxsn.ssklf.model;

/**
 * 作者：jiely on 2017/5/9 08:48
 * 邮箱：songlj@fweb.cn
 */
public class WarningInfo {
    private String title;
    private String content;
    private long createTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
