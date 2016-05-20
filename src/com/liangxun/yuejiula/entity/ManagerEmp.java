package com.liangxun.yuejiula.entity;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/11
 * Time: 11:09
 * 类的功能、说明写在此处.
 */
public class ManagerEmp {
    private String id;
    private String empId;
    private String admin;
    private String start;
    private String end;
    private String dateline;
    private String empNickName;

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

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getEmpNickName() {
        return empNickName;
    }

    public void setEmpNickName(String empNickName) {
        this.empNickName = empNickName;
    }
}
