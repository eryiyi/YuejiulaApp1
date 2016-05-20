package com.liangxun.yuejiula.huanxin.chat.my;

import com.easemob.chat.EMConversation;
import com.liangxun.yuejiula.entity.Emp;

/**
 * Created by Administrator on 2015/3/5.
 * 继承环信提供的EMConversation，增加emp属性，以及getter and setter 方法
 */
public class MyEMConversation {


    private Emp emp;

    private EMConversation emConversation;

    public EMConversation getEmConversation() {
        return emConversation;
    }

    public void setEmConversation(EMConversation emConversation) {
        this.emConversation = emConversation;
    }

    public Emp getEmp() {
        return emp;
    }

    public void setEmp(Emp emp) {
        this.emp = emp;
    }
}
