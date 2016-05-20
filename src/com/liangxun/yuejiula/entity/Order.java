package com.liangxun.yuejiula.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/8/14.
 */
public class Order implements Serializable{
    private String order_no;
    private String goods_id;
    private String emp_id;
    private String seller_emp_id;
    private String address_id;
    private String goods_count;
    private String payable_amount;
    private String create_time;
    private String pay_time;
    private String send_time;
    private String accept_time;
    private String completion_time;
    private String status;
    private String pay_status;
    private String distribution_type;
    private String distribution_status;
    private String postscript;
    private String point;
    private String trade_no;
    private String invoice;
    private String invoice_title;
    private String taxes;
    private String out_trade_no;

    private String provinceId;
    private String cityId;
    private String areaId;

    private String schoolId;

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getSeller_emp_id() {
        return seller_emp_id;
    }

    public void setSeller_emp_id(String seller_emp_id) {
        this.seller_emp_id = seller_emp_id;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getGoods_count() {
        return goods_count;
    }

    public void setGoods_count(String goods_count) {
        this.goods_count = goods_count;
    }

    public String getPayable_amount() {
        return payable_amount;
    }

    public void setPayable_amount(String payable_amount) {
        this.payable_amount = payable_amount;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getAccept_time() {
        return accept_time;
    }

    public void setAccept_time(String accept_time) {
        this.accept_time = accept_time;
    }

    public String getCompletion_time() {
        return completion_time;
    }

    public void setCompletion_time(String completion_time) {
        this.completion_time = completion_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public String getDistribution_type() {
        return distribution_type;
    }

    public void setDistribution_type(String distribution_type) {
        this.distribution_type = distribution_type;
    }

    public String getDistribution_status() {
        return distribution_status;
    }

    public void setDistribution_status(String distribution_status) {
        this.distribution_status = distribution_status;
    }

    public String getPostscript() {
        return postscript;
    }

    public void setPostscript(String postscript) {
        this.postscript = postscript;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getInvoice_title() {
        return invoice_title;
    }

    public void setInvoice_title(String invoice_title) {
        this.invoice_title = invoice_title;
    }

    public String getTaxes() {
        return taxes;
    }

    public void setTaxes(String taxes) {
        this.taxes = taxes;
    }

    public Order(String goods_id, String emp_id, String seller_emp_id, String address_id, String goods_count,String payable_amount, String distribution_type, String distribution_status, String postscript, String invoice, String invoice_title, String taxes ,String provinceId, String cityId, String areaId,String schoolId) {
        this.goods_id = goods_id;
        this.emp_id = emp_id;
        this.seller_emp_id = seller_emp_id;
        this.address_id = address_id;
        this.goods_count = goods_count;
        this.distribution_type = distribution_type;
        this.distribution_status = distribution_status;
        this.postscript = postscript;
        this.invoice = invoice;
        this.invoice_title = invoice_title;
        this.taxes = taxes;
        this.payable_amount = payable_amount;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.areaId = areaId;
        this.schoolId = schoolId;
    }
}
