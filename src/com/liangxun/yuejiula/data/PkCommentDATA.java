package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.PKComment;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/2
 * Time: 14:59
 * PK评论
 */
public class PkCommentDATA extends Data {

    private List<PKComment> data;

    public List<PKComment> getData() {
        return data;
    }

    public void setData(List<PKComment> data) {
        this.data = data;
    }
}
