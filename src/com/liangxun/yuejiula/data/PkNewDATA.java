package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.PKWork;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/2
 * Time: 14:59
 * 类的功能、说明写在此处.
 */
public class PkNewDATA extends Data {

    private List<PKWork> data;

    public List<PKWork> getData() {
        return data;
    }

    public void setData(List<PKWork> data) {
        this.data = data;
    }
}
