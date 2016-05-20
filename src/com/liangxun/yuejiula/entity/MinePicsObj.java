package com.liangxun.yuejiula.entity;

/**
 * Created by zhl on 2016/5/20.
 */
public class MinePicsObj {
    private Integer integer;
    private String title;

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MinePicsObj(Integer integer, String title) {
        this.integer = integer;
        this.title = title;
    }
}
