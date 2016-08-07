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
package com.liangxun.yuejiula.huanxin.chat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.EmpsDATA;
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.huanxin.chat.HxConstant;
import com.liangxun.yuejiula.huanxin.chat.adapter.NewFriendsMsgAdapter;
import com.liangxun.yuejiula.huanxin.chat.db.InviteMessgeDao;
import com.liangxun.yuejiula.huanxin.chat.domain.InviteMessage;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 申请与通知
 *
 */
public class NewFriendsMsgActivity extends BaseActivity {
    private ListView listView;
    List<InviteMessage> msgs;
    NewFriendsMsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_msg);

        listView = (ListView) findViewById(R.id.list);
        InviteMessgeDao dao = new InviteMessgeDao(this);
        msgs = dao.getMessagesList();
        if (msgs.size() > 0) {
            getNewInviteMessage(msgs);
        }
        //设置adapter
//		adapter = new NewFriendsMsgAdapter(this, 1, msgs);
//		listView.setAdapter(adapter);
//        UniversityApplication.getInstance().getContactList().get(HxConstant.NEW_FRIENDS_USERNAME).setUnreadMsgCount(0);

    }

    private void getNewInviteMessage(List<InviteMessage> msgs) {
        //需要获取联系人的昵称
        StringBuffer strUser = new StringBuffer();
        for (int i = 0; i < msgs.size(); i++) {
            strUser.append(msgs.get(i).getFrom());
            if (i < msgs.size() - 1) {
                strUser.append(",");
            }
        }
        getFriends(strUser.toString());

    }

    private void setEmpNameToInviteMessage(List<Emp> emps) {
        for (Emp emp : emps) {
            for (InviteMessage msg : msgs) {
                if (msg.getFrom().equals(emp.getHxUsername())) {
                    msg.setEmp(emp);
                }
            }
        }
        adapter = new NewFriendsMsgAdapter(this, 1, msgs);
        listView.setAdapter(adapter);
        getMyApp().getContactList().get(HxConstant.NEW_FRIENDS_USERNAME).setUnreadMsgCount(0);
    }

    public void getFriends(final String names) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.GET_INVITE_CONTACT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpsDATA data = getGson().fromJson(s, EmpsDATA.class);
                            if (data.getCode() == 200) {
                                setEmpNameToInviteMessage(data.getData());
                            } else {
                                Toast.makeText(NewFriendsMsgActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(NewFriendsMsgActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(NewFriendsMsgActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("hxUserNames", names);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);

    }

    public void back(View view) {
        finish();
    }


}
