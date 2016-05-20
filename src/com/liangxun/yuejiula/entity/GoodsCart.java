package com.liangxun.yuejiula.entity;

/**
 * Created by Administrator on 2015/8/2.
 * ���ﳵ
 */
public class GoodsCart {
    private int goodspic;
    private String goodstitle;
    private String goodsnumber;
    private String goodsmoney;

    public int getGoodspic() {
        return goodspic;
    }

    public void setGoodspic(int goodspic) {
        this.goodspic = goodspic;
    }

    public String getGoodstitle() {
        return goodstitle;
    }

    public void setGoodstitle(String goodstitle) {
        this.goodstitle = goodstitle;
    }

    public String getGoodsnumber() {
        return goodsnumber;
    }

    public void setGoodsnumber(String goodsnumber) {
        this.goodsnumber = goodsnumber;
    }

    public String getGoodsmoney() {
        return goodsmoney;
    }

    public void setGoodsmoney(String goodsmoney) {
        this.goodsmoney = goodsmoney;
    }

    public GoodsCart(int goodspic, String goodstitle, String goodsnumber, String goodsmoney) {
        this.goodspic = goodspic;
        this.goodstitle = goodstitle;
        this.goodsnumber = goodsnumber;
        this.goodsmoney = goodsmoney;
    }
}
