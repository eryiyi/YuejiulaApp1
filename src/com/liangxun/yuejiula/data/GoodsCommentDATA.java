package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.GoodsComment;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/2
 * Time: 14:59
 * 类的功能、说明写在此处.
 */
public class GoodsCommentDATA extends Data {

    private List<GoodsComment> data;

    public List<GoodsComment> getData() {
        return data;
    }

    public void setData(List<GoodsComment> data) {
        this.data = data;
    }
}
