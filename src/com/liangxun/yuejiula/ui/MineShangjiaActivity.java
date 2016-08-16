package com.liangxun.yuejiula.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.liangxun.yuejiula.adapter.MineShangjiaAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.SellerGoodsDATA;
import com.liangxun.yuejiula.entity.SellerGoods;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/3/27
 * Time: 8:37
 * 类的功能、说明写在此处.
 */
public class MineShangjiaActivity extends BaseActivity implements View.OnClickListener, OnClickContentItemListener {
    private ImageView mine_record_xml_menu;//返回
    private ImageView search_null;//查询无结果
    private PullToRefreshListView mine_lstv;//列表页
    private MineShangjiaAdapter adapter;
    private List<SellerGoods> emps = new ArrayList<SellerGoods>();

    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
        setContentView(R.layout.mine_shangjias);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView();
        progressDialog = new CustomProgressDialog(MineShangjiaActivity.this, "正在加载中",R.anim.custom_dialog_frame);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        initData();
    }

    private void initView() {
        mine_record_xml_menu = (ImageView) this.findViewById(R.id.mine_record_xml_menu);
        mine_record_xml_menu.setOnClickListener(this);
        search_null = (ImageView) this.findViewById(R.id.search_null);
        mine_lstv = (PullToRefreshListView) this.findViewById(R.id.mine_lstv);
        adapter = new MineShangjiaAdapter(emps, this);

        adapter.setOnClickContentItemListener(this);
        mine_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        mine_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineShangjiaActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;

                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineShangjiaActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;

                initData();
            }
        });
        mine_lstv.setAdapter(adapter);
        mine_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = new Intent(MineShangjiaActivity.this, DetailShangjiaActivity.class);
                SellerGoods record = emps.get(position - 1);
                detail.putExtra(Constants.SELLERGOODS_INFO, record);
                startActivity(detail);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_record_xml_menu:
                finish();
                break;
//            case R.id.mine_good_add:
//                //添加商家
//                Intent intent = new Intent(MineShangjiaActivity.this, MineAddShangjiaActivity.class);
//                startActivity(intent);
//                break;
        }
    }

    SellerGoods emp = null;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        emp = emps.get(position);
        switch (flag) {
            case 1://点击头像  跳到个人主页查看个人信息
                Intent comment = new Intent(MineShangjiaActivity.this, ProfilePersonalActivity.class);
                comment.putExtra(Constants.EMPID, emp.getEmpId());
                startActivity(comment);
                break;
        }
    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_SHANGJIAS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SellerGoodsDATA data = getGson().fromJson(s, SellerGoodsDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    emps.clear();
                                }
                                emps.addAll(data.getData());
                                if (emps.size() == 0) {
                                    search_null.setVisibility(View.VISIBLE);
                                } else {
                                    search_null.setVisibility(View.GONE);
                                }
                                mine_lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MineShangjiaActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineShangjiaActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(MineShangjiaActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("keyWords", "");
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

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.UPDATE_SHANGJIA_INFOS)) {
                progressDialog = new CustomProgressDialog(MineShangjiaActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.UPDATE_SHANGJIA_INFOS);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

}
