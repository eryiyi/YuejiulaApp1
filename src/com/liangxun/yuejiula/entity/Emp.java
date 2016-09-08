package com.liangxun.yuejiula.entity;

import java.io.Serializable;

/**
 * author: ${zhanghailong}
 * Date: 2015/1/29
 * Time: 22:29
 * 类的功能、说明写在此处.
 */
public class Emp implements Serializable {
    private String empId;
    private String empMobile;
    private String mobileStatus;
    private String empPass;
    private String empName;
    private String empCover;
    private String empSex;
    private String empSign;
    private String empQQ;
    private String schoolId;
    private String empTypeId;
    private String isUse;//是否禁用
    private String dateline;
    private String universityName;//圈子名字
    private String levelName;//等级名字
    private String jfcount;//积分
    private String hxUsername;
    private String isInGroup;
    private String groupId;
    private String is_fenghao;
    private String is_fengqun;

    public String getIs_fenghao() {
        return is_fenghao;
    }

    public void setIs_fenghao(String is_fenghao) {
        this.is_fenghao = is_fenghao;
    }

    public String getIs_fengqun() {
        return is_fengqun;
    }

    public void setIs_fengqun(String is_fengqun) {
        this.is_fengqun = is_fengqun;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getIsInGroup() {
        return isInGroup;
    }

    public void setIsInGroup(String isInGroup) {
        this.isInGroup = isInGroup;
    }

    private String pushId;

    public String getMobileStatus() {
        return mobileStatus;
    }

    public void setMobileStatus(String mobileStatus) {
        this.mobileStatus = mobileStatus;
    }


    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getJfcount() {
        return jfcount;
    }

    public void setJfcount(String jfcount) {
        this.jfcount = jfcount;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpMobile() {
        return empMobile;
    }

    public void setEmpMobile(String empMobile) {
        this.empMobile = empMobile;
    }

    public String getEmpPass() {
        return empPass;
    }

    public void setEmpPass(String empPass) {
        this.empPass = empPass;
    }

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

    public String getEmpSex() {
        return empSex;
    }

    public void setEmpSex(String empSex) {
        this.empSex = empSex;
    }

    public String getEmpSign() {
        return empSign;
    }

    public void setEmpSign(String empSign) {
        this.empSign = empSign;
    }

    public String getEmpQQ() {
        return empQQ;
    }

    public void setEmpQQ(String empQQ) {
        this.empQQ = empQQ;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getEmpTypeId() {
        return empTypeId;
    }

    public void setEmpTypeId(String empTypeId) {
        this.empTypeId = empTypeId;
    }

    public String getIsUse() {
        return isUse;
    }

    public void setIsUse(String isUse) {
        this.isUse = isUse;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getHxUsername() {
        return hxUsername;
    }

    public void setHxUsername(String hxUsername) {
        this.hxUsername = hxUsername;
    }
}
