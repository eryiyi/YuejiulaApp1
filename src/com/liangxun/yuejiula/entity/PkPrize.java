package com.liangxun.yuejiula.entity;

/**
 * Created by Administrator on 2015/4/9.
 * 奖品
 */
public class PkPrize {
    private String id;
    private String themeId;
    private String content;
    private String pic;
    private String schoolId;
    private String type;//0是我们   1是圈主
    private String dateline;
    private String themeNumber;//主题期次
    private String schoolName;//圈子名称

    public String getThemeNumber() {
        return themeNumber;
    }

    public void setThemeNumber(String themeNumber) {
        this.themeNumber = themeNumber;
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

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
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
}
