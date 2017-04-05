package com.andbase.ssk.entity;

import java.util.List;

/**
 * 用户提的问题
 * Created by jiely on 2017/1/19.
 */

public class QuestionInfo {
    private String id;
    private String time;                    //时间
    private String title;                 //内容
    private String url;                     //图片
    private int isResponse;                 //是否回复
    private String username;                //提问题的人
    private List<AnswerInfo> answerList;    //回复列表

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsResponse() {
        return isResponse;
    }

    public void setIsResponse(int isResponse) {
        this.isResponse = isResponse;
    }

    public List<AnswerInfo> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<AnswerInfo> answerList) {
        this.answerList = answerList;
    }
}
