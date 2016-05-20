package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.SchoolRecordMood;

import java.util.List;

/**
 * Created by zhl on 2016/5/20.
 */
public class SchoolRecordMoodData extends Data {
    private List<SchoolRecordMood> data;

    public List<SchoolRecordMood> getData() {
        return data;
    }

    public void setData(List<SchoolRecordMood> data) {
        this.data = data;
    }
}
