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
import com.liangxun.yuejiula.adapter.SJEmpAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.ContractSchoolDATA;
import com.liangxun.yuejiula.entity.ContractSchool;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/3/30.
 */
public class ChengbaoshangActivity extends BaseActivity implements View.OnClickListener {
    private ImageView shangjia_menu;
    private PullToRefreshListView shangjia_lstv;//�б�
    private ImageView search_null;//�������������
    private List<ContractSchool> emps = new ArrayList<ContractSchool>();
    private SJEmpAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private String cont = "";//����������
    private EditText searchText;//������
    private ImageView deleteContSearch;//���

    private TextView help;//����

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chengbaoshang_lstv);
        initView();
        initData();
    }

    private void initView() {
        shangjia_menu = (ImageView) this.findViewById(R.id.shangjia_menu);
        shangjia_menu.setOnClickListener(this);
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
                cont = searchText.getText().toString();//Ҫ�ѵ�����
                if (!StringUtil.isNullOrEmpty(cont)) {
                    deleteContSearch.setVisibility(View.VISIBLE);
                } else {
                    deleteContSearch.setVisibility(View.GONE);
                }
                pageIndex = 1;
                initData();
            }
        });
        deleteContSearch = (ImageView) this.findViewById(R.id.deleteContSearch);
        deleteContSearch.setOnClickListener(this);

        shangjia_lstv = (PullToRefreshListView) this.findViewById(R.id.shangjia_lstv);
        search_null = (ImageView) this.findViewById(R.id.search_null);
        search_null.setVisibility(View.GONE);
        adapter = new SJEmpAdapter(emps, this);
        shangjia_lstv.setAdapter(adapter);
        shangjia_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        shangjia_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(ChengbaoshangActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(ChengbaoshangActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        shangjia_lstv.setAdapter(adapter);
        shangjia_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContractSchool ctrs = emps.get(position - 1);
                Intent profile = new Intent(ChengbaoshangActivity.this, ProfilePersonalActivity.class);
                profile.putExtra(Constants.EMPID, ctrs.getEmpId());
                startActivity(profile);
            }
        });
        help = (TextView) this.findViewById(R.id.help);
        help.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shangjia_menu://����
                //����
                finish();
                break;
            case R.id.deleteContSearch://���
                searchText.setText("");
                break;
            case R.id.help://����
                Intent help = new Intent(ChengbaoshangActivity.this, HelpOneActivity.class);
                startActivity(help);
                break;
        }
    }

    //��ѯ���еĳа���
    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SEARCH_SJS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            ContractSchoolDATA data = getGson().fromJson(s, ContractSchoolDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    emps.clear();
                                }
                                emps.addAll(data.getData());
                                shangjia_lstv.onRefreshComplete();
                                if (emps.size() == 0) {
                                    search_null.setVisibility(View.VISIBLE);
                                } else {
                                    search_null.setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ChengbaoshangActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ChengbaoshangActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ChengbaoshangActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex));
                params.put("keyWords", cont);
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
