package com.liangxun.yuejiula.entity;

/**
 * Created by Administrator on 2015/8/14.
 */
public class OrderVo extends Order{

    private String empCover;//卖家头像
    private String empName;//卖家昵称
    private String goodsCover;//商品图片
    private String goodsTitle;//商品标题
    private String goodsPrice;//商品单价
    private String empMobile;//卖家手机号

    public String getEmpCover() {
        return empCover;
    }

    public void setEmpCover(String empCover) {
        this.empCover = empCover;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getGoodsCover() {
        return goodsCover;
    }

    public void setGoodsCover(String goodsCover) {
        this.goodsCover = goodsCover;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getEmpMobile() {
        return empMobile;
    }

    public void setEmpMobile(String empMobile) {
        this.empMobile = empMobile;
    }

    public OrderVo(String goods_id, String emp_id, String seller_emp_id, String address_id, String goods_count, String payable_amount, String distribution_type, String distribution_status, String postscript, String invoice, String invoice_title, String taxes, String provinceId, String cityId, String areaId, String empCover ,String schoolId) {
        super(goods_id, emp_id, seller_emp_id, address_id, goods_count, payable_amount, distribution_type, distribution_status, postscript, invoice, invoice_title, taxes, provinceId, cityId, areaId, schoolId);
        this.empCover = empCover;
    }
}
