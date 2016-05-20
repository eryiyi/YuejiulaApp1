package com.liangxun.yuejiula.entity;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/7
 * Time: 14:47
 * 类的功能、说明写在此处.
 */
public class PartTimeType {
    private String id;
    private String name;
    private String cover;

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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public PartTimeType(String id, String name, String cover) {
        this.id = id;
        this.name = name;
        this.cover = cover;
    }
}
