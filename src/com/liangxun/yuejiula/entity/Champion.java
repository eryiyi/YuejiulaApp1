package com.liangxun.yuejiula.entity;

public class Champion {
    private String id;
    private String themeNumber;//期次
    private String themeId;//主题ID
    private String empId;//会员ID
    private String schoolId;//圈子ID
    private String zpId;//作品ID
    private String type;//类别  0 全国  1圈子
    private String dateline;
    private String isPic;//是否传图  0未传  1传了
    private String uploadPic;//上传的图片
    private String isSure;//是否确认收到奖品 0  未领奖  1已领奖
    private String empName;//冠军昵称
    private String empCover;//冠军会员头像
    private String zpContent;//作品内容
    private String schoolName;//圈子名称
    private String picUrl;
    private String videoUrl;
    private String zpType;//作品类型
    private String themeName;//主题名称
    private String zanNum;//赞数量
    private String plNum;//评论数量

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThemeNumber() {
        return themeNumber;
    }

    public void setThemeNumber(String themeNumber) {
        this.themeNumber = themeNumber;
    }

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
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

    public String getZpId() {
        return zpId;
    }

    public void setZpId(String zpId) {
        this.zpId = zpId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getIsPic() {
        return isPic;
    }

    public void setIsPic(String isPic) {
        this.isPic = isPic;
    }

    public String getUploadPic() {
        return uploadPic;
    }

    public void setUploadPic(String uploadPic) {
        this.uploadPic = uploadPic;
    }

    public String getIsSure() {
        return isSure;
    }

    public void setIsSure(String isSure) {
        this.isSure = isSure;
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

    public String getZpContent() {
        return zpContent;
    }

    public void setZpContent(String zpContent) {
        this.zpContent = zpContent;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getZpType() {
        return zpType;
    }

    public void setZpType(String zpType) {
        this.zpType = zpType;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
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
}
