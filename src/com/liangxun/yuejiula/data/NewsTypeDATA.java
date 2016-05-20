package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.NewsClassify;

import java.util.List;

/**
 * Created by zhanghl on 2015/1/17.
 */
public class NewsTypeDATA extends Data {
    private List<NewsClassify> data;

    public List<NewsClassify> getData() {
        return data;
    }

    public void setData(List<NewsClassify> data) {
        this.data = data;
    }
}
