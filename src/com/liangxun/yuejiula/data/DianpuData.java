package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.EmpDianpu;

import java.util.List;

/**
 * Created by zhl on 2016/5/23.
 */
public class DianpuData extends Data {
    private List<EmpDianpu>  data;

    public List<EmpDianpu> getData() {
        return data;
    }

    public void setData(List<EmpDianpu> data) {
        this.data = data;
    }
}
