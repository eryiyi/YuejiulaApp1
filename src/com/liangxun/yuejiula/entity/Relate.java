package com.liangxun.yuejiula.entity;

import com.liangxun.yuejiula.db.DaoSession;
import com.liangxun.yuejiula.db.RelateDao;
import de.greenrobot.dao.DaoException;


// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table RELATE.
 */
public class Relate {

    /** Not-null value. */
    private String id;
    private String typeId;
    private String recordId;
    private String goodsId;
    private String empId;
    private String empTwoId;
    private String cont;
    private String dateline;
    private String orderId;
    private String is_read;
    private String empName;
    private String empCover;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient RelateDao myDao;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Relate() {
    }

    public Relate(String id) {
        this.id = id;
    }

    public Relate(String id, String typeId, String recordId, String goodsId, String empId, String empTwoId, String cont, String dateline, String orderId, String is_read, String empName, String empCover) {
        this.id = id;
        this.typeId = typeId;
        this.recordId = recordId;
        this.goodsId = goodsId;
        this.empId = empId;
        this.empTwoId = empTwoId;
        this.cont = cont;
        this.dateline = dateline;
        this.orderId = orderId;
        this.is_read = is_read;
        this.empName = empName;
        this.empCover = empCover;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRelateDao() : null;
    }

    /** Not-null value. */
    public String getId() {
        return id;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setId(String id) {
        this.id = id;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpTwoId() {
        return empTwoId;
    }

    public void setEmpTwoId(String empTwoId) {
        this.empTwoId = empTwoId;
    }

    public String getCont() {
        return cont;
    }

    public void setCont(String cont) {
        this.cont = cont;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
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
