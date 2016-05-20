package com.liangxun.yuejiula.entity;

import java.io.Serializable;
import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/3/27
 * Time: 14:59
 * 类的功能、说明写在此处.
 */
public class SellerGoodsForm implements Serializable {
    private List<SellerGoods> list;

    public List<SellerGoods> getList() {
        return list;
    }

    public void setList(List<SellerGoods> list) {
        this.list = list;
    }
}
