package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.Slide;

import java.util.List;

/**
 * Created by Administrator on 2015/9/1.
 */
public class SlideData extends Data {
    private List<Slide> data;

    public List<Slide> getData() {
        return data;
    }

    public void setData(List<Slide> data) {
        this.data = data;
    }
}
