package com.liangxun.yuejiula.entity;

import java.io.Serializable;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/1
 * Time: 15:05
 * 类的功能、说明写在此处.
 */
public class Guest implements Serializable {
    private String id;
    private String empOne;
    private String empTwo;
    private String dateline;
    private String nickName;
    private String cover;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpOne() {
        return empOne;
    }

    public void setEmpOne(String empOne) {
        this.empOne = empOne;
    }

    public String getEmpTwo() {
        return empTwo;
    }

    public void setEmpTwo(String empTwo) {
        this.empTwo = empTwo;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
