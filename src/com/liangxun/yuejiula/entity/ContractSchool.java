package com.liangxun.yuejiula.entity;

/**
 * author: ${zhanghailong}
 * Date: 2015/3/27
 * Time: 9:16
 * 商家承包学校表
 */
public class ContractSchool {
    private String id;
    private String empId;
    private String schoolId;
    private String endTime;//到期时间
    private String dateline;//保存时间
    private String empName;
    private String schoolName;
    private String empCover;
    private String isOpen;//是否开通，服务端无此字段 我用来标记代理商是否给商家开通此学校
    private String isSelected;//是否选中，服务端无此字段 我用来标记选择学校用  0否  1是

    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

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

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
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
}
