package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.SearchGoodsAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.GoodsDATA;
import com.liangxun.yuejiula.entity.PaopaoGoods;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/11.
 */
public class SearchGoodsActivity extends BaseActivity implements View.OnClickListener {
    private String typeId;
    private String typeName;
    private TextView title;
    private String schoolId = "";

    private PullToRefreshListView home_lstv ;
    private SearchGoodsAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private List<PaopaoGoods> listgoods = new ArrayList<PaopaoGoods>();
    private ImageView search_null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_goods_activity);
        typeId = getIntent().getExtras().getString("typeId");
        typeName = getIntent().getExtras().getString("typeName");
        title = (TextView) this.findViewById(R.id.title);
        title.setText(typeName);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        search_null = (ImageView) this.findViewById(R.id.search_null);
        adapter = new SearchGoodsAdapter(listgoods, SearchGoodsActivity.this);
        home_lstv = (PullToRefreshListView) this.findViewById(R.id.home_lstv);
        home_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        home_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        home_lstv.setAdapter(adapter);
        home_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    PaopaoGoods good = listgoods.get(position-1);
                    if (good != null) {
                        Intent detail = new Intent(SearchGoodsActivity.this, DetailPaopaoGoodsActivity.class);
                        detail.putExtra(Constants.GOODS, good);
                        startActivity(detail);
                    }
                } catch (Exception e) {

                }
            }
        });
//        adapter.setOnClickContentItemListener(this);

        initData();
    }

    @Override
    public void onClick(View view) {

    }
    public void back(View view){
        finish();
    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.GET_GOODS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            GoodsDATA data = getGson().fromJson(s, GoodsDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    listgoods.clear();
                                }
                                listgoods.addAll(data.getData());
                                home_lstv.onRefreshComplete();
                                if (listgoods.size() == 0) {
                                    search_null.setVisibility(View.VISIBLE);
                                    home_lstv.setVisibility(View.GONE);
                                } else {
                                    search_null.setVisibility(View.GONE);
                                    home_lstv.setVisibility(View.VISIBLE);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(SearchGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SearchGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(SearchGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cont", "");
                params.put("schoolId", schoolId);
                params.put("page", String.valueOf(pageIndex));
                params.put("typeId", typeId);
                params.put("type", "0");
                params.put("empId", "");
                params.put("isMine", "");
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
