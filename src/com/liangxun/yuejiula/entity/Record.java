package com.liangxun.yuejiula.entity;

import java.io.Serializable;

/**
 * author: ${zhanghailong}
 * Date: 2015/1/30
 * Time: 7:51
 * 动态类
 */
public class Record implements Serializable {
    private String recordId;
    private String recordType;
    private String recordCont;
    private String recordPicUrl;
    private String recordVoice;
    private String recordVideo;
    private String recordEmpId;
    private String recordSchoolId;
    private String recordIsUse;
    private String recordIsDel;
    private String dateLine;
    private String school_record_mood_id;
    private String school_record_mood_name;
    private String money;
    private String is_paimai;

    public String getIs_paimai() {
        return is_paimai;
    }

    public void setIs_paimai(String is_paimai) {
        this.is_paimai = is_paimai;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getSchool_record_mood_name() {
        return school_record_mood_name;
    }

    public void setSchool_record_mood_name(String school_record_mood_name) {
        this.school_record_mood_name = school_record_mood_name;
    }

    public String getSchool_record_mood_id() {
        return school_record_mood_id;
    }

    public void setSchool_record_mood_id(String school_record_mood_id) {
        this.school_record_mood_id = school_record_mood_id;
    }

    private String empName;//会员昵称
    private String empCover;//会员头像
    private String levelName;//会员等级
    private String zanNum;//赞数量
    private String plNum;//评论数量
    private int levelCount;//积分
    private String schoolName;//学校

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getRecordCont() {
        return recordCont;
    }

    public void setRecordCont(String recordCont) {
        this.recordCont = recordCont;
    }

    public String getRecordPicUrl() {
        return recordPicUrl;
    }

    public void setRecordPicUrl(String recordPicUrl) {
        this.recordPicUrl = recordPicUrl;
    }

    public String getRecordVoice() {
        return recordVoice;
    }

    public void setRecordVoice(String recordVoice) {
        this.recordVoice = recordVoice;
    }

    public String getRecordVideo() {
        return recordVideo;
    }

    public void setRecordVideo(String recordVideo) {
        this.recordVideo = recordVideo;
    }

    public String getRecordEmpId() {
        return recordEmpId;
    }

    public void setRecordEmpId(String recordEmpId) {
        this.recordEmpId = recordEmpId;
    }

    public String getRecordSchoolId() {
        return recordSchoolId;
    }

    public void setRecordSchoolId(String recordSchoolId) {
        this.recordSchoolId = recordSchoolId;
    }

    public String getRecordIsUse() {
        return recordIsUse;
    }

    public void setRecordIsUse(String recordIsUse) {
        this.recordIsUse = recordIsUse;
    }

    public String getRecordIsDel() {
        return recordIsDel;
    }

    public void setRecordIsDel(String recordIsDel) {
        this.recordIsDel = recordIsDel;
    }

    public String getDateLine() {
        return dateLine;
    }

    public void setDateLine(String dateLine) {
        this.dateLine = dateLine;
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

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getZanNum() {
        return zanNum;
    }

    public void setZanNum(String zanNum) {
        this.zanNum = zanNum;
    }

    public String getPlNum() {
        return plNum;
    }

    public void setPlNum(String plNum) {
        this.plNum = plNum;
    }

    public int getLevelCount() {
        return levelCount;
    }

    public void setLevelCount(int levelCount) {
        this.levelCount = levelCount;
    }
}
