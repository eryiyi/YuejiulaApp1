package com.liangxun.yuejiula.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/4/9.
 * * PK主题
 */
public class PKTheme implements Serializable {
    private String id;
    private String title;
    private int number;//第几期
    private String mudi;//PK目的
    private String startTime;//PK时间
    private String endTime;
    private String content;//PK内容
    private String dateline;
    private String isUse;//是否结束，0未开始  1 正在进行  2一结束
    private String picUrl;
    private String type;//主题类别  0 文字  1 图片  2 视频

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getMudi() {
        return mudi;
    }

    public void setMudi(String mudi) {
        this.mudi = mudi;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getIsUse() {
        return isUse;
    }

    public void setIsUse(String isUse) {
        this.isUse = isUse;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
