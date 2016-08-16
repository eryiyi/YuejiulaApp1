package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.FindEmpAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.EmpsDATA;
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.huanxin.chat.adapter.ContactsListAdapter;
import com.liangxun.yuejiula.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/16.
 */
public class AddFriendActivity extends BaseActivity implements View.OnClickListener {
    private EditText edit_note;
    private String toAddUsername;

    public ListView contacts;
    public FindEmpAdapter contactsListAdapter;
    public List<Emp> friends = new ArrayList<Emp>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend_activity);

        contacts = (ListView) this.findViewById(R.id.list_contacts);
        contactsListAdapter =  new FindEmpAdapter( friends, AddFriendActivity.this);
        contacts.setAdapter(contactsListAdapter);
        contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Emp emp = friends.get(i);
                //判断
                if (!getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class).equals(emp.getEmpId())) {
                    Intent intent = new Intent(AddFriendActivity.this, ProfilePersonalActivity.class);
                    intent.putExtra(Constants.EMPID, emp.getEmpId());
                    startActivity(intent);
                } else {
                    Intent profile = new Intent(AddFriendActivity.this, UpdateProfilePersonalActivity.class);
                    startActivity(profile);
                }
            }
        });

        edit_note = (EditText) findViewById(R.id.edit_note);
        edit_note.addTextChangedListener(new TextWatcher() {
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
    }

    @Override
    public void onClick(View view) {

    }

    public void back(View view){
        finish();
    }

    /**
     * 查找contact
     * @param
     */
    public void searchContact() {
        final String name = edit_note.getText().toString();
        toAddUsername = name;
        if (TextUtils.isEmpty(name)) {
            defaultFriends();
        }

        searchContact(name);
        //服务器存在此用户，显示此用户和添加按钮
//        searchedUserLayout.setVisibility(View.VISIBLE);
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
                        friends.clear();
                        friends.addAll(data.getData());
                        contactsListAdapter.notifyDataSetChanged();
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
                        friends.clear();
                        friends.addAll(data.getData());
//                        contactsListAdapter = new ContactsListAdapter(AddFriendActivity.this, R.layout.activity_so_contacts, friends, getMyApp().getImageLoader());
//                        contacts.setAdapter(contactsListAdapter);
                        contactsListAdapter.notifyDataSetChanged();
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


}
