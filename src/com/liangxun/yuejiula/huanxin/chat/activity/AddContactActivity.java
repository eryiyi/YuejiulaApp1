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

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
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
import com.liangxun.yuejiula.huanxin.chat.adapter.ContactsListAdapter;
import com.liangxun.yuejiula.ui.ProfilePersonalActivity;
import com.liangxun.yuejiula.ui.UpdateProfilePersonalActivity;
import com.liangxun.yuejiula.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddContactActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private EditText editText;
    private LinearLayout searchedUserLayout;
    private TextView nameText, mTextView;
    //	private ImageView avatar;
    private InputMethodManager inputMethodManager;
    private String toAddUsername;

    public ListView contacts;
    //    private ListView contact;
    public ContactsListAdapter contactsListAdapter;
    public List<Emp> friends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        mTextView = (TextView) findViewById(R.id.add_list_friends);

        editText = (EditText) findViewById(R.id.edit_note);
        String strAdd = getResources().getString(R.string.add_friend);
        mTextView.setText(strAdd);
        searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
        contacts = (ListView) findViewById(R.id.list_contacts);
        contacts.setOnItemClickListener(this);
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchContact();
            }
        });
        defaultFriends();

    }


    /**
     * 查找contact
     * @param
     */
    public void searchContact() {
        final String name = editText.getText().toString();
        toAddUsername = name;
        if (TextUtils.isEmpty(name)) {
            defaultFriends();
        }

        // TODO 从服务器获取此contact,如果不存在提示不存在此用户
        searchContact(name);
        //服务器存在此用户，显示此用户和添加按钮
        searchedUserLayout.setVisibility(View.VISIBLE);
//			nameText.setText(toAddUsername);
    }

    private void searchContact(final String username) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.SEARCH_CONTACT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        EmpsDATA data = getGson().fromJson(s, EmpsDATA.class);
                        friends = data.getData();
                        contactsListAdapter = new ContactsListAdapter(AddContactActivity.this, R.layout.activity_so_contacts, friends, getMyApp().getImageLoader());
                        contacts.setAdapter(contactsListAdapter);
//                        contact.setAdapter(contactsListAdapter);
//                        contactsListAdapter.setOnClickContentItemListener(AddContactActivity.this);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("keyWords", username);
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

    private void defaultFriends() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.RANDOM_GET_TEN_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        EmpsDATA data = getGson().fromJson(s, EmpsDATA.class);
                        friends = data.getData();
                        contactsListAdapter = new ContactsListAdapter(AddContactActivity.this, R.layout.activity_so_contacts, friends, getMyApp().getImageLoader());
                        contacts.setAdapter(contactsListAdapter);
//                        contact.setAdapter(contactsListAdapter);

//                        contactsListAdapter.setOnClickContentItemListener(AddContactActivity.this);
                        searchedUserLayout.setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("keyWords", username);
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

    public void back(View v) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Emp emp = friends.get(position);
        //判断
        if (!getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class).equals(emp.getEmpId())) {
            Intent intent = new Intent(this, ProfilePersonalActivity.class);
            intent.putExtra(Constants.EMPID, emp.getEmpId());
            startActivity(intent);
        } else {
            Intent profile = new Intent(this, UpdateProfilePersonalActivity.class);
            startActivity(profile);
        }
    }
}
