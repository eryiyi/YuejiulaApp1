package com.liangxun.yuejiula.entity;

import com.liangxun.yuejiula.db.DaoSession;
import com.liangxun.yuejiula.db.RecordDao;
import de.greenrobot.dao.DaoException;

import java.io.Serializable;


// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table RECORD.
 */
public class Record implements Serializable{

    /** Not-null value. */
    private String recordId;
    private String recordType;
    private String recordCont;
    private String recordPicUrl;
    private String recordVoice;
    private String recordVideo;
    private String recordEmpId;
    private String recordSchoolId;
    private String recordIsDel;
    private String dateLine;
    private String school_record_mood_id;
    private String school_record_mood_name;
    private String money;
    private String is_paimai;
    private String empName;
    private String empCover;
    private String levelName;
    private String plNum;
    private String zanNum;
    private String levelCount;
    private String schoolName;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient RecordDao myDao;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Record() {
    }

    public Record(String recordId) {
        this.recordId = recordId;
    }

    public Record(String recordId, String recordType, String recordCont, String recordPicUrl, String recordVoice, String recordVideo, String recordEmpId, String recordSchoolId, String recordIsDel, String dateLine, String school_record_mood_id, String school_record_mood_name, String money, String is_paimai, String empName, String empCover, String levelName, String plNum, String zanNum, String levelCount, String schoolName) {
        this.recordId = recordId;
        this.recordType = recordType;
        this.recordCont = recordCont;
        this.recordPicUrl = recordPicUrl;
        this.recordVoice = recordVoice;
        this.recordVideo = recordVideo;
        this.recordEmpId = recordEmpId;
        this.recordSchoolId = recordSchoolId;
        this.recordIsDel = recordIsDel;
        this.dateLine = dateLine;
        this.school_record_mood_id = school_record_mood_id;
        this.school_record_mood_name = school_record_mood_name;
        this.money = money;
        this.is_paimai = is_paimai;
        this.empName = empName;
        this.empCover = empCover;
        this.levelName = levelName;
        this.plNum = plNum;
        this.zanNum = zanNum;
        this.levelCount = levelCount;
        this.schoolName = schoolName;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRecordDao() : null;
    }

    /** Not-null value. */
    public String getRecordId() {
        return recordId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
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

    public String getSchool_record_mood_id() {
        return school_record_mood_id;
    }

    public void setSchool_record_mood_id(String school_record_mood_id) {
        this.school_record_mood_id = school_record_mood_id;
    }

    public String getSchool_record_mood_name() {
        return school_record_mood_name;
    }

    public void setSchool_record_mood_name(String school_record_mood_name) {
        this.school_record_mood_name = school_record_mood_name;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getIs_paimai() {
        return is_paimai;
    }

    public void setIs_paimai(String is_paimai) {
        this.is_paimai = is_paimai;
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

    public String getPlNum() {
        return plNum;
    }

    public void setPlNum(String plNum) {
        this.plNum = plNum;
    }

    public String getZanNum() {
        return zanNum;
    }

    public void setZanNum(String zanNum) {
        this.zanNum = zanNum;
    }

    public String getLevelCount() {
        return levelCount;
    }

    public void setLevelCount(String levelCount) {
        this.levelCount = levelCount;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
