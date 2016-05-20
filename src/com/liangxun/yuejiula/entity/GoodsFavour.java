package com.liangxun.yuejiula.entity;

/**
 * Created by Administrator on 2015/8/10.
 * 商品收藏
 */
public class GoodsFavour {
    private String favour_id;
    private String goods_id;
    private String emp_id_favour;
    private String emp_id_goods;
    private String dateline;

    public String getFavour_id() {
        return favour_id;
    }

    public void setFavour_id(String favour_id) {
        this.favour_id = favour_id;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getEmp_id_favour() {
        return emp_id_favour;
    }

    public void setEmp_id_favour(String emp_id_favour) {
        this.emp_id_favour = emp_id_favour;
    }

    public String getEmp_id_goods() {
        return emp_id_goods;
    }

    public void setEmp_id_goods(String emp_id_goods) {
        this.emp_id_goods = emp_id_goods;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }
}
