package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.Champion;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/2
 * Time: 14:59
 * 冠军榜
 */
public class ChampionsDATA extends Data {

    private List<Champion> data;

    public List<Champion> getData() {
        return data;
    }

    public void setData(List<Champion> data) {
        this.data = data;
    }
}
