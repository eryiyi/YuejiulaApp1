package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.UniversityAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.UnivertyDATA;
import com.liangxun.yuejiula.entity.Univerty;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/3/9
 * Time: 9:12
 * 类的功能、说明写在此处.
 */
public class OtherSchoolTwoActivity extends BaseActivity implements View.OnClickListener {
    private ImageView other_back;//返回
    private String cont;//搜索框内容
    private EditText searchText;//搜索框
    private ImageView deleteContSearch;//清除
    private List<Univerty> universitys = new ArrayList<Univerty>();
    private UniversityAdapter universityAdapter;
    private static boolean IS_REFRESH = true;

    //动态listview
    private PullToRefreshListView provinceGridView;
    private String province_uuid;//省份UUID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_school_two);
        initView();
        cont = getIntent().getExtras().getString(Constants.OTHER_PROVINCE_CONT);
        province_uuid = getIntent().getExtras().getString(Constants.OTHER_PROVINCE);
        getUniversity();
    }

    private void initView() {
        other_back = (ImageView) this.findViewById(R.id.other_back);
        other_back.setOnClickListener(this);
        searchText = (EditText) this.findViewById(R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cont = searchText.getText().toString();//要搜的内容
                if (!StringUtil.isNullOrEmpty(cont)) {
                    deleteContSearch.setVisibility(View.VISIBLE);
                } else {
                    deleteContSearch.setVisibility(View.GONE);
                }
                getUniversity();
            }
        });
        deleteContSearch = (ImageView) this.findViewById(R.id.deleteContSearch);
        deleteContSearch.setOnClickListener(this);
        provinceGridView = (PullToRefreshListView) this.findViewById(R.id.university);
        universityAdapter = new UniversityAdapter(universitys, OtherSchoolTwoActivity.this);
        provinceGridView.setAdapter(universityAdapter);
        provinceGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent gt = new Intent(OtherSchoolTwoActivity.this, OtherSchoolThreeActivity.class);
                Univerty province = universitys.get(position - 1);
                gt.putExtra(Constants.OTHER_UNIVERSITY, province.getCoid());
                gt.putExtra(Constants.OTHER_UNIVERSITY_TITLE, province.getName());
                startActivity(gt);
            }
        });
        provinceGridView.setMode(PullToRefreshBase.Mode.BOTH);
        provinceGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(OtherSchoolTwoActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                getUniversity();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(OtherSchoolTwoActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                getUniversity();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.other_back:
                finish();
                break;
            case R.id.deleteContSearch://清除
                searchText.setText("");
                break;
        }
    }

    private void getUniversity() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_COLLEAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            UnivertyDATA data = getGson().fromJson(s, UnivertyDATA.class);
                            if (data.getCode() == 200) {
                                universitys.clear();
                                universitys.addAll(data.getData());
                                provinceGridView.onRefreshComplete();
                                universityAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(OtherSchoolTwoActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(OtherSchoolTwoActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(OtherSchoolTwoActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("keyWords", cont);
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
}
