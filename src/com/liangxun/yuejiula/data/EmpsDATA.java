package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.Emp;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/2
 * Time: 14:59
 * 类的功能、说明写在此处.
 */
public class EmpsDATA extends Data {

    private List<Emp> data;

    public List<Emp> getData() {
        return data;
    }

    public void setData(List<Emp> data) {
        this.data = data;
    }
}
