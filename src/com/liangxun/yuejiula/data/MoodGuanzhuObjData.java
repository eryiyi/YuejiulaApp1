package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.MoodGuanzhuObj;

import java.util.List;

/**
 * Created by zhl on 2016/8/24.
 */
public class MoodGuanzhuObjData extends Data {
    private List<MoodGuanzhuObj> data;

    public List<MoodGuanzhuObj> getData() {

        return data;
    }

    public void setData(List<MoodGuanzhuObj> data) {
        this.data = data;
    }
}
