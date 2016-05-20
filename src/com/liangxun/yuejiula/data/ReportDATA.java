package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.Report;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/2
 * Time: 14:59
 * 类的功能、说明写在此处.
 */
public class ReportDATA extends Data {

    private List<Report> data;

    public List<Report> getData() {
        return data;
    }

    public void setData(List<Report> data) {
        this.data = data;
    }
}
