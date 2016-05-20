package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.News;

import java.util.List;

/**
 * Created by zhanghl on 2015/1/17.
 */
public class NewsDATA extends Data {
    private List<News> data;

    public List<News> getData() {
        return data;
    }

    public void setData(List<News> data) {
        this.data = data;
    }
}
