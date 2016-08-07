package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.AndMeAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.GoodSingleDATA;
import com.liangxun.yuejiula.data.OrdersSingleDATA;
import com.liangxun.yuejiula.data.RecordSingleDATA;
import com.liangxun.yuejiula.data.RelateDATA;
import com.liangxun.yuejiula.entity.Relate;
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
 * Date: 2015/2/8
 * Time: 13:39
 * 类的功能、说明写在此处.
 */
public class AndMeAcitvity extends BaseActivity implements View.OnClickListener {
    private ImageView andme_xml_back;
    private ImageView search_null;
    private List<Relate> recordList;
    //动态listview
    private PullToRefreshListView home_lstv;
    //动态适配器
    private AndMeAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID
    Relate recordtmp;//转换用


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.andme_xml);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView();
        initData();
    }

    private void initView() {
        search_null = (ImageView) this.findViewById(R.id.search_null);
        home_lstv = (PullToRefreshListView) this.findViewById(R.id.andme_lstv);
        andme_xml_back = (ImageView) this.findViewById(R.id.andme_xml_back);
        andme_xml_back.setOnClickListener(this);
        recordList = new ArrayList<Relate>();
        adapter = new AndMeAdapter(recordList, AndMeAcitvity.this);
        home_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        home_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(AndMeAcitvity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(AndMeAcitvity.this, System.currentTimeMillis(),
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
                Relate relate = recordList.get(position - 1);
                if (relate.getTypeId().equals("0")) {
                    //根据动态ID 查询动态详情
                    getRecordByUUID(relate);
                }
                if (relate.getTypeId().equals("1")) {
                    //查询商品
                    getGoodsByUUID(relate);
                }
                if (relate.getTypeId().equals("2")) {//卖家相关
                    //查询订单
                    getOrderByNo(relate, "0");
                }
                if (relate.getTypeId().equals("3")) {//买家相关
                    //查询订单
                    getOrderByNo(relate, "1");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.andme_xml_back:
                finish();
                break;
        }
    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +   InternetURL.ANDME_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RelateDATA data = getGson().fromJson(s, RelateDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    recordList.clear();
                                }
                                recordList.addAll(data.getData());
                                home_lstv.onRefreshComplete();
                                if (recordList.size() == 0) {
                                    search_null.setVisibility(View.VISIBLE);
                                } else {
                                    search_null.setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(AndMeAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AndMeAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(AndMeAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empId", emp_id);
                params.put("page", String.valueOf(pageIndex));
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

    Relate record;

    //根据动态UUID
    private void getRecordByUUID(final Relate relate) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_RECORD_DETAIL_BYUUID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordSingleDATA data = getGson().fromJson(s, RecordSingleDATA.class);
                            if (data.getCode() == 200) {
                                Intent pagedetail = new Intent(AndMeAcitvity.this, DetailPageAcitvity.class);
                                pagedetail.putExtra(Constants.INFO, data.getData());
                                startActivity(pagedetail);
                            } else {
                                Toast.makeText(AndMeAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AndMeAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(AndMeAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", relate.getRecordId());
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

    //根据商品UUID
    private void getGoodsByUUID(final Relate relate) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_GOODS_DETAIL_BYUUID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            if (StringUtil.isJson(s)) {
                                GoodSingleDATA data = getGson().fromJson(s, GoodSingleDATA.class);
                                if (data.getCode() == 200) {
                                    Intent goodsdetail = new Intent(AndMeAcitvity.this, DetailGoodsActivity.class);
                                    goodsdetail.putExtra(Constants.GOODS, data.getData());
                                    startActivity(goodsdetail);
                                } else {
                                    Toast.makeText(AndMeAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(AndMeAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AndMeAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(AndMeAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", relate.getGoodsId());
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

    //根据订单号查询订单状态
    private void getOrderByNo(final Relate relate, final String typeId) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.FIND_ORDER_BYNO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            OrdersSingleDATA data = getGson().fromJson(s, OrdersSingleDATA.class);
                            if (data.getCode() == 200) {
                                if ("0".equals(typeId)){
                                    Intent pagedetail = new Intent(AndMeAcitvity.this, DetailOrderMngActivity.class);
                                    pagedetail.putExtra("orderVo", data.getData());
                                    startActivity(pagedetail);
                                }else {
                                    Intent pagedetail = new Intent(AndMeAcitvity.this, DetailOrderActivity.class);
                                    pagedetail.putExtra("orderVo", data.getData());
                                    startActivity(pagedetail);
                                }
                            } else {
                                Toast.makeText(AndMeAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AndMeAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(AndMeAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("orderNo", relate.getOrderId());
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