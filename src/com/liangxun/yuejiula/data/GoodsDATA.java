package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.PaopaoGoods;

import java.util.List;

/**
 * Created by zhanghl on 2015/1/17.
 */
public class GoodsDATA extends Data {
    private List<PaopaoGoods> data;

    public List<PaopaoGoods> getData() {
        return data;
    }

    public void setData(List<PaopaoGoods> data) {
        this.data = data;
    }
}
