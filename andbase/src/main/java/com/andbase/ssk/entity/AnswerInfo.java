package com.andbase.ssk.entity;

/**
 * 回答的内容
 * Created by jiely on 2017/1/19.
 */

public class AnswerInfo {
    private String id;
    private String time;                    //时间
    private String content;                 //内容
    private String name;                    //回复人

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
