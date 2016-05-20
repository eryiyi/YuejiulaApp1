/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liangxun.yuejiula.huanxin.chat;

import android.content.Context;
import com.liangxun.yuejiula.huanxin.applib.model.DefaultHXSDKModel;
import com.liangxun.yuejiula.huanxin.chat.db.HxDbOpenHelper;
import com.liangxun.yuejiula.huanxin.chat.db.HxUserDao;
import com.liangxun.yuejiula.huanxin.chat.domain.HxUser;

import java.util.List;
import java.util.Map;

public class DemoHXSDKModel extends DefaultHXSDKModel {

    public DemoHXSDKModel(Context ctx) {
        super(ctx);
        // TODO Auto-generated constructor stub
    }

    // demo will use HuanXin roster
    public boolean getUseHXRoster() {
        // TODO Auto-generated method stub
        return true;
    }

    // demo will switch on debug mode
    public boolean isDebugMode() {
        return true;
    }

    public boolean saveContactList(List<HxUser> contactList) {
        // TODO Auto-generated method stub
        HxUserDao dao = new HxUserDao(context);
        dao.saveContactList(contactList);
        return true;
    }

    public Map<String, HxUser> getContactList() {
        // TODO Auto-generated method stub
        HxUserDao dao = new HxUserDao(context);
        return dao.getContactList();
    }

    public void closeDB() {
        // TODO Auto-generated method stub
        HxDbOpenHelper.getInstance(context).closeDB();
    }

    @Override
    public String getAppProcessName() {
        // TODO Auto-generated method stub
        return context.getPackageName();
    }
}
