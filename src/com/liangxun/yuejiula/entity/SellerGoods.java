package com.liangxun.yuejiula.entity;

import java.io.Serializable;

/**
 * author: ${zhanghailong}
 * Date: 2015/3/27
 * Time: 13:27
 * 商品商家表
 */
public class SellerGoods implements Serializable {
    private String id;
    private String empId;
    private String contractId;
    private String schoolId;
    private String endTime;
    private String dateline;
    private String empName;//�̼�����
    private String empCover;//�̼�ͷ��
    private String schoolName;//ѧУ����
    private String mobile;

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpCover() {
        return empCover;
    }

    public void setEmpCover(String empCover) {
        this.empCover = empCover;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public SellerGoods(String id, String empId, String contractId, String schoolId, String endTime, String dateline) {
        this.id = id;
        this.empId = empId;
        this.contractId = contractId;
        this.schoolId = schoolId;
        this.endTime = endTime;
        this.dateline = dateline;
    }
}
