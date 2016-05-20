package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.Record;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/2
 * Time: 14:59
 * 类的功能、说明写在此处.
 */
public class RecordDATA extends Data {

    private List<Record> data;

    public List<Record> getData() {
        return data;
    }

    public void setData(List<Record> data) {
        this.data = data;
    }
}
