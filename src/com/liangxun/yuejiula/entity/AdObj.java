package com.liangxun.yuejiula.entity;

/**
 * 商家店铺广告
 */
public class AdObj {
    private String mm_ad_id;
    private String mm_ad_url;
    private String mm_ad_pic;
    private String mm_ad_title;
    private String emp_id;
    private String mm_ad_num;

    public String getMm_ad_title() {
        return mm_ad_title;
    }

    public void setMm_ad_title(String mm_ad_title) {
        this.mm_ad_title = mm_ad_title;
    }

    public String getMm_ad_id() {
        return mm_ad_id;
    }

    public void setMm_ad_id(String mm_ad_id) {
        this.mm_ad_id = mm_ad_id;
    }

    public String getMm_ad_url() {
        return mm_ad_url;
    }

    public void setMm_ad_url(String mm_ad_url) {
        this.mm_ad_url = mm_ad_url;
    }

    public String getMm_ad_pic() {
        return mm_ad_pic;
    }

    public void setMm_ad_pic(String mm_ad_pic) {
        this.mm_ad_pic = mm_ad_pic;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getMm_ad_num() {
        return mm_ad_num;
    }

    public void setMm_ad_num(String mm_ad_num) {
        this.mm_ad_num = mm_ad_num;
    }
}
