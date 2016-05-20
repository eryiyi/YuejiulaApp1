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
import com.liangxun.yuejiula.adapter.PartTimeAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.PartTimeDATA;
import com.liangxun.yuejiula.entity.PartTime;
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
 * Date: 2015/2/7
 * Time: 15:07
 * 类的功能、说明写在此处.
 */
public class PartTimeLstActivity extends BaseActivity implements View.OnClickListener {
    private ImageView parttime_menu;//返回按钮
    private EditText searchText;//搜索框
    private ImageView search_null;
    private ImageView deleteContSearch;//清除

    private PullToRefreshListView parttime_lstv;//列表

    private PartTimeAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private String typeid = "";
    private String content = "";
    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID

    private List<PartTime> goods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parttime_xml);
        typeid = getIntent().getExtras().getString(Constants.PARTTYPE);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        if (StringUtil.isNullOrEmpty(content)) {
            content = "";
        }
        initView();
        initData(pageIndex);

    }

    private void initView() {
        goods = new ArrayList<PartTime>();
        search_null = (ImageView) this.findViewById(R.id.search_null);
        parttime_menu = (ImageView) this.findViewById(R.id.parttime_menu);
        parttime_menu.setOnClickListener(this);

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
                content = searchText.getText().toString();//要搜的内容
                if (!StringUtil.isNullOrEmpty(content)) {
                    deleteContSearch.setVisibility(View.VISIBLE);
                } else {
                    deleteContSearch.setVisibility(View.GONE);
                }
                initData(1);
            }
        });

        deleteContSearch = (ImageView) this.findViewById(R.id.deleteContSearch);
        deleteContSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText.setText("");
            }
        });
        deleteContSearch.setVisibility(View.GONE);
        parttime_lstv = (PullToRefreshListView) this.findViewById(R.id.parttime_lstv);

        adapter = new PartTimeAdapter(goods, PartTimeLstActivity.this);

        parttime_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        parttime_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(PartTimeLstActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData(pageIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(PartTimeLstActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData(pageIndex);
            }
        });
        parttime_lstv.setAdapter(adapter);
        parttime_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = new Intent(PartTimeLstActivity.this, PartTimeDetailActivity.class);
                PartTime record = goods.get(position - 1);
                detail.putExtra(Constants.PART_INFO, record);
                startActivity(detail);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parttime_menu:
                finish();
                break;
        }
    }

    private void initData(final int page) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_PARTTIME_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            PartTimeDATA data = getGson().fromJson(s, PartTimeDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    goods.clear();
                                }
                                goods.addAll(data.getData());
                                if (goods.size() == 0) {
                                    search_null.setVisibility(View.VISIBLE);
                                } else {
                                    search_null.setVisibility(View.GONE);
                                }
                                parttime_lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(PartTimeLstActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PartTimeLstActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PartTimeLstActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("typeId", typeid);
                params.put("keyWords", content);
                params.put("schoolId", schoolId);
                params.put("page", String.valueOf(page));
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
