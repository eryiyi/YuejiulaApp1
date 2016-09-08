package com.liangxun.yuejiula.entity;

import java.io.Serializable;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/4
 * Time: 8:30
 * 类的功能、说明写在此处.
 */
public class Ad implements Serializable {
    private String adId;
    private String adPic;//图片链接
    private String adTypeId;//是否大图
    private String adIsUse;//是否可用
    private String adSchoolId;//圈子ID
    private String dateline;//创建时间
    private String adUrl;//广告链接
    private String endTime;//过期时间

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getAdPic() {
        return adPic;
    }

    public void setAdPic(String adPic) {
        this.adPic = adPic;
    }

    public String getAdTypeId() {
        return adTypeId;
    }

    public void setAdTypeId(String adTypeId) {
        this.adTypeId = adTypeId;
    }

    public String getAdIsUse() {
        return adIsUse;
    }

    public void setAdIsUse(String adIsUse) {
        this.adIsUse = adIsUse;
    }

    public String getAdSchoolId() {
        return adSchoolId;
    }

    public void setAdSchoolId(String adSchoolId) {
        this.adSchoolId = adSchoolId;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
