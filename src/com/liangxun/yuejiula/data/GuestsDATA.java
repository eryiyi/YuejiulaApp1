package com.liangxun.yuejiula.data;

import com.liangxun.yuejiula.entity.Guest;

import java.util.List;

/**
 * Created by zhanghl on 2015/1/17.
 */
public class GuestsDATA extends Data {
    private List<Guest> data;

    public List<Guest> getData() {
        return data;
    }

    public void setData(List<Guest> data) {
        this.data = data;
    }
}
