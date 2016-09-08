package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.CollegeAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.data.UnivertyDATA;
import com.liangxun.yuejiula.entity.Univerty;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/6/19.
 */
public class SelectCollegeActivity extends BaseActivity implements View.OnClickListener {
    private ListView lstv;
    private CollegeAdapter adapter;
    List<Univerty> lists  = new ArrayList<Univerty>();
    private String province_uuid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_qiehuan_activity);
        province_uuid = getIntent().getExtras().getString(Constants.OTHER_PROVINCE);
        getUniversity();
        initView();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        lstv = (ListView) this.findViewById(R.id.lstv);
        adapter = new CollegeAdapter(lists, SelectCollegeActivity.this);
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //
                Univerty uni = lists.get(i);
                updateCollege(uni.getCoid());
            }
        });
    }

    private void getUniversity() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.GET_COLLEAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            UnivertyDATA data = getGson().fromJson(s, UnivertyDATA.class);
                            if (data.getCode() == 200) {
                                lists.clear();
                                lists.addAll(data.getData());
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(SelectCollegeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SelectCollegeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(SelectCollegeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("provinceId", province_uuid);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }

    private void updateCollege(final String coid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.UPDATE_COLLEGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                showMsg(SelectCollegeActivity.this,"切换圈子成功，请重新登录");
                                Intent loginV = new Intent(SelectCollegeActivity.this, LoginActivity.class);
                                startActivity(loginV);
                            } else {
                                Toast.makeText(SelectCollegeActivity.this, "操作失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SelectCollegeActivity.this, "操作失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(SelectCollegeActivity.this, "操作失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("school_id",coid);
                params.put("emp_id",getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class));
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
