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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.huanxin.chat.HxConstant;
import com.liangxun.yuejiula.huanxin.chat.adapter.ContactAdapter;
import com.liangxun.yuejiula.huanxin.chat.db.HxUserDao;
import com.liangxun.yuejiula.huanxin.chat.db.InviteMessgeDao;
import com.liangxun.yuejiula.huanxin.chat.domain.HxUser;
import com.liangxun.yuejiula.huanxin.chat.widget.Sidebar;
import com.liangxun.yuejiula.ui.PaoPaokefuActivity;

import java.util.*;
import java.util.Map.Entry;

/**
 * 联系人列表页
 *
 */
public class ContactlistActivity extends BaseActivity implements View.OnClickListener {
    private ContactAdapter adapter;
    private List<HxUser> contactList;
    private ListView listView;
    private boolean hidden;
    private Sidebar sidebar;
    private InputMethodManager inputMethodManager;
    private List<String> blackList;
    ImageButton clearSearch;
    EditText query;
    private ImageView add_contact;
    private TextView paopaokf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        //防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        add_contact = (ImageView) findViewById(R.id.add_contact);
        add_contact.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.list);
        sidebar = (Sidebar) findViewById(R.id.sidebar);
        sidebar.setListView(listView);
        //黑名单列表
        blackList = EMContactManager.getInstance().getBlackListUsernames();
        contactList = new ArrayList<HxUser>();
        // 获取设置contactlist
        getContactList();

        // 设置adapter
        adapter = new ContactAdapter(this, R.layout.row_contact, contactList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HxUser hxUser = adapter.getItem(position);
                String username ="";
                if(hxUser != null){
                    username = hxUser.getUsername();
                }
                if (HxConstant.NEW_FRIENDS_USERNAME.equals(username)) {
                    // 进入申请与通知页面
                    HxUser user = getMyApp().getContactList().get(HxConstant.NEW_FRIENDS_USERNAME);
                    user.setUnreadMsgCount(0);
                    startActivity(new Intent(ContactlistActivity.this, NewFriendsMsgActivity.class));
                } else {
                    // demo中直接进入聊天页面，实际一般是进入用户详情页
                    startActivity(new Intent(ContactlistActivity.this, ChatActivity.class).putExtra("emp", hxUser.getEmp()));
                }
            }
        });
        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        registerForContextMenu(listView);

        paopaokf = (TextView) this.findViewById(R.id.paopaokf);
        paopaokf.setOnClickListener(this);
    }


    /**
     * 获取联系人列表，并过滤掉黑名单和排序
     */
    private void getContactList() {
        contactList.clear();
        //获取本地好友列表
        Map<String, HxUser> users = getMyApp().getContactList();
        Iterator<Entry<String, HxUser>> iterator = users.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, HxUser> entry = iterator.next();
            if (!entry.getKey().equals(HxConstant.NEW_FRIENDS_USERNAME) && !entry.getKey().equals(HxConstant.GROUP_USERNAME)
                    && !blackList.contains(entry.getKey()))
                contactList.add(entry.getValue());
        }
        // 排序
        Collections.sort(contactList, new Comparator<HxUser>() {

            @Override
            public int compare(HxUser lhs, HxUser rhs) {
                return lhs.getHeader().compareTo(rhs.getHeader());
//                return  lhs.getEmp().getEmpName().compareTo(rhs.getEmp().getEmpName());
            }
        });
        // 把"申请与通知"添加到首位
        contactList.add(0, users.get(HxConstant.NEW_FRIENDS_USERNAME));

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // 长按第一个不弹menu
        if (((AdapterContextMenuInfo) menuInfo).position > 0) {
            getMenuInflater().inflate(R.menu.context_contact_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_contact) {
            HxUser tobeDeleteUser = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
            // 删除此联系人
            deleteContact(tobeDeleteUser);
            // 删除相关的邀请消息
            InviteMessgeDao dao = new InviteMessgeDao(this);
            dao.deleteMessage(tobeDeleteUser.getUsername());
            return true;
        } else if (item.getItemId() == R.id.add_to_blacklist) {
            HxUser user = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
            moveToBlacklist(user.getUsername());
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 删除联系人
     *
     * @param
     */
    public void deleteContact(final HxUser tobeDeleteUser) {
        String st1 = getResources().getString(R.string.deleting);
        final String st2 = getResources().getString(R.string.Delete_failed);
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMContactManager.getInstance().deleteContact(tobeDeleteUser.getEmp().getHxUsername());
                    // 删除db和内存中此用户的数据
                    HxUserDao dao = new HxUserDao(ContactlistActivity.this);
                    dao.deleteContact(tobeDeleteUser.getEmp().getHxUsername());
                    getMyApp().getContactList().remove(tobeDeleteUser.getEmp().getHxUsername());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            adapter.remove(tobeDeleteUser);
                            adapter.notifyDataSetChanged();

                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(ContactlistActivity.this, st2 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }
        }).start();

    }

    /**
     * 把user移入到黑名单
     */
    private void moveToBlacklist(final String username) {
        final ProgressDialog pd = new ProgressDialog(this);
        String st1 = getResources().getString(R.string.Is_moved_into_blacklist);
        final String st2 = getResources().getString(R.string.Move_into_blacklist_success);
        final String st3 = getResources().getString(R.string.Move_into_blacklist_failure);
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    //加入到黑名单
                    EMContactManager.getInstance().addUserToBlackList(username, false);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(ContactlistActivity.this, st2, Toast.LENGTH_SHORT).show();
                            refresh();
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(ContactlistActivity.this, st3, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }

    // 刷新ui
    public void refresh() {
        try {
            // 可能会在子线程中调到这方法
            runOnUiThread(new Runnable() {
                public void run() {
                    getContactList();
                    adapter.notifyDataSetChanged();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_contact:
                Intent add_intent = new Intent(this, AddContactActivity.class);
                startActivity(add_intent);
                break;
            case R.id.paopaokf:
                //查询约酒啦客服
                Intent intent = new Intent(ContactlistActivity.this, PaoPaokefuActivity.class);
                startActivity(intent);
                break;
        }
    }
}
