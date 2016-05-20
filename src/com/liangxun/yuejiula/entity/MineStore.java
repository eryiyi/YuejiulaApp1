package com.liangxun.yuejiula.entity;

/**
 * Created by Administrator on 2015/8/17.
 */
public class MineStore {
    private Float pricesAllDay;//今日总的收入
    private Float pricesAll;//总的收入
    private String numberDay;//今日订单数
    private String numberAll;//总的订单数

    public Float getPricesAllDay() {
        return pricesAllDay;
    }

    public void setPricesAllDay(Float pricesAllDay) {
        this.pricesAllDay = pricesAllDay;
    }

    public Float getPricesAll() {
        return pricesAll;
    }

    public void setPricesAll(Float pricesAll) {
        this.pricesAll = pricesAll;
    }

    public String getNumberDay() {
        return numberDay;
    }

    public void setNumberDay(String numberDay) {
        this.numberDay = numberDay;
    }

    public String getNumberAll() {
        return numberAll;
    }

    public void setNumberAll(String numberAll) {
        this.numberAll = numberAll;
    }
}
