package com.liangxun.yuejiula.huanxin.chat.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.BlackListAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.EmpsDATA;
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.*;

/**
 * 黑名单列表页面
 */
public class BlacklistActivity extends BaseActivity {
    private ListView listView;
    private BlackListAdapter adapter;
    List<Emp> list = new ArrayList<Emp>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);

        listView = (ListView) findViewById(R.id.list);

        // 从本地获取黑名单
        List<String> blacklist = EMContactManager.getInstance().getBlackListUsernames();
        String userNames = "";
        for(String str:blacklist){
            userNames +=str;
        }
        getFriends(userNames);


    }


    private void getFriends(final String userNames) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
//                InternetURL.GET_FRIENDS_URL,
                InternetURL.GET_INVITE_CONTACT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpsDATA data = getGson().fromJson(s, EmpsDATA.class);
                            if (data.getCode() == 200) {
                                list = data.getData();
                                // 显示黑名单列表
                                if (list != null) {
//                                    Collections.sort(list);
                                    adapter = new BlackListAdapter(list, BlacklistActivity.this);
                                    listView.setAdapter(adapter);
                                }
                                // 注册上下文菜单
                                registerForContextMenu(listView);

                            } else {
                                Toast.makeText(BlacklistActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(BlacklistActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(BlacklistActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("hxUserNames", userNames);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.remove_from_blacklist, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.remove) {
//            final String tobeRemoveUser = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
            Emp emp = (Emp) list.get(((AdapterContextMenuInfo) item.getMenuInfo()).position);
            final String  tobeRemoveUser = emp.getHxUsername();
            // 把目标user移出黑名单
            removeOutBlacklist(tobeRemoveUser);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 移出黑民单
     *
     * @param tobeRemoveUser
     */
    void removeOutBlacklist(final String tobeRemoveUser) {
        try {
            // 移出黑民单
            EMContactManager.getInstance().deleteUserFromBlackList(tobeRemoveUser);
//            adapter.remove(tobeRemoveUser);
            for(Emp emp:list){
                if(tobeRemoveUser.equals(emp.getHxUsername())){
                    list.remove(emp);
                }
            }
            adapter.notifyDataSetChanged();
        } catch (EaseMobException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                public void run() {
                    String str2 = getResources().getString(R.string.Removed_from_the_failure);
                    Toast.makeText(getApplicationContext(), str2, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }
}
